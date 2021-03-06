package gameLogic.obstacle;

import gameLogic.Game;
import gameLogic.map.Map;
import gameLogic.map.Station;

import java.util.ArrayList;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/** This class creates and stores the Obstacles from the Obstacles.json files */
public class ObstacleManager {
	/** The default value for the probability of an obstacle occurring if no probability is set in Obstacles.json*/
	private static final float DEFAULT_OBSTACLE_PROBABILITY = 0.1f;
	
	/** List of pairs of Obstacles and their associated probabilities of occurring */
	private ArrayList<Tuple<Obstacle,Float>> obstacles; 				
	
	/* The map that the Obstacle's stations are connected with */
	private Map map;
	
	/** Set the map accoridngly, populate the list of obstacle, probability pairings
	 * @param map The map that the Game's stations are associated with
	 */
	public ObstacleManager(Map map) {
		this.map = map;
		initialise();
	}
	
	/** Set the map accordingly, populate the list of obstacles from the JsonData.
	 * This method is called when loading a save game.
	 * @param map The map that the Game's stations are associated with
	 * @param jsonData The loaded json data that will be used to populate the obstacles
	 */
	public ObstacleManager(Map map, JsonValue jsonData)
	{
		this.map = map;
		initialise(jsonData);
	}

	/** Get all of the obstacles, their types, stations and the probabilities of them occurring from Obstacles.json*/
	private void initialise() {
		JsonReader jsonReader = new JsonReader();
		JsonValue jsonVal = jsonReader.parse(Gdx.files.local("obstacles.json"));

		obstacles = new ArrayList<Tuple<Obstacle, Float>>();
		for(JsonValue jObstacle = jsonVal.getChild("obstacles"); jObstacle != null; jObstacle = jObstacle.next()) {
			String typeName = "";
			String stationName = "";
			float probability = DEFAULT_OBSTACLE_PROBABILITY;
			for(JsonValue val  = jObstacle.child; val != null; val = val.next()) {
				if(val.name.equalsIgnoreCase("type")) {
					typeName = val.asString();
				} else if (val.name.equalsIgnoreCase("station")) {
					stationName = val.asString();
				} else {
					probability = val.asFloat();
				}
			}
			
			Obstacle obstacle = createObstacle(typeName, stationName);
			if (obstacle != null){
				obstacles.add(new Tuple<Obstacle, Float>(obstacle, probability));
			}
		}
	}

	/** Get all of the obstacles, their types, stations and the probabilities of them occurring, alongside their position and time remaining from loaded json data
	 * This method is used when loading a save game
	 * @param jsonData the loaded data from a save game
	 * */
	private void initialise(JsonValue jsonData)
	{
		obstacles = new ArrayList<Tuple<Obstacle, Float>>();
		for(JsonValue jObstacle = jsonData.getChild("obstacles"); jObstacle != null; jObstacle = jObstacle.next()) {
			//Set up the obstacle and add it to the array
			String typeName = jObstacle.getString("Obstacle Type");
			String stationName = jObstacle.getString("Station");
			System.out.println(typeName + ":" + stationName);
			float probability = jObstacle.getFloat("Probability");
			Obstacle o = createObstacle(typeName, stationName);
			obstacles.add(new Tuple<Obstacle, Float>(o, probability));
			
			//Get the turns remaining and if the obstacle is active
			boolean active = jObstacle.getBoolean("Active");
			int turnsRemaining = jObstacle.getInt("Time Left");
			if(active)
			{
				o.setStartFlag(true);
				o.setTimeLeft(turnsRemaining);
			}
		}
	}

	/** Create the obstacle that has given type, and is located at the station assoicated with the given string
	 * @param typeName The string that represents the name of the type of the obstacle (ObstacleType enum)
	 * @param stationName The string that represents the station that the obstacle is associated with
	 * @return An obstacle with type given, station given if both that type and station exist, otherwise null
	 */
	private Obstacle createObstacle(String typeName, String stationName) {
		ObstacleType type = null;
		Station station = null;
		if (typeName.equalsIgnoreCase("volcano")){
			type = ObstacleType.VOLCANO;
		} else if (typeName.equalsIgnoreCase("blizzard")) {
			type = ObstacleType.BLIZZARD;
		} else if (typeName.equalsIgnoreCase("flood")) {
			type = ObstacleType.FLOOD;
		} else if (typeName.equalsIgnoreCase("earthquake")) {
			type = ObstacleType.EARTHQUAKE;
		} 
		station = map.getStationByName(stationName);
		
		if (type != null && station != null){
			return new Obstacle(type, station);
		} else {
			return null;
		}
	}

	/** Get the list of pairs of the obstacles and the probability of that obstacle occurring
	 * @return The list of pairs of obstacles and the associated probability of that obstacle occurring
	 */
	public ArrayList<Tuple<Obstacle, Float>> getObstacles() {
		return this.obstacles;
	}

	public void activateIdleObstacles() {
		System.out.println("Obstacles Start");
		for(Tuple<Obstacle, Float> obstacle : obstacles)
		{
			Obstacle o = obstacle.getFirst();
			if(o.getStartFlag())
			{
				int turnCount = o.getTimeLeft();
				o.start();
				o.getStation().setObstacle(o); 
				// set the obstacle so its visible
				o.getActor().setVisible(true);
				o.setTimeLeft(turnCount);
			}
		}
	}
}
