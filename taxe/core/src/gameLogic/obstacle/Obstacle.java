package gameLogic.obstacle;

import fvs.taxe.actor.ObstacleActor;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

/** Class that represents an Obstacle- defined by its type and station it is located in*/
public class Obstacle {
	/** Boolean relevant to the Save / Load feature. This flag indiciates to a newly created Game.java that the Obstacle 
	 * should be started
	 */
	private Boolean startFlag = false;
	
	/** Station the obstacle is connected to */
	private Station station;	
	
	/** The enumerable type of the obstacle */
	private ObstacleType type;						
	
	/** Boolean to show wether the obstacle is currently active */
	private Boolean active;							
	
	/** Number of turns obstacle has left active (0 if not active) */
	private int time;								
	
	/** Position of the obstacle */
	private IPositionable position;		
	
	/** Corresponding actor for the obstacle */
	private ObstacleActor actor;					
	
	/** Constructor for the obstacle that sets the type, station, position and active fields of Obstacle
	 * @param type The type of the Obstacle
	 * @param station The station that the Obstacle is connected to
	 */
	public Obstacle(ObstacleType type, Station station) {
		this.type = type;
		this.station = station;
		this.position = station.getPosition();
		this.active = false;
	}

	/** Get the station that the obstacle is located at
	 * @return The station that the obstacle is associated with
	 */
	public Station getStation() {
		return this.station;
	}
	
	/** Get the type of the Obstacle
	 * @return The enumerable type of the obstacle
	 */
	public ObstacleType getType() {
		return this.type;
	}
	
	/** Get whether the obstacle is active or not
	 * @return True if the obstacle is active, false otherwise
	 */
	public boolean isActive(){
		return this.active;
	}
	
	/** Start the obstacle, setting it to active and the time to the duration of the obstacle*/
	public void start() {
		this.active = true;
		this.time = getDuration();
	}

	/** End the obstacle, setting it to inactive and the time to 0*/
	public void end() {
		this.active = false;
		this.time = 0;
	}

	/** Get the amount of time the obstacle has left active
	 * @return The number of turns the obstacle should still be active for
	 */
	public int getTimeLeft() {
		return this.time;
	}
	
	/**Used for loading the game from a save game. Sets the time the obstacle has left active
	 *@param timeLeft The number of turns the obstacle will be active for
	 */
	public void setTimeLeft(int timeLeft)
	{
		this.time = timeLeft;
	}
	
	/**Used for setting the start flag. The start flag indicates whether the Obstacle should be started when added to a game.java through
	 * loading
	 * @param b Whether the flag is true or false.
	 */
	public void setStartFlag(boolean b)
	{
		startFlag = true;
	}
	
	/**Gets whether this obstacle is waiting to start
	 */
	public boolean getStartFlag()
	{
		return startFlag;
	}
	/** Decreases the number of turns the obstacle has left
	 * @return True if the obstacle still has time left active, false otherwise
	 */
	public boolean decreaseTimeLeft() {
		// returns true if time left, false if no time left
		if (time > 0){
			this.time -= 1;
			return true;
		} else {
			return false;
		}
	}
	
	/**Get the number of turns the obstacle should be active for - dependent upon Obstacle Type
	 * @return Number of turns the obstacle should be active for
	 */
	private int getDuration() {
		if (type == ObstacleType.BLIZZARD){
			return 6;
		} else if (type == ObstacleType.EARTHQUAKE) {
			return 5;
		} else if (type == ObstacleType.FLOOD) {
			return 4;
		} else if (type == ObstacleType.VOLCANO) {
			return 8;
		} else {
			return -1; // invalid obstacle type!
		}
	}
	
	/** Get the position that the obstacle is located to
	 * @return The position of the Obstacle
	 */
	public IPositionable getPosition() {
		return this.position;
	}
	
	/** Set the ObstacleActor that is associated with this obstacle
	 * @param actor The new ObstacleActor to be associated with the obstacle
	 */
	public void setActor(ObstacleActor actor){
		this.actor = actor;
	}
	
	/** Get the ObstacleActor that is associated with this obstacle
	 * @return The ObstacleActor that is associated with this obstacle
	 */
	public ObstacleActor getActor(){
		return this.actor;
	}
}
