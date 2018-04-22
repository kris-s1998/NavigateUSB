package com.example.kskie.draft3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.kskie.draft3.MainActivity.*;

public class MapActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {
    public static int activityNum = 2;

    StorageReference storageRef;
    TouchImageView imageView;
    ProgressBar loading;
    Button up, down;
    ImageView mask;

    //determine the branch of the database containing the floor picture links
    static String FLOORS_DB_REFERENCE = "floors";


    /**
     * tracker for floor number and the container to iterate through
     * to change floor
     */
    int floorNo = 0;
    List<Floor> floors = new ArrayList<>();


    //shared preferences
    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Load the dark theme preference and check to see if dark mode is enabled
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(prefs.getBoolean(PREF_DARK_THEME, false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //initialise view items
        imageView = findViewById(R.id.image);
        storageRef = FirebaseStorage.getInstance().getReference();
        loading = findViewById(R.id.loadingBar);
        mask = findViewById(R.id.masks);

        //the following lines of code add the menu fragment to the activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MenuFragment menuFragment = MenuFragment.newInstance(activityNum);
        // so that the home button in the will be a different colour to show that this is the current page
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment);
        fragmentTransaction.commit();


        //initialise the database reference containing the image URL's to the correct branch
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FLOORS_DB_REFERENCE);

        //initialise the floors list to contain all the image URL's into a wrapper class floors
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //as this method is triggered every time the database changes
                //clear the floors list before populating it again to avoid duplicates
                floors.clear();

                //add each floor to the list from the data snapshot
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Floor f = d.getValue(Floor.class);
                    floors.add(f);
                }

                //load the first floor
                Glide.with(MapActivity.this)
                        .load(floors.get(0).getImageurl())
                        //using the placeholder image of a loading drawable
                        .placeholder(loading.getIndeterminateDrawable())
                        .into(imageView);
                imageView.setZoom((float)0.99);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(MapActivity.this, "Unable to read database", Toast.LENGTH_SHORT);
                toast.show(); //if database cannot be read, display error message
            }
        });

        //find and set the up button functionality
        up = findViewById(R.id.btn_up);
        up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    if (floorNo < floors.size()-1){
                        floorNo++;
                        Glide.with(MapActivity.this)
                                .load(floors.get(floorNo).getImageurl())
                                .placeholder(loading.getIndeterminateDrawable())
                                .into(imageView);
                    }
                }
        });

        //find and set the down button functionality
        down = findViewById(R.id.btn_down);
        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (floorNo > 0) {
                    floorNo--;
                    Glide.with(MapActivity.this)
                            .load(floors.get(floorNo).getImageurl())
                            .placeholder(loading.getIndeterminateDrawable())
                            .into(imageView);
                }
            }
        });


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}

