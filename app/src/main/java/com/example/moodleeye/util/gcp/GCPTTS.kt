package com.example.moodleeye.util.gcp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import androidx.core.os.postDelayed
import com.example.moodleeye.data.models.AnswerSheet
import com.example.moodleeye.ui.content.quiz.QuestionsAdapter
import com.example.moodleeye.util.SpeakMethod

import com.google.gson.JsonParser


import com.squareup.okhttp.Callback
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import java.io.IOException
import java.util.ArrayList

class GCPTTS {

    private val mSpeakListeners = ArrayList<ISpeakListener>()

    private var mGCPVoice: GCPVoice? = null
    private var mAudioConfig: AudioConfig? = null
    private var mMessage: String? = null
    private var mVoiceMessage: VoiceMessage? = null

    private var mVoiceLength = -1

    private val runnableSend = Runnable {
        Log.d(TAG, "Message: " + mVoiceMessage!!.toString())

        val okHttpClient = OkHttpClient()
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            mVoiceMessage!!.toString()
        )

        val config = Config()
        val request = Request.Builder()
            .url(config.SYNTHESIZE_ENDPOINT)
            .addHeader(config.API_KEY_HEADER, config.API_KEY)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .post(body)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(request: Request?, e: IOException?) {
                speakFail(mMessage, e!!)
                Log.e(TAG, "onFailure error : " + e.message)
            }
            @Throws(IOException::class)
            override fun onResponse( response: Response) {

                if (response != null) {
                    Log.i(TAG, "onResponse code = " + response!!.code())
                    if (response.code() == 200) {
                        val text = response.body()?.string()
                        val jsonElement = JsonParser().parse(text)
                        val jsonObject = jsonElement.getAsJsonObject()

                        if (jsonObject != null) {
                            var json = jsonObject!!.get("audioContent").toString()
                            json = json.replace("\"", "")
                            playAudio(json)
                            return
                        }
                    }
                }

                speakFail(mMessage, NullPointerException("get response fail"))
            }
        })
    }

    constructor(gcpVoice: GCPVoice, audioConfig: AudioConfig) {
        mGCPVoice = gcpVoice
        mAudioConfig = audioConfig
    }

    fun setGCPVoice(gcpVoice: GCPVoice) {
        mGCPVoice = gcpVoice
    }

    fun setAudioConfig(audioConfig: AudioConfig) {
        mAudioConfig = audioConfig
    }

    fun start(text: String) {
        if (mGCPVoice != null && mAudioConfig != null) {
            mMessage = text
            mVoiceMessage = VoiceMessage.Builder()
                .addParameter(Input(text))
                .addParameter(mGCPVoice!!)
                .addParameter(mAudioConfig!!)
                .build()
            Thread(runnableSend).start()
        } else {
            speakFail(text, NullPointerException("GcpVoice or AudioConfig does not setting"))
        }
    }

    var spoken = false

    private fun playAudio(base64EncodedString: String) {
        try {
            if(mMediaPlayer != null)
                prevDuration = mMediaPlayer!!.duration

            stopAudio()

            val url = "data:audio/mp3;base64,$base64EncodedString"
            mMediaPlayer = MediaPlayer()
            mMediaPlayer!!.setDataSource(url)

            try{
                mMediaPlayer!!.prepare()
            }catch(e: Exception){
                Log.d("catch",e.message!!)
                mMediaPlayer!!.prepare()
            }
            mMediaPlayer!!.start()
            Log.d("duration", "origin" + mMediaPlayer!!.duration)
            speakSuccess(mMessage)

           // stopAudio()
        } catch (IoEx: IOException) {
            speakFail(mMessage, IoEx)
        }

    }

    fun stopAudio() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.reset()
            mVoiceLength = -1
        }
    }

    fun recognizeVoice() {
        isDone = false

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-JO")
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "tk.oryx.voice")

        intent.putExtra(
            RecognizerIntent.EXTRA_MAX_RESULTS,
            1
        )  // 1 is the maximum number of results to be returned.

        // Get a handler that can be used to post to the main thread
        val mainHandler = Handler(Looper.getMainLooper())

        Log.d("amal", "hiii")

        val myRunnable = Runnable() {
            Log.d("amal", "hii")
           fun run() {
               Log.d("amal", "hi")
               try {
                   sr?.setRecognitionListener(voicelistener)
                   sr?.startListening(intent)
               } catch (e: Exception) {
                   Log.e("exp", "Exception:$e")
               }
           } // This is your code
            run()
        }
        mainHandler.post(myRunnable);

    }

    fun resumeAudio() {
        if (mMediaPlayer != null && !mMediaPlayer!!.isPlaying && mVoiceLength != -1) {
            mMediaPlayer!!.seekTo(mVoiceLength)
            mMediaPlayer!!.start()
        }
    }

    fun pauseAudio() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            mVoiceLength = mMediaPlayer!!.currentPosition
        }
    }

    fun exit() {
        stopAudio()
        mMediaPlayer = null
    }

    private fun speakSuccess(speakMessage: String?) {

        duration = mMediaPlayer!!.duration
        Log.d("duration", duration.toString())

        Log.d("test", mMessage + isDone)
        if (isDone) {
            Handler(Looper.getMainLooper()).postDelayed({
                recognizeVoice()
            }, mMediaPlayer!!.duration.toLong())
        }

        for (speakListener in mSpeakListeners) {
            speakListener.onSuccess(speakMessage)
        }
    }

    private fun speakFail(speakMessage: String?, e: Exception) {
        for (speakListener in mSpeakListeners) {
            speakListener.onFailure(speakMessage, e)
        }
    }

    fun addSpeakListener(iSpeakListener: ISpeakListener) {
        mSpeakListeners.add(iSpeakListener)
    }

    fun removeSpeakListener(iSpeakListener: ISpeakListener) {
        mSpeakListeners.remove(iSpeakListener)
    }

    fun removeSpeakListener() {
        mSpeakListeners.clear()
    }

    interface ISpeakListener {
        fun onSuccess(message: String?)

        fun onFailure(message: String?, e: Exception)
    }

    companion object {
        private val TAG = "hi"
        var isDone = false
        var duration = 0
        var prevDuration = 0

        var mMediaPlayer: MediaPlayer? = null

        lateinit var voicelistener: RecognitionListener
        lateinit var sr: SpeechRecognizer
    }
}
