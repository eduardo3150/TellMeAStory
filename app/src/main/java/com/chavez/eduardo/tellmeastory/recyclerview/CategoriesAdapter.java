package com.chavez.eduardo.tellmeastory.recyclerview;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.Categories;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.chavez.eduardo.tellmeastory.network.NetworkUtils.IMG_BASE_URL;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private Context context;
    private List<Categories> categories;
    private RecyclerFilterListAdapter listAdapter;
    String BASE_URL;

    public CategoriesAdapter(Context context, List<Categories> categories, RecyclerFilterListAdapter adapter) {
        this.context = context;
        this.categories = categories;
        this.listAdapter = adapter;
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        BASE_URL = sharedPreferences.getString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.SERVICE_BASE_URL);
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_category_card, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, int position) {
        final Categories category = categories.get(position);

        holder.categoryLabel.setText(category.getCategoryName());
        Picasso.with(context).load(NetworkUtils.IMG_BASE_URL + category.getCategoryThumbnail()).into(holder.categoryPicture);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapter.onCategoryFilterAdater(category.getStories());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_category_label)
        TextView categoryLabel;

        @BindView(R.id.row_category_picture)
        ImageView categoryPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
