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
import com.badlogic.gdx.utils.JsonValue;

/** Class that stores all of the connections and stations */
public class Map {
	/**The stations that exist on the map.*/
	private List<Station> stations;

	/**The connections that exist between stations on the map.*/
	private List<Connection> connections;

	/**Random used for random number generation.*/
	private Random random = new Random();

	/** Instance of Dijkstra used to calculate conenction distances */
	private Dijkstra dijkstra;

	/** Actor that corresponds to the map */
	private BlankMapActor actor;

	/** JSON Importer used to parse the json files to get the stations/ connections*/
	@SuppressWarnings("unused")
	private JSONImporter jsonImporter;

	/** Instantation to create all stations, connections and do dijkstras on it
	 * @param importedData Boolean to say whether data has been imported
	 * @param data The data to be imported (null if none)
	 */
	public Map(boolean importedData, JsonValue data) {

		stations = new ArrayList<Station>();
		connections = new ArrayList<Connection>();

		//Imports all values from the JSON file using the JSONImporter
		//If we're using loaded game data, send the data to the JSONImporter, otherwise load from hard data as normal
		if(importedData)
		{
			jsonImporter = new JSONImporter(this, data);
		}
		else
		{
			jsonImporter = new JSONImporter(this);
		}

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

	/**This method checks whether a connection exists between 2 stations.
	 * @param stationName The first station.
	 * @param anotherStationName The second station.
	 * @return True if there is a connection, false otherwise.
	 */
	public boolean doesConnectionExist(String stationName, String anotherStationName) {
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

	/**This method returns the connection between 2 stations.
	 * @param stationName The first station.
	 * @param anotherStationName The second station.
	 * @return The connection between stations if one exists, null otherwise.
	 */
	public Connection getConnection(Station station1, Station station2) {
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

	/**This method picks a random station from the Array of stations.*/
    public Station getRandomStation() {
		return stations.get(random.nextInt(stations.size()));
	}

    /**This method adds a new Station to the game.
     * @param name The name of the new station.
     * @param location The position of the new station in the game.
     * @return The newly added station.
     */
	public Station addStation(String name, Position location) {
		//This routine adds a new station the list of stations
		Station newStation = new Station(name, location);
		stations.add(newStation);
		return newStation;
	}

	/**This method adds a new Junction to the game.
     * @param name The name of the new Junction.
     * @param location The position of the new Junction in the game.
     * @return The newly added junction.
     */
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

	 /**This method adds a new connection between 2 stations to the game.
     * @param station1 The first station used in the connection.
     * @param station2 The second station used in the connection.
     * @return The newly created connection.
     */
	public Connection addConnection(Station station1, Station station2) {
		Connection newConnection = new Connection(station1, station2);
		connections.add(newConnection);
		return newConnection;
	}

	 /**This method replicated addConnection but allows the use of station names instead of objects.
     * @param station1 The name of the first station.
     * @param station2 The name of the second station.
     * @return The newly created connection.
     */
	public Connection addConnection(String station1, String station2) {
		Station st1 = getStationByName(station1);
		Station st2 = getStationByName(station2);
		return addConnection(st1, st2);
	}

	 /**This method finds a station by its name.
     * @param name The name of the station to be found.
     * @return The station, if found, null otherwise.
     */
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

	/** Get the station that is at the given position (if one exists)
	 * @param position The position to test against the stations
	 * @return The station at the given position, null if none at that position
	 */
	public Station getStationFromPosition(IPositionable position) {
		//Returns the station located at the position passed to the method
		for (Station station : stations) {
			if (station.getPosition().equals(position)) {
				return station;
			}
		}
		return null;
	}

	/**This method creates a route using a list of positions.
     * @param positions The list of positions that the route consists of.
     * @return The list of stations that make up the route.
     */
	public List<Station> createRoute(List<IPositionable> positions) {
		//Takes a list of positions and uses these to find the stations at these positions
		//These stations are then added to a list which acts as the route
		List<Station> route = new ArrayList<Station>();

		for (IPositionable position : positions) {
			route.add(getStationFromPosition(position));
		}
		return route;
	}

	/**Uses vector maths to find the absolute distance between two stations' locations in pixels
	 * @param s1 First station 
	 * @param s2 Second station 
	 * @return The distance in pixels between s1 and s2
	 */
	public float getStationDistance(Station s1, Station s2) {
		return Vector2.dst(s1.getPosition().getX(), s1.getPosition().getY(), s2.getPosition().getX(), s2.getPosition().getY());
	}

	/**This calls the relevant method from the Dijkstra's algorithm which finds the smallest distance between two stations
	 * @param s1 The first station 
	 * @param s2 The second station
	 * @return The distance of the shortest from s1 to s2
	 */
	public double getShortestDistance(Station s1, Station s2) {
		return dijkstra.findMinDistance(s1, s2);
	}

	/**This method calls the relevant method from Dijkstra's algorithm which checks whether or not s3 is in the shortest path from s1 to s2
	 * @param s1 First station
	 * @param s2 Second station
	 * @param s3 Station that is tested whether its in the shortest path from s1 to s2
	 * @return Whether s3 is in the shortest path from s1 to s2
	 */
	public boolean inShortestPath(Station s1, Station s2, Station s3) {
		return dijkstra.inShortestPath(s1, s2, s3);
	}

	/** Mehtod to say if a given station has a connection or is isolated
	 * @param station STation to be tested if isolated
	 * @return True if station has connections, false if station isolated (no connections)
	 */
	public boolean hasConnection(Station station){
		for (Connection connection: connections){
			if (connection.getStation1().equals(station) || connection.getStation2().equals(station)) {
				return true;
			}
		}
		return false;
	}

	/** test if a location is within 20 pixels to any stations 
	 * @param location Location that is being tested against
	 * @return True if the position is within a 20 pixel radius around any station, otherwise false
	 */
	public boolean nearStation(Position location) {
		for (Station station : stations) {
			if (Position.getDistance(location, station.getPosition()) <= StationActor.getStationHeight() + 20) {
				return true;
			}
		}
		return false;
	}

	/** Test if a location is near (20 pixel radius) an existing connection
	 * @param location The location that is being tested
	 * @return True if the position is within a 20 pixel radius of any connection, otherwise false
	 */
	public boolean nearConnection(Position location) {
		for (Connection connection : connections) {
			IPositionable p1 = connection.getStation1().getPosition();
			Vector2 v1 = new Vector2(p1.getX(), p1.getY());
			IPositionable p2 = connection.getStation2().getPosition();
			Vector2 v2 = new Vector2(p2.getX(), p2.getY());

			Vector2 v3 = new Vector2(location.getX(), location.getY());
			boolean intersect = Intersector.intersectSegmentCircle(v1, v2, v3, 20^2);
			if (intersect){
				return true;
			}
		}
		return false;
	}

	/** Test if a connection overlaps with an existing station on map
	 * @param s1 First station in connection
	 * @param s2 Second station in connection
	 * @return True if the connection (s1,s2) overlaps with an existing station, otherwise false
	 */
	public boolean connectionOverlaps(Station s1, Station s2) {
		IPositionable position1 = s1.getPosition();
		IPositionable position2 = s2.getPosition();
		int x1,x2,x3,x4,y1,y2,y3,y4;

		x3 = position1.getX();
		y3 = position1.getY();
		x4 = position2.getX();
		y4 = position2.getY();

		for (Station s: stations){
			// dont check if  station overlaps with itself
			if (s == s1 || s == s2){
				continue;
			}

			// get a square boundary around the station
			x1 = s.getPosition().getX() - StationActor.getStationWidth()/2 - 10;
			y1 = s.getPosition().getY() - StationActor.getStationHeight()/2 - 10;
			x2 = s.getPosition().getX() + StationActor.getStationWidth()/2 + 10;
			y2 = s.getPosition().getY() + StationActor.getStationHeight()/2 + 10;

			// must individually test each side of square around stationd ue to lac of good built in method
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

	/** Test whether a given string is a station name that has been previously used
	 * @param text Given string to compare to
	 * @return True if the station name doesn't exist, otherwise false
	 */
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