package com.example.kskie.draft3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    EditText search_edit_text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    Button btn_map;

    ArrayList<String> fullNameList;
    ArrayList<String> userNameList;
    ArrayList<String> profilePicList;

    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_edit_text = (EditText) findViewById(R.id.search_edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        fullNameList = new ArrayList<>();
        userNameList = new ArrayList<>();
        profilePicList = new ArrayList<>();

        btn_map = (Button)findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

				/*
				 * Intent is just like glue which helps to navigate one activity
				 * to another.
				 */Intent intent = new Intent(MainActivity.this,
                        MapActivity.class);
                startActivity(intent); // startActivity allow you to move
            }
        });


        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){
                    setAdapter(s.toString());

                }else{
                    fullNameList.clear();
                    userNameList.clear();
                    profilePicList.clear();
                    recyclerView.removeAllViews();
                }
            }
        });
    }

    private void setAdapter(final String searchedString) {


        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullNameList.clear();
                userNameList.clear();
                profilePicList.clear();
                recyclerView.removeAllViews();

                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String uid = snapshot.getKey();
                    String full_name = snapshot.child("full_name").getValue(String.class);
                    String user_name = snapshot.child("user_name").getValue(String.class);
                    String profile_pic = snapshot.child("profile_pic").getValue(String.class);

                    if (full_name.toLowerCase().contains(searchedString.toLowerCase())){
                        fullNameList.add(full_name);
                        userNameList.add(user_name);
                        profilePicList.add(profile_pic);
                        counter++;

                    }else if (user_name.toLowerCase().contains(searchedString.toLowerCase())){
                        fullNameList.add(full_name);
                        userNameList.add(user_name);
                        profilePicList.add(profile_pic);
                        counter++;

                    }

                    if(counter ==15)
                        break;

                }

                searchAdapter = new SearchAdapter(MainActivity.this, fullNameList, userNameList, profilePicList);
                recyclerView.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }}
        );
    }





}
