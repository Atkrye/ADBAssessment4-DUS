package gameLogic.map;

import fvs.taxe.actor.StationActor;
import fvs.taxe.controller.ConnectionController;
import gameLogic.dijkstra.Dijkstra;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.listeners.StationChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Map {
    private List<Station> stations;
    private List<Connection> connections;
    private Random random = new Random();
    private Dijkstra dijkstra;
	private BlankMapActor actor;
    @SuppressWarnings("unused")
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
        
        ConnectionController.subscribeStationAdded(new StationChangedListener() {
			@Override
			public void stationAdded(Station station) {
				stations.add(station);
			}

			@Override
			public void stationRemoved(Station station) {
				stations.remove(station); 
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

    public float getStationDistance(Station s1, Station s2) {
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
    
    public boolean hasConnection(Station station){
    	for (Connection connection: connections){
    		if (connection.getStation1().equals(station) || connection.getStation2().equals(station)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean nearStation(Position location) {
		// test if a location is near another station
		for (Station station : stations) {
			if (Position.getDistance(location, station.getPosition()) <= StationActor.getStationHeight() + 20) {
				return true;
			}
		}
		return false;
	}

	public boolean nearConnection(Position location) {
		// test if a location is near a connection
		for (Connection connection : connections) {
			IPositionable p1 = connection.getStation1().getPosition();
			Vector2 v1 = new Vector2(p1.getX(), p1.getY());
			IPositionable p2 = connection.getStation2().getPosition();
			Vector2 v2 = new Vector2(p2.getX(), p2.getY());

			Vector2 v3 = new Vector2(location.getX(), location.getY());
			boolean intersect = Intersector.intersectSegmentCircle(v1, v2, v3, 1000);
			if (intersect){
				return true;
			}
		}
		return false;
	}

	public boolean connectionOverlaps(Station firstStation, Station station) {
		// check if a connection overlaps with a station
		IPositionable position1 = firstStation.getPosition();
		IPositionable position2 = station.getPosition();
		int x1,x2,x3,x4,y1,y2,y3,y4;

		x3 = position1.getX();
		y3 = position1.getY();
		x4 = position2.getX();
		y4 = position2.getY();

		for (Station s: stations){
			if (s == firstStation || s == station){
				continue;
			}

			x1 = s.getPosition().getX() - StationActor.getStationWidth()/2 - 10;
			y1 = s.getPosition().getY() - StationActor.getStationHeight()/2 - 10;
			x2 = s.getPosition().getX() + StationActor.getStationWidth()/2 + 10;
			y2 = s.getPosition().getY() + StationActor.getStationHeight()/2 + 10;

			Position value = Position.getLineIntersect(x1, y1, x1, y2, x3, y3, x4, y4);
			if (value != null){
				return true;
			}

			value = Position.getLineIntersect(x1, y1, x2, y1, x3, y3, x4, y4);
			if (value != null){
				return true;
			}

			value = Position.getLineIntersect(x1, y2, x2, y2, x3, y3, x4, y4);
			if (value != null){
				return true;
			}

			value = Position.getLineIntersect(x2, y1, x2, y2, x3, y3, x4, y4);
			if (value != null){
				return true;
			}
		}
		return false;
	}

	public boolean isUniqueName(String text) {
		if (getStationByName(text) == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public BlankMapActor getMapActor() {
		return this.actor;
	}
	
	public void setMapActor(BlankMapActor actor){
		this.actor = actor;
	}
}