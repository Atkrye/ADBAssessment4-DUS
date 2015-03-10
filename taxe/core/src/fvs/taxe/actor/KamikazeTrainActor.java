package fvs.taxe.actor;

import fvs.taxe.controller.Context;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.Train;

public class KamikazeTrainActor extends TrainActor {
	
	KamikazeTrain train;

	public KamikazeTrainActor(KamikazeTrain train, Context context) {
		super(train, context);
		this.train = train;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
	}

}
