package com.example.moodleeye.ui.content.course

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.moodleeye.R
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.network.response.Quiz
import com.example.moodleeye.ui.content.quiz.*
import com.example.moodleeye.util.Listener
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.toast
import com.google.gson.Gson
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.Serializable
import java.util.*

class QuizAdapter(val mainActivity : Activity  ,val context: Context, val activity: FragmentActivity, val quizzes: List<Quiz>, val preferences: Preferences) :
    RecyclerView.Adapter<QuizAdapter.ViewHolder>() , KodeinAware, Listener {

    var grade: Int = 0

    var isZero = true

    override fun onStarted() {}

    override fun onSuccess(items: Any) {
        if(items is String){
            grade = Integer.parseInt(items)
        }
    }

    override fun onFailure(message: String) {}

    override val kodein by kodein(context)
    private val factory: QuizViewModelFactory by instance()
    var viewModel: QuizViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        viewModel = ViewModelProviders.of(activity, factory).get(QuizViewModel::class.java)
        viewModel!!.authListener = this

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.custome_quiz_block, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return quizzes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        preferences.saveQuiz(Gson().toJson(quizzes[position]))

        viewModel?.getGrade()

        holder.quizName.text = quizzes[position].quizName
        holder.quizIntro.text = quizzes[position].quizIntro

        holder.itemView.setOnClickListener {
            Intent(context, QuizActivity::class.java).let {
                it.putExtra("quizName", quizzes[position].quizName.toString())
                it.putExtra("questions" , quizzes[position].questions as Serializable)
                it.putExtra("quizId", quizzes[position].quizId)
                activity.startActivityForResult(it, 1)
            }
        }

        holder.btnBegin.setOnClickListener {

            val addQuiz = preferences.saveQuiz(Gson().toJson(quizzes[position]))
            val quizJson = preferences.getQuiz()
            val quiz = Gson().fromJson<Any>(quizJson, Quiz::class.java) as Quiz

            val quizOpenDate = Date(quiz.timeopen)
            val quizCloseDate = Date(quiz.timeclose)

            if((Date().after(quizOpenDate) || Date().equals(quizOpenDate)) && Date().before(quizCloseDate)){
                if(grade == 0){
                    Intent(context, QuizActivity::class.java).let {
                        it.putExtra("timeLimit", quiz.timelimit)
                        it.putExtra("quizName", quiz.quizName)
                        it.putExtra("questions", quizzes[position].questions as Serializable)
                        activity.startActivityForResult(it, 7)
                    }

                }else {
                    Intent(context, QuizGradeActivity::class.java).let {
                          it.putExtra("quizName" , quiz.quizName)
                          it.putExtra("gradeOf" , quizzes[position].questions.size)
                          context.startActivity(it)
                    }
                }
            }else if(Date().before(quizOpenDate)){
                Intent(context, EarlyQuizActivity::class.java).let {
                    it.putExtra("open" , quiz.timeopen)
                    it.putExtra("close" , quiz.timeclose)
                    context.startActivity(it)
                }
            }else{
                Intent(context, QuizGradeActivity::class.java).let {
                    it.putExtra("quizName" , quiz.quizName)
                    it.putExtra("gradeOf" , quizzes[position].questions.size)
                    context.startActivity(it)
                }
            }
        }

    }

    class ViewHolder: RecyclerView.ViewHolder {

        var quizName: TextView;
        var quizIntro: TextView
        var btnBegin: Button
        constructor(itemView : View) : super(itemView){
            quizName = itemView.findViewById<TextView>(R.id.tv_quiz_name)
            quizIntro = itemView.findViewById<TextView>(R.id.tv_quiz_intro)
            btnBegin = itemView.findViewById(R.id.btn_begin)
        }
    }
}