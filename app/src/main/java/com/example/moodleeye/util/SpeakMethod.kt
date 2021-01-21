package com.example.moodleeye.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import com.example.moodleeye.util.android.AndroidTTS
import com.example.moodleeye.util.android.AndroidVoice
import com.example.moodleeye.util.gcp.*


import com.google.gson.JsonParser

import java.util.Locale


/**
 * Author: Changemyminds.
 * Date: 2019/6/23.
 * Description:
 * Reference:
 */
class SpeakMethod(val context: Context , val view: Activity) {
    private var TOUCH_TIME: Long = 0

    val androidTTS : AndroidTTS? = null

    fun exitApp() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            // exit app
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        } else {
            TOUCH_TIME = System.currentTimeMillis()
        }
    }

    fun stopAudio(){
        gcptts?.stopAudio()
    }

    fun onTextToSpeechResult(context: Context, requestCode: Int, resultCode: Int) {
        if (requestCode == TEXT_TO_SPEECH_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                val androidVoice = AndroidVoice.Builder()
                    .addLanguage(Locale.ENGLISH)
                    .addPitch(1.0f)
                    .addSpeakingRate(1.0f)
                    .build()

                androidTTS?.setAndroidVoice(androidVoice)

            }
        }
    }
    var gcptts : GCPTTS? =null

    private fun initGCPTTSVoice() {

        val languageCode = "ar-XA"
        val name = "ar-XA-Wavenet-B"
        val pitch = 5f
        val speakRate = 1f

        val gcpVoice = GCPVoice(languageCode, name)
        val audioConfig = AudioConfig.Builder()
            .addAudioEncoding(EAudioEncoding.MP3)
            .addSpeakingRate(speakRate)
            .addPitch(pitch)
            .build()

         gcptts = GCPTTS(gcpVoice, audioConfig)
    }

    fun initAndroidTTSSetting() {
        val checkIntent = Intent()
        checkIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        view.startActivityForResult(checkIntent, TEXT_TO_SPEECH_CODE)
    }


    fun onResponse(text: String) {
        val jsonElement = JsonParser().parse(text)
        if (jsonElement == null || jsonElement.asJsonObject == null ||
            jsonElement.asJsonObject.get("voices").asJsonArray == null
        ) {
            Log.e(TAG, "get error json")
            return
        }

        val jsonObject = jsonElement.asJsonObject
        val jsonArray = jsonObject.get("voices").asJsonArray

        for (i in 0 until jsonArray.size()) {
            val jsonArrayLanguage = jsonArray.get(i)
                .asJsonObject.get("languageCodes")
                .asJsonArray

            if (jsonArrayLanguage.get(0) != null) {
                val language = jsonArrayLanguage.get(0).toString().replace("\"", "")
                val name = jsonArray.get(i).asJsonObject.get("name").toString().replace("\"", "")
                val ssmlGender =
                    jsonArray.get(i).asJsonObject.get("ssmlGender").toString().replace("\"", "")
                val essmLlVoiceGender = ESSMLlVoiceGender.convert(ssmlGender)
                val naturalSampleRateHertz =
                    jsonArray.get(i).asJsonObject.get("naturalSampleRateHertz").asInt

                val gcpVoice = GCPVoice(language, name, essmLlVoiceGender, naturalSampleRateHertz)
                val androidVoice = AndroidVoice()

                val androidTTs = AndroidTTS(context, androidVoice)
                androidTTs.speak(text)
            }
        }


    }

    fun onFailure(error: String) {

        Log.e(TAG, "Loading Voice List Error, error code : $error")
    }

    fun startSpeak(text: String) {
        initGCPTTSVoice()
        gcptts?.start(text)
    }

    companion object {
        private val TAG = "hi"
        private val TEXT_TO_SPEECH_CODE = 0x100

        private val WAIT_TIME = 2000L
    }
}



