package com.example.weatherapp.api

import com.example.weatherapp.model.ModelClass
import retrofit2.Response
import retrofit2.http.Query

interface ApiHelper {

    suspend fun getCurrentWeatherData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("APPID") api_key: String
    ): Response<ModelClass>

    suspend fun getCityWeatherData(
        @Query("q") cityName: String,
        @Query("APPID") api_key: String
    ): Response<ModelClass>
}