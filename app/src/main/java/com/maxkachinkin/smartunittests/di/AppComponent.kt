package com.maxkachinkin.smartunittests.di

import com.maxkachinkin.smartunittests.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, BindsModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(): AppComponent
    }
}
