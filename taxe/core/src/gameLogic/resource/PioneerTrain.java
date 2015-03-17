package gameLogic.resource;

import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import gameLogic.map.Connection;

public class PioneerTrain extends Train {

	private Connection connection;	// connection that the train is creating
	private boolean creating = false;
	private PioneerTrainActor actor;
	
	public PioneerTrain() {
		super("Pioneer", "PioneerTrain.png",  50);
	}
	
	@Override
	public void setActor(TrainActor actor){
		this.actor = (PioneerTrainActor) actor;
	}
	
	@Override
	public PioneerTrainActor getActor(){
		return actor;
	}
	
	public void setCreating(Connection connection) {
		this.connection = connection;
		this.creating = true;
		actor.setStationPositions(connection);
	}
	
	public Connection getConnection(){ 
		return this.connection;
	}
	
	public boolean isCreating() {
		return creating;
	}

	public void creationCompleted() {
		this.creating = false;
		actor.remove();
		player.removeResource(this);
	}
}
