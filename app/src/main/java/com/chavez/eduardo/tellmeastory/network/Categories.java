package com.chavez.eduardo.tellmeastory.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class Categories implements Parcelable {

    @SerializedName("id")
    private int categoryId;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("category_thumbnail")
    private String categoryThumbnail;

    @SerializedName("category_description")
    private String categoryDescription;

    @SerializedName("stories")
    private List<GeneralStory> stories = new ArrayList<>();

    protected Categories(Parcel in) {
        categoryId = in.readInt();
        categoryName = in.readString();
        categoryThumbnail = in.readString();
        categoryDescription = in.readString();
        in.readList(stories,GeneralStory.class.getClassLoader());
    }

    public static final Creator<Categories> CREATOR = new Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryThumbnail() {
        return categoryThumbnail;
    }

    public List<GeneralStory> getStories() {
        return stories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryThumbnail='" + categoryThumbnail + '\'' +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", stories=" + stories +
                '}';
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(categoryId);
        parcel.writeString(categoryName);
        parcel.writeString(categoryThumbnail);
        parcel.writeString(categoryDescription);
        parcel.writeList(stories);
    }
}
