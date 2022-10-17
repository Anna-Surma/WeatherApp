package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.Constants
import com.example.weatherapp.di.MainRepository
import com.example.weatherapp.model.ModelClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _pbLoading = MutableStateFlow<Boolean?>(null)
    val pbLoading = _pbLoading.asStateFlow()

    private val _weatherCurLoc = MutableStateFlow<ModelClass?>(null)
    val weatherCurLoc = _weatherCurLoc.asStateFlow()

    private val _currentDate = MutableStateFlow<String?>(null)
    val currentDate = _currentDate.asStateFlow()

    private val _dayMaxTemp = MutableStateFlow<String?>(null)
    val dayMaxTemp = _dayMaxTemp.asStateFlow()

    private val _dayMinTemp = MutableStateFlow<String?>(null)
    val dayMinTemp = _dayMinTemp.asStateFlow()

    private val _temp = MutableStateFlow<String?>(null)
    val temp = _temp.asStateFlow()

    private val _feelsLike = MutableStateFlow<String?>(null)
    val feelsLike = _feelsLike.asStateFlow()

    private val _weatherType = MutableStateFlow<String?>(null)
    val weatherType = _weatherType.asStateFlow()

    private val _sunrise = MutableStateFlow<String?>(null)
    val sunrise = _sunrise.asStateFlow()

    private val _sunset = MutableStateFlow<String?>(null)
    val sunset = _sunset.asStateFlow()

    private val _pressure = MutableStateFlow<String?>(null)
    val pressure = _pressure.asStateFlow()

    private val _humidity = MutableStateFlow<String?>(null)
    val humidity = _humidity.asStateFlow()

    private val _windSpeed = MutableStateFlow<String?>(null)
    val windSpeed = _windSpeed.asStateFlow()

    private val _tempFahrenheit = MutableStateFlow<String?>(null)
    val tempFahrenheit = _tempFahrenheit.asStateFlow()

    fun fetchCurrentLocationWeather(latitude: String, longitude: String, API_KEY: String) =
        viewModelScope.launch {
            _pbLoading.value = true
            val response =
                mainRepository.getCurrentWeatherData(latitude, longitude, API_KEY)
            if (response.isSuccessful) {
                _weatherCurLoc.value = response.body()
            }
        }

    fun getCityWeather(cityName: String, API_KEY: String) =
        viewModelScope.launch {
            // binding.pbLoading.visibility= View.VISIBLE
            val response =
                mainRepository.getCityWeatherData(cityName, API_KEY)
            if (response.isSuccessful) {
                _weatherCurLoc.value = response.body()
            }
        }

    private fun timeStampToLocalDate(timeStamp: Long): String {
        val localTime = timeStamp.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        }
        return localTime.toString()
    }

    private fun kelvinToCelsius(temp: Double): Double {
        var intTemp = temp
        intTemp = intTemp.minus(Constants.KELVIN_SCALE)
        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    fun getCurrentDate(body: ModelClass?) {
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

        _tempFahrenheit.value =
            ((kelvinToCelsius(body.main.temp)).times(1.8).plus(32).toInt()).toString()
    }

}