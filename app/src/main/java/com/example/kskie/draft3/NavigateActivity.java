package com.example.kskie.draft3;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    public static int ACTIVITY_NUM = 3; //identifier used by the menu fragment
    /*the class uses all nodes (destinations) to calculate routes, however not all locations are suitable and required to
    * be an option in the drop down menus (start and destination) doNotInclude is an array of generic locations which
    * are not included in the drop down menus to avoid cluttering and confusion, more string can be added here*/
    private String[] doNotInclude = {"corridor","stairs","tea point"};

    private ArrayList <Node> nodes = new ArrayList<>(); //list of all locations in the building
    private ArrayList <Edge> edges = new ArrayList<>(); //list of all connections between the locations in the building

    private DatabaseReference nodesReference; //database reference to the nodes branch
    private DatabaseReference edgesReference; //database reference to the edges branch

    private TextView txt_directions; //used to display directions between the two selected points
    private Button btn_get_directions; //shows directions between the two selected points when clicked
    private AutoCompleteTextView actv_start; //used to input starting point
    private AutoCompleteTextView actv_dest; //used to input destination point
    private ImageView img_btn_dest; //arrow image button to show drop down menu for the starting point field
    private ImageView img_btn_start; //arrow image button to show drop down menu for the destination point
    private ImageView img_start; //icon next to the starting point input field
    private ImageView img_dest; //icon next to the destination point input field
    private Button btn_swap_locations;

    private static final String PREFS = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE); //retrieve shared preferences
        if(prefs.getBoolean(PREF_DARK_THEME, false)){ //if dark mode is activated
            setTheme(R.style.AppTheme_Dark_NoActionBar); //chnage theme to dark on this activity
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        //the following lines of code add the menu fragment to the activity
        FragmentManager fragmentManager = getSupportFragmentManager(); //used to add fragments
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //used to add menu fragment
        MenuFragment menuFragment = MenuFragment.newInstance(ACTIVITY_NUM); //create new instance of menu fragment, passing the activity number as a parameter
        // so that the home button in the will be a different colour to show that this is the current page
        fragmentTransaction.add(R.id.bottomMenuBar, menuFragment); //add menu fragment
        fragmentTransaction.commit(); //commit the transaction

        //initialise the interface components
        txt_directions = findViewById(R.id.txtDirections);
        actv_start = findViewById(R.id.actvStart);
        img_btn_start = findViewById(R.id.startArrow);
        actv_dest = findViewById(R.id.actvDest);
        img_btn_dest = findViewById(R.id.destArrow);
        btn_get_directions = findViewById(R.id.btn_getDirections);
        img_start = findViewById(R.id.startIcon);
        img_dest = findViewById(R.id.destinationIcon);
        btn_swap_locations = findViewById(R.id.btn_swap_locations);
        //add click listener for the swap button
        btn_swap_locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = actv_start.getText().toString(); //store starting location temporarily
                actv_start.setText(actv_dest.getText().toString()); //set start to destination
                actv_dest.setText(start); //set destination to start
            }
        });

        if(prefs.getBoolean(PREF_DARK_THEME, false)) { //if dark mode is activated
            //then change the icons and drop down menu buttons to white so they are visible on the dark background
            img_btn_dest.setImageResource(R.drawable.drop_down_arrow_dark_mode);
            img_btn_start.setImageResource(R.drawable.drop_down_arrow_dark_mode);
            img_dest.setImageResource(R.drawable.destination_location_dark_mode);
            img_start.setImageResource(R.drawable.current_location_dark_mode);
        }

        nodesReference = FirebaseDatabase.getInstance().getReference("nodes"); //initialise nodes database reference
        edgesReference = FirebaseDatabase.getInstance().getReference("edges"); //initialise edges database reference

        //load all nodes from the database into a list, the list is updated if a change is made to the database
        nodesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Node> unorderedNodes = new ArrayList<>(); //a list of nodes in the order in which they are read from the database (random)
                for (DataSnapshot d : dataSnapshot.getChildren()) { //get each child of the nodes branch
                    Node n = d.getValue(Node.class);
                    unorderedNodes.add(n); //and add to the list of unordered nodes
                }
                //sort the nodes in order of index, so that the index value of each node matches its position in the nodes array list, this is necessary for route finding to work
                nodes.clear(); //clear list of nodes in case it was previously already initialised (in case of database update)
                for (int i = 0; i < unorderedNodes.size(); i++) { //add each node to the list of nodes in ascending order
                    for (Node n : unorderedNodes) {
                        if (n.getIndex() == i) {
                            nodes.add(n);
                            break; //if node with current index has been found then break out of the loop
                        }
                    }
                }
                setAdapter(); //when nodes are loaded, add them to the drop down menus (start and destination)
                Bundle b = getIntent().getExtras(); //get the bundle which was passed to this activity (when a room is selected)
                //the following variables are used to store the details of the room which was selected in the main activity
                String roomNo = ""; //initialise room number

                if(b != null) { //only if bundle is not null
                    for(Node n:nodes){ //find the node which matches the room number which was selected in the RoomActivity
                        if(n.getLocation().contains(b.getString(MainActivity.ROOM_NO))){
                            actv_dest.setText(n.getLocation()); //then set the destination to the selected room number
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            //display error message
                Toast toast = Toast.makeText(NavigateActivity.this, "Could not reach database", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //load all edges from the database and add them to a list
        edgesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                edges.clear(); //clear list of edges if previously initialised (in case of update to database)
                for (DataSnapshot d : dataSnapshot.getChildren()) { //for each child in the edges branch of the database
                    Edge e = d.getValue(Edge.class); //get the edge
                    edges.add(e); //and add to the list of edges
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //display error message
                Toast toast = Toast.makeText(NavigateActivity.this, "Could not reach database", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //when get directions button is clicked, calculate and show directions between the two selected points
        btn_get_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDirections();
            }
        });

    }

    //this method adds adapters for the two drop down menus (start and destination)
    public void setAdapter(){
       ArrayList<String> locations = new ArrayList<>(); //a list of locations which are to be included in the drop down menus
       for(Node n:nodes){ //for each node
          if(!stringContainsItemFromArray(n.getLocation(),doNotInclude)) { //check if the nodes locations contains any of the words/phrases which are not to be included
             locations.add(n.getLocation()); //if the node does not contain any of the words/phrases then add it to the list of locations
          }
       }
        //add locations to the starting point drop down menu
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,locations);
        actv_start.setAdapter(adapter);
        img_btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actv_start.showDropDown(); //when the arrow button next to actv_start is clicked, show the drop down menu
            }
        });
        //add locations to the destination point drop down menu
        ArrayAdapter<String>destAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,locations);
        actv_dest.setAdapter(destAdapter);
        img_btn_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actv_dest.showDropDown(); //when the arrow button next to actv_dest is clicked, show the drop down menu
            }
        });


    }

    //this method validates the data input by the user (for start and destination) and displays directions between the two selected points in the building
    public void showDirections() {
        for (Node n : nodes) { //reset all the nodes back to default
            n.lastShortestEdge = null; //remove all the last shortest edges for each node because the source node may have changed from the previous one
            n.setVisited(false); //reset all nodes' isVisited field
            n.setDistanceFromSource(Integer.MAX_VALUE); //reset all nodes' distance from source fields in case the source node has changed
        }
        txt_directions.setText(""); //clear the text view in case it already has text in it from a previous search
        Node startNode = new Node(); //initialise the sarting node
        Node destinationNode = new Node(); //initialise the destination node
        boolean foundStart = false; //used to keep note whether the selected starting point is valid and in the database
        boolean foundDestination = false; //used to keep note whether the selected destination point is valid and in the database
        String start = actv_start.getText().toString(); //retrieve the user's input from the start point text view
        String destination = actv_dest.getText().toString(); //retrieve the user's input from the destination point text view
        if (start.isEmpty() || destination.isEmpty()) { //if any of the input fields are empty
            Toast toast = Toast.makeText(NavigateActivity.this, "Starting point and destination cannot be empty.", Toast.LENGTH_SHORT);
            toast.show(); //then display an error message
        } else if (start.equals(destination)) { //if start and destination are the same
            Toast toast = Toast.makeText(NavigateActivity.this, "Starting point and destination cannot be the same.", Toast.LENGTH_SHORT);
            toast.show(); //then display an error message
        } else { //else check that start is one of the nodes from the list
            for (Node s : nodes) {
                if (s.getLocation().toLowerCase().equals(start.toLowerCase().trim())) { //check each node to see if start matches any of their locations
                    startNode = s; //if so, intiialise start node to the found matching node
                    foundStart = true; //and set found flag to true
                }
            }
            if (foundStart) { //if start has been found then repeat the same process for destination
                for (Node d : nodes) { //check each node to see if destination matches any of them
                    if (d.getLocation().toLowerCase().equals(destination.toLowerCase().trim())) {
                        destinationNode = d; //if so, intialise destination node to the found matching node
                        foundDestination = true; //and set found flag to true
                    }
                }
                if (foundDestination) { //if destination has also been found
                    Graph g = new Graph(edges, nodes); //create a new graph using all the nodes and edges
                    ArrayList<String> route = g.calculateShortestDistances(startNode.getIndex(), destinationNode.getIndex()); //calculate the shortest route between start and destination
                    int counter = 1; //this counter is used to number the directions
                    for (int i = route.size() - 1; i > -1; i--) { //read the "route" list from the back because dijkstra's algorithm finds shortest route backwards
                        txt_directions.append("" + counter + ".    " + route.get(i) + "\n"); //append each direction to the text view
                        counter++; //increment counter for the next direction
                    }
                } else {
                    //if destination has not been found then display error message
                    Toast toast = Toast.makeText(NavigateActivity.this, "Destination not found.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                //if start has not been found then display error message
                Toast toast = Toast.makeText(NavigateActivity.this, "Starting point not found.", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
        try {
            //hide the keyboard so that the user can read the directions easily
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            //exception thrown if keyboard is already hidden, no need to do anything as already hidden
        }
    }

    //this method checks if the specified string contains any of the strings in a string array
    public boolean stringContainsItemFromArray(String input, String[] items) {
        for(int i =0; i < items.length; i++) { //for each item in the array
            if(input.contains(items[i])) { //check if the specified string contains the item in the array
                return true; //if so return true
            }
        }
        return false; //if the string does not contain any items from the array, return false
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //no implementation needed
    }
}
