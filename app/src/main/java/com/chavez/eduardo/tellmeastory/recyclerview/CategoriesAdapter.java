package com.chavez.eduardo.tellmeastory.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.Categories;
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

    public CategoriesAdapter(Context context, List<Categories> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_category_card, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, int position) {
        Categories category = categories.get(position);

        holder.categoryLabel.setText(category.getCategoryName());
        Picasso.with(context).load(IMG_BASE_URL+category.getCategoryThumbnail()).into(holder.categoryPicture);
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
