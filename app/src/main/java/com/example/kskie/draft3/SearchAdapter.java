package com.example.kskie.draft3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by kskie on 21/02/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;

    ArrayList<Room> foundRooms;

    class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView roomNumber, tutorName;

        public SearchViewHolder(View itemView) {
            super(itemView);
            roomNumber = itemView.findViewById(R.id.roomNumber);
            tutorName = itemView.findViewById(R.id.tutorName);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RoomActivity.class);
                    Bundle b = new Bundle();
                    Room selectedRoom = foundRooms.get(getLayoutPosition());
                    b.putString(MainActivity.ROOM_NO,selectedRoom.getNumber());
                    b.putString(MainActivity.FIRST_NAME,selectedRoom.getFirstName());
                    b.putString(MainActivity.LAST_NAME,selectedRoom.getLastName());
                    intent.putExtras(b);
                    context.startActivity(intent);
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
        holder.roomNumber.setText(currentRoom.getFirstName() + " " + currentRoom.getLastName());
        holder.tutorName.setText("Room "+ currentRoom.getNumber() + " (Level " + currentRoom.getLevel() + ")");

    }

    @Override
    public int getItemCount() {
        return foundRooms.size();
    }
}
