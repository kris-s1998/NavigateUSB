package com.example.kskie.draft3;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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

    ArrayList <Node> nodes = new ArrayList<>();
    ArrayList <Edge> edges = new ArrayList<>();
    ArrayList <Node> foundNodes = new ArrayList<>();
    DatabaseReference nodesReference;
    DatabaseReference edgesReference;
    TextView txtDirections;

    EditText editTextStart;
    EditText editTextDest;

    RecyclerView recyclerStart;
    RecyclerView recyclerDest;

    NavigateSearchAdapter searchAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        editTextStart = findViewById(R.id.edit_text_start);
        editTextDest = findViewById(R.id.edit_text_dest);
        txtDirections = findViewById(R.id.txtDirections);
        recyclerStart = findViewById(R.id.recyclerStart);
        recyclerDest = findViewById(R.id.recyclerDest);

        nodesReference = FirebaseDatabase.getInstance().getReference("nodes");
        edgesReference = FirebaseDatabase.getInstance().getReference("edges");

        nodesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Node> unorderedNodes = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Node n = d.getValue(Node.class);
                    unorderedNodes.add(n);
                }

                //sort the nodes in order of index, so that the index value of each node matches its position in the nodes array list
                nodes.clear();
                for(int i = 0; i<unorderedNodes.size(); i++){
                    for(Node n : unorderedNodes) {
                        if (n.getIndex() == i){
                            nodes.add(n);
                            break;
                        }

                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        edgesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                edges.clear();

                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Edge e = d.getValue(Edge.class);
                    edges.add(e);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editTextStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable c) {
                if (!c.toString().isEmpty()){
                    setSearchAdapter(c.toString());
                    //txtDirections.append(""+nodes.size());
                }else{
                    foundNodes.clear();

                }
            }
        });



        /*

        edges.add(new Edge(1, 2, 2, "Turn to the right to find the cafe in the corner.", "Walk straight to the foyer."));
        edges.add(new Edge(1, 3, 1, "Walk straight to the ground floor stairs ahead.", "From the stairs, turn left to the foyer."));
        edges.add(new Edge(3, 2, 2, "Sharp turn left from the stairs and walk straight past reception.", "Walk straight from the cafe to find the stairs on the right."));
        edges.add(new Edge(3, 4, 1, "Walk up the stairs to the first floor","Walk down the stairs to the ground floor"));
        edges.add(new Edge(4, 6, 2,"From the stairs, walk straight down to the corridor","Walk straight ahead from the corridor to the stairs" ));
        edges.add(new Edge(6, 5, 2, "The door to the lecture theatre is in the left corner","Walk straight to the corridor opposite the lecture theatre."));
        edges.add(new Edge(6, 7, 1, "The room is directly opposite the stairs on the first floor", "The corridor is directly outside the room"));
        edges.add(new Edge(6, 8, 3, "Room 1.025 is in the far left corner", "Walk left to the first floor corridor"));
        edges.add(new Edge(6, 9, 1, "Room 1.025a is on the left, adjacent to room 1.025b.","Walk straight ahead to the first floor corridor"));
        edges.add(new Edge(6, 10, 2,"Turn right and walk down the end of the corridor to find room 1.025b at the end",""));
        edges.add(new Edge(10, 9, 1,"",""));
        edges.add(new Edge(8, 9, 1,"",""));
        edges.add( new Edge(10, 0, 3,"",""));
*/
     //   Graph g = new Graph(edges, nodes);
    //    ArrayList<String> route = g.calculateShortestDistances(8,1);
    //    for(int i = route.size() - 1; i>-1 ; i--){
     //       txtDirections.append("\n"+route.get(i));
     //   }
     //   txtDirections.append(g.printResult()); // let's try it !
    }


    public void setSearchAdapter( String searchString) {
        searchString = searchString.toLowerCase();
        foundNodes.clear();
        Node currentNode;
        for(int i = 0; i<nodes.size(); i++) {
            currentNode = nodes.get(i);

                if (currentNode.getLocation().toLowerCase().contains(searchString) ) {
                    foundNodes.add(currentNode);
                }

        }


        searchAdapter = new NavigateSearchAdapter(NavigateActivity.this, foundNodes);
        recyclerStart.setAdapter(searchAdapter);
    }





}
