package com.example.moodleeye.ui.content.quiz

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.moodleeye.data.network.response.Question
import com.example.moodleeye.util.toast
import kotlinx.android.synthetic.main.activity_quiz.*
import android.os.CountDownTimer
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.moodleeye.R
import com.example.moodleeye.util.Listener
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS
import kotlinx.android.synthetic.main.dialog_quiz_instructions.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit

class QuizActivity : AppCompatActivity() , KodeinAware, Listener {

    companion object{
        var isSubmitted = false
    }

    override fun onStarted() {}

    override fun onSuccess(items: Any) {
        if(items is Boolean) {
            finish()
        }
    }

    override fun onFailure(message: String) {}

    override val kodein by kodein()
    private var unAnsweredQs = ArrayList<Int>()
    private var isFromUnAnswered = false
    private var unAnsweredQIndex = 0
    private val factory: QuizViewModelFactory by instance()
    var viewModel: QuizViewModel? = null

    lateinit var speakMethod : SpeakMethod
    private var sr: SpeechRecognizer? = null

    var questions :List<Question>? = null
    lateinit var adapter: QuestionsAdapter

    lateinit var quizInstructionsDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        getWindow(). addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewModel = ViewModelProviders.of(this, factory).get(QuizViewModel::class.java)
        viewModel!!.authListener = this

        speakMethod = SpeakMethod(this,this)
        val voicelistener = listener()
        sr = SpeechRecognizer.createSpeechRecognizer(this)
        sr?.setRecognitionListener(voicelistener)

        GCPTTS.sr = sr!!
        GCPTTS.voicelistener = voicelistener

        val minutes = TimeUnit.MILLISECONDS.toMinutes(intent.getLongExtra("timeLimit", 0))

        quizInstructionsDialog = Dialog(this)
        quizInstructionsDialog.setContentView(R.layout.dialog_quiz_instructions)

        quizInstructionsDialog.tv_instructions.setText("مدة هذا الإختِبار " + minutes + " دَقِيقَهْ." +
        "\nقُم بإجابة أسئلة الإختيار من مُتَعَدِّدْ بِوَسِم الخيارات وهي (ألِف، باءْ، جيمْ)." +
        "\nأما بالنسبة لأسئلة الصح والخطأ فتتم الإجابة بِ(صحْ أو خطأ)")

        quizInstructionsDialog.btn_cont.setOnClickListener{
            sr?.stopListening()
            speakMethod.stopAudio()
            quizInstructionsDialog.dismiss()
            startQuizUtils()
        }

        quizInstructionsDialog.btn_cancel.setOnClickListener {
            finish()
            quizInstructionsDialog.dismiss()
        }
        if(!isSubmitted)
            quizInstructionsDialog.show()
        else{
            Intent().also {
                isSubmitted = false
                setResult(Activity.RESULT_OK, it)
                this@QuizActivity.finish()
            }
            //this@QuizActivity.finish()
        }
        speakMethod.startSpeak(quizInstructionsDialog.tv_instructions.text.toString() )

        Handler().postDelayed({
            GCPTTS.isDone = true
            speakMethod.startSpeak("قُم بنُطق مُتابَعَهْ لِبَدْء الإختِبار، أو تراجع للرجوع للصفحة الرئيسية.") },
            GCPTTS.duration.toLong() + 12000L)

    }

    private fun startQuizUtils(){
        val limit = intent.getLongExtra("timeLimit", 0)

        object : CountDownTimer(limit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_timer.setText(String.format("%d : %d",
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished))
                )
                if(limit/2 == millisUntilFinished)
                    toast("تم مرور نصف الوقت، متبقى ديقتان")
            }

            override fun onFinish() {
                calculateGrade()
            }
        }.start()

        if (intent.hasExtra("questions")){
            questions = intent.getSerializableExtra("questions") as List<Question>
        }

        var qs = ArrayList<Question>()
        for(index in 0..4){
            qs.add(questions!![index])
        }

        adapter = QuestionsAdapter(this , qs, this)
        view_pager_questions.adapter = adapter
        indicator_quiz.setViewPager(view_pager_questions)

        GCPTTS.sr = adapter.sr!!
        GCPTTS.voicelistener = adapter.listener()

        if(questions!![0].qtype.equals("true false")){
            adapter.speakMethod.startSpeak(questions!![0].questiontext!!)
            GCPTTS.prevDuration = GCPTTS.mMediaPlayer?.duration!! - 2500
            Handler().postDelayed({
                GCPTTS.isDone = true
                adapter.speakMethod.startSpeak("صحْ أَم خطأ")
            }, GCPTTS.prevDuration.toLong())

        }else{
            adapter.speakMethod.startSpeak(questions!![0].questiontext!!)
            GCPTTS.prevDuration = GCPTTS.mMediaPlayer?.duration!! - 3500
            Handler().postDelayed({
                GCPTTS.isDone = true
                adapter.speakMethod.startSpeak(questions!![0].answers[0].answer+ " " + questions!![0].answers[1].answer+ " " + questions!![0].answers[2].answer)
            },  GCPTTS.prevDuration.toLong() + 1000)
        }

        view_pager_questions.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

            override fun onPageSelected(position: Int) {
                var du = 0L
                if(questions!![position].qtype.equals("true false")){

                    adapter.speakMethod.startSpeak(questions!![position].questiontext!!)
                    if(position != 7 || position != 9|| position != 2)
                        du = 3500
                    if(position == 4)
                        du = 4100
                    else if(position == 5)
                        du = 5300

                    else if(position == 9)
                        du = 5500
                    else
                        du = 4500
                    Handler().postDelayed({
                        GCPTTS.isDone = true
                        adapter.speakMethod.startSpeak("صحْ أَم خطأ")
                    }, du)
                }else {
                    adapter.speakMethod.startSpeak(questions!![position].questiontext!!)
                    if(position == 2 || position == 6)
                        du = 3500
                    else if(position == 1)
                        du = 4200
                    else if(position == 3)
                        du = 3200
                    else
                        du = 4500
                    Handler().postDelayed({
                        GCPTTS.isDone = true
                        adapter.speakMethod.startSpeak(questions!![position].answers[0].answer+ " " + questions!![position].answers[1].answer+ " " + questions!![position].answers[2].answer)
                    }, du)


                }
            }

        })

        btn_next.setOnClickListener {
            adapter.sr?.stopListening()
            if(view_pager_questions.currentItem != 4)
                if(!isFromUnAnswered)
                    view_pager_questions.setCurrentItem(++view_pager_questions.currentItem)
                else{
                    QuestionsAdapter.qindex = unAnsweredQs.get(++unAnsweredQIndex) - 1
                    view_pager_questions.setCurrentItem(unAnsweredQs.get(unAnsweredQIndex))
                }
            else{
                // we should send the arraylist with question with their answers
                // and(we must have a model of question with answers and if it is correct or not
                sr?.stopListening()
                speakMethod.stopAudio()
                Intent(this@QuizActivity, QuizSubmissionActivity::class.java).also{
                    it.putExtra("quizId", intent.getIntExtra("quizId", 0))
                    it.putExtra("answers", adapter.answeredQs)
                    it.putExtra("quizName", intent?.getStringExtra("quizName"))
                    startActivityForResult(it, 3)
                }
            }
        }

        btn_prev.setOnClickListener {
            if(view_pager_questions.currentItem != 0)
                view_pager_questions.setCurrentItem(--view_pager_questions.currentItem)
        }
    }

    var isFromRevision = false
    override fun onResume() {
        super.onResume()
        if(!isFromRevision) {
            if (isFromUnAnswered){
                GCPTTS.voicelistener = adapter.listener()
                GCPTTS.sr = SpeechRecognizer.createSpeechRecognizer(this)
                GCPTTS.sr?.setRecognitionListener(adapter.listener())

                view_pager_questions.currentItem = unAnsweredQs.get(unAnsweredQIndex)
                isFromRevision = true

                if(questions!![unAnsweredQs.get(0)].qtype.equals("true false")){
                    adapter.speakMethod.startSpeak(questions!![unAnsweredQs.get(0)].questiontext!!)

                    Handler().postDelayed({
                        GCPTTS.isDone = true
                        adapter.speakMethod.startSpeak("صحْ أَم خطأ")
                    }, 4100)

                }else{
                    adapter.speakMethod.startSpeak(questions!![unAnsweredQs.get(0)].questiontext!!)
                    Handler().postDelayed({
                        GCPTTS.isDone = true
                        adapter.speakMethod.startSpeak(questions!![unAnsweredQs.get(0)].answers[0].answer+ " " + questions!![unAnsweredQs.get(0)].answers[1].answer+ " " + questions!![unAnsweredQs.get(0)].answers[2].answer)
                    }, 2900)
                }
            }
        }
    }

    //when uiz is submitted
    var isFinished = false

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            3 -> {
                if(data?.hasExtra("isFromUnAnswered")!!){
                    isFromUnAnswered = true
                    for(question in adapter.answeredQs){
                        if(question.value.answer.equals(""))
                            unAnsweredQs.add(question.key)
                    }
                    unAnsweredQIndex = 0
                    QuestionsAdapter.qindex = unAnsweredQs.get(0) - 1
                }else {
                    finish()
                    isFinished = true
                    speakMethod.startSpeak("تم التسليم وإنهاء الاختبار.")
                    Intent().also {
                        setResult(Activity.RESULT_OK, it)
                        this@QuizActivity.finish()
                    }
                    this@QuizActivity.finish()
                }
            }
        }
    }

    private fun calculateGrade(): Int{
        var grade = 0

        for(answer in adapter.answeredQs){
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
                "متابعه" -> {
                    quizInstructionsDialog.dismiss()
                    startQuizUtils()
                }
                "تراجع" -> {
                    quizInstructionsDialog.dismiss()
                    finish()
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle) {
        }

        override fun onEvent(eventType: Int, params: Bundle) {
        }
    }
}
