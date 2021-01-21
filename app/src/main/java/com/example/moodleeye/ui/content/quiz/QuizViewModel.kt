package com.example.moodleeye.ui.content.quiz

import androidx.lifecycle.ViewModel
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.network.response.Quiz
import com.example.moodleeye.data.network.response.User
import com.example.moodleeye.data.repositry.ContentRepository
import com.example.moodleeye.util.Coroutines
import com.example.moodleeye.util.Listener
import com.google.gson.Gson
import java.io.IOException
import java.sql.Date

class QuizViewModel(val preferences: Preferences, private val repository: ContentRepository
) : ViewModel() {

    var authListener: Listener? = null

    var courseId : Int? = 0

    fun postGrade(grade: Int){
        val json = preferences.getUser()
        val user = Gson().fromJson<Any>(json, User::class.java) as User

        val quizJson = preferences.getQuiz()
        val quiz = Gson().fromJson<Any>(quizJson, Quiz::class.java) as Quiz


        authListener?.onFailure(quiz.quizId.toString())

        Coroutines.main {
            try {
                val response =
                    repository.postGrade(user.id, quiz.quizId!!, grade)

                if(response.error.equals("false")){
                    authListener?.onSuccess(true)
                    return@main
                }

                authListener?.onFailure(response.msg!!)

            } catch (e: Exception) {
                authListener?.onFailure(e.message!!)
            }

        }
    }

    fun getGrade(){
        val json = preferences.getUser()
        val user = Gson().fromJson<Any>(json, User::class.java) as User

        val quizJson = preferences.getQuiz()
        val quiz = Gson().fromJson<Any>(quizJson, Quiz::class.java) as Quiz

        Coroutines.main {
            try {
                val response =
                    repository.getGrade(user.id, quiz.quizId!!)

                if(response.error.equals("false")){
                    authListener?.onSuccess(response.grade.take(1))
                    return@main
                }

                authListener?.onFailure(response.msg!!)

            } catch (e: IOException) {
                authListener?.onFailure(e.message!!)
            }

        }
    }
}