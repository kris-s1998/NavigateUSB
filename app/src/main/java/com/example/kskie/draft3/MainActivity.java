package com.example.kskie.draft3;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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


public class MainActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{

    public static int ACTIVITY_NUM = 1; //this number represents the main activity and is used by the menu fragment to determine which activity is currently active
    //the following constants are used when information about a room is being passed from once activity to another: they are the keys of the strings being passed between activities
    public static String ROOM_NO = "roomNo"; //the key for the room number string
    public static String FIRST_NAME = "firstName"; //the key for the first name string
    public static String LAST_NAME = "lastName";  //the key for the last name string
    public static String ROOMS_DB_REFERENCE = "rooms";

    private EditText searchEditText; //the edit text view used for searching for tutor's rooms
    private RecyclerView searchRecyclerView; //the recycler view used to display search results
    private ListView favouritesListView; //the list view used to display favourite rooms

    private DatabaseReference roomsDBReference; //firebase database reference used to retrieve data from the database (about the rooms in the building)

    private SearchAdapter searchAdapter; //adaptrer used to display search results in a list
    public FavouritesAdapter adapter; //adapter used to display favourite rooms in a list

    private ArrayList<Room> roomList; //a list of all the rooms retrieved from the database
    private ArrayList<Room> foundRooms; //a list of rooms found which match the search criteria
    private ArrayList<String> favourites; //a list of strings read from the favourites file (rooms in string form)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable()){
            Toast toast = Toast.makeText(MainActivity.this, "Internet connection not found. You must be connected to the internet to use the app!", Toast.LENGTH_LONG);
            toast.show();
        }

        //the following lines of code add the menu fragment to the activity
        FragmentManager fragmentManager = getSupportFragmentManager(); //used to add fragments
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //used to add menu fragment
        MenuFragment menuFragment = MenuFragment.newInstance(ACTIVITY_NUM); //create new instance of menu fragment, passing the activity number as a parameter
        // so that the home button in the will be a different colour to show that this is the current page
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment); //add menu fragment
        fragmentTransaction.commit(); //commit the transaction

        roomsDBReference = FirebaseDatabase.getInstance().getReference(ROOMS_DB_REFERENCE); //initialise database reference to the "rooms" branch of the database

        //load entire database into a list of Room objects
        roomsDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomList.clear(); //clear the list of rooms so that updated data can be added
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Room r = d.getValue(Room.class); //get each room from the database and convert it to a java object
                    roomList.add(r); //add each room to the rooms list
                }
                setFavouritesAdapter(); //populate the favourites list view
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(MainActivity.this, "Unable to read database", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        searchEditText = findViewById(R.id.search_edit_text);
        searchRecyclerView = findViewById(R.id.recyclerView);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        foundRooms = new ArrayList<>();
        roomList = new ArrayList<>();


        searchEditText.addTextChangedListener(new TextWatcher() {
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

        favouritesListView = findViewById(R.id.listFavourites);



    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        int favouritesWidth = View.MeasureSpec.makeMeasureSpec(favouritesListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int favouritesHeight = 0;
        View view = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, favouritesListView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(favouritesWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(favouritesWidth, View.MeasureSpec.UNSPECIFIED);
            favouritesHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = favouritesListView.getLayoutParams();
        layoutParams.height = favouritesHeight+(favouritesListView.getDividerHeight()*(adapter.getCount()-1));
        favouritesListView.setLayoutParams(layoutParams);
        favouritesListView.setAdapter(adapter);
        favouritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room clickedRoom = (Room)parent.getItemAtPosition(position);
                Context context = view.getContext();
                Intent intent = new Intent(context, RoomActivity.class);
                Bundle b = new Bundle();
                b.putString(ROOM_NO,clickedRoom.getNumber());
                b.putString(FIRST_NAME,clickedRoom.getFirstName());
                b.putString(LAST_NAME,clickedRoom.getLastName());
                intent.putExtras(b);
                 context.startActivity(intent);
            }
        });


    }

    public void setAdapter(String searchString) {
        searchString = searchString.toLowerCase();
        String[] splitSearchString = searchString.split(" ");
        foundRooms.clear();
        Room currentRoom;
        for(int i = 0; i<roomList.size(); i++) {
            currentRoom = roomList.get(i);
            for(int j = 0; j<splitSearchString.length; j++){
                if (    currentRoom.getNumber().contains(splitSearchString[j]) ||
                        currentRoom.getFirstName().toLowerCase().contains(splitSearchString[j])
                        || currentRoom.getLastName().toLowerCase().contains(splitSearchString[j]) ) {
                    foundRooms.add(currentRoom);

                }
            }
        }

        searchAdapter = new SearchAdapter(MainActivity.this, foundRooms);
        searchRecyclerView.setAdapter(searchAdapter);
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

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}
