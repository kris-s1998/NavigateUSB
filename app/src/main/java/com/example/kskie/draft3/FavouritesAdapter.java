package com.example.kskie.draft3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kskie on 26/03/2018.
 */

public class FavouritesAdapter extends BaseAdapter {

    ArrayList<Room> favourites;

    TextView tutorName, roomNumber;

    private Activity parentActivity;
    private LayoutInflater inflater;


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

        View v = convertView;
        v = inflater.inflate(R.layout.search_list_items, null);
        roomNumber = v.findViewById(R.id.roomNumber);
        tutorName = v.findViewById(R.id.tutorName);
        Room currentRoom = favourites.get(position);
        roomNumber.setText(currentRoom.getFirstName() + " " + currentRoom.getLastName());
        tutorName.setText("Room "+ currentRoom.getNumber() + " (Level " + currentRoom.getLevel() + ")");
        return v;
    }



}
