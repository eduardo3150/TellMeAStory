package com.chavez.eduardo.tellmeastory.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class DetailedStory implements Serializable {

    @SerializedName("id_section")
    private int idSection;

    @SerializedName("id_story")
    private int idStory;

    @SerializedName("section_title")
    private String sectionTitle;

    @SerializedName("section_text")
    private String sectionText;

    @SerializedName("section_image")
    private String sectionImage;

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

    @Override
    public String toString() {
        return "\nDetailedStory{" +
                "idSection=" + idSection +
                ", idStory=" + idStory +
                ", sectionTitle='" + sectionTitle + '\'' +
                ", sectionText='" + sectionText + '\'' +
                ", sectionImage='" + sectionImage + '\'' +
                '}';
    }
}
