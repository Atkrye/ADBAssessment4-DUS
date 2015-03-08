package gameLogic.map;

import fvs.taxe.controller.ConnectionController;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.StationController;
import gameLogic.dijkstra.Dijkstra;
import gameLogic.listeners.ConnectionChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Util.Tuple;

import com.badlogic.gdx.math.Vector2;

public class Map {
    private List<Station> stations;
    private List<Connection> connections;
    private Random random = new Random();
    private Dijkstra dijkstra;
    private JSONImporter jsonImporter;

    public Map() {
        stations = new ArrayList<Station>();
        connections = new ArrayList<Connection>();

        //Imports all values from the JSON file using the JSONImporter
        jsonImporter = new JSONImporter(this);

        //Analyses the graph using Dijkstra's algorithm
        dijkstra = new Dijkstra(this);
        
        ConnectionController.subscribeConnectionChanged(new ConnectionChangedListener() {
			
			@Override
			public void removed(Connection connection) {
				connections.remove(connection);
				dijkstra = new Dijkstra(Map.this);
			}
			
			@Override
			public void added(Connection connection) {
				connections.add(connection);
				dijkstra = new Dijkstra(Map.this);
			}
		});
    }

    public boolean doesConnectionExist(String stationName, String anotherStationName) {
        //Returns whether or not the connection exists by checking the two station names passed to it
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            //Checks whether or not the connection has station 1 and station 2 in its attributes, if so returns true, if not returns false
            if (s1.equals(stationName) && s2.equals(anotherStationName)
                    || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return true;
            }
        }

        return false;
    }

    public Connection getConnection(Station station1, Station station2) {
        //Returns the connection that connects station1 and station2 if it exists
        String stationName = station1.getName();
        String anotherStationName = station2.getName();

        //Iterates through every connection and checks them
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            //Checks whether the connection is between station1 and station2 by comparing the start and end to their names
            if (s1.equals(stationName) && s2.equals(anotherStationName) || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return connection;
            }
        }
        return null;
    }

    public Station getRandomStation() {
        //Returns a random station
        return stations.get(random.nextInt(stations.size()));
    }

    public Station addStation(String name, Position location) {
        //This routine adds a new station the list of stations
        Station newStation = new Station(name, location);
        stations.add(newStation);
        return newStation;
    }

    public CollisionStation addJunction(String name, Position location) {
        //This routine adds a new junction to the list of stations
        CollisionStation newJunction = new CollisionStation(name, location);
        stations.add(newJunction);
        return newJunction;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public Connection addConnection(Station station1, Station station2) {
        //Adds a new connection the map
        //This addConnection adds a connection based on stations
        Connection newConnection = new Connection(station1, station2);
        connections.add(newConnection);
        return newConnection;
    }

    //Add Connection by Names
    public Connection addConnection(String station1, String station2) {
        Station st1 = getStationByName(station1);
        Station st2 = getStationByName(station2);
        return addConnection(st1, st2);
    }

    public Station getStationByName(String name) {
        //Returns the station whose name matches the string passed to the method
        int i = 0;
        while (i < stations.size()) {
            if (stations.get(i).getName().equals(name)) {
                return stations.get(i);
            } else {
                i++;
            }
        }
        return null;
    }

    public Station getStationFromPosition(IPositionable position) {
        //Returns the station located at the position passed to the method
        for (Station station : stations) {
            if (station.getPosition().equals(position)) {
                return station;
            }
        }
       return null;
    }

    public List<Station> createRoute(List<IPositionable> positions) {
        //Takes a list of positions and uses these to find the stations at these positions
        //These stations are then added to a list which acts as the route
        List<Station> route = new ArrayList<Station>();

        for (IPositionable position : positions) {
            route.add(getStationFromPosition(position));
        }
        return route;
    }

    public float getDistance(Station s1, Station s2) {
        //Uses vector maths to find the absolute distance between two stations' locations in pixels
        return Vector2.dst(s1.getPosition().getX(), s1.getPosition().getY(), s2.getPosition().getX(), s2.getPosition().getY());
    }

    public double getShortestDistance(Station s1, Station s2) {
        //This calls the relevant method from the Dijkstra's algorithm which finds the smallest distance between two stations
        return dijkstra.findMinDistance(s1, s2);
    }

    public boolean inShortestPath(Station s1, Station s2, Station s3) {
        //This method calls the relevant method from Dijkstra's algorithm which checks whether or not s3 is in the shortest path from s1 to s2
        return dijkstra.inShortestPath(s1, s2, s3);
    }
}