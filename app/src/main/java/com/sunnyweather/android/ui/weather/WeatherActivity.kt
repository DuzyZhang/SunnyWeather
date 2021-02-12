package com.sunnyweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.*
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import com.sunnyweather.android.ui.place.PlaceViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
     lateinit var weatherBinding: ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherBinding = ActivityWeatherBinding.inflate(layoutInflater)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(weatherBinding.root)

        weatherBinding.now.navBtn.setOnClickListener {
            weatherBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        weatherBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {}

        })

        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng")?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat")?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name")?: ""
        }

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if(weather != null){
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            weatherBinding.swipeRefresh.isRefreshing = false
        })

        weatherBinding.swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary)
        refreshWeather()
        weatherBinding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

    }

     fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        weatherBinding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        weatherBinding.now.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        weatherBinding.now.currentTemp.text = currentTempText
        weatherBinding.now.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        weatherBinding.now.currentAQI.text = currentPM25Text
        weatherBinding.now.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)


        weatherBinding.forecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days){

            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = ForecastItemBinding.inflate(layoutInflater, weatherBinding.forecast.forecastLayout,false)
            val dateInfo = view.dateInfo
            val skyIcon = view.skyIcon
            val skyInfo = view.skyInfo
            val temperatureInfo = view.temperatureInfo
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            weatherBinding.forecast.forecastLayout.addView(view.root)
        }

        val lifeIndex = daily.lifeIndex

        weatherBinding.lifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        weatherBinding.lifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        weatherBinding.lifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        weatherBinding.lifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc
        weatherBinding.weatherLayout.visibility = View.VISIBLE
    }

}