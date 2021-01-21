package com.example.moodleeye.data.network.response

data class CourseResponse (
    var error : String,
    var msg : String,
    var courses: List<Course>
)


data class Course(
    var id:String ,
    var name : String,
    var summary: String
)