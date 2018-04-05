package com.example.kskie.draft3;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NavigateActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{
    public static int ACTIVITY_NUM = 3;

    ArrayList <Node> nodes = new ArrayList<>();
    ArrayList <Edge> edges = new ArrayList<>();
    DatabaseReference nodesReference;
    DatabaseReference edgesReference;
    TextView txtDirections;
    Button btnGetDirections;

    NodesAdapter searchAdapter;

    AutoCompleteTextView actvStart;
    AutoCompleteTextView actvDest;
    ImageView imgDest;
    ImageView imgStart;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);


        txtDirections = findViewById(R.id.txtDirections);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MenuFragment menuFragment = MenuFragment.newInstance(ACTIVITY_NUM);
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment);
        fragmentTransaction.commit();

        actvStart = findViewById(R.id.actvStart);
        imgStart = findViewById(R.id.startArrow);
        actvDest = findViewById(R.id.actvDest);
        imgDest = findViewById(R.id.destArrow);
        btnGetDirections = findViewById(R.id.btn_getDirections);

        nodesReference = FirebaseDatabase.getInstance().getReference("nodes");
        edgesReference = FirebaseDatabase.getInstance().getReference("edges");

        nodesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Node> unorderedNodes = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Node n = d.getValue(Node.class);
                    unorderedNodes.add(n);
                }

                //sort the nodes in order of index, so that the index value of each node matches its position in the nodes array list
                nodes.clear();
                for (int i = 0; i < unorderedNodes.size(); i++) {
                    for (Node n : unorderedNodes) {
                        if (n.getIndex() == i) {
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

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Edge e = d.getValue(Edge.class);
                    edges.add(e);
                }
                setAdapter();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDirections();
            }
        });

    }

        public void setAdapter(){
        String[] locationsArray = new String[nodes.size()];
        for(Node n:nodes){
            locationsArray[n.getIndex()] = n.getLocation();
        }
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,locationsArray);
        actvStart.setAdapter(adapter);
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvStart.showDropDown();
            }
        });
        ArrayAdapter<String>destAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,locationsArray);
        actvDest.setAdapter(destAdapter);
        imgDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvDest.showDropDown();
            }
        });


    }

       public void showDirections() {
           for(Node n:nodes){ //reset all the nodes back to default
               n.lastShortestEdge=null; //remove all the last shortest edges for each node because the source node may have changed from the previous one
               n.setVisited(false); //reset all nodes' isVisited field
               n.setDistanceFromSource(Integer.MAX_VALUE); //reset all nodes' distance from source fields in case the source node has changed
           }
           txtDirections.setText("");
           Node startNode = new Node();
           Node destinationNode = new Node();
           boolean foundStart = false;
           boolean foundDestination = false;
           String start = actvStart.getText().toString();
           String destination = actvDest.getText().toString();
           if(start.isEmpty() || destination.isEmpty()) {
               Toast toast = Toast.makeText(NavigateActivity.this, "Starting point and destination cannot be empty.", Toast.LENGTH_SHORT);
               toast.show();
           }else if(start.equals(destination)){
               Toast toast = Toast.makeText(NavigateActivity.this, "Starting point and destination cannot be the same.", Toast.LENGTH_SHORT);
               toast.show();
           }else {
               for (Node s:nodes){
                   if(s.getLocation().toLowerCase().equals(start.toLowerCase().trim())){
                       startNode = s;
                       foundStart = true;
                   }
               }
               if (foundStart){
                   for(Node d:nodes){
                       if(d.getLocation().toLowerCase().equals(destination.toLowerCase().trim())){
                           destinationNode = d;
                           foundDestination = true;
                       }

                   }
                   if(foundDestination) {
                       Graph g = new Graph(edges, nodes);
                       ArrayList<String> route = g.calculateShortestDistances(startNode.getIndex(), destinationNode.getIndex());
                       int counter = 1;
                       for (int i = route.size() - 1; i > -1; i--) {
                           txtDirections.append("" + counter + ".    " + route.get(i)+"\n");
                           counter++;
                       }
                   }else{
                       Toast toast = Toast.makeText(NavigateActivity.this, "Destination not found.", Toast.LENGTH_SHORT);
                       toast.show();
                   }
               }else{
                   Toast toast = Toast.makeText(NavigateActivity.this, "Starting point not found.", Toast.LENGTH_SHORT);
                   toast.show();
               }

           }
       }


    @Override
    public void onFragmentInteraction(Uri uri){
    }
}
