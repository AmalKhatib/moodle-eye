package com.example.moodleeye.util.gcp

import org.json.JSONException
import org.json.JSONObject

/**
 * Author: Changemyminds.
 * Date: 2018/6/23.
 * Description:
 * Reference:
 */
class Input : VoiceParameter {
    private var mText: String? = null
    private var mIsEnableSSML: Boolean = false

    override val jsonHeader: String
        get() = "input"

    internal constructor(text: String) {
        mText = text
        mIsEnableSSML = false
    }

    constructor(text: String, isSSML: Boolean) {
        mText = text
        mIsEnableSSML = isSSML
    }

    override fun toJSONObject(): JSONObject {
        val jsonText = JSONObject()
        try {
            jsonText.put(if (mIsEnableSSML) "ssml" else "text", mText)
            return jsonText
        } catch (e: JSONException) {
            //            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return jsonText
    }

    @Deprecated("")
    override fun toString(): String {
        var text = "'" + this.javaClass.simpleName.toLowerCase() + "':{"
        text += if (mIsEnableSSML) "'ssml':'$mText'}" else "'text':'$mText'}"
        return text
    }
}
