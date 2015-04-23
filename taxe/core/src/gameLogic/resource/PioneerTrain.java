package gameLogic.resource;

import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import gameLogic.map.Connection;
import gameLogic.map.Position;
import gameLogic.map.Station;

public class PioneerTrain extends Train {

	private Connection connection;	// connection that the train is creating
	private boolean creating = false;
	private PioneerTrainActor actor;
	//Fields for loading a PioneerTrain
	public Station setupFirstStation;
	public Station setupLastStation;
	public Position setupStartPos = null;
	
	public PioneerTrain() {
		super("Pioneer", "PioneerTrain.png",  50);
	}
	
	/**Constructor with ID enforced*/
	public PioneerTrain(int id) {
		super(id, "Pioneer", "PioneerTrain.png",  50);
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
