package com.example.kskie.draft3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    ArrayList<Room> roomList = new ArrayList<>();
    ArrayList<Room> foundRooms = new ArrayList<>();

    DatabaseReference databaseReference;

    TextView roomHeader;
    TextView roomDesc;
    TextView tutorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomHeader = findViewById(R.id.room_header);
        roomDesc = findViewById(R.id.room_desc);
        tutorList = findViewById(R.id.tutor_list);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Room r = d.getValue(Room.class);
                    roomList.add(r);
                }
                populateFields();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




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
                    if(!currentRoom.getViaRoom().equals(""))
                        roomDesc.setText("Access via room "+ currentRoom.getViaRoom());
                }
                tutorList.append(currentRoom.getFirstName() + " " + currentRoom.getLastName() + " | " + currentRoom.getGroup() +"\n");
            }
        }

    }

}
