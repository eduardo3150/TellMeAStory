package com.chavez.eduardo.tellmeastory.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.DetailedStory;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.chavez.eduardo.tellmeastory.network.NetworkUtils.IMG_BASE_URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailedStoryFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String DETAILED_STORY = "detailed_story";

    Unbinder unbinder;

    @BindView(R.id.section_title)
    TextView storyDetailedTitle;

    @BindView(R.id.section_text_content)
    TextView storyDetailedContent;

    @BindView(R.id.section_image)
    ImageView storyDetailedImage;

    @BindView(R.id.cardContainer)
    ViewGroup viewGroup;

    public static final String LOG_TAG = DetailedStoryFragment.class.getSimpleName();

    public DetailedStoryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DetailedStoryFragment newInstance(DetailedStory story) {
        DetailedStoryFragment fragment = new DetailedStoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(DETAILED_STORY, story);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_story_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments().getParcelable(DETAILED_STORY) != null) {
            DetailedStory story = getArguments().getParcelable(DETAILED_STORY);
            populateView(story);
        }
    }

    private void populateView(DetailedStory story) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        String BASE_URL = sharedPreferences.getString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.SERVICE_BASE_URL);
        storyDetailedTitle.setText(story.getSectionTitle());
        storyDetailedContent.setText(story.getSectionText());
        Picasso.with(getContext()).load(BASE_URL+story.getSectionImage()).noFade().into(storyDetailedImage);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                animateInitialView();
            }
        }, 250);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }



    private void animateInitialView() {
        TransitionSet set = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setInterpolator(new FastOutLinearInInterpolator())
                .addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {

                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }
                });
        TransitionManager.beginDelayedTransition(viewGroup, set);
        storyDetailedImage.setVisibility(View.VISIBLE);
        storyDetailedTitle.setVisibility(View.VISIBLE);
        storyDetailedContent.setVisibility(View.VISIBLE);
    }



}
