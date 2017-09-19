package com.chavez.eduardo.tellmeastory.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public interface CategoriesRequestClient {
    @GET("1675778/raw")
    Call<List<Categories>> getAllCategories();
}
