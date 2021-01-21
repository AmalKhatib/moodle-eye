package com.example.moodleeye.ui.content.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.moodleeye.R
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS
import com.example.moodleeye.util.toast
import kotlinx.android.synthetic.main.activity_early_quiz.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class EarlyQuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_early_quiz)

        ic_back.setOnClickListener { finish() }

        val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        tv_quiz_open.text = df.format(Date(intent.getLongExtra("open", 0)))
        tv_quiz_close.text = df.format(Date(intent.getLongExtra("close", 0)))

        val speakMethod = SpeakMethod(this , this)
        speakMethod.initAndroidTTSSetting()
        speakMethod.startSpeak("هذا الإختبار غير متاح بَعْدْ.")
        Handler().postDelayed({
            speakMethod.startSpeak(tv_early_nate_start.text.toString() + " " + tv_quiz_open.text.toString() +
                    " "+tv_early_nate_end.text.toString() + " " + tv_quiz_close.text.toString())
        }, GCPTTS.prevDuration.toLong() - 1000L)
    }
}
