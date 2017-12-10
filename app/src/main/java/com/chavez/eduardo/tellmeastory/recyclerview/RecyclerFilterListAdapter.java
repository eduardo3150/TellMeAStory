package com.chavez.eduardo.tellmeastory.recyclerview;

import com.chavez.eduardo.tellmeastory.network.GeneralStory;

import java.util.List;

/**
 * Created by Eduardo on 13/10/2017.
 */

public interface RecyclerFilterListAdapter {
    void onCategoryFilterAdater(List<GeneralStory> generalStories);
}
