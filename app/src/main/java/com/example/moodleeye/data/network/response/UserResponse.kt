package com.example.moodleeye.data.network.response

data class UserResponse(
    var error : String,
    var msg : String,
    var user: List<User>
)



data class User(
    var id:Int,
    var firstname :String,
    var lastname :String,
    var username :String


)