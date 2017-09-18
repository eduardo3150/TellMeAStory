package com.chavez.eduardo.tellmeastory.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.GeneralStory;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class MainStoryAdapter extends RecyclerView.Adapter<MainStoryAdapter.ViewHolder> {

    private List<GeneralStory> generalStories;
    private Context context;
    private RecyclerViewItemListener listener;

    public MainStoryAdapter(List<GeneralStory> generalStories, Context context, RecyclerViewItemListener listener) {
        this.generalStories = generalStories;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_empty_card, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GeneralStory story = generalStories.get(position);
        holder.story_title.setText(story.getStoryName());
        Picasso.with(context).load(story.getStoryThumbnail()).into(holder.story_thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecyclerViewItemClick(holder.getAdapterPosition(), story, holder.story_thumbnail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return generalStories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_story_thumbnail)
        ImageView story_thumbnail;

        @BindView(R.id.row_story_title)
        TextView story_title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
