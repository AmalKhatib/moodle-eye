package com.example.moodleeye.util.gcp

/**
 * Author: Changemyminds.
 * Date: 2018/6/24.
 * Description:
 * Reference:
 */
enum class EAudioEncoding private constructor(internal var mDescription: String) {
    AUDIO_ENCODING_UNSPECIFIED("AUDIO_ENCODING_UNSPECIFIED"),
    LINEAR16("LINEAR16"),
    MP3("MP3"),
    OGG_OPUS("OGG_OPUS");

    override fun toString(): String {
        return mDescription
    }
}
