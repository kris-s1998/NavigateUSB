package com.example.kskie.draft3;

import java.util.ArrayList;

/**
 * Created by kskie on 27/03/2018.
 */

public class Node {

    private int distanceFromSource = Integer.MAX_VALUE;
    private boolean visited;
    private ArrayList<Edge> edges = new ArrayList<>(); // now we must create edges
    public ArrayList<Edge> path = new ArrayList<>();
    public Edge lastShortestEdge;
    private String location;
    private int index;

    public Node(){


    }

    public Node(int index, String location){

        this.index = index;
        this.location = location;

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDistanceFromSource() {
        return distanceFromSource;
    }
    public void setDistanceFromSource(int distanceFromSource) {
        this.distanceFromSource = distanceFromSource;
    }
    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    public ArrayList<Edge> getEdges() {
        return edges;
    }
    public void addEdge(Edge edge){
        edges.add(edge);
    }
    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

}
