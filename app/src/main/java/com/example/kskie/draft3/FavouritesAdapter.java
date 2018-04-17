package com.example.kskie.draft3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class is used as an adapter for the list of favourite rooms.
 * It is used to display information about the favourite rooms.
 *
 * Created by Kris Skierniewski on 26/03/2018.
 */

public class FavouritesAdapter extends BaseAdapter {

    private ArrayList<Room> favourites; //a list of the favourite rooms
    private TextView tutorName, roomNumber; //the text views used for to display each roomm
    private Activity parentActivity; //the parent activity which called the favourites adapter class
    private LayoutInflater inflater; //layout inflater used to instantiate a layout XML file into its corresponding view objects


    //constructor used to initialise fields of the class
    public FavouritesAdapter(Activity parent, ArrayList<Room> favourites) {
        parentActivity = parent;
        this.favourites = favourites;
        inflater = (LayoutInflater)parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return favourites.size();
    }

    @Override
    public Object getItem(int i) {
        return favourites.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){ //if the current view cannot be recycled
            convertView = inflater.inflate(R.layout.search_list_items, null); //create new view
        }
        roomNumber = convertView.findViewById(R.id.txt_room_number); //initiate the room number text view
        tutorName = convertView.findViewById(R.id.txt_tutor_name); //initiate the tutor name text view
        Room currentRoom = favourites.get(position); //get the current room object and
        roomNumber.setText(currentRoom.getFirstName() + " " + currentRoom.getLastName()); //change the text in the view to represent the current room
        tutorName.setText("Room "+ currentRoom.getNumber() + " (Level " + currentRoom.getLevel() + ")");
        return convertView; //return updated/new view
    }



}
