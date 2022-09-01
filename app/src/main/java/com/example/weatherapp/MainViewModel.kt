package com.example.weatherapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.POJO.ModelClass
import com.example.weatherapp.Utilities.ApiUtilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel(){

    private val _weatherCurLocSend = MutableLiveData<Boolean>()
    val weatherCurLocSend: LiveData<Boolean> = _weatherCurLocSend

    private val _pbLoading = MutableLiveData<Boolean>()
    val pbLoading: LiveData<Boolean> = _pbLoading

    private val _weatherCurLoc = MutableLiveData<ModelClass>()
    val weatherCurLoc: LiveData<ModelClass> = _weatherCurLoc

    init {
    }

    private fun fetchCurrentLocationWeater(latitude: String, longitude: String, API_KEY: String) {
        _pbLoading.value = true
       // binding.pbLoading.visibility = View.VISIBLE
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

    private fun getCityWeather(cityName: String, API_KEY: String) {
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
}