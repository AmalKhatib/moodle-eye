package com.example.moodleeye.ui.content.quiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodleeye.R
import com.example.moodleeye.data.models.AnswerSheet
import com.example.moodleeye.data.network.response.Answer
import com.example.moodleeye.util.Listener
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS
import com.example.moodleeye.util.toast
import kotlinx.android.synthetic.main.activity_quiz_submission.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.ic_back
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class QuizSubmissionActivity : AppCompatActivity() , KodeinAware, Listener {

    private var sr: SpeechRecognizer? = null
    lateinit var speakMethod: SpeakMethod

    lateinit var quizSubmissionAdapter: QuizSubmissionAdapter

    override fun onStarted() {}

    override fun onSuccess(items: Any) {
        if(items is Boolean) {
            Intent().also {
                sr?.stopListening()
                speakMethod.stopAudio()
                setResult(Activity.RESULT_OK, it)
                finish()
            }
        }
    }

    override fun onFailure(message: String) {}

    override val kodein by kodein()
    private val factory: QuizViewModelFactory by instance()
    var viewModel: QuizViewModel? = null

    lateinit var answers: HashMap<Int, AnswerSheet>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_submission)

        speakMethod = SpeakMethod(this , this)
        speakMethod.initAndroidTTSSetting()

        val listener = this.listener()
        sr = SpeechRecognizer.createSpeechRecognizer(this)
        sr?.setRecognitionListener(listener)

        GCPTTS.sr = sr!!
        GCPTTS.voicelistener = listener

        viewModel = ViewModelProviders.of(this, factory).get(QuizViewModel::class.java)
        viewModel!!.authListener = this

        answers = intent.getSerializableExtra("answers") as HashMap<Int, AnswerSheet>

        ic_back.setOnClickListener { finish() }

        rv_questions_check.layoutManager = LinearLayoutManager(this)
        quizSubmissionAdapter = QuizSubmissionAdapter(answers, this)
        rv_questions_check.adapter = quizSubmissionAdapter

        tv_quiz_name.text = intent.getStringExtra("quizName")

        btn_submit.setOnClickListener {
            QuizActivity.isSubmitted = true
            var grade = 0
            for(answer in answers){
                if(answer.value.feedback.equals("t"))
                    grade++
            }
            viewModel?.postGrade(grade)
        }

        checkUnAnsweredQs()
    }

    private fun checkUnAnsweredQs(){
        var qsLine = ""
        if(quizSubmissionAdapter.getUnAnsweredQs().size != 0) {
            var index = 0
            for (answer in QuizSubmissionAdapter.unAnsweredQs) {
                index++
                if(index > 1)
                    qsLine = qsLine + "و " + answer.value.questionName + "\n"
                else
                    qsLine = qsLine + answer.value.questionName + "\n"
            }

            GCPTTS.isDone = true
            if(index != 1)
                speakMethod.startSpeak("لم تتم الإجابة على كل من $qsLine إن كنت ترغب بالإجابة قُم بِنُطق 'رُجوعْ' أما إن رَغِبْتَ بالتسليم قم بِنُطق تَسليمْ . ")
            else
                speakMethod.startSpeak("لم تتم الإجابة على $qsLine  إن كنت ترغب بالإجابة قُم بِنُطق 'رُجوعْ' أما إن رَغِبْتَ بالتسليم قم بِنُطق تَسليمْ. ")

        }else{
            GCPTTS.isDone = true
            speakMethod.startSpeak("قُم بِنُطق تسليم لتأكيد الإنهاء")
        }
    }

    private fun calculateGrade(): Int{
        var grade = 0

        for(answer in answers){
            if(answer.value.feedback.equals("t"))
                grade++
        }
        viewModel?.postGrade(grade)
        return grade
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
            when(str){
                "تسليم" -> {
                    QuizActivity.isSubmitted = true
                    var grade = 0

                    for(answer in answers){
                        if(answer.value.feedback.equals("t"))
                            grade++
                    }
                    viewModel?.postGrade(grade)
                }
                "رجوع" -> {
                    //here must be returning to the unanswered qs
                    Intent().also {
                        it.putExtra("isFromUnAnswered", true)
                        sr?.stopListening()
                        speakMethod.stopAudio()
                        setResult(Activity.RESULT_OK, it)
                        finish()
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
