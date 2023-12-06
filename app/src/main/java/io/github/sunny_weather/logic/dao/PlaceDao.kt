package io.github.sunny_weather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import io.github.sunny_weather.SunnyWeatherApplication
import io.github.sunny_weather.logic.model.Place


object PlaceDao {
    private fun sharePreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

    fun savePlace(place: Place) {
        sharePreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place {
        val placeJson = sharePreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharePreferences().contains("place")
}