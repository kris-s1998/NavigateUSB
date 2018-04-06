package com.example.kskie.draft3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.*;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{
    public static int activityNum = 2;

    StorageReference storageRef;
    TouchImageView imageView;
    ImageView mask;
    ProgressBar loading;
    Button up, down;
    int floorNo = 1;
    int maxFloor = 5;
    int minFloor = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageView = findViewById(R.id.image);
        mask = findViewById(R.id.masks);
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

        StorageReference floor2mask = storageRef.child("floorMaps/floor2mask.png");
        StorageReference floor3mask = storageRef.child("floorMaps/floor3mask.png");
        StorageReference floor4mask = storageRef.child("floorMaps/floor4mask.png");
        StorageReference floor5mask = storageRef.child("floorMaps/floor5mask.png");
        StorageReference floor6mask = storageRef.child("floorMaps/floor6mask.png");

        final List<StorageReference> floors = new ArrayList<StorageReference>();
        floors.addAll(Arrays.asList(floor1, floor2, floor3, floor4, floor5, floor6));

        final List<StorageReference> floorsMasks = new ArrayList<StorageReference>();
        floorsMasks.addAll(Arrays.asList(floor1, floor2mask, floor3mask, floor4mask, floor5mask, floor6mask));


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
                    Glide.with(MapActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(floorsMasks.get(floorNo))
                            .into(mask);

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
                Glide.with(MapActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(floorsMasks.get(floorNo))
                        .into(mask);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                Log.d("KillMe","I see you touched me");
                int evX = (int) ev.getX();
                int evY = (int) ev.getY();
                final int touchColor = getColor(R.id.masks,evX,evY);
                Log.d("KillMe",Integer.toString(touchColor));
                final int tolerance = 25;
                DatabaseReference matches = FirebaseDatabase.getInstance().getReference("rooms");
                matches.orderByChild("level").equalTo(Integer.toString(floorNo));
                matches.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot shot : dataSnapshot.getChildren()){
                            int col = Color.parseColor(shot.child("color").getValue(String.class));
                            if(similarity(col,touchColor,tolerance)){
                                Log.d("KillMe","Fucking hell it works");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                return true;
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }


    public int getColor(int id, int x, int y){
        ImageView img = (ImageView) findViewById (id);
        img.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(img.getDrawingCache());
        img.setDrawingCacheEnabled(false);
        int pixel = bitmap.getPixel(x,y);
        return pixel;
    }

    public boolean similarity(int color1, int color2, int tolerance){
        if ((int) Math.abs (Color.red (color1) - Color.red (color2)) > tolerance )
            return false;
        if ((int) Math.abs (Color.green (color1) - Color.green (color2)) > tolerance )
            return false;
        if ((int) Math.abs (Color.blue (color1) - Color.blue (color2)) > tolerance )
            return false;
        return true;
    }
}
