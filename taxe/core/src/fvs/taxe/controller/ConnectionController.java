package fvs.taxe.controller;

import fvs.taxe.clickListener.StationClickListener;
import gameLogic.GameState;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.listeners.GameStateListener;
import gameLogic.map.Connection;
import gameLogic.map.Station;

import java.util.ArrayList;

public class ConnectionController {
	private static ArrayList<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
	private Context context;
	private Station firstStation;
	private Station secondStation;
	private Connection connection;
	// class used for creating connections when in creating_connection mode mode

	public ConnectionController(final Context context) {
		this.context = context;
		StationController.subscribeStationClick(new StationClickListener() {

			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTIION) {
					if (firstStation == null) {
						firstStation = station;
					} else if (station != firstStation){
						secondStation = station;
						createConnection();
						connectionAdded();
						firstStation = null;
						secondStation = null;
					}
				}
			}
		});
		
		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				firstStation = null;
				secondStation = null;
			}
		});
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
