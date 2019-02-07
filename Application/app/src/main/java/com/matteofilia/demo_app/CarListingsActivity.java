package com.matteofilia.demo_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarListingsActivity extends AppCompatActivity {

    // This URL refers to the machine running the emulator.
    public static final String BASE_URL = "http://10.0.2.2:3001/";
    public static final String API_BASE_URL = "http://10.0.2.2:3001/api/";

    public static final String LOG_TAG = "Matteo's Demo App";
    private static final int INTERNET_PERMISSION_CODE = 0;

    public static final String BUNDLE_KEY_MIN_PRICE = "MIN_PRICE";
    public static final String BUNDLE_KEY_MAX_PRICE = "MAX_PRICE";
    public static final String BUNDLE_KEY_MIN_YEAR = "MIN_YEAR";
    public static final String BUNDLE_KEY_MAX_YEAR = "MAX_YEAR";
    public static final int KEY_NOT_FOUND = -1;


    private ListView carListView;
    private CarAdapter adapter;
    private FloatingActionButton fab;

    CarServerAPI client;

    private final Context context = this;
    private Bundle in;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cars_listings);

        in = bundle;
        setupOkHttp();
        setupUI();
        loadCars();
    }

    private void setupOkHttp() {
        // Create okHttp Client
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        // Create retrofit instance with okHttp Client
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                );
        Retrofit retrofit = retrofitBuilder
                .client(okHttpClient)
                .build();

        // Create API client using retrofit
        client = retrofit.create(CarServerAPI.class);
    }

    private void setupUI() {
        setTitle(getString(R.string.app_name));

        carListView = findViewById(R.id.cars_list);

        // Create adapter for collection list
        adapter = new CarAdapter();
        carListView.setAdapter(adapter);

        // Setup fab to refine search
        final Context context = this;
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start search activity
                Intent intent = new Intent(context, CarSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadCars() {
        // Check if any parameters were passed
        Integer minPrice = null, maxPrice = null, minYear = null, maxYear = null;
        String manufacturers = null;

        if (getIntent() != null && getIntent().getExtras() != null) {
            Log.d(LOG_TAG, "Activity has intent, loading data...");

            Bundle bundle = getIntent().getExtras();
            minPrice = bundle.getInt(BUNDLE_KEY_MIN_PRICE, KEY_NOT_FOUND);
            maxPrice = bundle.getInt(BUNDLE_KEY_MAX_PRICE, KEY_NOT_FOUND);
            minYear = bundle.getInt(BUNDLE_KEY_MIN_YEAR, KEY_NOT_FOUND);
            maxYear = bundle.getInt(BUNDLE_KEY_MAX_YEAR, KEY_NOT_FOUND);

            // If bundle didn't contain the key, reset to null
            if (minPrice == KEY_NOT_FOUND) minPrice = null;
            if (maxPrice == KEY_NOT_FOUND) maxPrice = null;
            if (minYear == KEY_NOT_FOUND) minYear = null;
            if (maxYear == KEY_NOT_FOUND) maxYear= null;
        } else {
            Log.d(LOG_TAG, "Bundle is NULL");
        }

        Log.d(LOG_TAG, "Loading cars...");
        client.getCars(manufacturers, minPrice, maxPrice, minYear, maxYear).enqueue(new Callback<List<Car>>() {
             @Override
             public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                 Log.d(LOG_TAG, "Cars loaded");
                 if (response.body() != null) {
                     Log.d(LOG_TAG, "Updating adapter to reflect new data");
                     adapter.setCars(response.body());
                 } else {
                     Log.w(LOG_TAG, "Response from API call is null");
                     Log.w(LOG_TAG, "Response: " + response.toString());
                 }
             }

             @Override
             public void onFailure(Call<List<Car>> call, Throwable t) {
                 Log.w(LOG_TAG, "Retrofit call failed: " + t.getLocalizedMessage());
             }
         });
    }

    class CarAdapter extends BaseAdapter {

        List<Car> cars = new ArrayList<>();

        public List<Car> getCars() {
            return cars;
        }

        public void setCars(List<Car> cars) {
            this.cars.clear();
            this.cars.addAll(cars);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return cars.size();
        }

        @Override
        public Object getItem(int i) {
            return cars.get(i);
        }

        @Override
        public long getItemId(int i) {
            return cars.get(i).getLongName().hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View item;
            if (view == null) {
                // Inflate view
                LayoutInflater li = getLayoutInflater();
                item = li.inflate(R.layout.item_car, null);
            } else {
                // Reuse old view - no need to inflate new view
                item = view;
            }

            TextView name = item.findViewById(R.id.name);
            TextView kilometres = item.findViewById(R.id.kilometres);
            TextView price = item.findViewById(R.id.price);
            ImageView image = item.findViewById(R.id.image);

            // Set stuff
            Car car = cars.get(i);
            name.setText(car.getLongName());
            kilometres.setText(String.valueOf(car.getKilometres()) + getString(R.string.kilometres_label));
            price.setText("$" + car.getListPrice());

            // Load image using Glide
            Glide.with(context).load(BASE_URL + car.getPictureLink()).into(image);

            return item;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == INTERNET_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Internet permission granted");

            // Return to previous function now that we have internet permission to load data
            loadCars();
        }
    }
}
