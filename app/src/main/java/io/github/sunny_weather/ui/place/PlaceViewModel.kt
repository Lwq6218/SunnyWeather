package io.github.sunny_weather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import io.github.sunny_weather.logic.Repository
import io.github.sunny_weather.logic.model.Place

class PlaceViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    //switchMap将searchLiveData转换成另一个LiveData对象
    //query是searchLiveData的值，也就是用户输入的搜索内容
    //switchMap会观察searchLiveData的变化，当searchLiveData的值发生变化时，switchMap会执行lambda表达式
    val placeLiveData = searchLiveData.switchMap { query ->
        Repository.searchPlaces(query)
    }

    //searchPlaces方法会将传入的query值赋值给searchLiveData
    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }
   
    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}