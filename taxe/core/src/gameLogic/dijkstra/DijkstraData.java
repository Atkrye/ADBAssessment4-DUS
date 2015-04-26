package gameLogic.dijkstra;

import java.util.ArrayList;

/** class is an abstract data structure used to be easily searchable and contain the results of running dijkstra's algorithm on the graph */  
public class DijkstraData {
	/** The source vertex of this particular path*/
    private Vertex source;
    
    /** The target vertex of this particular path*/
    private Vertex target;
    
    /** The distance of the shortest path between source and target*/
    private double distance;
    
    /** The shortest distance path that starts at source and completes at target*/
    private ArrayList<Vertex> shortestPath;

    /** Instantation
     * @param source Vertex to start the path from
     * @param target Vertex to finish the path at
     * @param distance The distance of the min path from source to target
     * @param shortestPath The path with the shortest distance from source to target
     */
    public DijkstraData(Vertex source, Vertex target, double distance, ArrayList<Vertex> shortestPath) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.shortestPath = shortestPath;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getTarget() {
        return target;
    }

    public double getDistance() {
        return distance;
    }

    /** Returns whether given station string is featured in shortest path
     * @param stationName The string that represents station, tested whether its in the shortest path
     * @return True if station with name stationName is in the shortest path from source to target
     */
    public boolean inShortestPath(String stationName) {
        for (Vertex v : shortestPath) {
            if (v.getName().equals(stationName)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return source.getName() + " to " + target.getName() + ": " + distance;
    }
}
