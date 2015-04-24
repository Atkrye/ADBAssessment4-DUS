package fvs.taxe.controller;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

import fvs.taxe.actor.KamikazeTrainActor;
import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.clickListener.TrainClicked;
import gameLogic.Game;
import gameLogic.player.Player;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;


/**Controller for managing games graphics*/
public class TrainController {
	/**The game context.*/
    private Context context;
    
    /** Group of actors that represent the trains */
	private static Group TrainActors;

	/**Instantiation method.
     * @param context The game context.
     */
	public TrainController(final Context context) {
		this.context = context;

		ConnectionController.subscribeConnectionChanged(new ConnectionChangedListener() {
			@Override
			public void removed(Connection connection) {
				// destroy any trains on connections that are then destroyed
				List<Train> removedResources = new ArrayList<Train>();
				for (Player player : Game.getInstance().getPlayerManager().getAllPlayers()) {
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

	/** Called once, Add the trainActors to the stage */
	public void drawTrains(Stage stage) {
		stage.addActor(TrainActors);
	}

	/** Setup the group of Train Actors */
	public void setupTrainActors()
	{
		if (TrainActors == null){
			TrainActors = new Group();
		}
	}

	/** Add the actor to the group of trainactors */
	public void addTrainToActors(Train t)
	{
		TrainActors.addActor(t.getActor());
	}

	/**This method renders a train by adding it to the Game as a TrainActor.
	 * Will render a pioneerTrain/KamikazeTrain depending upon train given
     * @param train The train to be rendered.
     * @return The TrainActor produced using the train.
     */
	public TrainActor renderTrain(Train train, boolean addListener) {
		//This renders the actor of the train which is passed to it
		TrainActor trainActor;
		if (train.getClass().equals(PioneerTrain.class)){
			trainActor = new PioneerTrainActor((PioneerTrain) train, context);
		} else if (train.getClass().equals(KamikazeTrain.class)){
			trainActor = new KamikazeTrainActor((KamikazeTrain) train, context);
		} else {
			trainActor = new TrainActor(train, context);
		}
		if(addListener)
		{
			trainActor.addListener(new TrainClicked(context, train));
		}
		trainActor.setVisible(false);
		TrainActors.addActor(trainActor);
		System.out.println(TrainActors.getChildren());
		return trainActor;
	}


	/** Sets all trains on the map visible or invisible except one that we are routing for */
	public void setTrainsVisible(Train train, boolean visible) {
		for (Player player : Game.getInstance().getPlayerManager().getAllPlayers()) {
			for (Resource resource : player.getResources()) {
				if (resource instanceof Train) {
					boolean trainAtStation = false;
					for (Station station : Game.getInstance().getMap().getStations()) {
						if (station.getPosition() == ((Train) resource).getPosition()) {
							trainAtStation = true;
							break;
						}
					}
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

	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext()
	{
		return context;
	}
}
