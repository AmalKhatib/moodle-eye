package com.example.moodleeye.util.gcp

/**
 * Author: Changemyminds.
 * Date: 2018/6/24.
 * Description:
 * Reference:
 */
enum class ESSMLlVoiceGender {
    SSML_VOICE_GENDER_UNSPECIFIED,
    MALE,
    FEMALE,
    NEUTRAL,
    NONE;


    companion object {

        fun convert(ssmlGender: String): ESSMLlVoiceGender {
            return if (ssmlGender.compareTo(SSML_VOICE_GENDER_UNSPECIFIED.toString()) == 0) {
                SSML_VOICE_GENDER_UNSPECIFIED
            } else if (ssmlGender.compareTo(MALE.toString()) == 0) {
                MALE
            } else if (ssmlGender.compareTo(FEMALE.toString()) == 0) {
                FEMALE
            } else if (ssmlGender.compareTo(NEUTRAL.toString()) == 0) {
                NEUTRAL
            } else {
                NONE
            }
        }
    }
}
