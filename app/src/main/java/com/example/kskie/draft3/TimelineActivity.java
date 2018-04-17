package com.example.kskie.draft3;

import android.app.ListActivity;
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

public class TimelineActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {

    //shared Preferences
    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private static final int ACTIVITY_NUM=5;

    private ListView tweetsList; //used to display the tweets

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(prefs.getBoolean(PREF_DARK_THEME, false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        //the following lines of code add the menu fragment to the activity
        FragmentManager fragmentManager = getSupportFragmentManager(); //used to add fragments
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //used to add menu fragment
        MenuFragment menuFragment = MenuFragment.newInstance(ACTIVITY_NUM); //create new instance of menu fragment, passing the activity number as a parameter
        // so that the home button in the will be a different colour to show that this is the current page
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment); //add menu fragment
        fragmentTransaction.commit(); //commit the transaction

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("VUcGycwBUZfAeipECg54hU01Z", "mzoAhvTbwDiL0b7ss2QUGfvFOR1WgsNUJdPXeC6MWJ0PLb4Q2S"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName("computingncl")
                .build();
        final TweetTimelineListAdapter adapter;
        if(prefs.getBoolean(PREF_DARK_THEME, true)) {
            adapter = new TweetTimelineListAdapter.Builder(this)
                    .setTimeline(userTimeline)
                    .setViewStyle(R.style.tw__TweetDarkStyle)
                    .build();
        }else {
            adapter = new TweetTimelineListAdapter.Builder(this)
                    .setTimeline(userTimeline)
                    .setViewStyle(R.style.tw__TweetLightStyle)
                    .build();
        }
        tweetsList=findViewById(R.id.timelineList);
        tweetsList.setAdapter(adapter);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}


