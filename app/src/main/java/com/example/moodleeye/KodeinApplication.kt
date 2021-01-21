package com.example.moodleeye

import android.app.Application
import com.example.moodleeye.data.Preferences
import com.example.moodleeye.data.network.API
import com.example.moodleeye.data.network.NetworkConnectionInterceptor
import com.example.moodleeye.data.repositry.AuthRepository
import com.example.moodleeye.data.repositry.ContentRepository
import com.example.moodleeye.ui.auth.AuthViewModelFactory
import com.example.moodleeye.ui.content.course.CourseViewModelFactory
import com.example.moodleeye.ui.content.quiz.QuizViewModelFactory

import org.kodein.di.generic.bind
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class KodeinApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@KodeinApplication))


        bind() from singleton { Preferences(instance()) }
        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { API(instance()) }
        bind() from singleton { AuthRepository(instance()) }
        bind() from singleton { ContentRepository(instance()) }
        bind() from singleton { AuthViewModelFactory(instance(),instance()) }
        bind() from singleton { CourseViewModelFactory(instance(),instance()) }
        bind() from singleton { QuizViewModelFactory(instance(),instance()) }

    }
}