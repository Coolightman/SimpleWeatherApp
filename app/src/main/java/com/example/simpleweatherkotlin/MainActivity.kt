package com.example.simpleweatherkotlin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val apiUrl = "https://api.openweathermap.org/data/2.5/weather?"
    private var currentCity = "Гродно"
    private val keyApi = "&appid=7c32db9bff01f94b3141484a50407d6b"
    private val unitsApi = "&units=metric"
    private val langApi = "&lang=ru"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        showWeather()
        listeners()
    }

    private fun listeners() {
        buttonShowWeather.setOnClickListener {
            currentCity = editTextLocation.text.toString().trim()
            if (currentCity.isNotEmpty()) showWeather()
        }
    }

    private fun showWeather() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val weatherJson = getWeatherJson()
            val city = getCity(weatherJson)
            val temp = getTemp(weatherJson)
            val pressure = getPressure(weatherJson)
            val humidity = getHumidity(weatherJson)
            val description: String = getDescription(weatherJson)

            handler.post {
                textViewCity.text = city
                setTempView(temp)
                setPressureView(pressure)
                setHumidityView(humidity)
                setDescriptionView(description)
            }
        }
    }

    private fun setDescriptionView(description: String) {
        val textDescription = description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        textViewDescription.text = textDescription
    }

    private fun setHumidityView(humidity: String) {
        var textHumidity = ""
        if (humidity.isNotEmpty()) {
            textHumidity = "$humidity %"
        }
        textViewHumidity.text = textHumidity
    }

    private fun setPressureView(pressure: String) {
        var textPressure = ""
        if (pressure.isNotEmpty()) {
            textPressure = "$pressure мм рт.ст."
        }
        textViewPressure.text = textPressure
    }

    private fun setTempView(temp: String) {
        var textTemp = ""
        if (temp.isNotEmpty()) {
            val cutTemp = temp.toFloat().toInt()
            textTemp = "$cutTemp \u2103"
        }
        textViewTemp.text = textTemp
    }

    private fun getDescription(weatherJson: JSONObject?): String {
        var description = ""
        weatherJson?.let {
            description = it.getJSONArray("weather")
                .getJSONObject(0)
                .getString("description")
        }
        return description
    }

    private fun getHumidity(weatherJson: JSONObject?): String {
        var humidity = ""
        weatherJson?.let {
            humidity = it.getJSONObject("main").getString("humidity")
        }
        return humidity
    }

    private fun getPressure(weatherJson: JSONObject?): String {
        var pressure = ""
        weatherJson?.let {
            pressure = it.getJSONObject("main").getString("pressure")
        }
        return pressure
    }

    private fun getTemp(weatherJson: JSONObject?): String {
        var temp = ""
        weatherJson?.let {
            temp = it.getJSONObject("main").getString("temp")
        }
        return temp
    }

    private fun getCity(weatherJson: JSONObject?): String {
        var city = getString(R.string.text_view_city_not)
        weatherJson?.let {
            city = it.getString("name")
        }
        return city
    }

    private fun getWeatherJson(): JSONObject? {
        val cityToRequest = "q=$currentCity"
        val urlLink = apiUrl + cityToRequest + keyApi + unitsApi + langApi
        val weatherJson: JSONObject? = try {
            val url = URL(urlLink)
            val text = url.readText()
            JSONObject(text)
        } catch (e: Exception) {
            Log.e("connection", "Bad connection")
            null
        }
        return weatherJson
    }
}