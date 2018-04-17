package com.example.kskie.draft3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;

public class SettingsActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {


    //shared Preferences
    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    public static int ACTIVITY_NUM = 4;

    private Button clearFavourites;

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

        //the following lines of code add the menu fragment to the activity
        FragmentManager fragmentManager = getSupportFragmentManager(); //used to add fragments
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //used to add menu fragment
        MenuFragment menuFragment = MenuFragment.newInstance(ACTIVITY_NUM); //create new instance of menu fragment, passing the activity number as a parameter
        // so that the home button in the will be a different colour to show that this is the current page
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment); //add menu fragment
        fragmentTransaction.commit(); //commit the transaction

        Switch viewMode = findViewById(R.id.darkModeSwitch);
        viewMode.setChecked(darkTheme);
        viewMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                toggleTheme(isChecked);
            }
        });

        clearFavourites = findViewById(R.id.clearFavouritesBtn);
        clearFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFile(RoomActivity.FILE_NAME);
                Toast toast = Toast.makeText(SettingsActivity.this, "All favourites deleted.", Toast.LENGTH_SHORT);
                toast.show();
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

