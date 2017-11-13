package com.chavez.eduardo.tellmeastory.ui;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.Categories;
import com.chavez.eduardo.tellmeastory.network.CategoriesRequestClient;
import com.chavez.eduardo.tellmeastory.network.GeneralStory;
import com.chavez.eduardo.tellmeastory.network.StoriesRequestClient;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.recyclerview.CategoriesAdapter;
import com.chavez.eduardo.tellmeastory.recyclerview.MainStoryAdapter;
import com.chavez.eduardo.tellmeastory.recyclerview.RecyclerFilterListAdapter;
import com.chavez.eduardo.tellmeastory.recyclerview.RecyclerViewItemListener;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecyclerViewItemListener, NavigationView.OnNavigationItemSelectedListener, RecyclerFilterListAdapter {


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

    @BindView(R.id.empty_message)
    TextView emptyMessage;

    @BindView(R.id.adView)
    AdView adView;

    private MainStoryAdapter storyAdapter;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        /**
         * I'll declare and build the client here
         **/
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);

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
        storyAdapter = new MainStoryAdapter(this, this);
        getGeneralStoriesData();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        Toast.makeText(this, "Hello! " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_category) {
            getCategoriesData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCategoriesData() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        String BASE_URL = sharedPreferences.getString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.SERVICE_BASE_URL);
        CategoriesRequestClient categoriesClient = new Retrofit.Builder()
                .baseUrl(NetworkUtils.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(CategoriesRequestClient.class);

        Call<List<Categories>> call = categoriesClient.getAllCategories();

        call.enqueue(new Callback<List<Categories>>() {
            @Override
            public void onResponse(Call<List<Categories>> call, Response<List<Categories>> response) {
                if (response.isSuccessful()) {
                    workCategoriesResponse(response.body());
                } else {
                    //Log.d(LOG_TAG, "ERROR" + response.message());
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Atencion")
                            .setMessage("No se puede completar la solicitud")
                            .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getGeneralStoriesData();
                                }
                            })
                            .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.this.finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Categories>> call, Throwable t) {
                //Log.d(LOG_TAG, t.getLocalizedMessage());
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Atencion")
                        .setMessage("No se puede completar la solicitud")
                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getCategoriesData();
                            }
                        })
                        .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        })
                        .show();
            }
        });

    }

    private void workCategoriesResponse(List<Categories> body) {
        Log.d(LOG_TAG, body.toString());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right);
        recyclerViewCategories.setLayoutAnimation(animation);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategories.setAdapter(new CategoriesAdapter(this, body, this));
        recyclerViewCategories.setNestedScrollingEnabled(false);
    }

    private void getGeneralStoriesData() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        String BASE_URL = sharedPreferences.getString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.SERVICE_BASE_URL);
        StoriesRequestClient client = new Retrofit.Builder()
                .baseUrl(NetworkUtils.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(StoriesRequestClient.class);

        Call<List<GeneralStory>> call = client.getGeneralStories();
        call.enqueue(new Callback<List<GeneralStory>>() {
            @Override
            public void onResponse(Call<List<GeneralStory>> call, Response<List<GeneralStory>> response) {
                if (response.isSuccessful()) {
                    workResponse(response.body());
                } else {
                    refreshLayout.setRefreshing(false);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Atencion")
                            .setMessage("No se puede completar la solicitud")
                            .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getGeneralStoriesData();
                                }
                            })
                            .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.this.finish();
                                }
                            })
                            .show();

                }
            }

            @Override
            public void onFailure(Call<List<GeneralStory>> call, Throwable t) {
                //Log.d(LOG_TAG,t.getLocalizedMessage());
                refreshLayout.setRefreshing(false);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Atencion")
                        .setMessage("No se puede completar la solicitud")
                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getGeneralStoriesData();
                            }
                        })
                        .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void workResponse(List<GeneralStory> body) {
        //Log.d(LOG_TAG, body.toString());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_animation_bottom);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        storyAdapter.swap(body);
        recyclerView.setAdapter(storyAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        if (body.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onRecyclerViewItemClick(int pos, int storyId, ImageView sharedImage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(MainActivity.this, StoryDetailActivity.class);
            intent.putExtra(ConfigurationUtils.BUNDLE_MAIN_KEY, storyId);
            intent.putExtra("transition", ViewCompat.getTransitionName(sharedImage));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                    sharedImage,
                    ViewCompat.getTransitionName(sharedImage));
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(MainActivity.this, StoryDetailActivity.class);
            intent.putExtra(ConfigurationUtils.BUNDLE_MAIN_KEY, storyId);
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

            signOut();

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

    @Override
    public void onCategoryFilterAdater(List<GeneralStory> generalStories) {
        if (generalStories != null) {
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_animation_bottom);
            recyclerView.setLayoutAnimation(animation);
            storyAdapter.swap(generalStories);
            if (generalStories.isEmpty()) {
                emptyMessage.setVisibility(View.VISIBLE);
            } else {
                emptyMessage.setVisibility(View.GONE);
            }
        }
    }

    private void signOut() {
        // Firebase sign out
        auth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MainActivity.this, LoginTemp.class));
                        MainActivity.this.finish();
                    }
                });
    }
}
