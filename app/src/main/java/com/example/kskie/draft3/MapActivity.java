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
    //not portable really (needs file input)
    int floorNo = 0;
    int maxFloor;
    int minFloor = 1;
    List<Floor> floors = new ArrayList<>();

    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // EDIT
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(prefs.getBoolean(PREF_DARK_THEME, false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageView = findViewById(R.id.image);
        storageRef = FirebaseStorage.getInstance().getReference();
        loading = findViewById(R.id.loadingBar);
        mask = findViewById(R.id.masks);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MenuFragment menuFragment = MenuFragment.newInstance(activityNum);
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment);
        fragmentTransaction.commit();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("floors");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                floors.clear();

                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Floor f = d.getValue(Floor.class);
                    floors.add(f);
                }

                Glide.with(MapActivity.this)
                        .load(floors.get(0).getImageurl())
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

        up = findViewById(R.id.btn_up);
        up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (floorNo < floors.size()-1)
                    floorNo++;
                    Glide.with(MapActivity.this)
                        .load(floors.get(floorNo).getImageurl())
                        .placeholder(loading.getIndeterminateDrawable())
                        .into(imageView);
                }
        });

        down = findViewById(R.id.btn_down);
        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (floorNo > 0)
                    floorNo--;
                Glide.with(MapActivity.this)
                        .load(floors.get(floorNo).getImageurl())
                        .placeholder(loading.getIndeterminateDrawable())
                        .into(imageView);
            }
        });


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

    private void loadImage(int floorIndex){
        try {

            Glide.with(MapActivity.this)
                    .load(floors.get(floorIndex).getImageurl())
                    .placeholder(loading.getIndeterminateDrawable())
                    .into(imageView);

        }catch (IndexOutOfBoundsException iobe){
            Toast toast = Toast.makeText(MapActivity.this, "No floor to display", Toast.LENGTH_SHORT);
            toast.show(); //if database cannot be read, display error message
        }
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

