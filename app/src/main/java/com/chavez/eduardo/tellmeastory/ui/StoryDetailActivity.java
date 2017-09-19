package com.chavez.eduardo.tellmeastory.ui;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.GeneralStory;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.chavez.eduardo.tellmeastory.utils.SpeakRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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

    private GeneralStory story;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        unbinder = ButterKnife.bind(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (getIntent().getExtras() != null) {
            extras = getIntent().getExtras();
            story = (GeneralStory) extras.getSerializable(ConfigurationUtils.BUNDLE_MAIN_KEY);

        }

        toolbar.setTitle(story.getStoryName());
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

        generateHeader();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        setUpViewPager(mSectionsPagerAdapter);


        speakRequest = new SpeakRequest(this);
        listenToFinishedText();
        fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_headset_24dp, null));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (story != null && !speakRequest.isSpeaking()) {
                    speakRequest.speak(story.getDetailedStories().get(whereAmISpeaking).getSectionText());
                    animateButtons();
                } else {
                    speakRequest.stopSpeak();
                    animateButtons();
                }

                if (rotated) {
                    Snackbar.make(view, "Reproduciendo", Snackbar.LENGTH_LONG)
                            .setAction("Detener", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (speakRequest.isSpeaking()) {
                                        speakRequest.stopSpeak();
                                        animateButtons();
                                    }
                                }
                            }).show();
                } else if (firstAppearTTS) {
                    firstAppearTTS = false;
                } else {
                    Snackbar.make(view, "Detenido", Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });

        appBarLayout.addOnOffsetChangedListener(this);

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
        rotated = !rotated;
        fab.setImageDrawable(rotated ? VectorDrawableCompat.create(getResources(), R.drawable.ic_play_arrow_24dp, null) : VectorDrawableCompat.create(getResources(), R.drawable.ic_stop_24dp, null));


    }


    private void generateHeader() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = extras.getString("transition");
            headerReceived.setTransitionName(transitionName);
            Picasso.with(this)
                    .load(story.getStoryThumbnail())
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
                    .load(story.getStoryThumbnail())
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

    private void setUpViewPager(SectionsPagerAdapter mSectionsPagerAdapter) {
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (speakRequest.isSpeaking()) {
                    speakRequest.stopSpeak();
                    whereAmISpeaking = position;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animateButtonsAfterSpeak();
                        }
                    });
                } else {
                    whereAmISpeaking = position;
                }

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
                /**
                 if (rotated) {
                 fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_play_arrow_24dp, null));
                 rotated = false;
                 } else {
                 fab.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_stop_24dp, null));
                 rotated = true;
                 } **/
                rotated = !rotated;
                fab.setImageDrawable(rotated ? VectorDrawableCompat.create(getResources(), R.drawable.ic_play_arrow_24dp, null) : VectorDrawableCompat.create(getResources(), R.drawable.ic_stop_24dp, null));

            }
        }, 400);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DetailedStoryFragment (defined as a static inner class below).
            return DetailedStoryFragment.newInstance(story.getDetailedStories().get(position));
        }

        @Override
        public int getCount() {
            // Show total pages according to the size of this list.
            return story.getDetailedStories().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
