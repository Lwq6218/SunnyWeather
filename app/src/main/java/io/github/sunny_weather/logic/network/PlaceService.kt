package io.github.sunny_weather.logic.network

import io.github.sunny_weather.SunnyWeatherApplication
import io.github.sunny_weather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String):Call<PlaceResponse>
}