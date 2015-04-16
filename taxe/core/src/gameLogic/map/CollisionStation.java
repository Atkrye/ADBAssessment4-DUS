package gameLogic.map;

import fvs.taxe.actor.CollisionStationActor;

public class CollisionStation extends Station {

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
