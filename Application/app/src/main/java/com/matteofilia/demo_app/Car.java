package com.matteofilia.demo_app;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Car {

    @SerializedName("Manufacturer")
    private String manufacter;

    @SerializedName("FullModelName")
    private String model;

    @SerializedName("Year")
    private int year;

    @SerializedName("YearIntroduced")
    private int yearIntroduced;

    @SerializedName("ListPrice")
    private int listPrice;

    @SerializedName("Kilometres")
    private int kilometres;

    @SerializedName("EngineType")
    private String engineType;

    @SerializedName("PictureLink")
    private String pictureLink;

    @SerializedName("Layout")
    private String layout;

    public String getManufacter() {
        return manufacter;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return manufacter + " " + model;
    }

    public String getLongName() {
        return year + " " + manufacter + " " + model;
    }

    public int getYear() {
        return year;
    }

    public int getYearIntroduced() {
        return yearIntroduced;
    }

    public int getListPrice() {
        return listPrice;
    }

    public int getKilometres() {
        return kilometres;
    }

    public String getEngineType() {
        return engineType;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public String getLayout() {
        return layout;
    }
}
