package com.matteofilia.demo_app

import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerUnitTests {

    private val API_BASE_URL = "http://www.matteofilia.com:3001/api/"
    private var client : CarServerAPI? = null

    private fun setupOkHttp() {
        // Create okHttp Client
        val okHttpClientBuilder = OkHttpClient.Builder()
        val okHttpClient = okHttpClientBuilder.build()

        // Create retrofit instance with okHttp Client
        val retrofitBuilder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
        val retrofit = retrofitBuilder
                .client(okHttpClient)
                .build()

        // Create API client using retrofit
        client = retrofit.create(CarServerAPI::class.java)
    }

    @Before
    fun setup() {
        setupOkHttp()
    }

    @Test
    fun check_connection() {

        // Check that we can connect to the server and that it returns a non-empty result
        var cars : List<Car>?

        client!!.getCars(null, null, null, null, null).enqueue(object : retrofit2.Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                if (response.body() != null) {
                    cars = response.body()

                    assertEquals(cars!!.isEmpty(), false)
                } else {
                    throw Error()
                }
            }

            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                throw Error()
            }
        })
    }
}