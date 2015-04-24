package adb.taxe.record;

import gameLogic.goal.Goal;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import com.badlogic.gdx.utils.Json;

public class GoalEvent extends Event{
	/**The name of the origin station of the goal*/
	private String origin;
	/**The name of the destination station of the goal*/
	private String destination;
	/**The intemediary station of the goal. If there is none, this value is simply "None"*/
	private String intermediary;
	/**The turn the goal was set in*/
	private int turn;
	/**The number of turns the goal must be completed in to earn the bonus. If none, the value is 0*/
	private int turnsTime;
	/**The score value of the goal*/
	private int score;
	/**The score value of the goal's bonus*/
	private int bonus;
	/**The name of the type of train that must be completed to earn the bonus. If none, the value is "None"*/
	private String train;
	/**This constructor takes a goal and converts it into a raw data format
	 * @param g The goal used to set up the Event
	 */
	public GoalEvent(Goal g)
	{
		this.origin = g.getOrigin().getName();
		this.destination = g.getDestination().getName();
		if(g.getIntermediary() == null)
		{
			this.intermediary = "None";
		}
		else
		{
			this.intermediary = g.getIntermediary().getName();
		}
		this.turn = g.getTurn();
		this.turnsTime = g.getTurnsTime();
		this.score = g.getScore();
		this.bonus = g.getBonus();
		if(g.getTrain() == null)
		{
			this.train = "None";
		}
		else
		{
			this.train = g.getTrain().getName();
		}
	}
	
	/**This constructor is used to load a goal event back from Json
	 * @param origin The name of the origin station of the goal
	 * @param destination The name of the destination station of the goal
	 * @param intermediary The name of the intermediary station of the goal. If none, the value is "None"
	 * @param turn The turn the goal was started in
	 * @param turnsTime The number of turns the player must do the goal in to earn a bonus. If none, value is 0
	 * @param score The score value of the goal
	 * @param bonus The bonus value of the goal
	 * @param train The type of train a player must use to earn a bonus. If none, the value is "None"
	 */
	public GoalEvent(String origin, String destination, String intermediary, int turn, int turnsTime, int score, int bonus, String train)
	{
		this.origin = origin;
		this.destination = destination;
		this.intermediary = intermediary;
		this.turn = turn;
		this.turnsTime = turnsTime;
		this.score = score;
		this.bonus = bonus;
		this.train = train;
	}
	
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Goal");
		json.writeValue("Origin", origin);
		json.writeValue("Destination", destination);
		json.writeValue("Intermediary", intermediary);
		json.writeValue("Turn", turn);
		json.writeValue("TurnsTime", turnsTime);
		json.writeValue("Score", score);
		json.writeValue("Bonus", bonus);
		json.writeValue("Train", train);
		json.writeObjectEnd();
	}

	public Goal asGoal(Map map, ResourceManager resourceManager) {
		Station origin = map.getStationByName(this.origin);
		Station destination = map.getStationByName(this.destination);
		Station intermediary = null;
		if(!this.intermediary.equals("None"))
		{
			intermediary = map.getStationByName(this.intermediary);
		}
		Train train = null;
		if(!this.train.equals("None"))
		{
			train = resourceManager.getTrainByName(this.train);
		}
		return new Goal(origin, destination, intermediary, turn, turnsTime, score, bonus, train);
	}

}
