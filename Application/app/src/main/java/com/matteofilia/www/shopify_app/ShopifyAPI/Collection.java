package com.matteofilia.www.shopify_app.ShopifyAPI;

import com.google.gson.annotations.SerializedName;

public class Collection {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("body_html")
    private String description;

    @SerializedName("image")
    private Image image;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }
}
