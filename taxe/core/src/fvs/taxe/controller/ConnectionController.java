package fvs.taxe.controller;

import fvs.taxe.TaxeGame;
import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.StationActor;
import fvs.taxe.clickListener.StationClickListener;
import gameLogic.GameState;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.listeners.StationChangedListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.player.PlayerManager;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConnectionController {
	// class used for creating connections when in creating_connection mode 
	private static ArrayList<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
	private Context context;
	private static ArrayList<Connection> connections = new ArrayList<Connection>();

	// when currently selecting a connection
	private PioneerTrain train;
	private Station firstStation;
	private TextButton back;
	private static ArrayList<StationChangedListener> slisteners = new ArrayList<StationChangedListener>();

	private static int junctionNumber = 0;

	
	public ConnectionController(final Context context) {
		context.getGameLogic().getMap().getMapActor();
		this.context = context;
		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION) {
					if (station != firstStation){
						if (!connectionOverlaps(station)){
							if (!context.getGameLogic().getMap().doesConnectionExist(firstStation.getName(), station.getName())) {
								if (!connectionBeingMade(station)){
									endCreating(station);
								} else {
									context.getTopBarController().displayFlashMessage("Connection being created", Color.RED);
								}
							} else {
								context.getTopBarController().displayFlashMessage("Connection already exists", Color.RED);
							}
						} else {
							context.getTopBarController().displayFlashMessage("Connection too close to a station", Color.RED);
						}
					}
				} 
			} 
		});

		context.getGameLogic().getMap().getMapActor().addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				x += 290; // offset for sidebar
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION){
					if (firstStation != null){
						Position location = new Position((int) x,(int)y);
						if (!nearStation(location) ) {
							if (!nearConnection(location)) {
							Station station = new Station("1", location); 
							StationController.renderStation(station);
							stationAdded(station);
							endCreating(station);
							} else {
								context.getTopBarController().displayFlashMessage("New city too close to connection", Color.RED);
							}
						} else {
							context.getTopBarController().displayFlashMessage("New city too close to existing city", Color.RED);
						}
					}
				}
			}
		});
	}

	protected boolean connectionBeingMade(Station station) {
		for (Connection connection: connections) {
			if (connection.getStation1().equals(firstStation)){
				if (connection.getStation2().equals(station)){
					return true;
				}
			} else if (connection.getStation2().equals(firstStation)){
				if (connection.getStation1().equals(station)){
					return true;
				}
			}
		}
		return false;
	}

	protected boolean nearStation(Position location) {
		// test if a location is near another station
		ArrayList<Station> stations = (ArrayList<Station>) context.getGameLogic().getMap().getStations();
		for (Station station : stations) {
			if (Position.getDistance(location, station.getPosition()) <= StationActor.height + 20) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean nearConnection(Position location) {
		// test if a location is near a connection
		ArrayList<Connection> connections = (ArrayList<Connection>) context.getGameLogic().getMap().getConnections();
		for (Connection connection : connections) {
			IPositionable p1 = connection.getStation1().getPosition();
			Vector2 v1 = new Vector2(p1.getX(), p1.getY());
			IPositionable p2 = connection.getStation2().getPosition();
			Vector2 v2 = new Vector2(p2.getX(), p2.getY());
			
			Vector2 v3 = new Vector2(location.getX(), location.getY());
			boolean intersect = Intersector.intersectSegmentCircle(v1, v2, v3, 1000);
			System.out.println("Hello");
			if (intersect){
				return true;
			}
		}
		return false;
	}

	protected boolean connectionOverlaps(Station station) {
		// check if a connection overlaps with a station
		IPositionable position1 = firstStation.getPosition();
		IPositionable position2 = station.getPosition();
		int x1,x2,x3,x4,y1,y2,y3,y4;

		x3 = position1.getX();
		y3 = position1.getY();
		x4 = position2.getX();
		y4 = position2.getY();

		ArrayList<Station> stations = (ArrayList<Station>) context.getGameLogic().getMap().getStations();

		for (Station s: stations){
			if (s == firstStation || s == station){
				continue;
			}

			x1 = s.getPosition().getX() - StationActor.width/2 - 10;
			y1 = s.getPosition().getY() - StationActor.height/2 - 10;
			x2 = s.getPosition().getX() + StationActor.width/2 + 10;
			y2 = s.getPosition().getY() + StationActor.height/2 + 10;

			Position value = Position.getLineIntersect(x1, y1, x1, y2, x3, y3, x4, y4);
			if (value != null){
				return true;
			}

			value = Position.getLineIntersect(x1, y1, x2, y1, x3, y3, x4, y4);
			if (value != null){
				return true;
			}

			value = Position.getLineIntersect(x1, y2, x2, y2, x3, y3, x4, y4);
			if (value != null){
				return true;
			}

			value = Position.getLineIntersect(x2, y1, x2, y2, x3, y3, x4, y4);
			if (value != null){
				return true;
			}
		}
		return false;
	}

	public void destroyConnection(KamikazeTrain train) {
		Station l1 = train.getLastStation();
		Station l2 = train.getNextStation();
		Connection connection = context.getGameLogic().getMap().getConnection(l1, l2);
		
		connectionRemoved(connection);
		
		if (l1.getClass().equals(CollisionStation.class)) {
			if (!context.getGameLogic().getMap().hasConnection(l1)) {
				stationRemoved(l1);
			}
		}
		
		if (l2.getClass().equals(CollisionStation.class)) {
			if (!context.getGameLogic().getMap().hasConnection(l2)) {
				stationRemoved(l2);
			}
		}
		
		
	}

	public void beginCreating(PioneerTrain train) {
		context.getGameLogic().setState(GameState.CREATING_CONNECTION);
		firstStation = train.getLastStation();
		this.train = train;

		//This makes all trains except the currently routed trains to be invisible.
		//This makes the screen less cluttered while routing and prevents overlapping trainActors from stopping the user being able to click stations.
		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, false);
		train.getActor().setVisible(true);

		context.getTopBarController().displayMessage("Select the destination station", Color.BLACK);
		drawCancelButton();
	}

	private void endCreating(Station station) {
		Connection connection = new Connection(firstStation, station);
		connections.add(connection);
		
		train.setPosition(new Position(-1, -1));
		train.setCreating(connection);
		context.getGameLogic().setState(GameState.NORMAL);

		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, true);
		back.setVisible(false);
	}

	public void drawMouse() {
		ShapeRenderer shapeRenderer = context.getTaxeGame().shapeRenderer;
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		context.getGameLogic().getPlayerManager();
		if (context.getGameLogic().getPlayerManager().isNight()){
			shapeRenderer.setColor(Color.WHITE);
		} else {
			shapeRenderer.setColor(Color.BLACK);
		}
		
		context.getTaxeGame();
		shapeRenderer.rectLine(firstStation.getPosition().getX(), firstStation.getPosition().getY(), 
				Gdx.input.getX(), TaxeGame.HEIGHT- Gdx.input.getY(), 5);
		shapeRenderer.end();
	}

	private void drawCancelButton() {
		//Adds a button to leave the view route screen
		if (back == null) {
			back = new TextButton("Return", context.getSkin());
			back.setPosition(TaxeGame.WIDTH - 100, TaxeGame.HEIGHT - 33);
			back.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					context.getTopBarController().clearMessage();
					back.setVisible(false);
					train.getActor().setVisible(false);
					firstStation = null;
					train = null;
					context.getGameLogic().setState(GameState.NORMAL);
				}
			});
			context.getStage().addActor(back);
		} else {
			back.setVisible(true);
		}
	}

	public static String getNextJunctionNum() {
		String string = Integer.toString(junctionNumber);
		junctionNumber+=1;
		return string;
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

	public static void subscribeStationAdded(StationChangedListener stationAddedListener){
		slisteners.add(stationAddedListener);
	}
	
	private void stationAdded(Station station) {
		for (StationChangedListener listener : slisteners ){
			listener.stationAdded(station);
		}
	}
	
	private void stationRemoved(Station station){
		for (StationChangedListener listener : slisteners ){
			listener.stationRemoved(station);
		}
	}

	public void pioneerTrainComplete(PioneerTrainActor actor) {
		ArrayList<Tuple<Connection, Position>> collidedPositions = actor.collidedConnection();
		CollisionStation prevJunction = null;
		Connection connection = actor.getConnection();
		
		if (collidedPositions.size() == 0){
			context.getConnectionController().connectionAdded(connection);

		} else {
			// if the train has collided with some connections
			Station startStation = connection.getStation1();
			Station endStation = connection.getStation2();
			for (int i = 0; i < collidedPositions.size(); i++) {
				Tuple<Connection, Position> pair = collidedPositions.get(i);
				Connection collidedConn = pair.getFirst();
				Position position = pair.getSecond();
				CollisionStation junction = context.getGameLogic().getMap().addJunction(ConnectionController.getNextJunctionNum(), position);
				StationController.renderCollisionStation(junction);

				context.getConnectionController().connectionRemoved(collidedConn);

				Station iStation1 = collidedConn.getStation1();
				Station iStation2 = collidedConn.getStation2();
				if (i == 0 && collidedPositions.size() == 1) {
					connectionAdded(new Connection(iStation1, junction));
					connectionAdded(new Connection(iStation2, junction));
					connectionAdded(new Connection(startStation, junction));
					connectionAdded(new Connection(endStation, junction));

				} else if (i == 0) {
					connectionAdded(new Connection(iStation1, junction));
					connectionAdded(new Connection(iStation2, junction));
					connectionAdded(new Connection(startStation, junction));
					prevJunction = junction;

				} else if (i == collidedPositions.size() -1) {
					connectionAdded(new Connection(iStation1, junction));
					connectionAdded(new Connection(iStation2, junction));
					connectionAdded(new Connection(prevJunction, junction));
					connectionAdded(new Connection(junction, endStation));

				} else {
					connectionAdded(new Connection(iStation1, junction));
					connectionAdded(new Connection(iStation2, junction));
					connectionAdded(new Connection(prevJunction, junction));
					prevJunction = junction;
				} 
			}
		}
		actor.getTrain().creationCompleted();
	}
	
}
