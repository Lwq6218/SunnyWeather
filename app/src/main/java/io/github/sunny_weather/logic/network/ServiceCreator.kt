package io.github.sunny_weather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit构建器
 */
object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    //inline是内联函数，reified是泛型实化，可以在运行时获取泛型的实际类型
    //inline的作用是将函数调用处的代码替换为函数体，减少函数调用的开销
    inline fun <reified T> create(): T = create(T::class.java)
}