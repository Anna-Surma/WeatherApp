package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.POJO.ModelClass
import com.example.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //   supportActionBar?.hide()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.weatherCurLoc.observe(this) {
            if (it != null) {
                setDataOnViews(it)
            }
        }
        viewModel.pbLoading.observe(this) {
            if (it != null) {
                if(it){
                    binding.pbLoading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setDataOnViews(body: ModelClass?) {
    }
}