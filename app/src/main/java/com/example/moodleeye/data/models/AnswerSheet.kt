package com.example.moodleeye.data.models

import java.io.Serializable

data class AnswerSheet(var questionName: String,
                       var answer: String,
                       var feedback: String) : Serializable