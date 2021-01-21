package com.example.moodleeye.ui.content.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodleeye.R
import com.example.moodleeye.data.network.response.Question
import com.example.moodleeye.data.network.response.Quiz
import com.example.moodleeye.ui.content.quiz.EarlyQuizActivity
import com.example.moodleeye.ui.content.quiz.QuizActivity
import com.example.moodleeye.ui.content.quiz.QuizGradeActivity
import com.example.moodleeye.util.Listener
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS
import com.example.moodleeye.util.toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_course_details.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class CourseDetailsActivity : AppCompatActivity(), KodeinAware, Listener {

    override val kodein by kodein()
    private val factory: CourseViewModelFactory by instance()
    var viewModel: CourseViewModel? = null

    private var sr: SpeechRecognizer? = null

    var courseId : Int? = 0
    var courseName : String? = null

    lateinit var speakMethod: SpeakMethod
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)

        speakMethod = SpeakMethod(this , this)
        speakMethod.initAndroidTTSSetting()

        val listener = this.listener()
        sr = SpeechRecognizer.createSpeechRecognizer(this)
        sr?.setRecognitionListener(listener)

        GCPTTS.sr = sr!!
        GCPTTS.voicelistener = listener

        toolbar.ic_back.setOnClickListener { finish() }

        rv_quizes.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProviders.of(this, factory).get(CourseViewModel::class.java)
        viewModel!!.authListener = this

        if (intent.hasExtra("courseId")){
            courseId = intent.getIntExtra("courseId",0)
            courseName = intent.getStringExtra("courseName")
            tv_course_name.text= courseName
            tv_course_details.text = intent.getStringExtra("courseSummary")
        }

        viewModel!!.courseId = courseId

        viewModel!!.getQuizzes(Integer.parseInt(intent.getStringExtra("courseId")))
       // viewModel!!.getQuizzes(intent.getStringExtra("courseId"))

    }

    override fun onStarted() {}

    var grade = 0
    var quizes = ArrayList<Quiz>()

    override fun onSuccess(items: Any) {
        if(items is String){
            grade = Integer.parseInt(items)
        }else {
            items as ArrayList<Quiz>
            var quizes = items
            if(courseName.equals("حاضر العالم الاسلامي")){
                quizes = ArrayList<Quiz>()
                quizes.add(Quiz(1, "اختبار نصفي", "اختبار لمادة حاضر العالم الاسلامي"
                    , 1593499500000, 1596091500000, 1200000, ArrayList<Question>()))
            }else{
                items.add(Quiz(1, "اختبار نصفي", "اختبار نصفي للعام 2020."
                    , 1593499500000, 1596091500000, 1200000, ArrayList<Question>()))
            }
            if(!isFromQuiz){
                when(quizes.size){
                    0 ->{
                        speakMethod.startSpeak("لا يوجد اختبارات حالية لهذا المَساقْ")
                        Handler().postDelayed({finish()}, 4000)
                    }
                    1 -> {
                        if(intent.getStringExtra("courseName")!!.equals("حاضر العالم الاسلامي")){
                            GCPTTS.isDone = true
                            speakMethod.startSpeak("يتواجد إختِبارٌ بِإسمْ "+ quizes.get(0).quizName + " قم بذكر اسمه للذهاب لصفحة الإختِبارْ")
                        }else {
                            GCPTTS.isDone = true
                            speakMethod.startSpeak("يتواجد إختِبارٌ بِإسمْ "+ " إِختِبارْ قَصِيرْْ "+ " قم بذكر اسمه للذهاب لصفحة الإختِبارْ")
                        }

                    }
                    else -> {
                        speakMethod.startSpeak("الإختبارات المتاحة هي")
                        var quizesLine = ""
                        var index = 1
                        for(quiz in quizes){
                            quizesLine = quizesLine + " \n $index. " + quiz.quizName
                            index++
                        }

                        Handler().postDelayed({
                            speakMethod.startSpeak(quizesLine)
                            Handler().postDelayed({
                                GCPTTS.isDone = true
                                speakMethod.startSpeak("قم بنُطق اسم الإختِبار الذي تود الذهاب إلَيْهْ.")
                            }, GCPTTS.prevDuration.toLong()+1000L)

                        }, 3000L)
                    }
                }
            }

            rv_quizes.adapter = QuizAdapter(this, this, this, quizes, viewModel?.preferences!!)
        }

    }

    var isFromQuiz = false

    override fun onPause() {
        super.onPause()

        sr?.stopListening()
        speakMethod.stopAudio()
    }

    override fun onFailure(message: String) {
        toast(message)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            7 -> {
                isFromQuiz = true
                viewModel!!.getQuizzes(Integer.parseInt(intent.getStringExtra("courseId")))
            }
        }
    }

    internal inner class listener : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            Log.d("voice error", "error $error")
        }

        override fun onResults(results: Bundle) {
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            val str :String? = data!![0]
            for(quiz in quizes){
                if(quiz.quizName.equals(str)){
                    viewModel?.preferences?.saveQuiz(Gson().toJson(quiz))

                    val quizOpenDate = Date(quiz.timeopen)
                    val quizCloseDate = Date(quiz.timeclose)

                    if((Date().after(quizOpenDate) || Date().equals(quizOpenDate)) && Date().before(quizCloseDate)){
                        sr?.stopListening()
                        speakMethod.stopAudio()
                        if(grade == 0){
                            Intent(this@CourseDetailsActivity, QuizActivity::class.java).let {
                                it.putExtra("timeLimit", quiz.timelimit)
                                it.putExtra("quizName", quiz.quizName)
                                it.putExtra("questions", quiz.questions as Serializable)
                                startActivityForResult(it, 7)
                            }

                        }else {
                            Intent(this@CourseDetailsActivity, QuizGradeActivity::class.java).let {
                                it.putExtra("quizName" , quiz.quizName)
                                it.putExtra("gradeOf" , quiz.questions.size)
                                startActivity(it)
                            }
                        }
                    }else if(Date().before(quizOpenDate)){
                        sr?.stopListening()
                        speakMethod.stopAudio()
                        Intent(this@CourseDetailsActivity, EarlyQuizActivity::class.java).let {
                            it.putExtra("open" , quiz.timeopen)
                            it.putExtra("close" , quiz.timeclose)
                            startActivity(it)
                        }
                    }else{
                        sr?.stopListening()
                        speakMethod.stopAudio()
                        Intent(this@CourseDetailsActivity, QuizGradeActivity::class.java).let {
                            it.putExtra("quizName" , quiz.quizName)
                            it.putExtra("gradeOf" , quiz.questions.size)
                            startActivity(it)
                        }
                    }

                }
            }
        }

        override fun onPartialResults(partialResults: Bundle) {
        }

        override fun onEvent(eventType: Int, params: Bundle) {
        }
    }

}
