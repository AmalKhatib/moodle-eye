package com.example.moodleeye.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

private const val KEY_USER = "user"
private const val KEY_QUIZ = "quiz"

class Preferences(val context: Context) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)


    fun saveUser(user : String){
        preference.edit().putString(KEY_USER, user).apply()
    }

    fun getUser() : String{
        preference.getString(KEY_USER, "")?.let {
            return it
        }
        return ""
    }

    fun saveQuiz(quiz: String){
        preference.edit().putString(KEY_QUIZ, quiz).apply()
    }

    fun getQuiz(): String{
        preference.getString(KEY_QUIZ, "")?.let {
            return it
        }
        return ""
    }
}