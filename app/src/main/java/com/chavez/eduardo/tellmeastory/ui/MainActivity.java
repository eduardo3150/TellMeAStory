package com.chavez.eduardo.tellmeastory.ui;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.GeneralStory;
import com.chavez.eduardo.tellmeastory.network.NetworkRequestClient;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.recyclerview.MainStoryAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    /**
     * I'll declare and build the client here
     **/
    private NetworkRequestClient client = new Retrofit.Builder()
            .baseUrl(NetworkUtils.SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(NetworkRequestClient.class);

    /**
     * Just for debugging purposes
     **/
    private static String LOG_TAG = MainActivity.class.getSimpleName();

    //Butterknife components
    private Unbinder unbinder;

    @BindView(R.id.recyclerContentVertical)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        getGeneralStoriesData();

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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new MainStoryAdapter(body,this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
