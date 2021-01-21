package com.example.moodleeye.ui.content.quiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.moodleeye.R
import com.example.moodleeye.data.models.AnswerSheet
import com.example.moodleeye.data.network.response.Question
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS

class QuestionsAdapter(private val activity: Activity, private val questions: List<Question>, private val context: Context) :
    PagerAdapter() {

    private var qMultipleChoices = HashMap<Int, ArrayList<Button>>()
    private var qTFChoices = HashMap<Int, ArrayList<ImageButton>>()

    var sr: SpeechRecognizer? = null
    var voicelistener: listener? = null
    var isInitiated = false

    val answeredQs = HashMap<Int, AnswerSheet>()

    private var layoutInflater: LayoutInflater? = null

    var speakMethod : SpeakMethod
    init {
        speakMethod = SpeakMethod(context, activity)
        speakMethod.initAndroidTTSSetting()

        voicelistener = listener()
        sr = SpeechRecognizer.createSpeechRecognizer(context)
        sr?.setRecognitionListener(voicelistener)

        isInitiated = true
    }

    var index = 0
    var isAdded = false
    override fun getCount(): Int {
        if(!isAdded){
            isAdded = true
            for(q in questions){
                answeredQs.put(index++, AnswerSheet(q.name!!, "", ""))
            }
        }

        return 5
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if(!isInitiated){
            voicelistener = listener()
            sr = SpeechRecognizer.createSpeechRecognizer(context)
            sr?.setRecognitionListener(voicelistener)

            isInitiated = true
        }

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View

        if (questions[position].qtype.equals("true false")) {
            view = layoutInflater!!.inflate(R.layout.custome_true_false, null)
            var questionText = view.findViewById(R.id.tv_question) as TextView
            questionText.text = questions[position].questiontext

            fillTrueFalseQuestions(view, position)
        } else {
            view = layoutInflater!!.inflate(R.layout.custom_multiple_choice, null)
            var questionText = view.findViewById(R.id.tv_question) as TextView
            questionText.text = questions[position].questiontext

            fillMultipleQuestions(view, position)
        }

        val vp = container as ViewPager
        vp.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun btnChoiceClicked(view: Button, position: Int, answerIndex: Int) {
        view.background = context.getDrawable(R.drawable.bg_border)
        view.setTextColor(context.resources.getColor(R.color.colorAccent))

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun btnChoiceUnClicked(view: Button) {
        view.background = context.getDrawable(R.drawable.bg_rounded)
        view.setTextColor(context.resources.getColor(R.color.choice))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun fillMultipleQuestions(view: View, position: Int) {
        val btn_firstChoice = view.findViewById<Button>(R.id.btn_first)
        val btn_secondChoice = view.findViewById<Button>(R.id.btn_second)
        val btn_ThirdChoice = view.findViewById<Button>(R.id.btn_third)

        btn_firstChoice.text = questions[position].answers[0].answer
        btn_secondChoice.text = questions[position].answers[1].answer
        btn_ThirdChoice.text = questions[position].answers[2].answer

        btn_firstChoice.setOnClickListener {
            btnChoiceClicked(btn_firstChoice, position, 0)

            answeredQs.put(position, AnswerSheet(questions[position].name!!,
                questions[position].answers[0].answer!!,
                questions[position].answers[0].feedback!!))

            btnChoiceUnClicked(btn_secondChoice)
            btnChoiceUnClicked(btn_ThirdChoice)
        }

        btn_secondChoice.setOnClickListener {
            btnChoiceClicked(btn_secondChoice, position, 1)

            answeredQs.put(position, AnswerSheet(questions[position].name!!,
                questions[position].answers[0].answer!!,
                questions[position].answers[0].feedback!!))

            btnChoiceUnClicked(btn_ThirdChoice)
            btnChoiceUnClicked(btn_firstChoice)
        }

        btn_ThirdChoice.setOnClickListener {
            btnChoiceClicked(btn_ThirdChoice, position, 2)

            answeredQs.put(position, AnswerSheet(questions[position].name!!,
                questions[position].answers[0].answer!!,
                questions[position].answers[0].feedback!!))

            btnChoiceUnClicked(btn_secondChoice)
            btnChoiceUnClicked(btn_firstChoice)
        }

        val multipleChoices = ArrayList<Button>()
        multipleChoices.add(btn_firstChoice)
        multipleChoices.add(btn_secondChoice)
        multipleChoices.add(btn_ThirdChoice)

        qMultipleChoices.put(position, multipleChoices)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun fillTrueFalseQuestions(view: View, position: Int) {
        val btn_true = view.findViewById<ImageButton>(R.id.btn_true)
        val btn_false = view.findViewById<ImageButton>(R.id.btn_false)

        btn_true.setOnClickListener {
            answeredQs.put(position, AnswerSheet(questions[position].name!!,
                questions[position].answers[0].answer!!,
                questions[position].answers[0].feedback!!))

            btn_true.background = context.getDrawable(R.drawable.bg_border)
            btn_false.background = context.getDrawable(R.drawable.bg_rounded)
        }

        btn_false.setOnClickListener {
            answeredQs.put(position, AnswerSheet(questions[position].name!!,
                questions[position].answers[1].answer!!,
                questions[position].answers[1].feedback!!))

            btn_false.background = context.getDrawable(R.drawable.bg_border)
            btn_true.background = context.getDrawable(R.drawable.bg_rounded)
        }

        val tfChoices = ArrayList<ImageButton>()
        tfChoices.add(btn_true)
        tfChoices.add(btn_false)

        qTFChoices.put(position, tfChoices)
        //voicelistener?.trueFalseChoices = tfChoices

    }

    companion object{

        var qindex = -1
    }

    inner class listener : RecognitionListener {

        var isAvailable = true

        override fun onReadyForSpeech(params: Bundle) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEndOfSpeech() {
            if(isAvailable) {
                //qindex++
                isAvailable = true
            }
        }

        override fun onError(error: Int) {
            Log.d("voice error", "error $error")
            //it is supposed to ask if he want to answer again
            if(error == 6)
                qindex++
            else if(error == 2){
                isAvailable = false
                speakMethod.startSpeak("يُرجى ذِكر الإجابة")
                Handler().postDelayed({recognizeVoice()}, GCPTTS.prevDuration.toLong())
            }
    }

        override fun onResults(results: Bundle) {
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val str :String? = data!![0]


            if(str.equals("صح") || str.equals("خطا")){
                if(str.equals("صح")) {
                    qindex++
                    qTFChoices.get(qindex)?.get(0)?.callOnClick()
                }
                else if((str.equals("خطا"))) {
                    qindex++
                    qTFChoices.get(qindex)?.get(1)?.callOnClick()
                }
                else {
                    isAvailable = false
                    speakMethod.startSpeak("الخَيار غير متاح، يُرجى إعادة الإجابة")
                    //qindex++
                    Handler().postDelayed({recognizeVoice()}, GCPTTS.prevDuration.toLong())
                }
            }else{
                when(str){
                    "الف" -> {
                        qindex++
                        qMultipleChoices.get(qindex)?.get(0)?.callOnClick()
                    }
                    "باء" -> {
                        qindex++
                        qMultipleChoices.get(qindex)?.get(1)?.callOnClick()}
                    "جيم" -> {
                        qindex++
                        qMultipleChoices.get(qindex)?.get(2)?.callOnClick()}
                    else -> {
                        isAvailable = false
                        GCPTTS.isDone = true
                        speakMethod.startSpeak("الخَيار غير متاح، يُرجى إعادة الإجابة")
                        //qindex++
                       // Handler().postDelayed({recognizeVoice()}, GCPTTS.prevDuration.toLong()+2500L)
                    }
                }
            }

            /*  Handler().postDelayed(Runnable {
                  QuizActivity.btnNext.callOnClick()
              }, 2000)*/

        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    fun recognizeVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-JO")
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "tk.oryx.voice")

        intent.putExtra(
            RecognizerIntent.EXTRA_MAX_RESULTS,
            1
        )  // 1 is the maximum number of results to be returned.
        sr?.startListening(intent)
    }
}