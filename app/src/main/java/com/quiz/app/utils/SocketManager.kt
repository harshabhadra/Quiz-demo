package com.quiz.app.utils

import android.util.Log
import com.quiz.app.MoxieApplication
import com.quiz.app.network.BASE_URL
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.Transport
import org.json.JSONObject

class SocketManager {
    companion object {
        //http://192.168.1.38:3000/
        private const val SERVER_URL = BASE_URL
        private const val TAG = "SocketManager"
    }

    private var socket: Socket? = null
    private var connectionListener: ConnectionListener? = null
    private var messageSendListener: MessageSendListener? = null


    init {
        try {
            socket = IO.socket(SERVER_URL)
            Log.e(TAG, "socket connecting")
        } catch (e: Exception) {
            Log.e(TAG, "failed to connect to socket: ${e.message}")
        }
    }

    fun connect() {
        val token = MoxieApplication.sessionManager?.getPrefString(SessionManager.ACCESS_TOKEN)

        socket?.io()?.on(Manager.EVENT_TRANSPORT, Emitter.Listener { args ->
            val transport: Transport = args[0] as Transport

            // Add headers before EVENT_REQUEST_HEADERS is called
            transport.on(Transport.EVENT_REQUEST_HEADERS) { args ->
                Log.v(TAG, "Caught EVENT_REQUEST_HEADERS, adding headers")
                val mHeaders = args[0] as MutableMap<String, List<String>>
                mHeaders["Authorization"] = listOf("Bearer $token")
            }

            transport.on(Transport.EVENT_ERROR) { args ->
                Log.e(TAG, "Transport error: ${args[0]}")
            }
        })

        socket?.on(Socket.EVENT_CONNECT) {
            connectionListener?.onConnected()
        }
        socket?.on(Socket.EVENT_DISCONNECT) {
            connectionListener?.onDisconnected()
        }
        socket?.on(Socket.EVENT_CONNECT_ERROR) {
            Log.e(TAG, "connection error: $it")
            connectionListener?.onConnectError(it)
        }
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun sendMessage(message: JSONObject, event: String) {
        Log.e(TAG, "emitting message for: $event")
        socket?.emit(event, message, Emitter.Listener { args ->
            Log.e(TAG, "message sent: ${args.isNotEmpty()}")
            messageSendListener?.onMessageSent(args.isNotEmpty())
        })
    }

    fun setEventListener(name: String, eventListener: EventListener) {
        Log.e(TAG,"listening to $name")
        socket?.on(name)
        { args ->
            // Handle event data and notify the listener
            Log.e(TAG, "message from socket: $args")
            eventListener.onEventReceived(args)
        }
    }

    fun setConnectionListener(listener: ConnectionListener) {
        connectionListener = listener
    }

    interface EventListener {
        fun onEventReceived(args: Array<Any>)
    }

    interface ConnectionListener {
        fun onConnected()
        fun onDisconnected()
        fun onConnectError(args: Array<Any>)
    }

    interface MessageSendListener {
        fun onMessageSent(isSuccessful: Boolean)
    }
}