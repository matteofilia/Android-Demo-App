package com.matteofilia.www.shopify_app.ShopifyAPI;

import com.google.gson.annotations.SerializedName;

public class Image {
    private int width;
    private int height;

    @SerializedName("src")
    private String sourceURL;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSourceURL(String source) {
        this.sourceURL = source;
    }
}
