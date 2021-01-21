package com.example.moodleeye.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.repositry.AuthRepository
import com.example.moodleeye.ui.auth.AuthViewModel


@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    val preferences: Preferences,
    private val repository: AuthRepository
)
    : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(preferences ,repository) as T
    }
}