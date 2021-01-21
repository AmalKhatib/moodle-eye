package com.example.moodleeye.util.gcp

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

/**
 * Author: Changemyminds.
 * Date: 2018/6/24.
 * Description:
 * Reference:
 */
class VoiceMessage private constructor() {
    private val mInput: Input? = null
    private val mGCPVoice: GCPVoice? = null
    private val mAudioConfig: AudioConfig? = null

    private val mVoiceParameters: MutableList<VoiceParameter>

    init {
        mVoiceParameters = ArrayList()
    }

    class Builder {
        private val mVoiceMessage: VoiceMessage

        init {
            mVoiceMessage = VoiceMessage()
        }

        fun addParameter(voiceParameter: VoiceParameter): Builder {
            mVoiceMessage.mVoiceParameters.add(voiceParameter)
            return this
        }

        @Deprecated("")
        fun add(input: Input): Builder {
            mVoiceMessage.mVoiceParameters.add(input)
            return this
        }

        @Deprecated("")
        fun add(GCPVoice: GCPVoice): Builder {
            mVoiceMessage.mVoiceParameters.add(GCPVoice)
            return this
        }

        @Deprecated("")
        fun add(audioConfig: AudioConfig): Builder {
            mVoiceMessage.mVoiceParameters.add(audioConfig)
            return this
        }

        fun build(): VoiceMessage {
            return mVoiceMessage
        }
    }

    override fun toString(): String {
        if (mVoiceParameters.size != 0) {
            val jsonObject = JSONObject()
            try {
                for (v in mVoiceParameters) {
                    jsonObject.put(v.jsonHeader, v.toJSONObject())
                }
                return jsonObject.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        return ""
    }
}
