package com.example.kskie.draft3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**This class is an adapter for the recycler view which displays the search results
 *
 * Created by Kris Skierniewski on 21/02/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;

    ArrayList<Room> foundRooms; //a list fo rooms found which match the search criteria

    class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView txt_room_number, txt_tutor_name; //text views used to display each room

        public SearchViewHolder(View itemView) {
            super(itemView);
            txt_room_number = itemView.findViewById(R.id.txt_room_number); //intialise the text views
            txt_tutor_name = itemView.findViewById(R.id.txt_tutor_name);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //when an item is selected, create a new bundle and put the room number, first name and last name into the bundle
                    //then start the room activity, passing the bundle with the room information so the room can be retrieved in that activity
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RoomActivity.class);
                    Bundle b = new Bundle(); //used to pass room information between the two activities
                    Room selectedRoom = foundRooms.get(getLayoutPosition()); //get the selected room
                    b.putString(MainActivity.ROOM_NO,selectedRoom.getNumber());
                    b.putString(MainActivity.FIRST_NAME,selectedRoom.getFirstName());
                    b.putString(MainActivity.LAST_NAME,selectedRoom.getLastName());
                    intent.putExtras(b);
                    context.startActivity(intent); //start room activity
                }
            });
        }
    }

    public SearchAdapter(Context context, ArrayList<Room> foundRooms) {
        this.context = context;
        this.foundRooms = foundRooms;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_items, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        Room currentRoom = foundRooms.get(position);
        holder.txt_room_number.setText(currentRoom.getFirstName() + " " + currentRoom.getLastName()); //display the first name and last name of the tutor in this room
        holder.txt_tutor_name.setText("Room "+ currentRoom.getNumber() + " (Level " + currentRoom.getLevel() + ")"); //display the room number and level which the floor is on
    }

    @Override
    public int getItemCount() {
        return foundRooms.size();
    }
}
