package com.example.weatherapp.di

import com.example.weatherapp.api.ApiHelper
import com.example.weatherapp.model.ModelClass
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Query
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiHelper: ApiHelper){
    suspend fun getCurrentWeatherData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("APPID") api_key: String
    ): Response<ModelClass> = apiHelper.getCurrentWeatherData(latitude, longitude, api_key)

    suspend fun getCityWeatherData(
        @Query("q") cityName: String,
        @Query("APPID") api_key: String
    ): Response<ModelClass> = apiHelper.getCityWeatherData(cityName, api_key)
}
