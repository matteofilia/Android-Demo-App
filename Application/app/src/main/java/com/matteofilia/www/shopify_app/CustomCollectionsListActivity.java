package com.matteofilia.www.shopify_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.matteofilia.www.shopify_app.ShopifyAPI.Collection;
import com.matteofilia.www.shopify_app.ShopifyAPI.ShopifyAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomCollectionsListActivity extends AppCompatActivity {

    public static final String LOG_TAG = "Shopify Challenge App";
    private static final int INTERNET_PERMISSION_CODE = 0;

    private ListView collectionListView;
    private CollectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_collections_list);

        setupUI();
        loadCollectionData();
    }

    private void setupUI() {
        collectionListView = findViewById(R.id.collection_list);

        // Create adapter for collection list
        adapter = new CollectionAdapter();
        collectionListView.setAdapter(adapter);

        // Set onItemSelected listener
        collectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Show products in collection when selected
                Log.v(LOG_TAG, "List view item selected = " + i);
                Collection collection = (Collection)adapterView.getAdapter().getItem(i);
                showProductsInCollection(collection);
            }
        });
    }

    private void loadCollectionData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            // Internet permission is ok, begin loading from internet
            Log.d(LOG_TAG, "Internet permission already granted");

            Log.d(LOG_TAG, "Loading collections...");
            ShopifyAPIClient client = new ShopifyAPIClient();
            client.getCollectionsAsync(0, new Callback<ShopifyAPIClient.CollectionList>() {
                @Override
                public void onResponse(Call<ShopifyAPIClient.CollectionList> call, Response<ShopifyAPIClient.CollectionList> response) {
                    Log.v(LOG_TAG, "Response: " + response.toString());

                    // Update adapter with new data
                    if (response.body() != null && response.body().getCollections() != null) {
                        Log.d(LOG_TAG, "Updating adapter to reflect new data");
                        adapter.setCollections(response.body().getCollections());
                    } else {
                        Log.w(LOG_TAG, "Response from API call is null");
                        Log.w(LOG_TAG, "Response: " + response.toString());
                    }
                }

                @Override
                public void onFailure(Call<ShopifyAPIClient.CollectionList> call, Throwable t) {
                    Log.w(LOG_TAG, "Collection loading failed");
                    Log.w(LOG_TAG, t.getMessage());
                }
            });
        } else {
            // Internet permission not granted, request and call this function again once granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
            Log.d(LOG_TAG, "Internet permission not granted. Requesting...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == INTERNET_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Internet permission granted");

            // Return to previous function now that we have internet permission to load data
            loadCollectionData();
        }
    }

    public void showProductsInCollection(Collection collection) {
        // Launch collection details activity
        Log.d(LOG_TAG, "Launching collection details activity to show products");
        Intent intent = new Intent(this, CollectionDetailsActivity.class);
        intent.putExtra(getString(R.string.extra_collection_id), collection.getId());
        intent.putExtra(getString(R.string.extra_collection_title), collection.getTitle());
        intent.putExtra(getString(R.string.extra_collection_image_url), collection.getImage().getSourceURL());
        startActivity(intent);
    }

    class CollectionAdapter extends BaseAdapter {

        ArrayList<Collection> collections = new ArrayList<>();

        public List<Collection> getCollections() {
            return collections;
        }

        public void setCollections(List<Collection> collections) {
            this.collections.clear();
            this.collections.addAll(collections);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return collections.size();
        }

        @Override
        public Object getItem(int i) {
            return collections.get(i);
        }

        @Override
        public long getItemId(int i) {
            return collections.get(i).getId();
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
                item = li.inflate(R.layout.item_collection, null);
            } else {
                // Reuse old view - no need to inflate new view
                item = view;
            }

            TextView collectionName = item.findViewById(R.id.collection_name);
            collectionName.setText(collections.get(i).getTitle());

            return item;
        }
    };
}
