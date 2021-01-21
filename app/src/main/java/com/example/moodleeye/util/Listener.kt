package com.example.moodleeye.util

interface Listener {
    fun onStarted()
    fun onSuccess(items: Any)
    fun onFailure(message: String)
}