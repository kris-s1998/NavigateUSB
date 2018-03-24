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

    SearchAdapter searchAdapter;

    ArrayList<Room> foundRooms;

    Button btn_map;

    ArrayList<Room> roomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Go to button page
        btn_map = findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

				Intent intent = new Intent(MainActivity.this,
                        MapActivity.class);
                startActivity(intent); // startActivity allow you to move
            }
        });



        //load entire database into a list of Room objects
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomList.clear();

                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Room r = d.getValue(Room.class);

                    roomList.add(r);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        search_edit_text = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        foundRooms = new ArrayList<Room>();


        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable c) {
                if (!c.toString().isEmpty()){
                    setAdapter(c.toString());
                }else{
                    foundRooms.clear();
                    searchAdapter.notifyDataSetChanged();

                }
            }
        });

    }

    public void setAdapter(String searchString) {
        searchString = searchString.toLowerCase();
        foundRooms.clear();
        Room currentRoom;
        for(int i = 0; i<roomList.size(); i++) {
            currentRoom = roomList.get(i);
            if (currentRoom.getNumber().contains(searchString) || currentRoom.getFirstName().toLowerCase().contains(searchString) || currentRoom.getLastName().toLowerCase().contains(searchString) ) {
                foundRooms.add(currentRoom);

            }
        }

        searchAdapter = new SearchAdapter(MainActivity.this, foundRooms);
        recyclerView.setAdapter(searchAdapter);
    }

}
