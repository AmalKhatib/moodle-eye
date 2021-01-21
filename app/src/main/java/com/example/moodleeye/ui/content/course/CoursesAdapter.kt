package com.example.moodleeye.ui.content.course

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodleeye.R
import com.example.moodleeye.data.network.response.Course
import com.example.moodleeye.util.SpeakMethod
import com.example.moodleeye.util.gcp.GCPTTS

class CoursesAdapter(val activity : Activity, val context: Context, val courses : List<Course>) :
    RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.custom_course, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.courseName.text = courses[position].name

        val speakMethod = SpeakMethod(context , activity)
        speakMethod.initAndroidTTSSetting()
      /*  Handler().postDelayed(
            { speakMethod.startSpeak(courses[position].name) },
            GCPTTS.duration.toLong()
        )*/


        /*if(position == courses.size-1){
            Handler().postDelayed(
                { speakMethod.startSpeak("قم بنُطق اسم المساق الذي تود الذهاب إليه") },
                GCPTTS.prevDuration.toLong()
            )
        }*/

        holder.itemView.setOnClickListener {
            speakMethod.stopAudio()
            Intent(context, CourseDetailsActivity::class.java).let {
                it.putExtra("courseId", courses[position].id)
                it.putExtra("courseName", courses[position].name)
                it.putExtra("courseSummary", courses[position].summary)
                context.startActivity(it)
            }
        }

    }

    class ViewHolder: RecyclerView.ViewHolder {

        var courseName: TextView;

        constructor(itemView : View) : super(itemView){
            courseName = itemView.findViewById<TextView>(R.id.tv_course_name)
        }

    }
}