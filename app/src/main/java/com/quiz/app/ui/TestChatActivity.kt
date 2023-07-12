package com.quiz.app.ui

import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quiz.app.R
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatOptions
import io.agora.chat.TextMessageBody
import java.text.SimpleDateFormat
import java.util.*


class TestChatActivity : AppCompatActivity() {

    private val USERNAME = "user2"
    private val TOKEN =
        "007eJxTYMisT9nUKzlVxumr8vo3Z5Zt37VtxacFH4LSPxmcuv96jsRnBYYUc3PTZMPkZEuTJEOTRIvkJGOzpJRUw6TExMQ0g2QT8zMZuikNgYwMG+0lGBgZWIGYkQHEV2FIMzVISjNKMtBNMU5M1jU0TE3RtUhOS9Y1Nk9LNTQwskwxNzAAABjhK5M="
    private val APP_KEY = "41937649#1095855"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        initView();
        initSDK();
        initListener();
    }

    // Initializes the view.
    private fun initView() {
        (findViewById<View>(R.id.tv_log) as TextView).movementMethod =
            ScrollingMovementMethod()
    }

    // Initializes the SDK.
    private fun initSDK() {
        val options = ChatOptions()
        // Gets your App Key from Agora Console.
        if (TextUtils.isEmpty(APP_KEY)) {
            Toast.makeText(
                this@TestChatActivity,
                "You should set your AppKey first!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        // Sets your App Key to options.
        options.appKey = APP_KEY
        // Initializes the Agora Chat SDK.
        ChatClient.getInstance().init(this, options)
        // Makes the Agora Chat SDK debuggable.
        ChatClient.getInstance().setDebugMode(true)
        // Shows the current user.
        (findViewById<View>(R.id.tv_username) as TextView).text = "Current user: $USERNAME"
    }

    private fun initListener() {
        // Adds message event callbacks.
        ChatClient.getInstance().chatManager().addMessageListener { messages: List<ChatMessage> ->
            for (message in messages) {
                val builder = StringBuilder()
                builder.append("Receive a ").append(message.type.name)
                    .append(" message from: ").append(message.from)
                if (message.type == ChatMessage.Type.TXT) {
                    builder.append(" content:")
                        .append((message.body as TextMessageBody).message)
                }
                showLog(builder.toString(), false)
            }
        }
        // Adds connection event callbacks.
        ChatClient.getInstance().addConnectionListener(object : ConnectionListener {
            override fun onConnected() {
                showLog("onConnected", false)
            }

            override fun onDisconnected(error: Int) {
                showLog("onDisconnected: $error", false)
            }

            override fun onLogout(errorCode: Int) {
                showLog("User needs to log out: $errorCode", false)
                ChatClient.getInstance().logout(false, null)
            }

            // This callback occurs when the token expires. When the callback is triggered, the app client must get a new token from the app server and logs in to the app again.
            override fun onTokenExpired() {
                showLog("ConnectionListener onTokenExpired", true)
            }

            // This callback occurs when the token is about to expire.
            override fun onTokenWillExpire() {
                showLog("ConnectionListener onTokenWillExpire", true)
            }
        })
    }

    // Logs in with a token.
    fun signInWithToken(view: View?) {
        loginToAgora()
    }

    private fun loginToAgora() {
        if (TextUtils.isEmpty(USERNAME) || TextUtils.isEmpty(TOKEN)) {
            showLog("Username or token is empty!", true)
            return
        }
        ChatClient.getInstance().loginWithAgoraToken(USERNAME, TOKEN, object : CallBack {
            override fun onSuccess() {
                showLog("Sign in success!", true)
            }

            override fun onError(code: Int, error: String) {
                showLog(error, true)
            }
        })
    }

    // Logs out.
    fun signOut(view: View?) {
        if (ChatClient.getInstance().isLoggedInBefore) {
            ChatClient.getInstance().logout(true, object : CallBack {
                override fun onSuccess() {
                    showLog("Sign out success!", true)
                }

                override fun onError(code: Int, error: String) {
                    showLog(error, true)
                }
            })
        } else {
            showLog("You were not logged in", false)
        }
    }

    // Sends the first message.
    fun sendFirstMessage(view: View?) {
        val toSendName = (findViewById<View>(R.id.et_to_chat_name) as EditText).text.toString()
            .trim { it <= ' ' }
        val content =
            (findViewById<View>(R.id.et_msg_content) as EditText).text.toString().trim { it <= ' ' }
        // Creates a text message.
        val message = ChatMessage.createTextSendMessage(content, toSendName)
        // Sets the message callback before sending the message.
        message.setMessageStatusCallback(object : CallBack {
            override fun onSuccess() {
                showLog("Send message success!", true)
            }

            override fun onError(code: Int, error: String) {
                showLog(error, true)
            }
        })

        // Sends the message.
        ChatClient.getInstance().chatManager().sendMessage(message)
    }

    // Shows logs.
    private fun showLog(content: String, showToast: Boolean) {
        if (TextUtils.isEmpty(content)) {
            return
        }
        runOnUiThread {
            if (showToast) {
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
            }
            val tv_log = findViewById<TextView>(R.id.tv_log)
            val preContent = tv_log.text.toString().trim { it <= ' ' }
            val builder = StringBuilder()
            builder.append(
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            )
                .append(" ").append(content).append("\n").append(preContent)
            tv_log.text = builder
        }
    }

}