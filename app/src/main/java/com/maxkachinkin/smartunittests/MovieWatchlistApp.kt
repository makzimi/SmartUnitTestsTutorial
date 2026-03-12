package com.maxkachinkin.smartunittests

import android.app.Application
import com.maxkachinkin.smartunittests.di.AppComponent
import com.maxkachinkin.smartunittests.di.DaggerAppComponent

class MovieWatchlistApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create()
    }
}
