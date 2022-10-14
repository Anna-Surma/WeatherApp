package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.api.Constants
import com.example.weatherapp.api.Constants.PERMISSION_REQUEST_ACCESS_LOCATION
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.ModelClass
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : AppCompatActivity(), Parcelable {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.rlMainLayout.visibility = View.GONE

        getCurrentLocation()

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this

        binding.etGetCityName.setOnEditorActionListener() { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.getCityWeather(binding.etGetCityName.text.toString(), Constants.API_KEY)
                val view = this.currentFocus

                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    binding.etGetCityName.clearFocus()
                }
                true
            } else false
        }

        viewModel.weatherCurLoc.observe(this) {
            if (it != null) {
                setDataOnViews(it)
            }
        }
        viewModel.pbLoading.observe(this) {
            if (it != null) {
                if (it) {
                    binding.pbLoading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.fetchCurrentLocationWeather(
                            location.latitude.toString(),
                            location.longitude.toString(), Constants.API_KEY
                        )
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(id: Int) {
        when (id) {
            //thunderstorm
            in 200..232 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor =
                    ContextCompat.getColor(applicationContext, R.color.thunderstorm)
                binding.rlToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.thunderstorm
                    )
                )
                binding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.thunderstrom_bg
                )
                binding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.thunderstrom_bg
                )
                binding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.thunderstrom_bg
                )
                binding.ivWeatherBg.setImageResource(R.drawable.thunderstrom_bg)
                binding.ivWeatherIcon.setImageResource(R.drawable.thunderstrom)
            }
            //drizzle
            in 300..321 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.drizzle)
                binding.rlToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.drizzle
                    )
                )
                binding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.drizzle_bg
                )
                binding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.drizzle_bg
                )
                binding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.drizzle_bg
                )
                binding.ivWeatherBg.setImageResource(R.drawable.drizzle_bg)
                binding.ivWeatherIcon.setImageResource(R.drawable.drizzle)
            }
            //rainy
            in 500..531 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.rain)
                binding.rlToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.rain
                    )
                )
                binding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.rainy_bg
                )
                binding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.rainy_bg
                )
                binding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.rainy_bg
                )
                binding.ivWeatherBg.setImageResource(R.drawable.rainy_bg)
                binding.ivWeatherIcon.setImageResource(R.drawable.rain)
            }
            //snow
            in 600..620 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.snow)
                binding.rlToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.snow
                    )
                )
                binding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.snow_bg
                )
                binding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.snow_bg
                )
                binding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.snow_bg
                )
                binding.ivWeatherBg.setImageResource(R.drawable.snow_bg)
                binding.ivWeatherIcon.setImageResource(R.drawable.snow)
            }
            //mist
            in 701..781 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor =
                    ContextCompat.getColor(applicationContext, R.color.atmosphere)
                binding.rlToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.atmosphere
                    )
                )
                binding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.mist_bg
                )
                binding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.mist_bg
                )
                binding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.mist_bg
                )
                binding.ivWeatherBg.setImageResource(R.drawable.mist_bg)
                binding.ivWeatherIcon.setImageResource(R.drawable.mist)
            }
            //clear
            800 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.clear)
                binding.rlToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.clear
                    )
                )
                binding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear_bg
                )
                binding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear_bg
                )
                binding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear_bg
                )
                binding.ivWeatherBg.setImageResource(R.drawable.clear_bg)
                binding.ivWeatherIcon.setImageResource(R.drawable.clear)
            }
            //clouds
            else -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.clouds)
                binding.rlToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.clouds
                    )
                )
                binding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clouds_bg
                )
                binding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clouds_bg
                )
                binding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clouds_bg
                )
                binding.ivWeatherBg.setImageResource(R.drawable.clouds_bg)
                binding.ivWeatherIcon.setImageResource(R.drawable.clouds)
            }
        }

        binding.pbLoading.visibility = View.GONE
        binding.rlMainLayout.visibility = View.VISIBLE
    }

    private fun setDataOnViews(body: ModelClass?) {
        viewModel.getCurrentDate(body)
        binding.etGetCityName.setText(body!!.name)
        updateUI(body.weather[0].id)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
}