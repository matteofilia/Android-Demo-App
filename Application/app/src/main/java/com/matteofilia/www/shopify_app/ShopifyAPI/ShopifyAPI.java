package com.matteofilia.www.shopify_app.ShopifyAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ShopifyAPI {

    @GET("custom_collections.json")
    Call<ShopifyAPIClient.CollectionList> getCollections(@Query("page") int page, @Query("access_token") String accessToken);

    @GET("collects.json")
    Call<ShopifyAPIClient.CollectionProductsList> getCollectionProducts(@Query("collection_id") long collectionID, @Query("page") int page, @Query("access_token") String accessToken);

    @GET("products.json")
    Call<ShopifyAPIClient.ProductsList> getProducts(@Query("ids") String productIDs, @Query("page") int page, @Query("access_token") String accessToken);
}
