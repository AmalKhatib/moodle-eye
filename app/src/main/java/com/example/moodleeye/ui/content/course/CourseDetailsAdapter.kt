package com.example.moodleeye.ui.content.course

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodleeye.R
import com.example.moodleeye.data.network.response.Quiz
import com.example.moodleeye.ui.auth.LoginActivity
import com.example.moodleeye.ui.content.quiz.QuizActivity
import java.io.Serializable

class CourseDetailsAdapter(val context: Context ,val quizzes: List<Quiz>) :
    RecyclerView.Adapter<CourseDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.custome_quiz_block, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return quizzes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.quizName.text = quizzes[position].quizName
        holder.quizIntro.text = quizzes[position].quizIntro

        holder.itemView.setOnClickListener {
            Intent(context, QuizActivity::class.java).let {
                it.putExtra("questions" , quizzes[position].questions as Serializable)
                context.startActivity(it)
            }
        }

        holder.btnBegin.setOnClickListener {
            Intent(context, QuizActivity::class.java).let {
                it.putExtra("questions" , quizzes[position].questions as Serializable)
                context.startActivity(it)
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