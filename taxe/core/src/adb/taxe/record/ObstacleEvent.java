package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

import gameLogic.obstacle.Obstacle;

public class ObstacleEvent extends Event{
	/**The Obstacle type, formatted as a string*/
	private String obstacleType;
	/**The obstacle station's name*/
	private String obstacleStation;
	
	public ObstacleEvent(Obstacle ob)
	{
		this.obstacleType = ob.getType().toString();
		this.obstacleStation = ob.getStation().getName();
	}
	
	/**Constructer without argument indicates that this turn, no obstacle occurred*/
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
	
	public String getType()
	{
		return obstacleType;
	}
	
	public String getStation()
	{
		return obstacleStation;
	}

}
