package com.example.moodleeye.util.gcp

import org.json.JSONException
import org.json.JSONObject

/**
 * Author: Changemyminds.
 * Date: 2018/6/23.
 * Description:
 * Reference:
 */
class GCPVoice : VoiceParameter {
    var languageCode: String? = null
        private set
    var name: String? = null
        private set
    var essmLlGender: ESSMLlVoiceGender? = null
        private set
    var naturalSampleRateHertz: Int = 0
        private set

    override val jsonHeader: String
        get() = "voice"

    constructor(languageCode: String, name: String) {
        this.languageCode = languageCode
        this.name = name
        essmLlGender = ESSMLlVoiceGender.NONE
        naturalSampleRateHertz = 0
    }

    constructor(languageCode: String, name: String, eSSMLlGender: ESSMLlVoiceGender) {
        this.languageCode = languageCode
        this.name = name
        essmLlGender = eSSMLlGender
        naturalSampleRateHertz = 0
    }

    constructor(
        languageCode: String, name: String, eSSMLlGender: ESSMLlVoiceGender,
        naturalSampleRateHertz: Int
    ) {
        this.languageCode = languageCode
        this.name = name
        essmLlGender = eSSMLlGender
        this.naturalSampleRateHertz = naturalSampleRateHertz
    }

    override fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("languageCode", languageCode)
            jsonObject.put("name", name)
            if (essmLlGender !== ESSMLlVoiceGender.NONE) {
                jsonObject.put("name", essmLlGender!!.toString())
            }
            if (naturalSampleRateHertz != 0) {
                jsonObject.put("naturalSampleRateHertz", naturalSampleRateHertz.toString())
            }
            return jsonObject
        } catch (e: JSONException) {
            //            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return jsonObject
    }


    @Deprecated("")
    override fun toString(): String {
        var text = "'voice':{"
        text += "'languageCode':'$languageCode',"
        text += "'name':'$name'"
        text += if (essmLlGender === ESSMLlVoiceGender.NONE)
            ""
        else
            ",'ssmlGender':'" + essmLlGender!!.toString() + "'"
        text += if (naturalSampleRateHertz == 0)
            ""
        else
            ",'naturalSampleRateHertz':'$naturalSampleRateHertz'"
        text += "}"
        return text
    }
}
