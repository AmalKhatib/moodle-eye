package com.example.moodleeye.ui.auth

import android.annotation.TargetApi
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.moodleeye.ui.content.course.CoursesActivity
import com.example.moodleeye.util.Listener
import com.example.moodleeye.util.toast
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import android.os.Build
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS
import java.sql.Date
import java.text.SimpleDateFormat

class LoginActivity : AppCompatActivity()
    , KodeinAware, Listener {

    override val kodein by kodein()
        private val factory: AuthViewModelFactory by instance()
        var type: String? = "customer"
        var viewModel: AuthViewModel? = null

    lateinit var speakMethod : SpeakMethod
    lateinit var  sr : SpeechRecognizer

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.moodleeye.R.layout.activity_login)

        sr = SpeechRecognizer.createSpeechRecognizer(this)
        val listener = this.listener()
        sr?.setRecognitionListener(listener)

        GCPTTS.sr = sr
        GCPTTS.voicelistener = listener

        speakMethod = SpeakMethod(this@LoginActivity , this@LoginActivity)
        speakMethod.initAndroidTTSSetting()
        GCPTTS.isDone = true
        speakMethod.startSpeak( "قم بنُطق أو كتابة الرقم الجامِعِّيّْ.")

        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel!!.authListener = this

        btn_login.setOnClickListener {
            if (!et_id.text.isEmpty()) {
                viewModel!!.userName = et_id.text.toString()
                viewModel!!.onLoginButtonClick(btn_login)
            }

        }

    }

    override fun onStarted() {
    }

    override fun onSuccess(items: Any) {
        sr.stopListening()
        speakMethod.stopAudio()
        Intent(this, CoursesActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    override fun onFailure(message: String) {
        toast(message)
    }

    internal inner class listener : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            Log.d("voice error", "error $error")
        }

        override fun onResults(results: Bundle) {
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val chars = data!![0].split(" ")
            var id :String? = ""
            for(digit in chars){
                id += digit
            }
            et_id.setText(id)

            btn_login.callOnClick()

        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}
    }
}
