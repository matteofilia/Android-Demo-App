package com.matteofilia.www.shopify_app.ShopifyAPI;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    private long collectionID;
    private long id;

    @SerializedName("title")
    private String title;
    private String vendor;
    private String tags;
    private List<ProductVariant> variants;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public long getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(long collectionID) {
        this.collectionID = collectionID;
    }

    public int getStock() {
        int totalStock = 0;
        for (ProductVariant v : variants) {
            totalStock += v.getInventoryQuantity();
        }
        return totalStock;
    }
}
