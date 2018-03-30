package com.example.kskie.draft3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    public static final String FILE_NAME = "testFile.txt";
    public static final String SEPARATOR = "%";

    ArrayList<Room> roomList;
    ArrayList<Room> foundRooms;
    Room thisRoom;

    DatabaseReference databaseReference;

    TextView roomHeader;
    TextView roomDesc;
    TextView tutorList;
    ImageButton btnFavourite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomList = new ArrayList<>();
        foundRooms = new ArrayList();

        roomHeader = findViewById(R.id.room_header);
        roomDesc = findViewById(R.id.room_desc);
        tutorList = findViewById(R.id.tutor_list);
        btnFavourite = findViewById(R.id.btn_favourite);


        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){
                    roomList.add(d.getValue(Room.class));
                }
                    populateFields();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavourite()) { //if this room is already a favourite then delete it
                    removeFromFavourites(); //delete room from favourites
                    btnFavourite.setImageResource(R.drawable.star_border); //and change the image to indicate that it is no longer a favourite
                }else {
                    addToFavourites(); //else if the room is not a favourite then add it to favourites
                    btnFavourite.setImageResource(R.drawable.star_filled); //and change the image on the button to indicate that it is now a favourite
                }
            }
        });

        readFromFile();
        if(roomList.size()>0){
            if (isFavourite()) {
                btnFavourite.setImageResource(R.drawable.star_filled);
            }else {
                btnFavourite.setImageResource(R.drawable.star_border);
            }
        }




    }


    public void populateFields(){
        Bundle b = getIntent().getExtras();
        String roomNo = "";
        if(b != null) {
            roomNo = b.getString("roomNo");
        }  //else go back to main activity?

        foundRooms.clear();
        for (int count = 0; count<roomList.size(); count++){

            if (roomList.get(count).getNumber().equals(roomNo)){
                foundRooms.add(roomList.get(count));
                //roomHeader.setText("hi");
            }
        }
        if (foundRooms.size() > 0){
            for (int i =0; i<foundRooms.size(); i++){
                Room currentRoom = foundRooms.get(i);
                if(i == 0){
                    roomHeader.setText("Room "+currentRoom.getNumber()+" (Level "+ currentRoom.getLevel()+ ")");
                    thisRoom= new Room(currentRoom);
                    if(!currentRoom.getViaRoom().equals(""))
                        roomDesc.setText("Access via room "+ currentRoom.getViaRoom());
                }
                tutorList.append(currentRoom.getFirstName() + " " + currentRoom.getLastName() + " | " + currentRoom.getGroup() +"\n");
            }
        }
        if(isFavourite()){
            btnFavourite.setImageResource(R.drawable.star_filled);
        }else{
            btnFavourite.setImageResource(R.drawable.star_border);
        }

    }

    public void removeFromFavourites() {

        //remove from favourites by removing the line which refers to this room in the favourites file
        String lineToRemove = (thisRoom.getNumber() + SEPARATOR + thisRoom.getFirstName() + SEPARATOR + thisRoom.getLastName());
        ArrayList<String> newFavourites = new ArrayList<>(); //array of rooms which should remain favourites
        try {
            File oldFile = getFileStreamPath(FILE_NAME);
            InputStream inputStream = openFileInput(oldFile.getName());

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
        catch (FileNotFoundException ex) {
            Toast toast = Toast.makeText(RoomActivity.this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (IOException ex) {
            Toast toast = Toast.makeText(RoomActivity.this, "File could not be read", Toast.LENGTH_SHORT);
            toast.show();
        }
        Toast toast = Toast.makeText(RoomActivity.this, "This room has been removed from favourites", Toast.LENGTH_SHORT);
        toast.show();
    }


    private void addToFavourites() {
        try {
            String roomNum = thisRoom.getNumber();
            String firstName = thisRoom.getFirstName();
            String lastName = thisRoom.getLastName();
            String input = (roomNum + SEPARATOR + firstName + SEPARATOR + lastName+"\n");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_APPEND));
            outputStreamWriter.write(input);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Toast toast = Toast.makeText(RoomActivity.this, "File could not be written to", Toast.LENGTH_SHORT);
            toast.show();
        }
        Toast toast = Toast.makeText(RoomActivity.this, "This room has been added to favourites", Toast.LENGTH_SHORT);
        toast.show();

    }

    private void writeFavouritesToFile(ArrayList<String> favourites) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_APPEND));
            for(int i = 0; i<favourites.size();i++){
                String input = favourites.get(i);
                outputStreamWriter.write(input+"\n");

            }
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Toast toast = Toast.makeText(RoomActivity.this, "File could not be written to", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private boolean isFavourite() {
        ArrayList<String> favourites = readFromFile(); //read list of favourites from the file
        boolean favourite = false; //initialise favourites variable
        for(int i = 0; i<favourites.size(); i++){ //for each item in the favourites list
            String[] splitStrings = favourites.get(i).trim().split(SEPARATOR); //split up the text string and
            if (splitStrings[0].equals(thisRoom.getNumber())){ //check if this room is in the list of favourites
                favourite = true; //if yes then set to true
            }
        }
        return favourite;


    }

    private ArrayList<String> readFromFile() {

        ArrayList<String> favourites = new ArrayList<>();

        try {
            OutputStream outputStream = openFileOutput(FILE_NAME,MODE_APPEND); //establish output stream first in order to make sure that a favourites file is created if it does not already exist
            InputStream inputStream = openFileInput(FILE_NAME); //create input stream to the favourites file

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
            Toast toast = Toast.makeText(RoomActivity.this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) {
            Toast toast = Toast.makeText(RoomActivity.this, "Could not read file", Toast.LENGTH_SHORT);
            toast.show();
        }
        return favourites;
    }


}
