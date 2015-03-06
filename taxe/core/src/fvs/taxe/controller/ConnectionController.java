package fvs.taxe.controller;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.clickListener.StationClickListener;
import gameLogic.GameState;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.listeners.GameStateListener;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

public class ConnectionController {
	private static ArrayList<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
	private Context context;
	private ArrayList<Tuple<Train,Connection>> connections;	// pairs of station that have not yet been created 
	
	// when currently selecting a connection
	private Train train;
	private Station firstStation;
	private Station secondStation;
	// class used for creating connections when in creating_connection mode mode
	private double angle;
	

	public ConnectionController(final Context context) {
		this.context = context;
		connections = new ArrayList<Tuple<Train,Connection>>();
		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTIION) {
					System.out.println("station 2 clicked");
					if (station != firstStation){
						secondStation = station;
						Connection connection = createConnection();
						/*connectionAdded();
						
						
						trains.getActor().remove();
						trains.getPlayer().removeResource(trains);
						*/
						
						setCreatingRoute(train, connection);

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

	protected void setCreatingRoute(Train train, Connection connection) {
		
		train.setPosition(new Position(-1, -1));
		this.angle = getAngle(connection);
		System.out.println(angle);
		/*IPositionable current = trains.getPosition();
		if (trains.getPosition().getX() == -1) {
			current = new Position((int) trains.getActor().getBounds().getX(), (int) trains.getActor().getBounds().getY());
		}
		trains.getActor().clearActions();
		
		IPositionable next = secondStation.getLocation();
		//This calculates how long it will take for the trains to travel to the next station on the route
		float duration = getDistance(current, next) / trains.getSpeed();

		//This adds the action to the actor which makes it move from point A to point B in a certain amount of time, calculated using duration and the two station positions.
		trains.getActor().addAction(moveTo(next.getX() - TrainActor.width / 2, next.getY() - TrainActor.height / 2, duration));
		//trains.getActor().addAction(action);
*/	}
	
	private double getAngle(Connection connection) {
		Position p1 = (Position) connection.getStation1().getLocation();
		Position p2 = (Position) connection.getStation2().getLocation();
		return Position.getAngle(p1,p2);
	}

	public void drawCreatingConnection() {
		for (Tuple<Train,Connection> pair: connections){
			Train train1 = pair.getFirst();
			TrainActor train = train1.getActor();
			IPositionable next = pair.getSecond().getStation2().getLocation();
			/*if (train.getX() == next.getX() && train.getY() == next.getY()){
				trainComplete(pair);
				*/
			if (train.getBounds().overlaps(pair.getSecond().getStation2().getActor().getBounds())){
				trainComplete(pair);
			} else {
				
				train.addAction(moveBy((float) (train1.getSpeed()*Math.cos(angle)), (float) (train1.getSpeed()*Math.sin(angle)), 2f));
				
			}
			
		}
	}

	private void trainComplete(Tuple<Train, Connection> pair) {
		connectionAdded(pair.getSecond());
		
		
		pair.getFirst().getActor().remove();
		pair.getFirst().getPlayer().removeResource(pair.getFirst());
		
		//connections.remove(pair);
	}

	private float getDistance(IPositionable a, IPositionable b) {
		//This method returns the absolute distance from point A to point B in pixels
		return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
	}
	
	public void begin(Train train) {
		System.out.println("begin called");
		context.getGameLogic().setState(GameState.CREATING_CONNECTIION);
		firstStation = train.getLastStation();
		this.train = train;
		
		//This makes all trains except the currently routed trains to be invisible.
		//This makes the screen less cluttered while routing and prevents overlapping trainActors from stopping the user being able to click stations.
		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, false);
		train.getActor().setVisible(true);
	}

	private Connection createConnection() {
		Connection connection = new Connection(firstStation, secondStation);
		connections.add(new Tuple<Train, Connection>(train, connection));
		return connection;
	}

	public static void subscribeConnectionChanged(ConnectionChangedListener connectionChangedListener) {
		listeners.add(connectionChangedListener);
	}

	public void connectionAdded(Connection connection){
		for (ConnectionChangedListener listener: listeners){
			listener.added(connection);
		}
	}

	public void connectionRemoved(Connection connection){
		for (ConnectionChangedListener listener: listeners){
			listener.removed(connection);
		}
	}
}
