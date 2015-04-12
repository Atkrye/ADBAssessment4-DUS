package gameLogic.dijkstra;

import gameLogic.map.Map;
import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Dijkstra {
    ArrayList<Vertex> vertices;
    ArrayList<DijkstraData> dijkstras = new ArrayList<DijkstraData>();

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

    public static ArrayList<Vertex> getShortestPathTo(Vertex target) {
        //Returns the shortest path from the source node to the target node
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious()) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }


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

    private void addEdges(Map map, Station s1) {
        //Converts all connections to edges
        for (Station s2 : map.getStations()) {
            if (map.doesConnectionExist(s1.getName(), s2.getName())) {
                Edge edge = new Edge(findVertex(s2), map.getStationDistance(s1, s2));
                findVertex(s1).addAdjacency(edge);
            }
        }

    }

    private Vertex findVertex(Station s) {
        for (Vertex v : vertices) {
            if (v.getName().equals(s.getName())) {
                return v;
            }
        }
        return null;
    }

    public double findMinDistance(Station s1, Station s2) {
        //Returns the minimum distance between two stations
        for (DijkstraData d : dijkstras) {
            if (d.getSource().getName().equals(s1.getName()) && d.getTarget().getName().equals(s2.getName())) {
                return d.getDistance();
            }
        }
        //This return statement is irrelevant as every pair of stations will be represented in dijkstra's, but Java requires a return statement that will always be reached.
        return -1;
    }

    public boolean inShortestPath(Station s1, Station s2, Station s3) {
        //Returns whether not station s3 is in the shortest path between s1 and s2
        for (DijkstraData d : dijkstras) {
            if (d.getSource().getName().equals(s1.getName()) && d.getTarget().getName().equals(s2.getName())) {
                return d.inShortestPath(s3.getName());
            }
        }
        //This return statement is irrelevant as every pair of stations will be represented in dijkstra's, but Java requires a return statement that will always be reached.
        return false;
    }
}
