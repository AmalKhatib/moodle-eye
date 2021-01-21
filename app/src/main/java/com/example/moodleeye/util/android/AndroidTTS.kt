package com.example.moodleeye.util.android

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log

import java.util.ArrayList

/**
 * Author: Changemyminds.
 * Date: 2018/6/25.
 * Description:
 * Reference:
 */
class AndroidTTS(context: Context) : TextToSpeech.OnInitListener {

    private var mTextToSpeech: TextToSpeech? = null
    private var mAndroidVoice: AndroidVoice? = null
    private val mSpeakListeners = ArrayList<ISpeakListener>()
    private var mIsEnable: Boolean = false

    init {
        mIsEnable = false
        mTextToSpeech = TextToSpeech(context, this)
    }

    constructor(context: Context, androidVoice: AndroidVoice) : this(context) {
        mAndroidVoice = androidVoice
    }

    fun setAndroidVoice(androidVoice: AndroidVoice) {
        mAndroidVoice = androidVoice
    }

    fun speak(text: String) {
        if (mTextToSpeech != null && mIsEnable) {
            if (mAndroidVoice != null) {
                if (!isSetAndroidVoiceEnable(mAndroidVoice!!)) {
                    val message = "can't set the value to tts android library"
                    Log.e(TAG, message)
                    speakFailure(message)
                    return
                }
            }

            val isSpeakFail: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isSpeakFail = mTextToSpeech!!.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                ) == TextToSpeech.ERROR
            } else {
                isSpeakFail = mTextToSpeech!!.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null
                ) == TextToSpeech.ERROR
            }

            if (isSpeakFail) {
                speakFailure("TextToSpeech.ERROR")
            } else {
                speakSuccess(text)
            }
        }
    }

    fun stop() {
        if (mTextToSpeech != null) {
            mTextToSpeech!!.stop()
        }
    }

    fun exit() {
        if (mTextToSpeech != null) {
            mTextToSpeech!!.stop()
            mTextToSpeech!!.shutdown()
            mTextToSpeech = null
        }
    }

    fun addSpeakListener(speakListener: ISpeakListener) {
        mSpeakListeners.add(speakListener)
    }

    fun removeSpeakListener(speakListener: ISpeakListener) {
        mSpeakListeners.remove(speakListener)
    }

    fun removeSpeakListener() {
        mSpeakListeners.clear()
    }

    private fun speakSuccess(message: String) {
        for (speakListener in mSpeakListeners) {
            speakListener.onSuccess(message)
        }
    }

    private fun speakFailure(errorMessage: String) {
        for (speakListener in mSpeakListeners) {
            speakListener.onFailure(errorMessage)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            if (mAndroidVoice == null) {
                mAndroidVoice = AndroidVoice.Builder().build()
            }

            mIsEnable = isSetAndroidVoiceEnable(mAndroidVoice!!)
            if (!mIsEnable) {
                Log.e(TAG, "can't get the android tts library.")
            }
        } else {
            Log.e(TAG, "can't get the android tts library.")
            mIsEnable = false
        }
    }

    private fun isSetAndroidVoiceEnable(androidVoice: AndroidVoice): Boolean {
        return if (mTextToSpeech!!.setSpeechRate(androidVoice.speakingRate) == TextToSpeech.ERROR ||
            mTextToSpeech!!.setPitch(androidVoice.pitch) == TextToSpeech.ERROR ||
            mTextToSpeech!!.setLanguage(androidVoice.locale) == TextToSpeech.LANG_MISSING_DATA ||
            mTextToSpeech!!.setLanguage(androidVoice.locale) == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            false
        } else true

    }


    interface ISpeakListener {
        fun onSuccess(message: String)

        fun onFailure(errorMessage: String)
    }

    companion object {
        private val TAG = AndroidTTS::class.java.name
    }

}
