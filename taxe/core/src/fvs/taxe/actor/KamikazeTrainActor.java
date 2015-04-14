package fvs.taxe.actor;

import fvs.taxe.controller.Context;
import gameLogic.resource.KamikazeTrain;

/** Stub class for adding any extra functionality to the KamikazeTrains, similar to PionerrTrainActor */
public class KamikazeTrainActor extends TrainActor {
	/** The KamikazeTrain that corresponds to this KamikazeTrainActor */
	KamikazeTrain train;

	public KamikazeTrainActor(KamikazeTrain train, Context context) {
		super(train, context);
		this.train = train;
	}
}
