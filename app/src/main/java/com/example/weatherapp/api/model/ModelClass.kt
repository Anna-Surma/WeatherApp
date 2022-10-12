package com.example.weatherapp.model

data class ModelClass(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val id: Int,
    val name: String
)
