package com.matteofilia.www.shopify_app.ShopifyAPI;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopifyAPIClient {

    public static final String API_BASE_URL = "https://shopicruit.myshopify.com/admin/";
    public static final String API_ACCESS_TOKEN = "c32313df0d0ef512ca64d5b336a0d7c6";

    private final ShopifyAPI client;

    public ShopifyAPIClient() {
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

        // Create Shopify API client using retrofit
        client =  retrofit.create(ShopifyAPI.class);
    }

    public void getCollectionsAsync(int page, Callback<CollectionList> callback) {
        // Create asynchronous request to API
        Call<CollectionList> call = client.getCollections(page, API_ACCESS_TOKEN);
        call.enqueue(callback);
    }

    public void getCollectionProductsAsync(long collectionID, int page, Callback<CollectionProductsList> callback) {
        // Create asynchronous request to API
        Call<CollectionProductsList> call = client.getCollectionProducts(collectionID, page, API_ACCESS_TOKEN);
        call.enqueue(callback);
    }

    public void getProductsAsync(long[] productIDs, int page, Callback<ProductsList> callback) {
        // Turn array of product IDs into String query
        String query = String.valueOf(productIDs[0]);
        for (int i = 1; i < productIDs.length; i++) {
            query = query+","+String.valueOf(productIDs[1]);
        }

        // Create asynchronous request to API
        Call<ProductsList> call = client.getProducts(query, page, API_ACCESS_TOKEN);
        call.enqueue(callback);
    }

    public class CollectionList {

        @SerializedName("custom_collections")
        private List<Collection> collections;

        public List<Collection> getCollections() {
            return collections;
        }
    }

    public class CollectionProductsList {

        @SerializedName("collects")
        private List<CollectionProduct> collectionProducts;

        public long[] getAllProductIDs() {

            long[] productIDs = new long[collectionProducts.size()];
            for (int i = 0; i < collectionProducts.size(); i++) {
                productIDs[i] = collectionProducts.get(i).getProductID();
            }

            return productIDs;
        }
    }

    public class ProductsList {

        @SerializedName("products")
        private List<Product> products;

        public List<Product> getProducts() {
            return products;
        }
    }

    public class CollectionProduct {

        @SerializedName("product_id")
        private long productID;

        @SerializedName("collection_id")
        private long collectionID;

        public long getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public long getCollectionID() {
            return collectionID;
        }

        public void setCollectionID(int collectionID) {
            this.collectionID = collectionID;
        }
    }

}
