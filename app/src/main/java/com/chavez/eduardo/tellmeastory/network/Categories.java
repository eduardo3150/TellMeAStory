package com.chavez.eduardo.tellmeastory.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class Categories implements Serializable {

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("category_thumbnail")
    private String categoryThumbnail;

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryThumbnail() {
        return categoryThumbnail;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryThumbnail='" + categoryThumbnail + '\'' +
                '}';
    }
}
