package com.example.moodleeye.util.gcp

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

/**
 * Author: Changemyminds.
 * Date: 2018/6/22.
 * Description:
 * Reference:
 */
class AudioConfig private constructor() : VoiceParameter {
    private var mEAudioEncoding: EAudioEncoding? = null
    private var mSpeakingRate: Float = 0.toFloat()            // range: 0.25 ~ 4.00
    private var mPitch: Float = 0.toFloat()                   // range: -20.00 ~ 20.00
    private var mVolumeGainDb: Int = 0
    private var mSampleRateHertz: Int = 0

    override val jsonHeader: String
        get() = "audioConfig"

    private val pitch: String
        get() {
            val pitchList = ArrayList<String>()
            pitchList.add(mPitch.toString())
            if (mVolumeGainDb != 0) {
                pitchList.add(mVolumeGainDb.toString())
            }
            if (mSampleRateHertz != 0) {
                pitchList.add(mSampleRateHertz.toString())
            }
            return pitchList.toString().replace("[", "").replace("]", "")
        }

    init {
        mEAudioEncoding = EAudioEncoding.LINEAR16
        mSpeakingRate = 1.0f
        mPitch = 0.0f
        mVolumeGainDb = 0
        mSampleRateHertz = 0
    }

    class Builder {
        private val mAudioConfig: AudioConfig

        init {
            mAudioConfig = AudioConfig()
        }

        fun addAudioEncoding(EAudioEncoding: EAudioEncoding): Builder {
            mAudioConfig.mEAudioEncoding = EAudioEncoding
            return this
        }

        fun addSpeakingRate(speakingRate: Float): Builder {
            mAudioConfig.mSpeakingRate = speakingRate
            return this
        }

        fun addPitch(pitch: Float): Builder {
            mAudioConfig.mPitch = pitch
            return this
        }

        fun addVolumeGainDb(volumeGainDb: Int): Builder {
            mAudioConfig.mVolumeGainDb = volumeGainDb
            return this
        }

        fun addSampleRateHertz(sampleRateHertz: Int): Builder {
            mAudioConfig.mSampleRateHertz = sampleRateHertz
            return this
        }

        fun build(): AudioConfig {
            return mAudioConfig
        }
    }

    override fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("audioEncoding", mEAudioEncoding!!.toString())
            jsonObject.put("speakingRate", mSpeakingRate.toString())
            jsonObject.put("pitch", pitch)
            return jsonObject
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObject
    }

    @Deprecated("")
    override fun toString(): String {
        var text = "'audioConfig':{"
        text += "'audioEncoding':'" + mEAudioEncoding!!.toString() + "',"
        text += "'speakingRate':'$mSpeakingRate',"
        text += "'pitch':'$mPitch'"
        text += if (mVolumeGainDb == 0) "" else ",'$mVolumeGainDb'"
        text += if (mSampleRateHertz == 0) "" else ",'$mSampleRateHertz'"
        text += "}"
        return text
    }
}
