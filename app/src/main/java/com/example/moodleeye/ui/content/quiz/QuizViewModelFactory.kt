package com.example.moodleeye.ui.content.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.repositry.ContentRepository

class QuizViewModelFactory (
    val preferences: Preferences,
    private val repository: ContentRepository
)
    : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuizViewModel(preferences, repository) as T
    }
}