package com.example.kskie.draft3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    public static final String FILE_NAME = "favourites.txt";
    public static final String SEPARATOR = "%";


    private ArrayList<Room> allRooms; //a list of all the rooms retrieved from the database
    private ArrayList<Room> foundRooms; //a list of rooms found which match the search criteria
    private Room thisRoom; //the current room

    DatabaseReference databaseReference; //firebase database reference used to retrieve data from the database (about the rooms in the building)

    private TextView txt_room_header; //used to display room number and which level it is on
    private TextView txt_via_room; //used to display the via room
    private TextView txt_tutors; //used to display the tutors which use the current room
    private ImageButton btn_favourite; //used to add/remove room from favourites
    private ImageView img_room_pic; //used to display a picture of the room
    private Button btn_direct_here; //used to navigate to the directions page and insert the number of this room into destination field

    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE); //get shared preferences
        if(prefs.getBoolean(PREF_DARK_THEME, false)) { //if the app is currently in dark mode
            setTheme(R.style.AppTheme_Dark_NoActionBar); //set the theme to dark mode
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        allRooms = new ArrayList<>(); //initialise room list
        foundRooms = new ArrayList(); //initialise found rooms list

        //initialise all the interface components
        txt_room_header = findViewById(R.id.room_header);
        txt_via_room = findViewById(R.id.room_desc);
        txt_tutors = findViewById(R.id.tutor_list);
        btn_favourite = findViewById(R.id.btn_favourite);
        img_room_pic = findViewById(R.id.roomImageView);
        btn_direct_here = findViewById(R.id.btn_direct_here);
        btn_direct_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, NavigateActivity.class);
                Bundle b = new Bundle(); //used to pass room information between the two activities
                b.putString(MainActivity.ROOM_NO,thisRoom.getNumber()); //put the room number in the bundle so it can be set as the destination
                intent.putExtras(b);
                startActivity(intent); //start navigate activity
            }
        });

        //initialise the database reference to the "rooms" branch of the database
        databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.ROOMS_DB_REFERENCE);
        databaseReference.addValueEventListener(new ValueEventListener() { //initialises and updates info from database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){ //get all rooms
                    allRooms.add(d.getValue(Room.class)); //and add them to the list
                }
                    populateFields(); //populate the text views and image view, and update favourites button
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //display error message
                Toast toast = Toast.makeText(RoomActivity.this, "Could not reach database", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //when the favourite button (star) is clicked
        btn_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavourite()) { //if this room is already a favourite then delete it
                    removeFromFavourites(); //delete room from favourites
                    btn_favourite.setImageResource(R.drawable.star_border); //and change the image to indicate that it is no longer a favourite
                }else {
                    addToFavourites(); //else if the room is not a favourite then add it to favourites
                    btn_favourite.setImageResource(R.drawable.star_filled); //and change the image on the button to indicate that it is now a favourite
                }
            }
        });
    }

    //this method populates the text view and image view on the activity
    public void populateFields(){
        Bundle b = getIntent().getExtras(); //get the bundle which was passed to this activity (when a room is selected)
        //the following variables are used to store the details of the room which was selected in the main activity
        String roomNo = ""; //initialise room number
        String firstName =""; //initialise first name
        String lastName = ""; //initialise last name

        if(b != null) { //if bundle is not null
            firstName = b.getString(MainActivity.FIRST_NAME); //get the first name of the tutor
            lastName = b.getString(MainActivity.LAST_NAME); //get the last name of the tutor
            roomNo = b.getString(MainActivity.ROOM_NO); //get the room number
        }
        foundRooms.clear(); //clear list of found rooms (to update it)
        for (int i = 0; i< allRooms.size(); i++){ //for every room retrieved from the database
            if (allRooms.get(i).getNumber().equals(roomNo)){ //if the room number matches the room number of the selected room
                foundRooms.add(allRooms.get(i)); //then add it to the list of found rooms
            }
        }
        if (foundRooms.size() > 0){ //if any matching rooms have been found
            for (int i =0; i<foundRooms.size(); i++){ //for each matching room found
                Room currentRoom = foundRooms.get(i); //get each room
                if(currentRoom.getNumber().equals(roomNo) && currentRoom.getFirstName().equals(firstName) && currentRoom.getLastName().equals(lastName)){ //if the first name and last name also match the selected room
                    txt_room_header.setText("Room "+currentRoom.getNumber()+" (Level "+ currentRoom.getLevel()+ ")");  //then display the name and floor number of the room
                    Glide.with(this) //load the image of the room using the Glide library
                            .load(currentRoom.getImage()) // image url
                            .placeholder(R.drawable.loading) // any placeholder to load at start
                            .error(R.drawable.logo2)  // any image in case of error
                            .centerCrop().into(img_room_pic); // resizing
                    thisRoom= currentRoom; //set the global variable "thisRoom" to the current room as this is the room that matches room number AND first name and last name
                    if(!currentRoom.getViaRoom().equals("")) //if room has a via room
                        txt_via_room.setText("Access via room "+ currentRoom.getViaRoom()); //then display the via room
                }
                txt_tutors.append(currentRoom.getFirstName() + " " + currentRoom.getLastName() + " | " + currentRoom.getGroup() +"\n"); //append the name of the tutor (more than one tutor may be in this room)
            }
        }
        if(isFavourite()){ //if this room is a favourite room
            btn_favourite.setImageResource(R.drawable.star_filled); //then display a filled star on btn_favourite
        }else{
            btn_favourite.setImageResource(R.drawable.star_border); //else display an empty star on btn_favourite
        }

    }

    //this method removes the current room from favourites by removing the line which refers to this room in the favourites file
    public void removeFromFavourites() {
        String lineToRemove = (thisRoom.getNumber() + SEPARATOR + thisRoom.getFirstName() + SEPARATOR + thisRoom.getLastName()); //put the details of the current room into the correct format of the favourites file
        //this line needs to be removed from the file
        ArrayList<String> newFavourites = new ArrayList<>(); //array of rooms which should remain favourites
        try {
            File oldFile = getFileStreamPath(FILE_NAME); //the current favourites file which needs to be deleted
            InputStream inputStream = openFileInput(oldFile.getName()); //input stream used to read the current favourites file
            //read old file and add every line to new file except the line which is to be deleted
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = ""; //initialise string which is read from the file

                while ((receiveString = bufferedReader.readLine()) != null) { //while file has another line of text
                    if (!receiveString.equals(lineToRemove)) { //if this line is NOT the room which needs to be removed
                        newFavourites.add(receiveString); //then add this room to the new list of favourites
                    }
                }
                oldFile.delete(); //delete old file
                inputStream.close(); //close the input stream
            }
            writeFavouritesToFile(newFavourites); //write the new list of favourites back onto a new favourites file (with the same file name)
        }
        catch (FileNotFoundException ex) { //catch file not found exception and display error message
            Toast toast = Toast.makeText(RoomActivity.this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (IOException ex) { //catch IO exception and display error message
            Toast toast = Toast.makeText(RoomActivity.this, "File could not be read", Toast.LENGTH_SHORT);
            toast.show();
        }
        //display message to confirm deletion
        Toast toast = Toast.makeText(RoomActivity.this, "This room has been removed from favourites", Toast.LENGTH_SHORT);
        toast.show();
    }


    //this method adds the current room to favourites
    private void addToFavourites() {
        try {
            String roomNum = thisRoom.getNumber(); //get the room number of the current room
            String firstName = thisRoom.getFirstName(); //get the first name of the tutor in the current room
            String lastName = thisRoom.getLastName(); //get the last name of the tutor in the current room
            String input = (roomNum + SEPARATOR + firstName + SEPARATOR + lastName+"\n"); //put the room information in the correct format to be stored on the file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_APPEND)); //create output stream writer to write to file
            outputStreamWriter.write(input); //write the room info to the file
            outputStreamWriter.flush(); //flush output stream
            outputStreamWriter.close(); //close stream writer
        }
        catch (IOException e) { //catch IO exception and display error message
            Toast toast = Toast.makeText(RoomActivity.this, "File could not be written to", Toast.LENGTH_SHORT);
            toast.show();
        }
        //show message to confirm that the room has been added to favourites
        Toast toast = Toast.makeText(RoomActivity.this, "This room has been added to favourites", Toast.LENGTH_SHORT);
        toast.show();

    }

    //this method writes a list of strings (rooms in string format) to the favourites file
    private void writeFavouritesToFile(ArrayList<String> favourites) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_APPEND)); //create output stream writer to write to favourites file
            for(int i = 0; i<favourites.size();i++){ //for every room in the list of favourites
                String input = favourites.get(i);
                outputStreamWriter.write(input+"\n"); //write the room to the file and go on a new line
            }
            outputStreamWriter.flush(); //flush output stream writer
            outputStreamWriter.close(); //close the stream writer
        }
        catch (IOException e) { //catch IO exception adn display error message
            Toast toast = Toast.makeText(RoomActivity.this, "File could not be written to", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    //this method checks whether the current room is on the favourites file
    private boolean isFavourite() {
        ArrayList<String> favourites = readFromFile(); //read list of favourites from the file
        boolean favourite = false; //initialise favourites variable
        for(int i = 0; i<favourites.size(); i++){ //for each item in the favourites list
            String[] splitStrings = favourites.get(i).trim().split(SEPARATOR); //split up the text string and
            if (splitStrings[0].equals(thisRoom.getNumber()) && splitStrings[1].equals(thisRoom.getFirstName()) && splitStrings[2].equals(thisRoom.getLastName())){ //check if this room is in the list of favourites
                favourite = true; //if yes then set to true
            }
        }
        return favourite; //return whether the room is a favourite or not
    }

    //this method reads each line of the file and adds each line to a list. this list is then returned
    private ArrayList<String> readFromFile() {
        ArrayList<String> favourites = new ArrayList<>(); //declare and initialise a list of strings
        try {
            OutputStream outputStream = openFileOutput(FILE_NAME,MODE_APPEND); //establish output stream first in order to make sure that a favourites file is created if it does not already exist
            InputStream inputStream = openFileInput(FILE_NAME); //create input stream to the favourites file

            if ( inputStream != null && outputStream != null) { //check that input and output streams successfully established
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) { //while file has next line
                    favourites.add(receiveString); //add each line to a list
                }
                inputStream.close(); //close input and output streams
                outputStream.close();
            }
        }
        catch (FileNotFoundException e) { //catch file not found exception and display error exception
            Toast toast = Toast.makeText(RoomActivity.this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) { //catch IO exception and display error message
            Toast toast = Toast.makeText(RoomActivity.this, "Could not read file", Toast.LENGTH_SHORT);
            toast.show();
        }
        return favourites; //return list of string read from the favourites file
    }
}
