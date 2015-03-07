package fvs.taxe.controller;

import java.util.ArrayList;
import java.util.List;

import fvs.taxe.actor.KamikazeTrainActor;
import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.clickListener.TrainClicked;
import gameLogic.player.Player;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;


public class TrainController {
	//This class controls all the train actors
	private Context context;


	public TrainController(final Context context) {
		this.context = context;

		ConnectionController.subscribeConnectionChanged(new ConnectionChangedListener() {
			@Override
			public void removed(Connection connection) {
				// destroy any trains on connections that are then destroyed
				List<Train> removedResources = new ArrayList<Train>();
				for (Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
					for (Resource resource : player.getResources()) {
						if (resource instanceof Train) {
							Train train = (Train) resource;
							if ((train.getRoute() != null) && (train.getPosition() != null)){
								if ((train.getLastStation() == connection.getStation1())
										&& (train.getNextStation() == connection.getStation2())) {
									removedResources.add(train);

								} else if ((train.getLastStation() == connection.getStation2())
										&& (train.getNextStation() == connection.getStation1())) {
									removedResources.add(train);
								}
							}
						}
					}
				}

				for (Train train : removedResources) {
					train.getPlayer().removeResource(train);
					train.getActor().remove();
				}
			}

			@Override
			public void added(Connection connection) {
			}
		});
	}

	public TrainActor renderTrain(Train train) {
		//This renders the actor of the train which is passed to it
		TrainActor trainActor;
		if (train.getClass().equals(PioneerTrain.class)){
			trainActor = new PioneerTrainActor((PioneerTrain) train, context);
			trainActor.addListener(new TrainClicked(context, train));

			trainActor.setVisible(false);
			context.getStage().addActor(trainActor);

		} else if (train.getClass().equals(KamikazeTrain.class)){
			trainActor = new KamikazeTrainActor((KamikazeTrain) train, context);
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
					/*if (resource.getClass().equals(PioneerTrain.class)){
						// if a train is creating a route, its visibility doesnt get changed
						if (!((PioneerTrain) resource).isCreating()) {
							((Train) resource).getActor().setVisible(visible);
						}
					}*/
					if (((Train) resource).getActor() != null && resource != train && !trainAtStation) {
						if (resource.getClass().equals(PioneerTrain.class)){
							// if a train is creating a route, its visibility doesnt get changed
							if (!((PioneerTrain) resource).isCreating()) {
								((Train) resource).getActor().setVisible(visible);
							}
						} else {
							((Train) resource).getActor().setVisible(visible);
						}
					}
				}
			}
		}
	}
}
