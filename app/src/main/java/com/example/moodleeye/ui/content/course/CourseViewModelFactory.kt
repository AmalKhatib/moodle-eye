package com.example.moodleeye.ui.content.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.repositry.ContentRepository

@Suppress("UNCHECKED_CAST")
class CourseViewModelFactory(
    val preferences: Preferences,
    private val repository: ContentRepository
)
    : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CourseViewModel(preferences, repository) as T
    }
}