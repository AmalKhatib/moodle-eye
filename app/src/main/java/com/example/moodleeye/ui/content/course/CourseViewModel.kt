package com.example.moodleeye.ui.content.course

import androidx.lifecycle.ViewModel
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.network.response.User
import com.example.moodleeye.data.repositry.ContentRepository
import com.example.moodleeye.util.Coroutines
import com.example.moodleeye.util.Listener
import com.google.gson.Gson
import java.io.IOException

class CourseViewModel (val preferences: Preferences, private val repository: ContentRepository
) : ViewModel() {

    var authListener: Listener? = null

    var courseId : Int? = 0

    fun getCourses(){
        val json = preferences.getUser()
       val user = Gson().fromJson<Any>(json, User::class.java) as User

        Coroutines.main {
            try {
                val response =
                    repository.getCourses(user.id)

                response.courses?.let {

                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(response.msg!!)

            } catch (e: IOException) {
                authListener?.onFailure(e.message!!)
            }

        }
    }

    fun getQuizzes(courseId: Int){
        val json = preferences.getUser()
        val user = Gson().fromJson<Any>(json, User::class.java) as User


        Coroutines.main {
            try {
                val response =
                    repository.getQuizzes(user.id , courseId)

                response.quizes?.let {
                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(response.msg!!)

            } catch (e: IOException) {
                authListener?.onFailure(e.message!!)
            }

        }
    }
}