package io.github.sunny_weather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.github.sunny_weather.R
import io.github.sunny_weather.databinding.ActivityWeatherBinding
import io.github.sunny_weather.databinding.ForecastItemBinding
import io.github.sunny_weather.logic.model.Weather
import io.github.sunny_weather.logic.model.getSky
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherBinding

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        val view = binding.root
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            val decorView = window.decorView
//            decorView.windowInsetsController?.hide(android.view.WindowInsets.Type.statusBars())
//        }

        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        binding.includeNow.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.drawerLayout.addDrawerListener(object :
            androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // do nothing
            }

            override fun onDrawerOpened(drawerView: View) {
                // do nothing
            }

            override fun onDrawerClosed(drawerView: View) {
                // do nothing
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    binding.drawerLayout.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onDrawerStateChanged(newState: Int) {
                // do nothing
            }
        })
        setContentView(view)

        if (viewModel.locationLang.isEmpty()) {
            viewModel.locationLang = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false
        })

        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        viewModel.refreshWeather(viewModel.locationLang, viewModel.locationLat)
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLang, viewModel.locationLat)
        binding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        binding.includeNow.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        binding.includeNow.currentTemp.text = "${realtime.temperature.toInt()} ℃"
        binding.includeNow.currentSky.text = getSky(realtime.skycon).info
        binding.includeNow.currentAQI.text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.includeNow.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        binding.includeForecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = ForecastItemBinding.inflate(
                layoutInflater,
                binding.includeForecast.forecastLayout,
                false
            )
            val zoneDateTime = ZonedDateTime.parse(skycon.date)
            val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = zoneDateTime.format(formater)
            view.dateInfo.text = date
            val sky = getSky(skycon.value)
            view.skyIcon.setImageResource(sky.icon)
            view.skyInfo.text = sky.info
            view.temperatureInfo.text = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            binding.includeForecast.forecastLayout.addView(view.root)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        binding.includeLifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.includeLifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binding.includeLifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.includeLifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility = android.view.View.VISIBLE

    }
}