package com.example.kskie.draft3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {


    //shared Preferences
    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    public static int activityNum = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // EDIT
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        Boolean darkTheme = prefs.getBoolean(PREF_DARK_THEME, false);
        if(darkTheme) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MenuFragment menuFragment = MenuFragment.newInstance(activityNum);
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment);
        fragmentTransaction.commit();

        Switch viewMode = (Switch) findViewById(R.id.darkModeSwitch);
        viewMode.setChecked(darkTheme);
        viewMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                toggleTheme(isChecked);
            }
        });

    }

    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent i = getIntent();
        finish();

        startActivity(i);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}

