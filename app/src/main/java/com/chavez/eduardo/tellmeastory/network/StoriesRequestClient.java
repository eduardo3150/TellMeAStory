package com.chavez.eduardo.tellmeastory.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public interface StoriesRequestClient {

    @GET("stories")
    Call<List<GeneralStory>> getGeneralStories();

    @GET("stories/{id}")
    Call<GeneralStory> getStory(@Path("id") String id);
}
