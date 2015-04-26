package gameLogic.dijkstra;

import gameLogic.map.Map;
import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/** Master class that controls and uses Dijkstra's algorithm on a map*/
public class Dijkstra {
	/** List of vertices for map*/
    ArrayList<Vertex> vertices;
    
    /** List of DijkstraData for all of the possible paths on a map*/
    ArrayList<DijkstraData> dijkstras = new ArrayList<DijkstraData>();

    /** Compute the paths that can come from a given vertex
     * @param source The source vertex tyo test paths from 
     */
    public void computePaths(Vertex source) {
        for (Vertex v : vertices) {
            //Resets the necessary values for all vertices
            v.setMinDistance(Double.POSITIVE_INFINITY);
            v.setPrevious(null);
        }
        source.setMinDistance(0.);
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.getAdjacencies()) {
                Vertex v = e.getTarget();
                double weight = e.getWeight();
                double distanceThroughU = u.getMinDistance() + weight;
                if (distanceThroughU < v.getMinDistance()) {
                    //Continuously adds the smallest distance vertices to the queue until they have all been checked
                    vertexQueue.remove(v);
                    v.setMinDistance(distanceThroughU);

                    //Sets previous vertex for each vertex in the queue, this is later used in shortestPath
                    v.setPrevious(u);
                    vertexQueue.add(v);
                }
            }
        }
        return;
    }

    /** Returns the shortest path from the source node to the target node
     * @param target The target node to get the path to
     * @return The path that begins at the source node to target
     */
    public static ArrayList<Vertex> getShortestPathTo(Vertex target) {
        
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious()) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }

    /** Instantation, used to convert a given map into Dijkstra's and a list of vertices
     * @param map Map to convert connections into edges, stations into vertices from
     */
    public Dijkstra(Map map) {
        //Convert the current stations to vertices
        convertToVertices(map);

        //Add the edges to all the vertices
        for (Station s : map.getStations()) {
            addEdges(map, s);
        }

        for (Vertex vSource : vertices) {
            //This sets every node as a source for the algorithm
            computePaths(vSource);
            for (Vertex vDestination : vertices) {
                //This sets every node as a destination for every source for the algorithm. These two for loops cover all combinations of stations
                DijkstraData tempDijkstra = new DijkstraData(vSource, vDestination, vDestination.getMinDistance(), getShortestPathTo(vDestination));
                dijkstras.add(tempDijkstra);

            }
        }
    }

    private void convertToVertices(Map map) {
        //Converts all stations to vertices
        vertices = new ArrayList<Vertex>();
        for (Station s : map.getStations()) {
            Vertex v = new Vertex(s.getName());
            vertices.add(v);
        }
    }

    /**Converts all connections from a given station to edges
     * @param map Map the station is in
     * @param s1 The source station from which connections are found
     */
    private void addEdges(Map map, Station s1) {
        for (Station s2 : map.getStations()) {
            if (map.doesConnectionExist(s1.getName(), s2.getName())) {
                Edge edge = new Edge(findVertex(s2), map.getStationDistance(s1, s2));
                findVertex(s1).addAdjacency(edge);
            }
        }

    }

    /** Convert a given station into a vertex of Dijkstra's
     * @param s A station to convert
     * @return The station, given as a vertex
     */
    private Vertex findVertex(Station s) {
        for (Vertex v : vertices) {
            if (v.getName().equals(s.getName())) {
                return v;
            }
        }
        return null;
    }

    /** Finds the minimum distance between 2 stations*/
    public double findMinDistance(Station s1, Station s2) {
        for (DijkstraData d : dijkstras) {
            if (d.getSource().getName().equals(s1.getName()) && d.getTarget().getName().equals(s2.getName())) {
                return d.getDistance();
            }
        }
        //This return statement is irrelevant as every pair of stations will be represented in dijkstra's, but Java requires a return statement that will always be reached.
        return -1;
    }

    /** Returns whether or not station s3 is in the shortest path between s1 and s2 */
    public boolean inShortestPath(Station s1, Station s2, Station s3) {
        for (DijkstraData d : dijkstras) {
            if (d.getSource().getName().equals(s1.getName()) && d.getTarget().getName().equals(s2.getName())) {
                return d.inShortestPath(s3.getName());
            }
        }
        //This return statement is irrelevant as every pair of stations will be represented in dijkstra's, but Java requires a return statement that will always be reached.
        return false;
    }
}
