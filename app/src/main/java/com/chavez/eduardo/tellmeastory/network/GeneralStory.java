package com.chavez.eduardo.tellmeastory.network;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class GeneralStory implements Serializable {

    @SerializedName("id_story")
    private int idStory;

    @SerializedName("story_name")
    private String storyName;

    @SerializedName("story_author")
    private int storyAuthor;

    @SerializedName("approved")
    private boolean approved;

    @SerializedName("story_thumbnail")
    private String storyThumbnail;

    @SerializedName("story_content")
    private List<DetailedStory> detailedStories;

    public int getIdStory() {
        return idStory;
    }

    public String getStoryName() {
        return storyName;
    }

    public int getStoryAuthor() {
        return storyAuthor;
    }

    public boolean isApproved() {
        return approved;
    }

    public List<DetailedStory> getDetailedStories() {
        return detailedStories;
    }

    public String getStoryThumbnail() {
        return storyThumbnail;
    }




    @Override
    public String toString() {
        return "\nGeneralStory{" +
                "idStory=" + idStory +
                ", storyName='" + storyName + '\'' +
                ", storyAuthor=" + storyAuthor +
                ", approved=" + approved +
                ", storyThumbnail='" + storyThumbnail + '\'' +
                ", detailedStories=" + detailedStories +
                '}';
    }
}
