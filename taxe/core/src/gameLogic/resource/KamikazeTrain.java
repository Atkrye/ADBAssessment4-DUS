package gameLogic.resource;

import fvs.taxe.actor.KamikazeTrainActor;
import fvs.taxe.actor.TrainActor;

/** Train that represents the kamikaze train, which can destroy connections*/
public class KamikazeTrain extends Train {

	/** The actor that corresponds to this train*/
	private KamikazeTrainActor actor;
	
	/** Create a train with correct image and speed*/
	public KamikazeTrain() {
		super("Kamikaze", "KamikazeTrain.png", 10);
	}
	
	/**Constructor with ID enforced*/
	public KamikazeTrain(int id) {
		super(id, "Kamikaze", "KamikazeTrain.png", 10);
	}
	
	
	@Override
	public void setActor(TrainActor actor){
		this.actor = (KamikazeTrainActor) actor;
	}
	
	@Override
	public KamikazeTrainActor getActor(){
		return actor;
	}
	
	/** Destroy the train and its actor*/
	public void selfDestruct(){
		actor.remove();
		player.removeResource(this);
	}
}
