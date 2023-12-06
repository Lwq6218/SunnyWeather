package io.github.sunny_weather.logic

import androidx.lifecycle.liveData
import io.github.sunny_weather.logic.dao.PlaceDao
import io.github.sunny_weather.logic.model.Place
import io.github.sunny_weather.logic.model.Weather
import io.github.sunny_weather.logic.network.SunnyWeatherNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object Repository {
    fun searchPlaces(query: String) = liveData<Result<List<Place>>>(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetWork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }

    fun refreshWeather(lan: String, lat: String) = liveData<Result<Weather>>(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val deferredRealtime = async {
                    SunnyWeatherNetWork.getRealtimeWeather(lan, lat)
                }
                val deferredDaily = async {
                    SunnyWeatherNetWork.getDailyWeather(lan, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather =
                        Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                    Result.success(weather)
                } else {
                    Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}"
                                    + "daily response status is ${dailyResponse.status}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure<Weather>(e)
        }
        emit(result)
    }

    //TODO 用协程
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}