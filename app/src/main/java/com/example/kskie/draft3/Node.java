package com.example.kskie.draft3;

import java.util.ArrayList;

/** This class is used to store information about the nodes (locations) in the building
 *
 * Created by Kris Skierniewski on 27/03/2018.
 */

public class Node {

    private double distanceFromSource = Integer.MAX_VALUE; //distance from source is initialised to a very high value (following djkstra's algorithm)
    private boolean visited; //flag to store whether the node has been visited or not
    private ArrayList<Edge> edges = new ArrayList<>(); // a list of edges attached to this node
    public Edge lastShortestEdge; //the last shortest edge (on the shortest route from the source) to this node
    private String location; //the location associated with this node
    private int index; //the index of the node

    public Node(){
        //the empty constructor is required to load data from the database
    }

    //constructor which initialises fields
    public Node(int index, String location){
        this.index = index;
        this.location = location;

    }

    //getter methods for all the field
    public String getLocation() {
        return location;
    }
    public int getIndex() {
        return index;
    }
    public double getDistanceFromSource() {
        return distanceFromSource;
    }

    public boolean isVisited() {
        return visited;
    }
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge){
        edges.add(edge); //add an edge to this node
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    } //setter for isVisited
    public void setDistanceFromSource(double distanceFromSource) { //setter for distanceFromSource
        this.distanceFromSource = distanceFromSource;}

}
