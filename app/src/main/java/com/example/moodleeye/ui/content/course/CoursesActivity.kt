package com.example.moodleeye.ui.content.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodleeye.R
import com.example.moodleeye.data.network.response.Course
import com.example.moodleeye.ui.auth.AuthViewModel
import com.example.moodleeye.ui.auth.AuthViewModelFactory
import com.example.moodleeye.util.Listener
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS
import com.example.moodleeye.util.toast
import kotlinx.android.synthetic.main.activity_courses.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CoursesActivity : AppCompatActivity()  , KodeinAware, Listener {

    override val kodein by kodein()
    private val factory: CourseViewModelFactory by instance()
    var viewModel: CourseViewModel? = null

    var isFirstApearing = true

    lateinit var speakMethod : SpeakMethod
    private var sr: SpeechRecognizer? = null
    var courses = ArrayList<Course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)

        speakMethod = SpeakMethod(this , this)
        speakMethod.initAndroidTTSSetting()

        val voicelistener = listener()
        sr = SpeechRecognizer.createSpeechRecognizer(this)
        sr?.setRecognitionListener(voicelistener)

        rv_courses.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProviders.of(this, factory).get(CourseViewModel::class.java)
        viewModel!!.authListener = this
        viewModel!!.getCourses()
    }

    override fun onResume() {
        super.onResume()

        val voicelistener = listener()
        GCPTTS.sr = sr!!
        GCPTTS.voicelistener = voicelistener

        if(!isFirstApearing){
            GCPTTS.isDone = true
            speakMethod.startSpeak("قُم بنُطق اسم أحد المساقات المُسَجَّلَهْ")
        }

    }

    override fun onStarted() {}
    override fun onSuccess(items: Any) {
        items as ArrayList<Course>
        items.add(Course("2", "حاضر العالم الاسلامي", "وصف المساق"))
        rv_courses.adapter = CoursesAdapter(this@CoursesActivity, this , items)

        courses = items

        speakMethod.startSpeak("المساقات المُسَجَّلَهْ")

        var coursesLine = ""
        for(course in items){
            coursesLine = coursesLine + "مساقْ "+course.name + "\n"
    }

       Handler().postDelayed(
            {
                speakMethod.startSpeak(coursesLine)
                Handler().postDelayed(
                    {
                    GCPTTS.isDone = true
                    speakMethod.startSpeak("قم بنُطق اسم المَساقْ الذي تود الذهاب إلَيْهْ")
                    }
                    , GCPTTS.prevDuration.toLong()+1900L)
            },
            GCPTTS.prevDuration.toLong()-2000L)
    }

    override fun onFailure(message: String) {
        toast(message)
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
            for(course in courses){
                if(course.name.equals(str)){
                    isFirstApearing = false

                    sr?.stopListening()
                    speakMethod.stopAudio()
                    Intent(this@CoursesActivity, CourseDetailsActivity::class.java).let {
                        it.putExtra("courseId", course.id)
                        it.putExtra("courseName", course.name)
                        it.putExtra("courseSummary", course.summary)
                        startActivity(it)
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