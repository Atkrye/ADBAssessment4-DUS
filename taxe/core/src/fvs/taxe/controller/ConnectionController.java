package fvs.taxe.controller;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import fvs.taxe.TaxeGame;
import fvs.taxe.actor.StationActor;
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

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
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION) {
					System.out.println("station 2 clicked");
					if (station != firstStation){
						if (!context.getGameLogic().getMap().doesConnectionExist(firstStation.getName(), station.getName())) {
							secondStation = station;
							Connection connection = createConnection();
							setCreatingRoute(train, connection);
							context.getGameLogic().setState(GameState.NORMAL);
						} 
					}
				}
			}
		});

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state != GameState.CREATING_CONNECTION){
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
	}

	private double getAngle(Connection connection) {
		Position p1 = (Position) connection.getStation1().getLocation();
		Position p2 = (Position) connection.getStation2().getLocation();
		return Position.getAngle(p1,p2);
	}

	public void drawMouse() {
		ShapeRenderer shapeRenderer = context.getTaxeGame().shapeRenderer;
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		context.getTaxeGame();
		shapeRenderer.rectLine(firstStation.getLocation().getX(), firstStation.getLocation().getY(), 
				Gdx.input.getX(), TaxeGame.HEIGHT- Gdx.input.getY(), 5);
		shapeRenderer.end();
	}
	
	public void drawCreatingConnection(float delta) {
		ArrayList<Tuple<Train, Connection>> removed = new ArrayList<Tuple<Train, Connection>>();
		for (Tuple<Train,Connection> pair: connections){
			Train train = pair.getFirst();
			StationActor next = pair.getSecond().getStation2().getActor();
			if (context.getGameLogic().getState() == GameState.ANIMATING) {
				if (reachedLocation(train.getActor(), next)){
					trainComplete(pair);
					removed.add(pair);
				} else {
					float t = delta;
					train.getActor().addAction(moveBy((float) (train.getSpeed()*Math.cos(angle))*t, (float) (train.getSpeed()*Math.sin(angle))*t, t));
				}
			}
			ShapeRenderer shapeRenderer = context.getTaxeGame().shapeRenderer;
			StationActor first = pair.getSecond().getStation1().getActor();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rectLine(train.getActor().getX()+TrainActor.width/2, train.getActor().getY()+ TrainActor.height/2, 
					first.getX() + StationActor.width/2, first.getY() + StationActor.height/2, 5);
			shapeRenderer.end();
		}
		
		for (Tuple<Train, Connection> pair : removed){
			connections.remove(pair);
		}
	}

	private boolean reachedLocation(TrainActor train, StationActor next) {
		float trainx =train.getX()+ TrainActor.width/2;
		float nextx = next.getX() + StationActor.width/2;
		float trainy = train.getY() + TrainActor.height/2;
		float nexty = next.getY() + StationActor.height/2;

		if (Math.abs(trainx-nextx)<1 && Math.abs(trainy- nexty)<1){
			return true;
		}
		return false;
	}

	private void trainComplete(final Tuple<Train, Connection> pair) {
		connectionAdded(pair.getSecond());

		/*pair.getFirst().getActor().addAction(sequence(fadeOut(1f), Actions.hide() , run(new Runnable() {
			public void run() {
				System.out.println("remove");
				pair.getFirst().getActor().remove();
				pair.getFirst().getPlayer().removeResource(pair.getFirst());
				connections.remove(pair);
			}
		})));*/

		pair.getFirst().getActor().remove();
		pair.getFirst().getPlayer().removeResource(pair.getFirst());
		//connections.remove(pair);
	}

	private float getDistance(IPositionable a, IPositionable b) {
		//This method returns the absolute distance from point A to point B in pixels
		return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
	}

	public void begin(Train train) {
		context.getGameLogic().setState(GameState.CREATING_CONNECTION);
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
