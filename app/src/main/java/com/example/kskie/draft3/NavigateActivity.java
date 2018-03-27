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
    TextView txtDirections;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        txtDirections = findViewById(R.id.txtDirections);
        Edge[] edges = {
                new Edge(0, 2, 1), new Edge(0, 3, 4), new Edge(0, 4, 2),
                new Edge(0, 1, 3), new Edge(1, 3, 2), new Edge(1, 4, 3),
                new Edge(1, 5, 1), new Edge(2, 4, 1), new Edge(3, 5, 4),
                new Edge(4, 5, 2), new Edge(4, 6, 7), new Edge(4, 7, 2),
                new Edge(5, 6, 4), new Edge(6, 7, 5)
        };
        Graph g = new Graph(edges);
        g.calculateShortestDistances();
        txtDirections.append(g.printResult()); // let's try it !
    }


    





}
