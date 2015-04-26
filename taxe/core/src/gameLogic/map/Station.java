package gameLogic.map;

import fvs.taxe.actor.StationActor;
import gameLogic.obstacle.Obstacle;

/**This class is used to store information about a station.*/
public class Station{
	/**The name of the station.*/
	private String name;
	
	/**The in game position of the station.*/
	private IPositionable position;
	
	/**The actor that represents the station graphically.*/
	private StationActor actor;
	
	/**The obstacle occupying the station, if any.*/
	private Obstacle obstacle;
	
	/** Boolean to say whether the station is a collisionStation or not */
	public boolean isJunction = false;

	/**Instantiation method.
	 * @param name The name of the station.
	 * @param location The location of the station.
	 */
    public Station(String name, IPositionable location) {
        this.name = name;
        this.position = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IPositionable getPosition() {
        return position;
    }

    public void setPosition(IPositionable position) {
        this.position = position;
    }

    public void setActor(StationActor actor) {
        this.actor = actor;
    }

    public StationActor getActor() {
        return actor;
    }

    public boolean equals(Object o) {
        //Allows stations to be compared to each other, to check if they are the same station
        if (o instanceof Station) {
            Station s = (Station) o;
            return getName().equals(s.getName()) &&
                    getPosition().getX() == s.getPosition().getX() &&
                    getPosition().getY() == s.getPosition().getY();
        } else {
            return false;
        }
    }
    
    public void setObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

    /**@return True if the Station has an obstacle on it, false otherwise.*/
	public boolean hasObstacle(){
		if (this.obstacle == null){
			return false;
		} else {
			return true;
		}
	}
	
	public Obstacle getObstacle(){
		return this.obstacle;
	}

	public void clearObstacle() {
		this.obstacle = null;
	}
}
