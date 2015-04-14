package fvs.taxe.controller;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import fvs.taxe.TaxeGame;
import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.clickListener.StationClickListener;
import gameLogic.GameState;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.listeners.StationChangedListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.Map;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;

import Util.TextEntryBar;
import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	private TextEntryBar stationName;
	private boolean isNaming = false;
	private Image nameBackground;
	private InputAdapter nameip;
	private static ArrayList<StationChangedListener> slisteners = new ArrayList<StationChangedListener>();

	private static int junctionNumber = 0;

	public ConnectionController(final Context context) {
		this.context = context;

		final Map map = context.getGameLogic().getMap();
		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION) {
					if (station != firstStation){
						if (!map.connectionOverlaps(firstStation, station)){
							if (!map.doesConnectionExist(firstStation.getName(), station.getName())) {
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

		// for clicking to create new cities
		map.getMapActor().addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				x += 290; // offset for sidebar
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION){
					if (firstStation != null){
						Position location = new Position((int) x,(int)y);
						if (!map.nearStation(location) ) {
							if (!map.nearConnection(location)) {
								context.getGameLogic().setState(GameState.WAITING);
								getNewStationName(location);
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
		isNaming = false;
		Connection connection = new Connection(firstStation, station);
		connections.add(connection);

		train.setPosition(new Position(-1, -1));
		train.setCreating(connection);

		train.getActor().setStationPositions(connection);
		addPioneerActions(station);
		context.getGameLogic().setState(GameState.NORMAL);

		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, true);
		back.setVisible(false);
	}

	public void addPioneerActions(Station station) {
		train.getActor().clearActions();
		SequenceAction actions = Actions.sequence();

		//action to rotate the train so it is facing the direction it creates track in
		// actions require an angle in degrees for rotation 
		float radAngle = Position.getAngle(firstStation.getPosition(),station.getPosition());
		float degAngle = (float) (MathUtils.radiansToDegrees*radAngle);
		actions.addAction(Actions.rotateTo((float) degAngle));

		// action to move train to city
		float duration = Position.getDistance(firstStation.getPosition(), station.getPosition()) / train.getSpeed();
		actions.addAction(moveTo(station.getPosition().getX() - TrainActor.width / 2, station.getPosition().getY() - TrainActor.height / 2, duration));

		// Action to say that train has finished moving and reached destination, call pioneerTrainComplete()
		Action finishedCreating = new Action(){
			@Override
			public boolean act(float delta) {
				pioneerTrainComplete(train.getActor());
				System.out.println("poioneertriancomplete");
				return true;
			}
		};
		actions.addAction(finishedCreating);

		train.getActor().addAction(actions);
	}

	protected boolean connectionBeingMade(Station station) {
		// test if the connection is already being made
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

	public void drawStationNameBackground() {
		nameBackground = new Image(new Texture(Gdx.files.internal("NewStationDialog.png")));
		int midx = Math.round(context.getStage().getWidth() / 2 - nameBackground.getWidth()/2); 
		int midy = Math.round(context.getStage().getHeight() / 2- nameBackground.getHeight()/2);
		nameBackground.setPosition(midx , midy);
		context.getStage().addActor(nameBackground);
		nameBackground.setVisible(false);

		stationName = new TextEntryBar(midx + 80, midy + 30, 0, context.getTaxeGame());
	}

	private void getNewStationName(final Position location) {

		nameBackground.setVisible(true);

		final InputProcessor ip = Gdx.input.getInputProcessor();

		nameip = new InputAdapter () {
			//The input processor which acts upon a user pressing keys
			public boolean keyDown(int keycode) {
				if (keycode == Keys.BACKSPACE){
					//backspace deletes a character from the active entry bar
					stationName.deleteLetter();
				}
				if (keycode == Keys.ENTER){
					String text = stationName.getLabelValue();
					if (context.getGameLogic().getMap().isUniqueName(text)) {
						createNewStation(text, location);
						stationName.clear();
						nameBackground.setVisible(false);
						Gdx.input.setInputProcessor(ip);

					} else {
						context.getTopBarController().displayFlashMessage("Please enter a unique station name", Color.RED);
					}
				}
				if (keycode == Input.Keys.ESCAPE) {
					context.getGameLogic().setState(GameState.CREATING_CONNECTION);
					stationName.clear();
					nameBackground.setVisible(false);
					Gdx.input.setInputProcessor(ip);
				}
				return true;
			}

			public boolean keyTyped (char character) {
				//This adds a character to the active entry bar  
				stationName.makeLabel(character);
				return true;
			}
		};
		Gdx.input.setInputProcessor(nameip);
		isNaming = true;
	}

	private void createNewStation(String string, Position location) {
		Station station = new Station(string, location); 
		StationController.renderStation(station);
		stationAdded(station);
		endCreating(station);
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

	public static String getNextJunctionNum() {
		String string = Integer.toString(junctionNumber);
		junctionNumber+=1;
		return string;
	}

	public boolean isNamingStation() {
		return isNaming;
	}

	public TextEntryBar getStationName() {
		return stationName;
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
}
