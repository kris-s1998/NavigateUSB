package com.example.kskie.draft3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageView = (ImageView) findViewById(R.id.image);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/user1");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String profile_pic = dataSnapshot.child("profile_pic").getValue(String.class);
                    Glide.with(MapActivity.this).load(profile_pic).into(imageView);

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
}
