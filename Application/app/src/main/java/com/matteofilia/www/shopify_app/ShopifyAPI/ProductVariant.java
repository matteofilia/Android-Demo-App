package com.matteofilia.www.shopify_app.ShopifyAPI;

import com.google.gson.annotations.SerializedName;

public class ProductVariant {

    @SerializedName("inventory_quantity")
    private int inventoryQuantity;
    private String title;

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
