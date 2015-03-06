package fvs.taxe.controller;

import fvs.taxe.clickListener.StationClickListener;
import gameLogic.GameState;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.listeners.GameStateListener;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;

public class ConnectionController {
	private static ArrayList<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
	private Context context;
	private Station firstStation;
	private Station secondStation;
	private Connection connection;
	private Train train;
	// class used for creating connections when in creating_connection mode mode

	public ConnectionController(final Context context) {
		this.context = context;

		StationController.subscribeStationClick(new StationClickListener() {

			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTIION) {
					System.out.println("station 2 clicked");
					if (station != firstStation){
						secondStation = station;
						createConnection();
						connectionAdded();

						train.getActor().remove();
						train.getPlayer().removeResource(train);
						

						context.getGameLogic().setState(GameState.NORMAL);
					}
				}
			}
		});

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state != GameState.CREATING_CONNECTIION){
					firstStation = null;
					secondStation = null;
					train = null;
				}
			}
		});
	}

	public void begin(Train train) {
		System.out.println("begin called");
		context.getGameLogic().setState(GameState.CREATING_CONNECTIION);
		firstStation = train.getLastStation();
		this.train = train;

		//This makes all trains except the currently routed train to be invisible.
		//This makes the screen less cluttered while routing and prevents overlapping trainActors from stopping the user being able to click stations.
		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, false);
		train.getActor().setVisible(true);
	}

	private void createConnection() {
		connection = new Connection(firstStation, secondStation);
	}

	public static void subscribeConnectionChanged(ConnectionChangedListener connectionChangedListener) {
		listeners.add(connectionChangedListener);
	}

	public void connectionAdded(){
		for (ConnectionChangedListener listener: listeners){
			listener.added(connection);
		}
	}

	public void connectionRemoved(){
		for (ConnectionChangedListener listener: listeners){
			listener.removed(connection);
		}
	}
}
