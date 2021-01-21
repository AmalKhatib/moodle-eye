package com.example.moodleeye.ui.promo

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.moodleeye.R
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        updateResources(this, "ar")

        Handler().postDelayed(Runnable {
            Intent(this@SplashActivity, WalkthroughtActivity::class.java).also {
                startActivity(it)
                this@SplashActivity.finish()
            }
        }, 2500)
    }

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

