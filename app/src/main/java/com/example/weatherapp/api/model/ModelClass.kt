package com.example.weatherapp.POJO

data class ModelClass(val weather:List<Weather>,
                      val basic:Basic,
                      val wind:Wind,
                      val sys:Sys,
                      val id:Int,
                      val name:String
                      )