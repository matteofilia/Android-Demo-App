package com.matteofilia.demo_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import static com.matteofilia.demo_app.CarListingsActivity.BUNDLE_KEY_MAX_PRICE;
import static com.matteofilia.demo_app.CarListingsActivity.BUNDLE_KEY_MAX_YEAR;
import static com.matteofilia.demo_app.CarListingsActivity.BUNDLE_KEY_MIN_PRICE;
import static com.matteofilia.demo_app.CarListingsActivity.BUNDLE_KEY_MIN_YEAR;
import static com.matteofilia.demo_app.CarListingsActivity.KEY_NOT_FOUND;

public class CarSearchActivity extends AppCompatActivity {

    private EditText minPrice;
    private EditText maxPrice;
    private EditText minYear;
    private EditText maxYear;

    @Override
    protected void onCreate(@Nullable Bundle in) {
        super.onCreate(in);
        setContentView(R.layout.activity_search);

        setupUI();
        if (in != null) loadSavedInstanceState(in);
    }

    private void setupUI() {
        minPrice = findViewById(R.id.minPrice);
        maxPrice = findViewById(R.id.maxPrice);
        minYear = findViewById(R.id.minYear);
        maxYear = findViewById(R.id.maxYear);
    }

    private void loadSavedInstanceState(Bundle in) {
        minPrice.setText(String.valueOf(in.getInt(BUNDLE_KEY_MIN_PRICE, KEY_NOT_FOUND)));
        maxPrice.setText(String.valueOf(in.getInt(BUNDLE_KEY_MAX_PRICE, KEY_NOT_FOUND)));
        minYear.setText(String.valueOf(in.getInt(BUNDLE_KEY_MIN_YEAR, KEY_NOT_FOUND)));
        maxYear.setText(String.valueOf(in.getInt(BUNDLE_KEY_MAX_YEAR, KEY_NOT_FOUND)));

        // If bundle didn't contain the key, reset to null
        if (minPrice.getText().toString().equals(String.valueOf(KEY_NOT_FOUND))) minPrice.setText("");
        if (maxPrice.getText().toString().equals(String.valueOf(KEY_NOT_FOUND))) maxPrice.setText("");
        if (minYear.getText().toString().equals(String.valueOf(KEY_NOT_FOUND))) minYear.setText("");
        if (maxYear.getText().toString().equals(String.valueOf(KEY_NOT_FOUND))) maxYear.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save user inputs
        if (!minPrice.getText().toString().equals("")) {
            outState.putInt(BUNDLE_KEY_MIN_PRICE, Integer.parseInt(minPrice.getText().toString()));
        }
        if (!maxPrice.getText().toString().equals("")) {
            outState.putInt(BUNDLE_KEY_MAX_PRICE, Integer.parseInt(maxPrice.getText().toString()));
        }
        if (!minYear.getText().toString().equals("")) {
            outState.putInt(BUNDLE_KEY_MIN_YEAR, Integer.parseInt(minYear.getText().toString()));
        }
        if (!maxYear.getText().toString().equals("")) {
            outState.putInt(BUNDLE_KEY_MAX_YEAR, Integer.parseInt(maxYear.getText().toString()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_confirm) {
            // Add data to bundle
            Intent intent = new Intent(this, CarListingsActivity.class);

            try {
                if (!minPrice.getText().toString().equals("")) {
                    intent.putExtra(BUNDLE_KEY_MIN_PRICE, Integer.parseInt(minPrice.getText().toString()));
                }
                if (!maxPrice.getText().toString().equals("")) {
                    intent.putExtra(BUNDLE_KEY_MAX_PRICE, Integer.parseInt(maxPrice.getText().toString()));
                }
                if (!minYear.getText().toString().equals("")) {
                    intent.putExtra(BUNDLE_KEY_MIN_YEAR, Integer.parseInt(minYear.getText().toString()));
                }
                if (!maxYear.getText().toString().equals("")) {
                    intent.putExtra(BUNDLE_KEY_MAX_YEAR, Integer.parseInt(maxYear.getText().toString()));
                }
            } catch (NumberFormatException e) {

                // User entered invalid input, do not launch intent
                Toast toast = Toast.makeText(this, "Input invalid", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }

            // Start new activity
            startActivity(intent);
        }

        return true;
    }
}
