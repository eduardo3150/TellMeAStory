package com.chavez.eduardo.tellmeastory.recyclerview;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.GeneralStory;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class MainStoryAdapter extends RecyclerView.Adapter<MainStoryAdapter.ViewHolder> implements Filterable {

    private List<GeneralStory> generalStoriesFilter = new ArrayList<>();
    private List<GeneralStory> generalStories = new ArrayList<>();
    private Context context;
    private RecyclerViewItemListener listener;
    String BASE_URL;

    public MainStoryAdapter(Context context, RecyclerViewItemListener listener) {
        //this.generalStoriesFilter = generalStoriesFilter;
        this.context = context;
        this.listener = listener;
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        BASE_URL = sharedPreferences.getString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.SERVICE_BASE_URL);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_empty_card, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final GeneralStory story = generalStoriesFilter.get(position);

        holder.story_title.setText(story.getStoryName());
        Picasso.with(context).load(NetworkUtils.IMG_BASE_URL+story.getStoryThumbnail()).into(holder.story_thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecyclerViewItemClick(holder.getAdapterPosition(), story.getIdStory(), holder.story_thumbnail);
            }
        });

        holder.story_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(holder.story_menu, position);
            }
        });

    }

    public void swap(List<GeneralStory> list){
        if (generalStoriesFilter != null) {
            generalStoriesFilter.clear();
            generalStoriesFilter.addAll(list);
            generalStories.clear();
            generalStories.addAll(list);
        }
        else {
            generalStoriesFilter = list;
            generalStories = list;
        }
        notifyDataSetChanged();
    }

    private void showMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_story, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, view));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return generalStoriesFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    generalStoriesFilter = generalStories;
                } else {
                    ArrayList<GeneralStory> filteredList = new ArrayList<>();
                    for (GeneralStory d : generalStories) {
                        if (d.getStoryName().toLowerCase().contains(charSequence)) {
                            filteredList.add(d);
                        }
                    }
                    generalStoriesFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = generalStoriesFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                generalStoriesFilter = (ArrayList<GeneralStory>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_story_thumbnail)
        ImageView story_thumbnail;

        @BindView(R.id.row_story_title)
        TextView story_title;

        @BindView(R.id.row_menu_trigger)
        ImageView story_menu;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        private View view;

        public MyMenuItemClickListener(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_add_favourite:
                    Snackbar.make(view, String.format(Locale.ENGLISH, "%1$d Agregado a favoritos", position), Snackbar.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Snackbar.make(view, "Editar", Snackbar.LENGTH_SHORT).show();
                    return true;
                default:
            }

            return false;
        }
    }
}
