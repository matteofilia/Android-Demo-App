package com.matteofilia.www.shopify_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.matteofilia.www.shopify_app.ShopifyAPI.Collection;
import com.matteofilia.www.shopify_app.ShopifyAPI.Product;
import com.matteofilia.www.shopify_app.ShopifyAPI.ShopifyAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionDetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = "Shopify Challenge App";
    private static final int INTERNET_PERMISSION_CODE = 0;

    private long collectionID;
    private String collectionName;
    private String collectionImageURL;

    private ListView productsListView;
    private ProductsAdapter adapter;
    private List<Collection> products;

    ShopifyAPIClient client;

    private final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_collection_details);

        setupMisc(bundle);
        setupUI();
        loadCollectionProductData();
    }

    private void setupMisc(Bundle bundle) {
        // Load products for collection
        bundle = getIntent().getExtras();
        if (bundle != null) {
            collectionID = bundle.getLong(getString(R.string.extra_collection_id), 0);
            collectionName = bundle.getString(getString(R.string.extra_collection_title), "Error");
            collectionImageURL = bundle.getString(getString(R.string.extra_collection_image_url), "Error");
            Log.d(LOG_TAG, "Loading products for collection: " + collectionName + "("+collectionID+"), Image URL = "+collectionImageURL);
        } else {
            Log.w(LOG_TAG, "Bundle is NULL");
        }
        client = new ShopifyAPIClient();
    }

    private void setupUI() {
        setTitle(collectionName);

        productsListView = findViewById(R.id.products_list);

        // Create adapter for collection list
        adapter = new ProductsAdapter();
        adapter.setCollectionName(collectionName);
        adapter.setCollectionImageURL(collectionImageURL);
        productsListView.setAdapter(adapter);
    }

    private void loadCollectionProductData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            // Internet permission is ok, begin loading from internet
            Log.d(LOG_TAG, "Internet permission already granted");

            Log.d(LOG_TAG, "Loading collection products...");
            client.getCollectionProductsAsync(collectionID,0, new Callback<ShopifyAPIClient.CollectionProductsList>() {
                @Override
                public void onResponse(Call<ShopifyAPIClient.CollectionProductsList> call, Response<ShopifyAPIClient.CollectionProductsList> response) {
                    if (response.body() != null && response.body().getAllProductIDs() != null) {
                        Log.d(LOG_TAG, "Got collection products data. Proceeding to load products...");
                        long[] productIDs = response.body().getAllProductIDs();

                        // Load products from IDs
                        loadProductData(productIDs);
                    } else {
                        Log.w(LOG_TAG, "Response from API call is null");
                        Log.w(LOG_TAG, "Response: " + response.toString());
                    }
                }

                @Override
                public void onFailure(Call<ShopifyAPIClient.CollectionProductsList> call, Throwable t) {
                    Log.w(LOG_TAG, "Collection product loading failed");
                    Log.w(LOG_TAG, t.getMessage());
                }
            });
        } else {
            // Internet permission not granted, request and call this function again once granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
            Log.d(LOG_TAG, "Internet permission not granted. Requesting...");
        }
    }

    private void loadProductData(long[] productIDs) {
        Log.d(LOG_TAG, "Loading products...");
        client.getProductsAsync(productIDs, 0, new Callback<ShopifyAPIClient.ProductsList>() {
            @Override
            public void onResponse(Call<ShopifyAPIClient.ProductsList> call, Response<ShopifyAPIClient.ProductsList> response) {
                Log.d(LOG_TAG, "Products loaded");
                if (response.body() != null && response.body().getProducts() != null) {
                    Log.d(LOG_TAG, "Updating adapter to reflect new data");
                    adapter.setProducts(response.body().getProducts());
                } else {
                    Log.w(LOG_TAG, "Response from API call is null");
                    Log.w(LOG_TAG, "Response: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<ShopifyAPIClient.ProductsList> call, Throwable t) {
                // A perfect hero
                Log.w(LOG_TAG, "P L E A S E H A N G U P A N D T R Y A G A I N");
                Log.w(LOG_TAG, t.getMessage());
            }
        });
    }

    class CollectionProductsAdapter extends BaseAdapter {

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

    class ProductsAdapter extends BaseAdapter {

        ArrayList<Product> products = new ArrayList<>();
        String collectionName;
        String collectionImageURL;

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products.clear();
            this.products.addAll(products);
            notifyDataSetChanged();
        }

        public void setProducts(ArrayList<Product> products) {
            this.products = products;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }

        public String getCollectionImageURL() {
            return collectionImageURL;
        }

        public void setCollectionImageURL(String collectionImageURL) {
            this.collectionImageURL = collectionImageURL;
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int i) {
            return products.get(i);
        }

        @Override
        public long getItemId(int i) {
            return products.get(i).getId();
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
                item = li.inflate(R.layout.item_product, null);
            } else {
                // Reuse old view - no need to inflate new view
                item = view;
            }

            TextView collectionName = item.findViewById(R.id.collection_name);
            TextView productName = item.findViewById(R.id.product_name);
            TextView stock = item.findViewById(R.id.stock);
            TextView tags = item.findViewById(R.id.tags);
            ImageView collectionImage = item.findViewById(R.id.collection_image);

            // Set stuff
            Product product = products.get(i);
            collectionName.setText(this.collectionName);
            productName.setText(product.getTitle());
            tags.setText(product.getTags());
            stock.setText(String.valueOf(product.getStock()));

            // Load image using Glide
            Glide.with(context).load(collectionImageURL).into(collectionImage);

            return item;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == INTERNET_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Internet permission granted");

            // Return to previous function now that we have internet permission to load data
            loadCollectionProductData();
        }
    }
}
