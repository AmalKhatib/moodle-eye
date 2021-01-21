package com.example.moodleeye.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration

import java.util.Locale

//object for singlton
object Local{

    fun updateResources(activity: Activity, language: String): Boolean {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = activity.resources

        val configuration = Configuration()
        configuration.locale = locale

        activity.baseContext.resources.updateConfiguration(
            configuration,
            activity.baseContext.resources.displayMetrics
        )
        //resources.updateConfiguration(configuration, resources.displayMetrics)
        return true
    }

}
