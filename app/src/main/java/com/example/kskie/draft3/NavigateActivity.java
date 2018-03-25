package com.example.kskie.draft3;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class NavigateActivity extends AppCompatActivity {

    ArrayList<Room> roomList = new ArrayList<>();

    ArrayList<String> output = new ArrayList<>();

    DatabaseReference databaseReference;

    TextView txtWrite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        txtWrite = findViewById(R.id.txtWrite);

        databaseReference = FirebaseDatabase.getInstance().getReference();
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

        writeToFile("1.1","smeg","smeg2");
        writeToFile("2.2","poop","poop2");
        output = readFromFile();
        for(int i = 0; i<output.size(); i++){
            txtWrite.append(output.get(i));
        }



    }



    private void writeToFile(String roomNum, String firstName, String lastName) {
        try {
            String input = (roomNum + "%" + firstName + "%" + lastName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("cyc.txt", Context.MODE_APPEND));
            outputStreamWriter.write(input);
            outputStreamWriter.close();
        }
        catch (IOException e) {

        }

    }

    private ArrayList<String> readFromFile() {

        ArrayList<String> ret = new ArrayList<>();

        try {
            InputStream inputStream = openFileInput("cyc.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

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

        return ret;
    }

    private ArrayList<Room> findRooms(ArrayList<String> fileData){
        ArrayList<Room> foundRooms = new ArrayList<>();

        for(int i = 0; i<fileData.size(); i++){
            String[] splitStrings = fileData.get(i).split("%");
            String roomNum = splitStrings[0];
            String firstName = splitStrings[1];
            String lastName = splitStrings[2];;

            for(int j = 0; j<roomList.size(); i++){
                if( roomList.get(j).getNumber() == roomNum && roomList.get(j).getFirstName() == firstName && roomList.get(j).getLastName() == lastName ){
                    foundRooms.add(roomList.get(j));
                }
            }

        }
        return foundRooms;


    }

}
