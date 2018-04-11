package com.example.kskie.draft3;

/**
 * This class is used to hold information about all the different edges which join all the nodes in a graph.
 * This graph is then used to calculate the shortest route between 2 locations in the building.
 *
 * Created by Kris Skierniewski on 27/03/2018.
 */

public class Edge {

    private int fromNodeIndex; //this variable holds the index of the node at one end of the edge
    private String directionFromNode; //this variable holds the written directions from the node with index "fromNodeIndex" to the node with index "toNodeIndex"
    private int toNodeIndex; //this variable holds the index of the node at the other end of the edge
    private String directionToNode; //this variable holds the directions from the node with index "toNodeIndex" to the node with index "fromNodeIndex"
    private double length; //this variable holds the length of the edge (it is only a symbolic representation and is not to scale)

    //empty contructor is required to get data from a firebase database into objects of this java class
    public Edge(){
    }

    //constructor which initialises all the fields of a new node object
    public Edge(int fromNodeIndex, int toNodeIndex, int length, String directionFromNode, String directionToNode) {
        this.fromNodeIndex = fromNodeIndex;
        this.toNodeIndex = toNodeIndex;
        this.length = length;
        this.directionFromNode = directionFromNode;
        this.directionToNode = directionToNode;
    }

    //fromNodeIndex getter
    public int getFromNodeIndex() {
        return fromNodeIndex;
    }

    //toNodeIndex getter
    public int getToNodeIndex() {
        return toNodeIndex;
    }

    //length getter
    public double getLength() {
        return length;
    }

    // get the index of the neighbour node (the other node which IS NOT the node which has been specified)
    public int getNeighbourNodeIndex(int nodeIndex) {
        if (this.fromNodeIndex == nodeIndex) {
            return this.toNodeIndex;
        } else {
            return this.fromNodeIndex;
        }
    }

    //directionFromNode getter
    public String getDirectionFromNode() {
        return directionFromNode;
    }

    //directionToNode getter
    public String getDirectionToNode() {
        return directionToNode;
    }

}
