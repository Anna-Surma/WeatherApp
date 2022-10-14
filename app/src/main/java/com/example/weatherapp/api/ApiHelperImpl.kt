package com.example.weatherapp.api

import com.example.weatherapp.model.ModelClass
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiInterface: ApiInterface,
) : ApiHelper{

    override suspend fun getCurrentWeatherData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("APPID") api_key: String
    ): Response<ModelClass> = apiInterface.getCurrentWeatherData(latitude, longitude, api_key)

    override suspend fun getCityWeatherData(
        @Query("q") cityName: String,
        @Query("APPID") api_key: String
    ): Response<ModelClass> = apiInterface.getCityWeatherData(cityName, api_key)
}