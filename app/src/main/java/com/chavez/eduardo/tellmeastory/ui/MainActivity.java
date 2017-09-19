package com.chavez.eduardo.tellmeastory.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.Categories;
import com.chavez.eduardo.tellmeastory.network.CategoriesRequestClient;
import com.chavez.eduardo.tellmeastory.network.GeneralStory;
import com.chavez.eduardo.tellmeastory.network.StoriesRequestClient;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.recyclerview.CategoriesAdapter;
import com.chavez.eduardo.tellmeastory.recyclerview.MainStoryAdapter;
import com.chavez.eduardo.tellmeastory.recyclerview.RecyclerViewItemListener;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecyclerViewItemListener, NavigationView.OnNavigationItemSelectedListener {

    /**
     * I'll declare and build the client here
     **/
    private StoriesRequestClient client = new Retrofit.Builder()
            .baseUrl(NetworkUtils.SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(StoriesRequestClient.class);

    private CategoriesRequestClient categoriesClient = new Retrofit.Builder()
            .baseUrl(NetworkUtils.SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(CategoriesRequestClient.class);

    /**
     * Just for debugging purposes
     **/
    private static String LOG_TAG = MainActivity.class.getSimpleName();


    /**
     * Butterknife and UI Components
     **/
    private Unbinder unbinder;

    @BindView(R.id.recyclerContentVertical)
    RecyclerView recyclerView;

    @BindView(R.id.recyclerCategoriesHorizontal)
    RecyclerView recyclerViewCategories;

    @BindView(R.id.refreshContainer)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getCategoriesData();

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGeneralStoriesData();
            }
        });

        refreshLayout.setRefreshing(true);
        getGeneralStoriesData();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }

    private void getCategoriesData() {
        Call<List<Categories>> call = categoriesClient.getAllCategories();

        call.enqueue(new Callback<List<Categories>>() {
            @Override
            public void onResponse(Call<List<Categories>> call, Response<List<Categories>> response) {
                if (response.isSuccessful()) {
                    workCategoriesResponse(response.body());
                } else {
                    Log.d(LOG_TAG, "ERROR" + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Categories>> call, Throwable t) {
                Log.d(LOG_TAG, t.getLocalizedMessage());
            }
        });

    }

    private void workCategoriesResponse(List<Categories> body) {
        Log.d(LOG_TAG, body.toString());
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategories.setAdapter(new CategoriesAdapter(this, body));
    }

    private void getGeneralStoriesData() {
        Call<List<GeneralStory>> call = client.getGeneralStories();
        call.enqueue(new Callback<List<GeneralStory>>() {
            @Override
            public void onResponse(Call<List<GeneralStory>> call, Response<List<GeneralStory>> response) {
                if (response.isSuccessful())
                    workResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<GeneralStory>> call, Throwable t) {

            }
        });
    }

    private void workResponse(List<GeneralStory> body) {
        Log.d(LOG_TAG, body.toString());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new MainStoryAdapter(body, this, this));
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onRecyclerViewItemClick(int pos, GeneralStory generalStory, ImageView sharedImage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(MainActivity.this, StoryDetailActivity.class);
            intent.putExtra(ConfigurationUtils.BUNDLE_MAIN_KEY, generalStory);
            intent.putExtra("transition", ViewCompat.getTransitionName(sharedImage));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                    sharedImage,
                    ViewCompat.getTransitionName(sharedImage));
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(MainActivity.this, StoryDetailActivity.class);
            intent.putExtra(ConfigurationUtils.BUNDLE_MAIN_KEY, generalStory);
            startActivity(intent);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Snackbar.make(getCurrentFocus(), "Pendiente", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            Snackbar.make(getCurrentFocus(), "Pendiente", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Snackbar.make(getCurrentFocus(), "Pendiente", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Snackbar.make(getCurrentFocus(), "Pendiente", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Snackbar.make(getCurrentFocus(), "Pendiente", Snackbar.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
