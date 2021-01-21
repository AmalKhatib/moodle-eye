package com.example.moodleeye.ui.promo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.moodleeye.R
import com.example.moodleeye.ui.auth.LoginActivity
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS
import kotlinx.android.synthetic.main.activity_walkthrought.*

class WalkthroughtActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkthrought)

        getWindow(). addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val speakMethod = SpeakMethod(this@WalkthroughtActivity , this@WalkthroughtActivity)
        speakMethod.initAndroidTTSSetting()
        speakMethod.startSpeak("تطبيق Moodle للطلاب المكفوفين لتقديم الاختبارات الإلِكترونيَّهْ.")

        Handler().postDelayed({btn_next.callOnClick()}, 8000)

        btn_next.setOnClickListener {
            speakMethod.stopAudio()
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}
