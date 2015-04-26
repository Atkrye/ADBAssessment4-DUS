package gameLogic.dijkstra;

import java.util.ArrayList;

/** Class that represents a vertex in DIjkstra's algorithm*/
public class Vertex implements Comparable<Vertex> {
    private final String name;
    //Stores the list of all vertices connected to it and their weights
    private ArrayList<Edge> adjacencies = new ArrayList<Edge>();
    private double minDistance = Double.POSITIVE_INFINITY;
    private Vertex previous;

    public Vertex(String name) {
        this.name = name;
        this.previous = null;
    }

    public String toString() {
        return name;
    }

    /** Compare the vertex to another vertex to determine the smallest weight */
    public int compareTo(Vertex other) {
        //Used as a comparator to compare two vertices, necessary do determine the smallest weight
        return Double.compare(minDistance, other.minDistance);
    }

    public ArrayList<Edge> getAdjacencies() {
        return adjacencies;
    }

    public void addAdjacency(Edge edge) {
        this.adjacencies.add(edge);
    }

    public String getName() {
        return this.name;
    }

    public void setPrevious(Vertex previous) {
        this.previous = previous;
    }

    public Vertex getPrevious() {
        return previous;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }
}