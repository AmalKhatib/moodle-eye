package com.example.moodleeye.data.repositry

import com.example.moodleeye.data.network.API
import com.example.moodleeye.data.network.SafeApiRequest
import com.example.moodleeye.data.network.response.CourseResponse
import com.example.moodleeye.data.network.response.GetGradeResponse
import com.example.moodleeye.data.network.response.PostGradeResponse
import com.example.moodleeye.data.network.response.QuizResponse

class ContentRepository (private val api: API
) : SafeApiRequest() {

    suspend fun getCourses(id : Int) : CourseResponse {

        return apiRequest { api.getCourses(id) }
    }

    suspend fun getQuizzes(userId : Int, courseId:Int) : QuizResponse {

        return apiRequest { api.getQuizzes(userId,courseId) }
    }

    suspend fun getGrade(userId : Int, quizId:Int) : GetGradeResponse {

        return apiRequest { api.gettGrade(userId,quizId) }
    }

    suspend fun postGrade(userId : Int, quizId:Int, grade: Int) : PostGradeResponse {

        return apiRequest { api.postGrade(userId,quizId, grade) }
    }

}