package com.example.weatherapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.api.ApiUtilities
import com.example.weatherapp.model.ModelClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

class MainViewModel: ViewModel(){

    private val _pbLoading = MutableLiveData<Boolean>()
    val pbLoading: LiveData<Boolean> = _pbLoading

    private val _weatherCurLoc = MutableLiveData<ModelClass>()
    val weatherCurLoc: LiveData<ModelClass> = _weatherCurLoc

    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> = _currentDate

    private val _dayMaxTemp = MutableLiveData<String>()
    val dayMaxTemp: LiveData<String> = _dayMaxTemp

    private val _dayMinTemp = MutableLiveData<String>()
    val dayMinTemp: LiveData<String> = _dayMinTemp

    private val _temp = MutableLiveData<String>()
    val temp: LiveData<String> = _temp

    private val _feelsLike = MutableLiveData<String>()
    val feelsLike: LiveData<String> = _feelsLike

    private val _weatherType = MutableLiveData<String>()
    val weatherType: LiveData<String> = _weatherType

    private val _sunrise = MutableLiveData<String>()
    val sunrise: LiveData<String> = _sunrise

    private val _sunset = MutableLiveData<String>()
    val sunset: LiveData<String> = _sunset

    private val _pressure = MutableLiveData<String>()
    val pressure: LiveData<String> = _pressure

    private val _humidity = MutableLiveData<String>()
    val humidity: LiveData<String> = _humidity

    private val _windSpeed = MutableLiveData<String>()
    val windSpeed: LiveData<String> = _windSpeed

    private val _tempFahrenheit = MutableLiveData<String>()
    val tempFahrenheit: LiveData<String> = _tempFahrenheit

    fun fetchCurrentLocationWeather(latitude: String, longitude: String, API_KEY: String) {
        _pbLoading.value = true
        ApiUtilities.getApiInterface()?.getCurrentWeatherData(latitude, longitude, API_KEY)
            ?.enqueue(object : Callback<ModelClass> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                    if (response.isSuccessful) {
                        _weatherCurLoc.value = response.body()
                    }
                }
                override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                }
            })
    }

    fun getCityWeather(cityName: String, API_KEY: String) {
        // binding.pbLoading.visibility= View.VISIBLE
        ApiUtilities.getApiInterface()?.getCityWeatherData(cityName, API_KEY)
            ?.enqueue(object : Callback<ModelClass> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                    _weatherCurLoc.value = response.body()
                }
                override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStampToLocalDate(timeStamp: Long): String {
        val localTime = timeStamp.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        }
        return localTime.toString()
    }

    private fun kelvinToCelsius(temp: Double): Double {
        var intTemp = temp
        intTemp = intTemp.minus(273)
        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    fun getCurrentDate(body: ModelClass?){
        _currentDate.value = SimpleDateFormat("dd/MM/yyyy hh:mm").format(Date())
        _dayMaxTemp.value = kelvinToCelsius(body!!.main.temp_max).toString()
        _dayMinTemp.value = kelvinToCelsius(body.main.temp_min).toString()

        _temp.value = kelvinToCelsius(body.main.temp).toString()
        _feelsLike.value = kelvinToCelsius(body.main.feels_like).toString()

        _weatherType.value = body.weather[0].main
        _sunrise.value = timeStampToLocalDate(body.sys.sunrise.toLong())
        _sunset.value = timeStampToLocalDate(body.sys.sunset.toLong())

        _pressure.value = body.main.pressure.toString()
        _humidity.value = body.main.humidity.toString() + "%"
        _windSpeed.value = body.wind.speed.toString() + "m/s"

        _tempFahrenheit.value = ((kelvinToCelsius(body.main.temp)).times(1.8).plus(32).toInt()).toString()
    }

}