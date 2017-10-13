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

public class GeneralStory implements Parcelable {

    @SerializedName("id")
    private int idStory;

    @SerializedName("story_name")
    private String storyName;

    @SerializedName("author_id")
    private int authorId;

    @SerializedName("status_id")
    private int statusId;

    @SerializedName("story_thumbnail")
    private String storyThumbnail;

    @SerializedName("content")
    private List<DetailedStory> detailedStories = new ArrayList<>();


    protected GeneralStory(Parcel in) {
        idStory = in.readInt();
        storyName = in.readString();
        authorId = in.readInt();
        statusId = in.readInt();
        storyThumbnail = in.readString();
        in.readList(detailedStories, DetailedStory.class.getClassLoader());
    }

    public static final Creator<GeneralStory> CREATOR = new Creator<GeneralStory>() {
        @Override
        public GeneralStory createFromParcel(Parcel in) {
            return new GeneralStory(in);
        }

        @Override
        public GeneralStory[] newArray(int size) {
            return new GeneralStory[size];
        }
    };

    public int getIdStory() {
        return idStory;
    }

    public String getStoryName() {
        return storyName;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getStatusId() {
        return statusId;
    }

    public List<DetailedStory> getDetailedStories() {
        return detailedStories;
    }

    public String getStoryThumbnail() {
        return storyThumbnail;
    }


    @Override
    public String toString() {
        return "GeneralStory{" +
                "idStory=" + idStory +
                ", storyName='" + storyName + '\'' +
                ", authorId=" + authorId +
                ", statusId=" + statusId +
                ", storyThumbnail='" + storyThumbnail + '\'' +
                ", detailedStories=" + detailedStories +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idStory);
        parcel.writeString(storyName);
        parcel.writeInt(authorId);
        parcel.writeInt(statusId);
        parcel.writeString(storyThumbnail);
        parcel.writeList(detailedStories);
    }
}
