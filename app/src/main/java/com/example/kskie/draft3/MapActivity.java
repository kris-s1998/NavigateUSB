package com.example.kskie.draft3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.storage.*;

public class MapActivity extends AppCompatActivity {

    StorageReference storageRef;
    TouchImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageView = findViewById(R.id.image);
        storageRef = FirebaseStorage.getInstance().getReference();


        StorageReference pathReference = storageRef.child("floorMaps/floor1.png");


        Glide.with(MapActivity.this)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(imageView);
    }

}
