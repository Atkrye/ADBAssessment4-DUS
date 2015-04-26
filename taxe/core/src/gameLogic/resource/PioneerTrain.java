package gameLogic.resource;

import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import gameLogic.map.Connection;
import gameLogic.map.Position;
import gameLogic.map.Station;

/** Train that represents the PioneerTrain, which can create connections*/
public class PioneerTrain extends Train {

	/** connection that the train is creating (null if none) */
	private Connection connection;	
	
	/** Boolean to say whether the train is currently creating a connection*/
	private boolean creating = false;
	
	/** The actor that corresponds to this pioneer train*/
	private PioneerTrainActor actor;
	
	//Fields for loading a PioneerTrain
	/** If the train has been loaded and it is creating a connection, the source of the new connection*/
	public Station setupFirstStation;
	
	/** If the train has been loaded and it is creating a connection, the destination of the new connection*/
	public Station setupLastStation;
	
	/** If the train has been loaded and it is creating a connection, the position of the train along the new connection*/
	public Position setupStartPos = null;
	
	/** Create the pioneer train with the correct image and speed*/
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
	
	/** Set the pioneer train into creating mode- give it the connection it is creating*/
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

	/** The pioneer train has completed creating, remove the actor and itself*/
	public void creationCompleted() {
		this.creating = false;
		actor.remove();
		player.removeResource(this);
	}
}
