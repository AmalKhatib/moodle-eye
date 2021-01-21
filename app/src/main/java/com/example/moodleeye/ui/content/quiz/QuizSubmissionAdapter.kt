package com.example.moodleeye.ui.content.quiz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodleeye.R
import com.example.moodleeye.data.models.AnswerSheet
import com.example.moodleeye.data.network.response.Answer
import com.example.moodleeye.util.toast

class QuizSubmissionAdapter (private val answers: HashMap<Int, AnswerSheet>, private val context: Context) : RecyclerView.Adapter<QuizSubmissionAdapter.ViewHolder>() {

    init {
        unAnsweredQs.clear()
        var index = 0
        for(answer in answers){
            if(answer.value.answer.equals("")){
                // context.toast(answer.value.questionName)
                unAnsweredQs.put(index, answers.get(index)!!)
            }
            index++
        }
    }

    companion object{
        var unAnsweredQs = HashMap<Int, AnswerSheet>()
    }

    fun getUnAnsweredQs(): HashMap<Int, AnswerSheet>{
        return unAnsweredQs
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.custom_check_question, null)
        return ViewHolder(view)
    }

    var isChecked = false
    override fun getItemCount(): Int {

        return answers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.question.text = answers.get(position)?.questionName
        holder.answer.text = answers.get(position)?.answer

        if(answers.get(position)?.answer.equals("")) {
            holder.img_check.setImageResource(R.drawable.ic_circle_grey)
            holder.answer.text = ""
        }
    }

    private var layoutInflater: LayoutInflater? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var question: TextView
        var answer: TextView
        var img_check: ImageView
        var line: View

        init {
            line = itemView.findViewById(R.id.line)
            question  = itemView.findViewById(R.id.tv_question)
            img_check = itemView.findViewById(R.id.img_check)
            answer = itemView.findViewById(R.id.tv_answer)
        }

    }
}