package io.github.sunny_weather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import io.github.sunny_weather.logic.Repository
import io.github.sunny_weather.logic.model.Location

class WeatherViewModel : ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()
    var locationLang = ""
    var locationLat = ""
    var placeName = ""
    val weatherLiveData = locationLiveData.switchMap { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }
}