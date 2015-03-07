package fvs.taxe.controller;

import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.clickListener.TrainClicked;
import gameLogic.player.Player;
import gameLogic.map.Station;
import gameLogic.resource.PioneerTrain;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;


public class TrainController {
	//This class controls all the train actors
	private Context context;


	public TrainController(Context context) {
		this.context = context;
	}

	public TrainActor renderTrain(Train train) {
		//This renders the actor of the train which is passed to it
		TrainActor trainActor;
		if (train.getClass().equals(PioneerTrain.class)){
			trainActor = new PioneerTrainActor((PioneerTrain) train, context);
			trainActor.addListener(new TrainClicked(context, train));

			trainActor.setVisible(false);
			context.getStage().addActor(trainActor);
		} else {
			trainActor = new TrainActor(train, context);
			trainActor.addListener(new TrainClicked(context, train));

			trainActor.setVisible(false);
			context.getStage().addActor(trainActor);
		}
		return trainActor;
	}


	// Sets all trains on the map visible or invisible except one that we are routing for
	public void setTrainsVisible(Train train, boolean visible) {

		for (Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
			for (Resource resource : player.getResources()) {
				if (resource instanceof Train) {
					boolean trainAtStation = false;
					for (Station station : context.getGameLogic().getMap().getStations()) {
						if (station.getPosition() == ((Train) resource).getPosition()) {
							trainAtStation = true;
							break;
						}
					}
					if (((Train) resource).getActor() != null && resource != train && !trainAtStation) {
						((Train) resource).getActor().setVisible(visible);
					}
				}
			}
		}
	}
}
