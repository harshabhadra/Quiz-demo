package com.quiz.app.ui.viewer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.quiz.app.MoxieApplication.Companion.session
import com.quiz.app.R
import com.quiz.app.ServiceLocator
import com.quiz.app.databinding.ActivityViewerBinding
import com.quiz.app.domain.DomainOption
import com.quiz.app.domain.StreamStatus
import com.quiz.app.network.apiService
import com.quiz.app.network.model.Option
import com.quiz.app.network.model.Question
import com.quiz.app.network.model.StreamState
import com.quiz.app.network.model.asDomainOptionList
import com.quiz.app.ui.EmojiAdapter
import com.quiz.app.ui.MainActivity
import com.quiz.app.utils.ConstUtils
import com.quiz.app.utils.FragConst
import com.quiz.app.utils.NetworkUtils
import com.quiz.app.utils.SessionManager
import com.quiz.app.utils.SocketManager
import com.quiz.app.utils.formatWithTwoDecimals
import com.quiz.app.utils.getEmojiList
import com.quiz.app.utils.hideKeyboard
import com.quiz.app.utils.showFragment
import io.agora.CallBack
import io.agora.ChatRoomChangeListener
import io.agora.ConnectionListener
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatOptions
import io.agora.chat.ChatRoom
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IAudioEffectManager
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class ViewerActivity : AppCompatActivity(), ChatRoomChangeListener,
    AnswerAdapter.OnAnswerItemClickListener, SocketManager.ConnectionListener {
    private var timeTakenHandler: Handler? = null

    private lateinit var viewModel: ViewerViewModel
    private val chatUserName = "user1"
    private val receiverName = "user1"
    private val chatToken =
        ""

    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    // Fill the App ID of your project generated on Agora Console.
    private val appId = ""

    // Fill the channel name.
    private val channelName = "test"

    // Fill the temp token generated on Agora Console.
    private lateinit var rtcToken: String

    private val APP_KEY = ""

    private val messageList: MutableSet<ChatMessage> = mutableSetOf()
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var emojiAdapter: EmojiAdapter

    // An integer that identifies the local user.
    private var uid = -1
    private var isJoined = false

    private var agoraEngine: RtcEngine? = null

    //SurfaceView to render local video in a Container.
    private var localSurfaceView: SurfaceView? = null

    //SurfaceView to render Remote video in a Container.
    private var remoteSurfaceView: SurfaceView? = null
    private lateinit var localContainer: FrameLayout
    private lateinit var remoteContainer: FrameLayout
    private lateinit var binding: ActivityViewerBinding
    private val chatroomId: String = ""

    private var type: Int = 0
    private lateinit var answerAdapter: AnswerAdapter
    private var hasAnswered = false
    private var totalVotes = 0
    private var canVote = true
    private var quizId: String = "644f93624333c27bbe91cb01"
    private lateinit var userId: String
    private lateinit var questionId: String
    private val joinLeaveJson = JSONObject()
    private var isCorrectAnswer = false
    private var isLeaving = false
    private var isTimerRunning = false
    var seconds = 0
    private var hasJoined = false
    private var seenQuestions = false
    private var isMuted = false

    private var audioEffectManager: IAudioEffectManager? = null
    private val soundEffectId = 1 // Unique identifier for the sound effect file
    private val soundEffectIdTwo = 2
    private val soundEffectIdThree = 3
    private var currentEffectId = -1
    private val soundEffectAppluse =
        "https://www.soundjay.com/human/applause-01.mp3" // URL or path to the sound effect
    private val soundEffectFail =
        "https://www.soundjay.com/misc/sounds/fail-buzzer-01.mp3" // URL or path to the sound effect

    private val soundEffectClock =
        "https://assets.mixkit.co/active_storage/sfx/1047/1047-preview.mp3"
    private var soundEffectStatus = 0
    private val voiceEffectIndex = 0
    private var audioPlaying = false // Manage the audio mixing state


    private var audioFilePath = "https://www.kozco.com/tech/organfinale.mp3"
    //https://www.kozco.com/tech/organfinale.mp3

    companion object {
        private const val TAG = "ViewerActivity"
    }

    private val options = mutableListOf<DomainOption>()
    private var counter: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewerBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val viewModelFactory = ServiceLocator.provideViewModelFactory(this, apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[ViewerViewModel::class.java]

//        audioFilePath = "android.resource://" + packageName + "/" + R.raw.question_start
        type = intent.getIntExtra(ConstUtils.CUSTOMER_TYPE, 0)
        rtcToken = intent.getStringExtra(ConstUtils.RTC_TOKEN) ?: ""
        uid = intent.getIntExtra(ConstUtils.UID, -1)
        quizId = "644f93624333c27bbe91cb01"
        userId = session(this).getPrefString(SessionManager.USER_ID) ?: ""

        answerAdapter = AnswerAdapter(this)
        binding.optionsRecyclerview.adapter = answerAdapter

        joinLeaveJson.put("quiz_id", quizId)
        joinLeaveJson.put("user_id", userId)
        Log.e(TAG, "socket start input: $joinLeaveJson")
        viewModel.sendMessage(joinLeaveJson, NetworkUtils.SOCKET_VIEWER_JOIN_LIVE)

        //Add Progress listener to circular progress bar
        binding.circularProgressBar.onProgressChangeListener = { progress ->
            canVote = progress < 100f
            if (audioPlaying && progress == 100f) {
                stopSoundEffect()
                audioPlaying = false
            }
        }
        Log.e(TAG, "rtc token: $rtcToken")
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        localContainer = binding.localVideoViewContainer
        remoteContainer = binding.remoteVideoViewContainer

        localContainer.isVisible = type == Constants.CLIENT_ROLE_BROADCASTER
        remoteContainer.isVisible = type == Constants.CLIENT_ROLE_AUDIENCE

        binding.messageRecyclerView.isVisible = type == Constants.CLIENT_ROLE_AUDIENCE

        emojiAdapter = EmojiAdapter(getEmojiList())
        chatMessageAdapter = ChatMessageAdapter()
        binding.messageRecyclerView.adapter = emojiAdapter
        setClicks()
        viewModel.deleteQuiz().apply {
            startCounter()
        }
        initSDK()
//        initListener()
    }

    private fun startCounter() {
        counter = object : CountDownTimer(5000, 1000) {
            override fun onTick(p0: Long) {
                try {
                    val anim = AlphaAnimation(1.0f, 0.0f)
                    anim.duration = 950
                    anim.repeatCount = 1
                    anim.repeatMode = Animation.REVERSE
                    binding.counterTv.startAnimation(anim)
                    binding.counterTv.text = "${1 + (p0 / 1000)}"
                } catch (e: Exception) {
                    Log.e(TAG, "counter exception: ${e.message}")
                }
            }

            override fun onFinish() {
                binding.counterGroup.visibility = View.GONE
                setupVideoSDKEngine()
                registerObservers()
                binding.calculateLayout.visibility = View.GONE
                binding.leaderBoardContainer.visibility = View.VISIBLE
                hasJoined = true
//                val bundle = Bundle().apply {
//                    putString("quiz_id", quizId)
//                }
//                showFragment(FragConst.LEADER_BOARD, bundle, false, R.id.leader_board_container)

            }
        }
        counter?.start()
    }

    private fun updateUi(streamStatus: StreamStatus, questionStart: Boolean = false) {
        Log.e(TAG, "stream state: $streamStatus, question start: $questionStart")
        when (streamStatus) {
            StreamStatus.LIVE -> {
                stopAudio()
                binding.timerGroup.visibility = View.GONE
                binding.rightBackTv.visibility = View.GONE
                if (questionStart) {
                    localContainer.visibility = View.VISIBLE
                    remoteContainer.visibility = View.GONE
                    binding.questionLayout.visibility = View.VISIBLE
                    showSmallVideo()
                } else {
                    stopSoundEffect()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.questionLayout.visibility = View.GONE
                        localContainer.visibility = View.GONE
                        remoteContainer.visibility = View.VISIBLE
                        showBigVideo()
                    }, 3000)
                }
            }

            StreamStatus.END -> {
                stopAudio()
                binding.questionLayout.visibility = View.GONE
                remoteSurfaceView?.visibility = View.GONE
            }

            StreamStatus.PAUSED -> {
                showBigVideo()
                playAudioEffect()
                binding.timerGroup.visibility = View.VISIBLE
                binding.rightBackTv.visibility = View.VISIBLE
                binding.questionLayout.visibility = View.GONE
                binding.timerTv.text = "Game will begin shortly"
                localContainer.visibility = View.VISIBLE
                remoteContainer.visibility = View.GONE
            }
        }
    }

    private fun showQuestionAndOptions(it: Question) {
        options.clear()
        if (it.totalQuestions != null) {
            binding.questionNumberTv.text = "Question ${it.question_index} of ${it.totalQuestions}"
            binding.questionTv.text = it.question.question
            binding.circularProgressBar.progress = 0f
            questionId = it.question.id
//            playAudioEffect()
        }
        it.question.options?.let { options ->
            submitOptionData(options)
        }
            ?: let {
                binding.optionsRecyclerview.visibility = View.GONE
                answerAdapter.submitList(options)
            }
    }

    private fun submitOptionData(optionsList: List<Option>) {
        if (optionsList.isNotEmpty()) {
            playSoundEffect(soundEffectIdThree, soundEffectClock)
            binding.optionsRecyclerview.visibility = View.VISIBLE
            options.addAll(optionsList.asDomainOptionList())
            hasAnswered = false
            totalVotes = 0
            answerAdapter.submitAnswerDetails(totalVotes, hasAnswered, false)
            answerAdapter.submitList(options.toList())
            startProgress()
            startTimer()
        }
    }

    private fun startTimer() {
        isTimerRunning = true
        timeTakenHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                seconds += 1
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                timeTakenHandler?.postDelayed(this, 1000)
            }
        }
    }

    private fun startProgress() {
        binding.circularProgressBar.progress = 0f
        binding.circularProgressBar.apply {
            progressMax = 100f
            setProgressWithAnimation(100f, 15000L)
        }
    }

    private fun registerObservers() {
        viewModel.getSocketQuestionLiveData().observe(this, Observer {
            it?.let {
                if (hasJoined) {
                    seenQuestions = true
                    binding.timeTookTv.text = ""
                    updateUi(StreamStatus.LIVE, true)
                    Log.e(TAG, "received question: ${it.question.options}")
                    showQuestionAndOptions(it)
                }
            }
        })

        viewModel.getSocketAnswerLiveData().observe(this) {
            it?.let {
                Log.e(TAG, "answers:$it")
                if (hasJoined && seenQuestions) {
                    it.forEachIndexed { index, totalAnswer ->
                        options[index].votes = totalAnswer.totalAnswers
                    }
                    answerAdapter.submitAnswerDetails(totalVotes, hasAnswered, false)
                    answerAdapter.submitList(options)
                    answerAdapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.getSocketQuestionEndLiveData().observe(this) {
            it?.let {
                if (hasJoined && seenQuestions) {
                    it.question.options?.forEachIndexed { index: Int, option: Option ->
                        options[index].isCorrect = option.isCorrect == true
                    }
                    answerAdapter.submitAnswerDetails(totalVotes, hasAnswered, true)
                    answerAdapter.submitList(options.toList())
                    answerAdapter.notifyDataSetChanged()
                    updateUi(StreamStatus.LIVE, false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        options.clear()
                        answerAdapter.submitList(options)
                        answerAdapter.notifyDataSetChanged()
                    }, 3000)
                }
            }
        }

        viewModel.getStreamStateLiveData().observe(this) {
            it?.let { streamState: StreamState ->
                if (hasJoined && seenQuestions) {
                    Toast.makeText(this, "${streamState.status}", Toast.LENGTH_SHORT).show()
                    if (streamState.status == "paused") {
                        updateUi(StreamStatus.PAUSED)
                    } else {
                        updateUi(StreamStatus.LIVE)
                    }
                }
            }
        }

        viewModel.getCalculationLiveData().observe(this) {
            it?.let {
                Log.e(TAG, "$it")
                if (hasJoined) {
                    if (it == NetworkUtils.SOCKET_CALCULATION_START) {
                        binding.mainConainer.visibility = View.GONE
                        binding.calculateLayout.visibility = View.VISIBLE

                    } else {
                        binding.calculateLayout.visibility = View.GONE
                        binding.leaderBoardContainer.visibility = View.VISIBLE
                        val bundle = Bundle().apply {
                            putString("quiz_id", quizId)
                        }
                        showFragment(
                            FragConst.LEADER_BOARD,
                            bundle,
                            false,
                            R.id.leader_board_container
                        )
                        Log.e(TAG, "$it")
                    }
                }
            }
        }
    }


    private fun setClicks() {
        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }
        binding.closeIv.setOnClickListener {
            leaveChannel()
        }

        binding.muteIv.setOnClickListener {
            isMuted = isMuted.not()
            if (isJoined) {
                agoraEngine?.muteRemoteAudioStream(uid, !isMuted)
                binding.muteIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        if (isMuted) R.drawable.mute_icon_white else R.drawable.outline_volume_mute_24
                    )
                )
            }
        }
    }

    override fun onBackPressed() {
        if (isLeaving.not()) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
            isLeaving = isLeaving.not()
        } else {
            leaveChannel()
        }
    }

    private fun goBack() {
        startActivity(Intent(this@ViewerActivity, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.stopPreview()
        agoraEngine?.leaveChannel()
        counter?.cancel()
//        viewModel.sendMessage(joinLeaveJson, NetworkUtils.SOCKET_VIEWER_LEAVE_LIVE)
        // Destroy the engine in a sub-thread to avoid congestion

        // Destroy the engine in a sub-thread to avoid congestion
        lifecycleScope.launch(Dispatchers.Default) {
            RtcEngine.destroy()
            agoraEngine = null
        }
    }

    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }

    //-------------------------------------------------------------Live Streaming-------------------------------------------------------
    private fun setupVideoSDKEngine() {
        try {
            Log.i(TAG, "starting agora enghine")
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine?.enableVideo()
            joinChannel()
        } catch (e: Exception) {
            showMessage(e.toString())
            Log.e(TAG, "error set up video engine: ${e.message}")
        }
    }

    //Event handler for live streaming
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote host joining the channel to get the uid of the host.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")
            if (type != Constants.CLIENT_ROLE_AUDIENCE) return
            // Set the remote video view
            runOnUiThread {
                if (type == Constants.CLIENT_ROLE_AUDIENCE) {
                    setupRemoteVideo(uid)
                }
            }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel $channel, uid: $uid")
            lifecycleScope.launch(Dispatchers.Main) {
                setAudioEffects()
//                loginToChatAgora()
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Live Stream Ended")
            runOnUiThread {
                updateUi(StreamStatus.END)
                leaveChannel()
                Log.e(TAG, "live stream ended")
                Toast.makeText(this@ViewerActivity, "Stream Ended", Toast.LENGTH_SHORT).show()
            }

        }

        override fun onConnectionLost() {
            Log.e(TAG, "connection lost")
        }

        override fun onLeaveChannel(stats: RtcStats?) {
            super.onLeaveChannel(stats)
        }

        override fun onAudioEffectFinished(soundId: Int) {
            super.onAudioEffectFinished(soundId)
            audioEffectManager?.stopEffect(soundId)
            soundEffectStatus = 0
        }
    }

    private fun stopAudio() {
        agoraEngine?.stopAudioMixing()
    }

    private fun stopSoundEffect() {
        Log.e(TAG,"current effect id: $currentEffectId")
        if (currentEffectId != -1) {
            audioEffectManager?.stopEffect(currentEffectId)
        }

    }

    private fun setAudioEffects() {
        // Set up the audio effects manager
        audioEffectManager = agoraEngine?.audioEffectManager
        audioEffectManager?.preloadEffect(soundEffectId, soundEffectAppluse)
        audioEffectManager?.preloadEffect(soundEffectIdTwo, soundEffectFail)
        audioEffectManager?.preloadEffect(soundEffectIdThree, soundEffectClock)
    }

    private fun playAudioEffect() {
        Log.e(TAG, "playing audio effect")
        try {
            agoraEngine?.startAudioMixing(audioFilePath, true, 1, 0)
            audioPlaying = true
            showMessage("Audio playing")
        } catch (e: java.lang.Exception) {
            showMessage("Exception playing audio\n$e")
        }
    }

    private fun playSoundEffect(effectId: Int, path: String, loop: Int = 0) {
        currentEffectId = effectId
        audioEffectManager?.playEffect(
            effectId,   // The ID of the sound effect file.
            path,   // The path of the sound effect file.
            loop,  // The number of sound effect loops. -1 means an infinite loop. 0 means once.
            1.0,   // The pitch of the audio effect. 1 represents the original pitch.
            0.0, // The spatial position of the audio effect. 0.0 represents that the audio effect plays in the front.
            100.0, // The volume of the audio effect. 100 represents the original volume.
            false,// Whether to publish the audio effect to remote users.
            0    // The playback starting position of the audio effect file in ms.
        )
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView?.setZOrderMediaOverlay(true)
        showBigVideo()
        agoraEngine?.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                uid
            )
        )
        // Display RemoteSurfaceView.
        remoteSurfaceView?.visibility = View.VISIBLE
    }

    private fun showBigVideo() {
        binding.smallVideoContainer.removeAllViews()
        remoteContainer.removeAllViews()
        remoteContainer.addView(remoteSurfaceView)
    }

    private fun showSmallVideo() {
        remoteContainer.removeAllViews()
        binding.smallVideoContainer.removeAllViews()
        binding.smallVideoContainer.addView(remoteSurfaceView)
    }

    private fun setupLocalVideo() {
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = SurfaceView(baseContext)
        localContainer.addView(localSurfaceView)
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine?.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    private fun joinChannel() {
        if (checkSelfPermission()) {
            if (rtcToken.isNotEmpty() || uid == -1) {
                Log.e(TAG, "joining channel")
                try {
                    val options = ChannelMediaOptions()
                    // For Live Streaming, set the channel profile as LIVE_BROADCASTING.
                    options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
                    // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
                    options.clientRoleType = type
                    // You need to specify the user ID yourself, and ensure that it is unique in the channel.
                    agoraEngine?.joinChannel(rtcToken, channelName, uid, options)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to join channel: ${e.message}")
                }
            } else {
                Log.e(TAG, "rtc token is empty or invalid uid: $uid")
                finish()
            }
        } else {
            Toast.makeText(applicationContext, "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }


    fun leaveChannel() {
        if (!isJoined) {
            showMessage("Join a channel first")
            joinChannel()
        } else {
            agoraEngine?.leaveChannel()
            isTimerRunning = false
            showMessage("You left the channel")
            goBack()
//            leaveChatRoom()
//            signOutChat()
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
        }
    }

    //---------------------------------------------------------------Chat Message-------------------------------------------------------
    // Initializes the SDK.
    private fun initSDK() {
        val options = ChatOptions()
        // Gets your App Key from Agora Console.
        if (TextUtils.isEmpty(APP_KEY)) {
            Toast.makeText(
                this,
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
    }

    private fun initListener() {
        // Adds message event callbacks.
        ChatClient.getInstance().chatManager().addMessageListener { messages: List<ChatMessage> ->
            Log.e(TAG, "messages received: ${messages.size}, $messages")
            runOnUiThread {
                messageList.addAll(messages)
                chatMessageAdapter.submitList(messageList.toList())
                chatMessageAdapter.notifyDataSetChanged()
            }
        }
        // Adds connection event callbacks.
        ChatClient.getInstance().addConnectionListener(object : ConnectionListener {
            override fun onConnected() {
                Log.e(TAG, "onConnected")
            }

            override fun onDisconnected(error: Int) {
                Log.e(TAG, "onDisconnected: $error")
            }

            override fun onLogout(errorCode: Int) {
                Log.e(TAG, "User needs to log out: $errorCode")
                ChatClient.getInstance().logout(false, null)
            }

            // This callback occurs when the token expires. When the callback is triggered, the app client must get a new token from the app server and logs in to the app again.
            override fun onTokenExpired() {
                Log.e(TAG, "ConnectionListener onTokenExpired")
            }

            // This callback occurs when the token is about to expire.
            override fun onTokenWillExpire() {
                Log.e(TAG, "ConnectionListener onTokenWillExpire")
            }
        })
    }

    private fun loginToChatAgora() {
        if (TextUtils.isEmpty(chatUserName) || TextUtils.isEmpty(chatToken)) {
            showMessage("Username or token is empty!")
            return
        }
        ChatClient.getInstance().loginWithAgoraToken(chatUserName, chatToken, object : CallBack {
            override fun onSuccess() {
                showMessage("Sign in success!")
                joinChatRoom()
                initListener()
            }

            override fun onError(code: Int, error: String) {
                Log.e(TAG, "Error login to chat: $error")
            }
        })
    }

    // Logs out.
    private fun signOutChat() {
        if (ChatClient.getInstance().isLoggedInBefore) {
            ChatClient.getInstance().logout(true, object : CallBack {
                override fun onSuccess() {
                    runOnUiThread {
                        goBack()
                    }
                }

                override fun onError(code: Int, error: String) {
                    Log.e(TAG, "error sign our chat: $code, $error")
                }
            })
        } else {
            showMessage("You were not logged in")
        }
    }

    // Sends the first message.
    private fun sendMessage() {
        val toSendName = receiverName
        val content = binding.messageEditText.text.toString()
        // Creates a text message.
        val message = ChatMessage.createTextSendMessage(content, chatroomId)
        message.chatType = ChatMessage.ChatType.ChatRoom
        binding.messageEditText.setText("")
        hideKeyboard()
        // Sets the message callback before sending the message.
        message.setMessageStatusCallback(object : CallBack {
            override fun onSuccess() {
                showMessage("Send message success!")
                runOnUiThread {
                    messageList.add(message)
                    chatMessageAdapter.submitList(messageList.toList())
                }
            }

            override fun onError(code: Int, error: String) {
                Log.e(TAG, "error sending message: $error, code: $code")
            }
        })

        // Sends the message.
        ChatClient.getInstance().chatManager().sendMessage(message)
    }

    //-------------------------------------------------------------chat Group-----------------------------------------------------------
    private fun createChatRoom(
        subject: String,
        description: String,
        welcomMessage: String,
        maxUserCount: Int,
        members: List<String>
    ) {
        val chatRoom = ChatClient.getInstance().chatroomManager()
            .createChatRoom(subject, description, welcomMessage, maxUserCount, members)
    }

    private fun joinChatRoom() {
        ChatClient.getInstance().chatroomManager()
            .joinChatRoom(chatroomId, object : ValueCallBack<ChatRoom?> {
                override fun onSuccess(value: ChatRoom?) {
                    showMessage("Join chat room: ${value?.name}")
                }

                override fun onError(error: Int, errorMsg: String) {
                    Log.e(TAG, "error joining chat room: $errorMsg")
                }
            })
    }

    private fun leaveChatRoom() {
        ChatClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);
    }

    override fun onChatRoomDestroyed(roomId: String?, roomName: String?) {

    }

    override fun onMemberJoined(roomId: String?, participant: String?) {
        showMessage("$participant joined")
    }

    override fun onMemberExited(roomId: String?, roomName: String?, participant: String?) {

    }

    override fun onRemovedFromChatRoom(
        reason: Int,
        roomId: String?,
        roomName: String?,
        participant: String?
    ) {

    }

    override fun onMuteListAdded(
        chatRoomId: String?,
        mutes: MutableList<String>?,
        expireTime: Long
    ) {

    }

    override fun onMuteListRemoved(chatRoomId: String?, mutes: MutableList<String>?) {

    }

    override fun onWhiteListAdded(chatRoomId: String?, whitelist: MutableList<String>?) {

    }

    override fun onWhiteListRemoved(chatRoomId: String?, whitelist: MutableList<String>?) {

    }

    override fun onAllMemberMuteStateChanged(chatRoomId: String?, isMuted: Boolean) {

    }

    override fun onAdminAdded(chatRoomId: String?, admin: String?) {

    }

    override fun onAdminRemoved(chatRoomId: String?, admin: String?) {

    }

    override fun onOwnerChanged(chatRoomId: String?, newOwner: String?, oldOwner: String?) {

    }

    override fun onAnnouncementChanged(chatRoomId: String?, announcement: String?) {

    }

    override fun onAnswerItemClick(option: DomainOption, position: Int) {
        if (canVote) {
            isCorrectAnswer = option.isCorrect
            hasAnswered = true
            options[position].votes = option.votes.plus(1)
            options[position].hasVoted = true
            totalVotes += 1
            answerAdapter.submitAnswerDetails(totalVotes, hasAnswered, false)
            answerAdapter.notifyItemChanged(position)
            submitAnswer(option.id)
        }
    }

    override fun onCorrectAnswerClick(correct: Boolean) {
        Log.e(TAG, "is correct answer: $correct")
        playSoundEffect(
            if (correct) soundEffectId else soundEffectIdTwo,
            if (correct) soundEffectAppluse else soundEffectFail
        )
    }

    private fun submitAnswer(optionId: String) {
        val timeTaken = seconds.toDouble()
        val answerJson = JSONObject()
        answerJson.put("quiz_id", quizId)
        answerJson.put("user_id", userId)
        answerJson.put("question_id", questionId)
        answerJson.put("option_id", optionId)
        answerJson.put("duration", timeTaken.formatWithTwoDecimals())
        Log.e(TAG, "socket submit answer input: $answerJson")
        viewModel.sendMessage(answerJson, NetworkUtils.SOCKET_SUBMIT_ANSWER)
        isTimerRunning = false
        Log.e(TAG, "time taken:$seconds $timeTaken")
        binding.timeTookTv.text = "You took $timeTaken sec to answer!"
        seconds = 0
        timeTakenHandler?.removeCallbacks(mStatusChecker)
    }

    override fun onConnected() {
        Log.e(TAG, "socket connected")
    }

    override fun onDisconnected() {
    }

    override fun onConnectError(args: Array<Any>) {
    }
}