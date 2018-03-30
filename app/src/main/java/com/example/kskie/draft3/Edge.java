package com.example.kskie.draft3;

/**
 * Created by kskie on 27/03/2018.
 */

public class Edge {

    private int fromNodeIndex;
    private String directionFromNode;
    private String directionToNode;
    private int toNodeIndex;
    private int length;

    public Edge(){


    }

    public Edge(int fromNodeIndex, int toNodeIndex, int length, String directionFromNode, String directionToNode) {
        this.fromNodeIndex = fromNodeIndex;
        this.toNodeIndex = toNodeIndex;
        this.length = length;
        this.directionFromNode = directionFromNode;
        this.directionToNode = directionToNode;

    }
    public int getFromNodeIndex() {
        return fromNodeIndex;
    }
    public int getToNodeIndex() {
        return toNodeIndex;
    }
    public int getLength() {
        return length;
    }
    // determines the neighbouring node of a supplied node, based on the two nodes connected by this edge
    public int getNeighbourIndex(int nodeIndex) {
        if (this.fromNodeIndex == nodeIndex) {
            return this.toNodeIndex;
        } else {
            return this.fromNodeIndex;
        }
    }

    public String getDirectionFromNode() {
        return directionFromNode;
    }

    public void setDirectionFromNode(String directionFromNode) {
        this.directionFromNode = directionFromNode;
    }

    public String getDirectionToNode() {
        return directionToNode;
    }

    public void setDirectionToNode(String directionToNode) {
        this.directionToNode = directionToNode;
    }
}
