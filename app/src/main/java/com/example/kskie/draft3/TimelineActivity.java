package com.example.kskie.draft3;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

/**
 * This activity is used to display the latest updates about the building (from twitter) in a scrollable list.
 *
 * Created by Kris Skierniewski on 15/04/2018.
 */

public class TimelineActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {

    //shared Preferences
    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private static final int ACTIVITY_NUM=5; //unique identifier of the activity for the menu fragment

    private ListView tweetsList; //used to display the tweets

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(prefs.getBoolean(PREF_DARK_THEME, false)) { //if dark theme is activated
            setTheme(R.style.AppTheme_Dark_NoActionBar); //set dark theme for the current activity
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        tweetsList = findViewById(R.id.timelineList); //initialise the list of tweets


        //the following lines of code add the menu fragment to the activity
        FragmentManager fragmentManager = getSupportFragmentManager(); //used to add fragments
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //used to add menu fragment
        MenuFragment menuFragment = MenuFragment.newInstance(ACTIVITY_NUM); //create new instance of menu fragment, passing the activity number as a parameter
        // so that the home button in the will be a different colour to show that this is the current page
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment); //add menu fragment
        fragmentTransaction.commit(); //commit the transaction

        //configure twitter before it can be used to build a timeline
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.consumer_key), getString(R.string.consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config); //intiialise twitter

        //build a user timeline for the newcastle university school of computing
        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(getString(R.string.twitter_screen_name)) //screen name is retrieved from strings (resources)
                .build();
        final TweetTimelineListAdapter adapter; //create an adapter for the list of tweets
        if(prefs.getBoolean(PREF_DARK_THEME, false)) { //if dark mode is activated
            adapter = new TweetTimelineListAdapter.Builder(this)
                    .setTimeline(userTimeline)
                    .setViewStyle(R.style.tw__TweetDarkStyle) //then set style of the tweets list to dark
                    .build();
        }else {
            adapter = new TweetTimelineListAdapter.Builder(this)
                    .setTimeline(userTimeline)
                    .setViewStyle(R.style.tw__TweetLightStyle) //else set style of the tweets list to light
                    .build();
        }

        tweetsList.setAdapter(adapter); //add the tweets adapter to the tweets list

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}


