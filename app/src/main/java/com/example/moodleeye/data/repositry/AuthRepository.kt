package com.example.moodleeye.data.repositry

import com.example.moodleeye.data.network.API
import com.example.moodleeye.data.network.SafeApiRequest
import com.example.moodleeye.data.network.response.UserResponse

class AuthRepository( private val api: API
) : SafeApiRequest() {

suspend fun userLogin(userName : String) : UserResponse {

    return apiRequest { api.getUser(userName) }
}
}