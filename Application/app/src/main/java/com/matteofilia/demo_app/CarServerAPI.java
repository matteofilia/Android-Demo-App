package com.matteofilia.demo_app;

import com.matteofilia.demo_app.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CarServerAPI {

    @GET("getMakes")
    Call<List<String>> getMakes(@Query("access_token") String accessToken);

    @GET("getCars")
    Call<List<Car>> getCars(
            @Query("manufacturer") String manufacturer,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice,
            @Query("minYear") Integer minYear,
            @Query("maxYear") Integer maxYear);
}
