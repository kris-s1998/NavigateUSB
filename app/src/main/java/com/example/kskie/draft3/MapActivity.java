package com.example.kskie.draft3;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.storage.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {
    public static int activityNum = 2;

    StorageReference storageRef;
    TouchImageView imageView;
    ProgressBar loading;
    Button up, down;
    //not portable really (needs file input)
    int floorNo = 0;
    int maxFloor = 5;
    int minFloor = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageView = findViewById(R.id.image);
        storageRef = FirebaseStorage.getInstance().getReference();
        loading = findViewById(R.id.loadingBar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MenuFragment menuFragment = MenuFragment.newInstance(activityNum);
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment);
        fragmentTransaction.commit();

        //need to make portable
        StorageReference floor1 = storageRef.child("floorMaps/floor1.png");

        Glide.with(MapActivity.this)
                .using(new FirebaseImageLoader())
                .load(floor1)
                .placeholder(loading.getIndeterminateDrawable())
                .into(imageView);

        StorageReference floor2 = storageRef.child("floorMaps/floor2.png");
        StorageReference floor3 = storageRef.child("floorMaps/floor3.png");
        StorageReference floor4 = storageRef.child("floorMaps/floor4.png");
        StorageReference floor5 = storageRef.child("floorMaps/floor5.png");
        StorageReference floor6 = storageRef.child("floorMaps/floor6.png");
        final List<StorageReference> floors = new ArrayList<>();
        floors.addAll(Arrays.asList(floor1, floor2, floor3, floor4, floor5, floor6));


        up = findViewById(R.id.btn_up);
        up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (floorNo < maxFloor)
                    floorNo++;
                    Glide.with(MapActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(floors.get(floorNo))
                            .into(imageView);
                }
        });

        down = findViewById(R.id.btn_down);
        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (floorNo >= minFloor)
                    floorNo--;
                Glide.with(MapActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(floors.get(floorNo))
                        .into(imageView);
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}
