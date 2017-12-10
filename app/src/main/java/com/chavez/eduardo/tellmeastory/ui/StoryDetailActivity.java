package com.chavez.eduardo.tellmeastory.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.DetailedStory;
import com.chavez.eduardo.tellmeastory.network.GeneralStory;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.network.StoriesRequestClient;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.chavez.eduardo.tellmeastory.utils.SpeakRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.chavez.eduardo.tellmeastory.network.NetworkUtils.IMG_BASE_URL;
import static com.chavez.eduardo.tellmeastory.network.NetworkUtils.SERVICE_BASE_URL;

public class StoryDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container)
    ViewPager mViewPager;

    //private GeneralStory story;

    private static final String LOG_TAG = StoryDetailActivity.class.getSimpleName();

    Bundle extras;

    @BindView(R.id.tab_header)
    ImageView headerReceived;

    Unbinder unbinder;



    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewGroupContainer)
    ViewGroup viewGroup;
    //GOOGLE TTS
    private SpeakRequest speakRequest;
    private int whereAmISpeaking = 0;
    private boolean rotated = false;
    private boolean firstAppearTTS = true;
    private boolean canTransform = false;

    private int storyId = 0;

    @BindView(R.id.pager_counter)
    TextView pager_counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        unbinder = ButterKnife.bind(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (getIntent().getExtras() != null) {
            extras = getIntent().getExtras();
            storyId = extras.getInt(ConfigurationUtils.BUNDLE_MAIN_KEY);
            askForDetails(storyId);

        }



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        speakRequest = new SpeakRequest(this);
        listenToFinishedText();
        fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_headset_24dp, null));

        appBarLayout.addOnOffsetChangedListener(this);
    }

    private void askForDetails(int storyId) {
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        String BASE_URL = sharedPreferences.getString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.SERVICE_BASE_URL);
        StoriesRequestClient client = new Retrofit.Builder()
                .baseUrl(NetworkUtils.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(StoriesRequestClient.class);
        Call<GeneralStory> call = client.getStory(String.valueOf(storyId));
        call.enqueue(new retrofit2.Callback<GeneralStory>() {
            @Override
            public void onResponse(Call<GeneralStory> call, Response<GeneralStory> response) {
                if (response.isSuccessful()) {
                    workResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<GeneralStory> call, Throwable t) {

            }
        });
    }

    private void workResponse(final GeneralStory body) {
        Log.d(LOG_TAG, body.toString());
        toolbar.setTitle(body.getStoryName());
        generateHeader(body);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        if (body.getDetailedStories().isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Atencion")
                    .setCancelable(false)
                    .setMessage("Es necesario que la historia poseea secciones, porfavor contacta un administrador para verificar esta historia")
                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            askForDetails(storyId);
                        }
                    })
                    .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            StoryDetailActivity.this.finish();
                        }
                    })
                    .show();
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (firstAppearTTS) {
                        firstAppearTTS = false;
                        Snackbar.make(view, "Reproduccion lista", Snackbar.LENGTH_LONG)
                                .show();
                        rotated = true;
                        animateButtons();
                    } else {
                        if (body != null && !speakRequest.isSpeaking() && rotated) {
                            speakRequest.speak(body.getDetailedStories().get(whereAmISpeaking).getSectionText());
                            rotated = false;
                            animateButtons();

                            Snackbar.make(view, "Reproduciendo", Snackbar.LENGTH_LONG)
                                    .setAction("Detener", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (speakRequest.isSpeaking()) {
                                                speakRequest.stopSpeak();
                                                rotated = true;
                                                animateButtons();
                                            }
                                        }
                                    }).show();


                        } else {
                            speakRequest.stopSpeak();
                            rotated = true;
                            animateButtons();
                            Snackbar.make(view, "Detenido", Snackbar.LENGTH_LONG)
                                    .show();
                        }

                    }
                }
            });

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), body.getDetailedStories());

            setUpViewPager(mSectionsPagerAdapter, body);
        }
    }

    private void listenToFinishedText() {
        speakRequest.getTextToSpeech().setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rotated = true;
                        animateButtonsAfterSpeak();
                    }
                });
            }

            @Override
            public void onError(String s) {

            }
        });
    }

    private void animateButtonsAfterSpeak() {
        ObjectAnimator.ofFloat(fab, "rotation", 0f, 360f).setDuration(600).start();
        if (rotated) {
            fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_play_arrow_24dp, null));
        } else {
            fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_stop_24dp, null));
        }
    }


    private void generateHeader(GeneralStory body) {
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        String BASE_URL = sharedPreferences.getString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.SERVICE_BASE_URL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            String transitionName = extras.getString("transition");
            headerReceived.setTransitionName(transitionName);
            Picasso.with(this)
                    .load(NetworkUtils.IMG_BASE_URL + body.getStoryThumbnail())
                    .noFade()
                    .into(headerReceived, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportPostponeEnterTransition();
                        }
                    });
        } else {
            Picasso.with(this)
                    .load(NetworkUtils.IMG_BASE_URL + body.getStoryThumbnail())
                    .noFade()
                    .into(headerReceived, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportPostponeEnterTransition();
                        }
                    });
        }
    }

    private void setUpViewPager(SectionsPagerAdapter mSectionsPagerAdapter, final GeneralStory body) {
        mViewPager.setAdapter(mSectionsPagerAdapter);
        pager_counter.setText(1 + "/" + body.getDetailedStories().size());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            @Override
            public void onPageSelected(final int position) {

                if (speakRequest.isSpeaking()) {
                    speakRequest.stopSpeak();
                    whereAmISpeaking = position;
                    rotated = true;
                    animateButtons();
                } else {
                    whereAmISpeaking = position;
                    rotated = true;
                    animateButtons();
                }
                pager_counter.setText((position + 1) + "/" + body.getDetailedStories().size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(fab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(fab).scaleY(1).scaleX(1).start();
            }
        }
    }

    @Override
    protected void onPause() {
        speakRequest.stopSpeak();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speakRequest.onDestroy();
        unbinder.unbind();
    }

    private void animateButtons() {
        ObjectAnimator.ofFloat(fab, "rotation", 0f, 360f).setDuration(600).start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rotated) {
                    fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_play_arrow_24dp, null));
                } else {
                    fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_stop_24dp, null));
                }

            }
        }, 400);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<DetailedStory> detailedStories;

        public SectionsPagerAdapter(FragmentManager fm, List<DetailedStory> stories) {
            super(fm);
            this.detailedStories = stories;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DetailedStoryFragment (defined as a static inner class below).
            return DetailedStoryFragment.newInstance(detailedStories.get(position));
        }

        @Override
        public int getCount() {
            // Show total pages according to the size of this list.
            return detailedStories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
