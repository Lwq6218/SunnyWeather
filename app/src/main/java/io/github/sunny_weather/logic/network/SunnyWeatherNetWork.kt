package io.github.sunny_weather.logic.network

import retrofit2.Call
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 网络请求入口
 */
object SunnyWeatherNetWork {
    private val placeService = ServiceCreator.create<PlaceService>()

    //await是一个扩展函数，挂起当前协程，等待PlaceResponse对象的结果
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        ServiceCreator.create<WeatherService>().getRealtimeWeather(lng, lat).await()

    suspend fun getDailyWeather(lng: String, lat: String) =
        ServiceCreator.create<WeatherService>().getDailyWeather(lng, lat).await()

    //suspendCoroutine将当前协程挂起，等待回调结果
    //resume恢复协程执行，resumeWithException恢复协程执行并抛出异常
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            //enqueue将网络请求加入调度队列，等待执行
            enqueue(object : retrofit2.Callback<T> {
                //当网络请求成功时，调用onResponse方法
                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                    val body = response.body()
                    //如果response的body不为空，则调用resume方法恢复协程执行
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                //当网络请求失败时，调用onFailure方法
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}