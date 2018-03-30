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

public class NodesAdapter extends BaseAdapter {

    ArrayList<Node> foundNodes;
    TextView txtLocation;

    private Activity parentActivity;
    private LayoutInflater inflater;


    public NodesAdapter(Activity parent, ArrayList<Node> foundNodes) {
        parentActivity = parent;
        this.foundNodes = foundNodes;
        inflater = (LayoutInflater)parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return foundNodes.size();
    }

    @Override
    public Object getItem(int i) {
        return foundNodes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;
        v = inflater.inflate(R.layout.navigate_list_items, null);
        txtLocation = v.findViewById(R.id.txt_location);
        Node currentNode = foundNodes.get(position);
        txtLocation.setText(currentNode.getLocation());
        return v;
    }



}
