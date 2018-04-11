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
            nextNode = getNextNearestNode(); // next node must be with shortest distance

        }
        ArrayList<Edge> routeEdges = new ArrayList<>(); //this list will hold the edges which make up the shortest route
        ArrayList<String> directions = new ArrayList<>(); //this will hold the written directions for the shortest route
        int i = destinationIndex; //the index of the destination node
        //the following loop works backwards from the destination node, taking the last shortest edge, adding it to the route list,
        //then taking the neighbour node and repeating the process until the source node is reached
        do{
            Node currentNode = nodes.get(i); //the current node
            routeEdges.add(currentNode.lastShortestEdge); //add the last shortest edge to the route list
            //the next if statement checks which side of the edge the current node is on
            if(currentNode.lastShortestEdge.getFromNodeIndex() == i){ //if current node index is on the "from" side
                directions.add(currentNode.lastShortestEdge.getDirectionToNode()); //add direction from node to the directions list
                i = currentNode.lastShortestEdge.getToNodeIndex(); //set current node to the other node on  this edge

            }else{//else, current node is on the "to" side
                directions.add(currentNode.lastShortestEdge.getDirectionFromNode()); //add direction to node to the directions list
                i = currentNode.lastShortestEdge.getFromNodeIndex(); //set current node to the other node on this edge
            }
        }while(i!=sourceIndex); //repeat the loop until the source node is reached and the route is complete
        return directions; //when the route is complete, return the list of directions
    }
    //this method finds index of the next node nearest to the source which has not been visited yet
    private int getNextNearestNode() {
        int storedNodeIndex = 0; //initialise node index
        double storedDist = Integer.MAX_VALUE; //initialise stored distance, must be high because the method is looking for the lowest distance
        for (int i = 0; i < nodes.size(); i++) { //for every node in the nodes list
            double currentDist = nodes.get(i).getDistanceFromSource(); //get the distance from the source
            if (!nodes.get(i).isVisited() && currentDist < storedDist) { //if node has not been visited and distance is lowest so far
                storedDist = currentDist; //update the stored distance
                storedNodeIndex = i; //update the index of the next nearest node
            }
        }
        return storedNodeIndex; //return index of the next nearest node that has not been visited yet
    }
}
