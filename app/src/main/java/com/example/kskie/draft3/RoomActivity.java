package com.example.kskie.draft3;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    private static final String FILE_NAME = "hello.txt";

    ArrayList<Room> roomList;
    ArrayList<Room> foundRooms;
    Room thisRoom;

    DatabaseReference databaseReference;

    TextView roomHeader;
    TextView roomDesc;
    TextView tutorList;
    ImageButton btnFavourite;

    TextView txtFile;

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

        txtFile = findViewById(R.id.txtFile);



        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){
                    roomList.add(d.getValue(Room.class));
                    ;
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

                if (isFavourite()) {
                    String delLine = (thisRoom.getNumber() + "%" + thisRoom.getFirstName() + "%" + thisRoom.getLastName());
                    removeLineFromFile(delLine);
                    btnFavourite.setImageResource(R.drawable.star_border);
                }else {
                    writeToFile();
                    btnFavourite.setImageResource(R.drawable.star_filled);
                }
            }
        });

        readFromFile();

        txtFile.setText(" "+roomList.size());




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

    public void removeLineFromFile( String lineToRemove) {


        try {

            File temp = new File("temp.txt");
            File inFile = new File(FILE_NAME);
            InputStream inputStream = openFileInput(inFile.getName());
            OutputStream outputStream = openFileOutput(temp.getName(), Context.MODE_APPEND);

            if ( inputStream != null && outputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    if(!receiveString.trim().equals(lineToRemove)){
                        outputStreamWriter.write(receiveString);
                    }
                }

                inputStream.close();
                outputStream.close();
            }

            if (!deleteFile(FILE_NAME)) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!temp.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeToFile() {
        try {
            String roomNum = thisRoom.getNumber();
            String firstName = thisRoom.getFirstName();
            String lastName = thisRoom.getLastName();
            String input = (roomNum + "%" + firstName + "%" + lastName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_APPEND));
            outputStreamWriter.write(input);
            outputStreamWriter.close();
        }
        catch (IOException e) {

        }

    }

    private boolean isFavourite() {

        ArrayList<String> ret = new ArrayList<>();

        try {
            InputStream inputStream = openFileInput(FILE_NAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    ret.add(receiveString);
                    //stringBuilder.append(receiveString);
                }

                inputStream.close();
                //ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        boolean favourite = false;
        for(int i = 0; i<ret.size(); i++){
            String[] splitStrings = ret.get(i).trim().split("%");
            if (splitStrings[0].equals(thisRoom.getNumber())){
                favourite = true;
            }
        }
        return favourite;


    }

    private ArrayList<String> readFromFile() {

        ArrayList<String> ret = new ArrayList<>();

        try {
            InputStream inputStream = openFileInput(FILE_NAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    ret.add(receiveString);
                    txtFile.append("\n"+receiveString);
                    //stringBuilder.append(receiveString);
                }

                inputStream.close();
                //ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        return ret;
    }


}
