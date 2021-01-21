package com.example.moodleeye.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.repositry.AuthRepository
import com.example.moodleeye.util.Coroutines
import com.example.moodleeye.util.Listener
import com.google.gson.Gson
import java.io.IOException

class AuthViewModel(
    val preferences: Preferences,
    private val repository: AuthRepository
) : ViewModel() {

    var userName: String? = null



    var authListener: Listener? = null


    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()
        if (userName.isNullOrEmpty()) {
            authListener?.onFailure("Invalid email or password")
            return
        }

        Coroutines.main {
            try {
                val response =
                    repository.userLogin(userName!!)

                if(response.error.equals("false")){
                    response.user.let {
                        preferences.saveUser(Gson().toJson(it[0]))
                        authListener?.onSuccess(it)
                        return@main
                    }
                }else
                    authListener?.onFailure(response.msg!!)

            } catch (e: IOException) {
                authListener?.onFailure(e.message!!)
            }


        }


    }
}