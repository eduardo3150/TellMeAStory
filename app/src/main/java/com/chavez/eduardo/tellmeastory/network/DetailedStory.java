package com.chavez.eduardo.tellmeastory.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class DetailedStory implements Parcelable {

    @SerializedName("id")
    private int idSection;

    @SerializedName("story_id")
    private int idStory;

    @SerializedName("author_id")
    private int authorId;

    @SerializedName("content_section_title")
    private String sectionTitle;

    @SerializedName("content_section_text")
    private String sectionText;

    @SerializedName("content_section_picture")
    private String sectionImage;

    protected DetailedStory(Parcel in) {
        idSection = in.readInt();
        idStory = in.readInt();
        authorId = in.readInt();
        sectionTitle = in.readString();
        sectionText = in.readString();
        sectionImage = in.readString();
    }

    public static final Creator<DetailedStory> CREATOR = new Creator<DetailedStory>() {
        @Override
        public DetailedStory createFromParcel(Parcel in) {
            return new DetailedStory(in);
        }

        @Override
        public DetailedStory[] newArray(int size) {
            return new DetailedStory[size];
        }
    };

    public int getIdSection() {
        return idSection;
    }

    public int getIdStory() {
        return idStory;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public String getSectionText() {
        return sectionText;
    }

    public String getSectionImage() {
        return sectionImage;
    }

    public int getAuthorId() {
        return authorId;
    }

    @Override
    public String toString() {
        return "DetailedStory{" +
                "idSection=" + idSection +
                ", idStory=" + idStory +
                ", authorId=" + authorId +
                ", sectionTitle='" + sectionTitle + '\'' +
                ", sectionText='" + sectionText + '\'' +
                ", sectionImage='" + sectionImage + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idSection);
        parcel.writeInt(idStory);
        parcel.writeInt(authorId);
        parcel.writeString(sectionTitle);
        parcel.writeString(sectionText);
        parcel.writeString(sectionImage);
    }
}
