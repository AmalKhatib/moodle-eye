package com.example.moodleeye.util.gcp

import org.json.JSONObject

/**
 * Author: Changemyminds.
 * Date: 2018/12/27.
 * Description:
 * Reference:
 */
interface VoiceParameter {
    val jsonHeader: String
    fun toJSONObject(): JSONObject
}
