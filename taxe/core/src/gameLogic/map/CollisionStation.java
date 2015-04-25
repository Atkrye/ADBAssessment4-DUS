package gameLogic.map;

import fvs.taxe.actor.CollisionStationActor;

/**CollisionStation is a specialised type of station*/
public class CollisionStation extends Station {
	/** Actor for the collision station */
	CollisionStationActor actor;
	
    public CollisionStation(String name, IPositionable location) {
        super(name, location);
        isJunction = true;
    }
    
    public CollisionStationActor getCollisionStationActor() {
    	return actor;
    }
    
	public void setActor(CollisionStationActor collisionStationActor) {
		this.actor = collisionStationActor;
	}
    
}
