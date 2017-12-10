package com.chavez.eduardo.tellmeastory.recyclerview;

import android.widget.ImageView;

import com.chavez.eduardo.tellmeastory.network.GeneralStory;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public interface RecyclerViewItemListener {

    void onRecyclerViewItemClick(int pos, int storyId, ImageView sharedImage);
}
