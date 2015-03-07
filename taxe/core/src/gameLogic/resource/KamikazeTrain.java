package gameLogic.resource;

import fvs.taxe.actor.KamikazeTrainActor;
import fvs.taxe.actor.TrainActor;

public class KamikazeTrain extends Train {

	private KamikazeTrainActor actor;
	
	public KamikazeTrain() {
		super("Kamikaze", "BulletTrain.png", "BulletTrainRight.png", 10);
	}
	
	@Override
	public void setActor(TrainActor actor){
		this.actor = (KamikazeTrainActor) actor;
	}
	
	@Override
	public KamikazeTrainActor getActor(){
		return actor;
	}
	
	public void selfDestruct(){
		actor.remove();
		player.removeResource(this);
	}
}
