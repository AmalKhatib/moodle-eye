package com.example.moodleeye.util.android

import java.util.Locale

/**
 * Author: Changemyminds.
 * Date: 2018/6/24.
 * Description:
 * Reference:
 */
class AndroidVoice  {
    var locale: Locale? = null
        private set
    var speakingRate: Float = 0.toFloat()
        private set            // range: 0.0 ~ 2.0
    var pitch: Float = 0.toFloat()
        private set                   // range: 1.0

    init {
        locale = Locale.ENGLISH
        speakingRate = 1.0f
        pitch = 1.0f
    }

    private fun translate(language: String): Locale {
        var language = language
        language = language.toLowerCase()
        var locale = Locale.ENGLISH
        when (language) {
            "eng" -> locale = Locale.ENGLISH
            "cht" -> locale = Locale.TAIWAN
            "chi" -> locale = Locale.CHINA
        }

        return locale
    }

    class Builder {
        private val mAndroidVoice: AndroidVoice

        init {
            mAndroidVoice = AndroidVoice()
        }

        fun addPitch(pitch: Float): Builder {
            mAndroidVoice.pitch = pitch
            return this
        }

        fun addSpeakingRate(speakingRate: Float): Builder {
            mAndroidVoice.speakingRate = speakingRate
            return this
        }

        fun addLanguage(locale: Locale): Builder {
            mAndroidVoice.locale = locale
            return this
        }

        fun addLanguage(language: String): Builder {
            mAndroidVoice.locale = mAndroidVoice.translate(language)
            return this
        }

        fun build(): AndroidVoice {
            return mAndroidVoice
        }
    }
}
