package io.github.sunny_weather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication:Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
        const val  TOKEN = "l6IEiXUQvzaBPOxk"
    }
    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }
}
