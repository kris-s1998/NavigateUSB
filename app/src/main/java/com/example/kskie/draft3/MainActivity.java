package com.example.kskie.draft3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    EditText search_edit_text;
    RecyclerView recyclerView;
    ListView listFavourites;
    DatabaseReference databaseReference;

    TextView txtTest;

    SearchAdapter searchAdapter;
    public FavouritesAdapter adapter;

    ArrayList<Room> foundRooms;
    ArrayList<String> favourites;

    Button btn_map;
    Button btn_navigate;

    ArrayList<Room> roomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");

        //Go to button page
        btn_map = findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
                        MapActivity.class);
                startActivity(intent); // startActivity allow you to move
            }
        });

        //go to navigate button
        btn_navigate = findViewById(R.id.btn_navigate);
        btn_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NavigateActivity.class);
                startActivity(intent);
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
                setFavouritesAdapter();
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
        favourites = readFromFile();
        listFavourites = findViewById(R.id.listFavourites);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(favourites != null && adapter != null) //if not launching activity for the first time
            favourites = readFromFile(); //read the favourites file to make sure list of favourites is up to date
            setFavouritesAdapter(); //update the list view of favourites
    }

    public void setFavouritesAdapter(){
        ArrayList<Room> favouriteRooms = new ArrayList<>();

        foundRooms.clear();
        Room currentRoom;
        for(int i = 0; i<favourites.size(); i++){
            for(int j = 0; j<roomList.size();j++){
                currentRoom = roomList.get(j);
                String[] splitStrings = favourites.get(i).split(RoomActivity.SEPARATOR);
                if ( splitStrings[0].equals(currentRoom.getNumber()) && splitStrings[1].equals(currentRoom.getFirstName()) && splitStrings[2].equals(currentRoom.getLastName()) ){
                    favouriteRooms.add(roomList.get(j));
                }
            }
        }

        adapter = new FavouritesAdapter(this,favouriteRooms);
        listFavourites.setAdapter(adapter);
        listFavourites.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room clickedRoom = (Room)parent.getItemAtPosition(position);
                Context context = view.getContext();
                Intent intent = new Intent(context, RoomActivity.class);
                Bundle b = new Bundle();
                String roomNo = clickedRoom.getNumber();
                b.putString("roomNo",roomNo);
                intent.putExtras(b);
                context.startActivity(intent);
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

    private ArrayList<String> readFromFile() {

        ArrayList<String> favourites = new ArrayList<>();

        try {
            OutputStream outputStream = openFileOutput(RoomActivity.FILE_NAME,MODE_APPEND); //establish output stream first in order to make sure that a favourites file is created if it does not already exist
            InputStream inputStream = openFileInput(RoomActivity.FILE_NAME); //create input stream to the favourites file

            if ( inputStream != null && outputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    favourites.add(receiveString);
                }
                inputStream.close();
                outputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(MainActivity.this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
            return favourites;
        } catch (IOException e) {
            Toast toast = Toast.makeText(MainActivity.this, "Could not read file", Toast.LENGTH_SHORT);
            toast.show();
            return favourites;
        }
        return favourites;
    }

}
