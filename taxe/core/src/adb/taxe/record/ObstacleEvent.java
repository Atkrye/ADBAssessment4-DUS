package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

import gameLogic.obstacle.Obstacle;

/**This class is a type of event used to describe when an obstacle occurs within the game. It is important to note
 * that if no obstacle occurs, an event is still created with no arguments passed (see the constructor without arguments)
 * that is used to tell the recording that no obstacles occured that turn
 */
public class ObstacleEvent extends Event{
	/**The Obstacle type, formatted as a string*/
	private String obstacleType;
	/**The obstacle station's name*/
	private String obstacleStation;
	
	/**This constructor takes an obstacle and stores the important information about it*/
	public ObstacleEvent(Obstacle ob)
	{
		this.obstacleType = ob.getType().toString();
		this.obstacleStation = ob.getStation().getName();
	}
	
	/**Constructer without argument indicates that this turn, no obstacle occurred. When the playback is reading back
	 * it will observe the NULL values for the obstacle type and station in this event and this will indicate that
	 * no obstacle occured that turn
	 */
	public ObstacleEvent() {
		this.obstacleType = "NULL";
		this.obstacleStation = "NULL";
	}
	
	/**Constructor for loading back in from a recording*/
	public ObstacleEvent(String type, String station)
	{
		this.obstacleType = type;
		this.obstacleStation = station;
	}

	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Obstacle");
		json.writeValue("ObstacleType", obstacleType);
		json.writeValue("ObstacleStation", obstacleStation);
		json.writeObjectEnd();
	}
	
	/**This method gets the type of obstacle that this event represents
	 * @return The type of obstacle stored by this event
	 */
	public String getType()
	{
		return obstacleType;
	}
	
	/**This method gets the name of the station where the obstacle that this event represents occured
	 * @return The name of the station corresponding to this obstacle
	 */
	public String getStation()
	{
		return obstacleStation;
	}

}
