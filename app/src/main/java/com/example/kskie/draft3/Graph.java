package com.example.kskie.draft3;

import java.util.ArrayList;

/**
 * Created by kskie on 27/03/2018.
 */

public class Graph {

    private ArrayList<Node> nodes = new ArrayList<>();
    private int noOfNodes;
    private ArrayList<Edge> edges = new ArrayList<>();
    private int noOfEdges;


    public Graph(ArrayList<Edge> edges, ArrayList<Node> nodes) {
        this.edges = edges;
        this.nodes = nodes;
        // create all nodes ready to be updated with the edges

        // add all the edges to the nodes, each edge added to two nodes (to and from)
        this.noOfEdges = edges.size();
        for (int edgeToAdd = 0; edgeToAdd < this.noOfEdges; edgeToAdd++) {
            this.nodes.get(edges.get(edgeToAdd).getFromNodeIndex()).addEdge(edges.get(edgeToAdd));
            this.nodes.get(edges.get(edgeToAdd).getToNodeIndex()).addEdge(edges.get(edgeToAdd));
        }
    }


    public ArrayList<String> calculateShortestDistances(int sourceIndex, int destinationIndex) {
        // node 0 as source
        nodes.get(sourceIndex).setDistanceFromSource(0);
        int nextNode = sourceIndex;
        // visit every node
        for (int i = 0; i < nodes.size(); i++) {
            // loop around the edges of current node
            ArrayList<Edge> currentNodeEdges = nodes.get(nextNode).getEdges();
            for (int j = 0; j < currentNodeEdges.size(); j++) {
                int neighbourIndex = currentNodeEdges.get(j).getNeighbourIndex(nextNode);
                if (!nodes.get(neighbourIndex).isVisited()) { //if neighbour node has not been visited then it is a potential node for the shortest route
                    double temp = nodes.get(nextNode).getDistanceFromSource() + currentNodeEdges.get(j).getLength(); //calculate distance if this edge was to be used in the shortest route
                    if (temp < nodes.get(neighbourIndex).getDistanceFromSource()) { //if this is the shortest way to get to the neighbour node then
                        //nodes[neighbourIndex].path.add(currentNodeEdges.get(j));
                        nodes.get(neighbourIndex).lastShortestEdge = currentNodeEdges.get(j);
                        //for(int k = 0; k<nodes[nextNode].path.size(); k++){
                        //    nodes[neighbourIndex].path.add(nodes[nextNode].path.get(k));
                       // }
                        nodes.get(neighbourIndex).setDistanceFromSource(temp); //update the distance from source to the new, smaller value

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
    public int getNoOfNodes() {
        return noOfNodes;
    }
    public ArrayList <Edge> getEdges() {
        return edges;
    }
    public int getNoOfEdges() {
        return noOfEdges;
    }

}
