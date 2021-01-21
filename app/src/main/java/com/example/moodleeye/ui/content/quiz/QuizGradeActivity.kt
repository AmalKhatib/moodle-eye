package com.example.moodleeye.ui.content.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.moodleeye.R
import com.example.moodleeye.util.Listener
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.toast
import kotlinx.android.synthetic.main.activity_quiz_grade.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class QuizGradeActivity : AppCompatActivity(), KodeinAware, Listener {

    override fun onStarted() {}

    override fun onSuccess(items: Any) {
        if(items is String) {
            tv_grade.text = items.toString() +"/"+  5
            tv_quiz_grade_info.append(items.toString() + " من "+ 5)
            val speakMethod = SpeakMethod(this@QuizGradeActivity , this@QuizGradeActivity)
            speakMethod.initAndroidTTSSetting()
            speakMethod.startSpeak(tv_quiz_grade_info.text.toString())
        }
    }

    override fun onFailure(message: String) {}

    override val kodein by kodein()
    private val factory: QuizViewModelFactory by instance()
    var viewModel: QuizViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_grade)

        viewModel = ViewModelProviders.of(this, factory).get(QuizViewModel::class.java)
        viewModel!!.authListener = this
        viewModel?.getGrade()

        toolbar.tv_title.text = "النتيجة"

        toolbar.ic_back.setOnClickListener { finish() }

        tv_quiz_name.text = intent.getStringExtra("quizName")
    }
}