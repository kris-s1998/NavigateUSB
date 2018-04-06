package com.example.kskie.draft3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * This is the main activity of the app: the home page. When the app is first launched, this activity appears.
 * It contains a search bar, a list of favourite rooms and a twitter feed of tweets relating to the building.
 *
 * Created by Kris Skierniewski on 28/02/2018.
 */

public class MainActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{

    //shared Preferences
    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

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

        // EDIT
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(prefs.getBoolean(PREF_DARK_THEME, false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable()){ //checks that the device has an internet connection, if not
            Toast toast = Toast.makeText(MainActivity.this, "Internet connection not found. You must be connected to the internet to use the app!", Toast.LENGTH_LONG);
            toast.show(); //then display error message for the user
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
                toast.show(); //if database cannot be read, display error message
            }
        });

        //the following code initialises the interface elements on the home screen
        searchEditText = findViewById(R.id.search_edit_text);
        searchRecyclerView = findViewById(R.id.recyclerView);
        favouritesListView = findViewById(R.id.listFavourites);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        roomList = new ArrayList<>(); //instantiate list of all rooms
        foundRooms = new ArrayList<>(); //in list of rooms which match the search criteria

        //add a listener to the search box
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable c) { //when text in the search box has been changed
                if (!c.toString().isEmpty()){ //if search string is not empty then
                    setSearchAdapter(c.toString()); //find results which match the search string and display them in a list
                }else{
                    foundRooms.clear(); //else clear the list of rooms found
                    searchAdapter.notifyDataSetChanged(); //and clear the list of displayed search results
                }
            }
        });
        favourites = readFromFile(); //initialise the favourites list with a list of strings read from the favourites file
    }

    //this method checks if the device is connected to the internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo(); //retrieve information about the network connection
        boolean isConnected = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        return isConnected; //returns true if there is an active network connection that is connected to the internet, else false
    }


    @Override
    protected void onResume() {
        super.onResume(); //when the user returns to this activity from another activity or another app then
        if(favourites != null && adapter != null) //if not launching activity for the first time
            favourites = readFromFile(); //read the favourites file to make sure list of favourites is up to date
            setFavouritesAdapter(); //update the list view of favourites
    }

    //this method displays the user's favourite rooms in a list, it takes the favourites (String) list that has been read from the file
    //and finds the corresponding Room objects from the database and displays them for the user in a list
    public void setFavouritesAdapter(){
        ArrayList<Room> favouriteRooms = new ArrayList<>(); //declare and instantiate a list of the user's favourite rooms
        foundRooms.clear(); //clear list of rooms that match the criteria
        Room currentRoom; //the room currently being checked against the criteria
        for(int i = 0; i<favourites.size(); i++){ //for each string in the favourites list
            for(int j = 0; j<roomList.size();j++){ //check each room from the database
                currentRoom = roomList.get(j); //get the current room
                String[] splitStrings = favourites.get(i).split(RoomActivity.SEPARATOR); //divide the string (from the favourites list) into 3 parts: room number,first name and last name
                if ( splitStrings[0].equals(currentRoom.getNumber()) && splitStrings[1].equals(currentRoom.getFirstName()) && splitStrings[2].equals(currentRoom.getLastName()) ){ //if the current room matches the room number, first name and last name
                    favouriteRooms.add(roomList.get(j)); //then add current room the list of favourite rooms
                    break; //the room which corresponds to the current favourites string has been found so break out of the loop and move onto the next one
                }
            }
        }
        adapter = new FavouritesAdapter(this,favouriteRooms); //create a new instance of favrourites adapter, with the list of favourite rooms
        //the favourites list view is inside a scroll view therefore the next lines of code are required to adjust the height of the list view and make it display all of the found rooms
        int favouritesWidth = View.MeasureSpec.makeMeasureSpec(favouritesListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int favouritesHeight = 0;
        View view = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, favouritesListView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(favouritesWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(favouritesWidth, View.MeasureSpec.UNSPECIFIED);
            favouritesHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = favouritesListView.getLayoutParams();
        layoutParams.height = favouritesHeight+(favouritesListView.getDividerHeight()*(adapter.getCount()-1));
        favouritesListView.setLayoutParams(layoutParams); //set size parameters for the adapter and
        favouritesListView.setAdapter(adapter); //add the adapter to the favourites list view
        favouritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //when item is selected from the favourites list
                Room clickedRoom = (Room)parent.getItemAtPosition(position); //retrieve the clicked room
                Context context = view.getContext();
                Intent intent = new Intent(context, RoomActivity.class); //information about the room will be shown in the room activity
                Bundle b = new Bundle();
                //put information about the clicked room in a bundle
                b.putString(ROOM_NO,clickedRoom.getNumber());
                b.putString(FIRST_NAME,clickedRoom.getFirstName());
                b.putString(LAST_NAME,clickedRoom.getLastName());
                intent.putExtras(b);
                //and start the room activity, passing on the information about the room
                context.startActivity(intent);
            }
        });
    }

    //this method displays the search results in a recycler view
    public void setSearchAdapter(String searchString) {
        searchString = searchString.toLowerCase(); //convert search string to lowercase in case user has not used the correct case
        String[] splitSearchString = searchString.split(" "); //divide the search string into parts
        foundRooms.clear(); //clear list of previous search results
        Room currentRoom; //this is the room currently being checked
        for(int i = 0; i<roomList.size(); i++) { //for each room in the list of all rooms
            currentRoom = roomList.get(i);
            for(int j = 0; j<splitSearchString.length; j++){ //check if any of the parts of the search string match
                //either the room number, first name or last name of the current room
                if (currentRoom.getNumber().contains(splitSearchString[j]) ||
                    currentRoom.getFirstName().toLowerCase().contains(splitSearchString[j])
                    || currentRoom.getLastName().toLowerCase().contains(splitSearchString[j]) )
                {
                    foundRooms.add(currentRoom); //if so then add the room to the list of found rooms
                }
            }
        }
        searchAdapter = new SearchAdapter(MainActivity.this, foundRooms); //create and initialise search adapter
        searchRecyclerView.setAdapter(searchAdapter); //and add the adapter to the recycler view
    }

    //this method reads the favourites file and returns a list of strings
    private ArrayList<String> readFromFile() {
        ArrayList<String> favourites = new ArrayList<>(); //the data read from the file is stored in this list
        try {
            OutputStream outputStream = openFileOutput(RoomActivity.FILE_NAME,MODE_APPEND); //establish output stream first in order to make sure that a favourites file is created if it does not already exist
            InputStream inputStream = openFileInput(RoomActivity.FILE_NAME); //create input stream to the favourites file

            if ( inputStream != null && outputStream != null) { //if both streams have been successfully established
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //then read each line of text in the file and
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    favourites.add(receiveString); //add it to the list
                }
                inputStream.close(); //close input and output streams
                outputStream.close();
            }
        }
        //returns appropriate error messages if exceptions are thrown
        catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(MainActivity.this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
            return favourites;
        } catch (IOException e) {
            Toast toast = Toast.makeText(MainActivity.this, "Could not read file", Toast.LENGTH_SHORT);
            toast.show();
            return favourites;
        }
        return favourites; //return the list of strings read from the favourites file
        //favourites are stored in a "room number%first name%last name" format where "%" is used to separate the parts
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}
