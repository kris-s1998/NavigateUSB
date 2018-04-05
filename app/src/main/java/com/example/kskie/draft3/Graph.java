package com.example.kskie.draft3;

import java.util.ArrayList;

/**
 * This class is used to represent the relationship between all the locations in the building and the directions between them.
 * All the nodes and edges in the building are added to a graph which is used to calculate the shortest route between two locations in the building.
 *
 * Created by Kris Skierniewski on 27/03/2018.
 */

public class Graph {

    private ArrayList<Node> nodes = new ArrayList<>(); //a list of all the nodes (locations) in the building
    private ArrayList<Edge> edges = new ArrayList<>(); //a list of all the edges (connections between the nodes) in the building
    private int noOfEdges; //the total number of edges

    //the constructor initialises all the variables
    public Graph(ArrayList<Edge> edges, ArrayList<Node> nodes) {
        this.edges = edges; //initialise edges list
        this.nodes = nodes; //initialise nodes list
        this.noOfEdges = edges.size(); //initialise number of edges
        for (int edgeToAdd = 0; edgeToAdd < this.noOfEdges; edgeToAdd++) { //add each edge to the corresponding nodes (each node has a list of edges connected to it)
            this.nodes.get(edges.get(edgeToAdd).getFromNodeIndex()).addEdge(edges.get(edgeToAdd)); //each edge is added to 2 nodes because every edge has a node at each end
            this.nodes.get(edges.get(edgeToAdd).getToNodeIndex()).addEdge(edges.get(edgeToAdd));
        }
    }

    /*this method calculates the shortest distance from the source node to every node in the graph, adding the last shortest edge to each node
      then, it backtracks from the destination node, adding the last shortest node at each stage to a list of edges called "route", this forms
      the shortest route from the source node to the destination node
     */
    public ArrayList<String> calculateShortestDistances(int sourceIndex, int destinationIndex) {
        nodes.get(sourceIndex).setDistanceFromSource(0); //initialise the source node, distance from source = 0 because it is the source node
        int nextNode = sourceIndex; //set nextNode to be the source node so that all of the source's edges are explored
        for (int i = 0; i < nodes.size(); i++){ //loop through every node in the building (must visit every node)
            ArrayList<Edge> currentNodeEdges = nodes.get(nextNode).getEdges(); //retrieve a list of all the edges of the current node
            for (int j = 0; j < currentNodeEdges.size(); j++){ //loop through every edge of the current node (must check every edge)
                int neighbourNodeIndex = currentNodeEdges.get(j).getNeighbourNodeIndex(nextNode); //get the index of the neighbour node (i.e. not the node currently being visited)
                if (!nodes.get(neighbourNodeIndex).isVisited()){ //if neighbour node has not been visited then it needs to be checked
                    double temp = nodes.get(nextNode).getDistanceFromSource() + currentNodeEdges.get(j).getLength(); //calculate distance to the neighbour node (distance from source + length of the current edge)
                    if (temp < nodes.get(neighbourNodeIndex).getDistanceFromSource()){ //if this is the shortest way to get to the neighbour node
                        nodes.get(neighbourNodeIndex).lastShortestEdge = currentNodeEdges.get(j); //then set the current edge as the lastShortestEdge of the neighbour node
                        nodes.get(neighbourNodeIndex).setDistanceFromSource(temp); //update the distance from source to the new, smaller value

                    }
                }
            }
            nodes.get(nextNode).setVisited(true); // once all the neighbour nodes have been visited, set the node to visited
            nextNode = getNodeShortestDistanced(); // next node must be with shortest distance

        }
        ArrayList<String> route = new ArrayList<>();
        ArrayList<Edge> properRoute = new ArrayList<>();
        int i = destinationIndex;
        do{
            Node currentNode = nodes.get(i);
            properRoute.add(currentNode.lastShortestEdge);
            if(currentNode.lastShortestEdge.getFromNodeIndex() == i){
                route.add(currentNode.lastShortestEdge.getDirectionToNode());
                i = currentNode.lastShortestEdge.getToNodeIndex();

            }else{
                route.add(currentNode.lastShortestEdge.getDirectionFromNode());
                i = currentNode.lastShortestEdge.getFromNodeIndex();
            }



        }while(i!=sourceIndex);
        return route;
    }
    // now we're going to implement this method in next part !
    private int getNodeShortestDistanced() {
        int storedNodeIndex = 0;
        double storedDist = Integer.MAX_VALUE;
        for (int i = 0; i < nodes.size(); i++) {
            double currentDist = nodes.get(i).getDistanceFromSource();
            if (!nodes.get(i).isVisited() && currentDist < storedDist) {
                storedDist = currentDist;
                storedNodeIndex = i;
            }
        }
        return storedNodeIndex;
    }

    public ArrayList <Node> getNodes() {
        return nodes;
    }
    public ArrayList <Edge> getEdges() {
        return edges;
    }

}
