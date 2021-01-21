package com.example.moodleeye.data.network.response

import java.io.Serializable

data class QuizResponse(
    var error: String,
    var msg: String,
    var quizes: List<Quiz>
):Serializable

data class Quiz(
    var quizId:Int?,
    var quizName:String,
    var quizIntro:String?,
    var timeopen: Long,
    var timeclose: Long,
    var timelimit: Long,
    var questions:List<Question>
):Serializable

data class Question(
    var id:Int?,
    var name:String?,
    var questiontext:String?,
    var qtype:String?,
    var answers:List<Answer>
):Serializable

data class Answer(
    var answer:String?,
    var feedback:String?
):Serializable

