package fvs.taxe.controller;


import fvs.taxe.TaxeGame;
import gameLogic.GameState;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.listeners.GameStateListener;
import gameLogic.listeners.StationChangedListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;

import Util.TextEntryDialog;
import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConnectionController {
	private Context context;

	private static ArrayList<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
	private static ArrayList<StationChangedListener> slisteners = new ArrayList<StationChangedListener>();

	private static ArrayList<Connection> connections = new ArrayList<Connection>();

	// when currently selecting a connection
	private PioneerTrain train;
	private Station firstStation;

	private InputAdapter nameip;

	private ImageButton cancel;
	private Image cancelImage;

	private static int junctionNumber = 0;

	private PioneerTrainController controller;



	public ConnectionController(final Context context) {
		this.context = context;
		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state == GameState.CREATING_CONNECTION) {
					//This makes all trains except the currently routed trains to be invisible.
					//This makes the screen less cluttered while routing and prevents overlapping trainActors from stopping the user being able to click stations.
					TrainController trainController = new TrainController(context);
					trainController.setTrainsVisible(train, false);

					context.getTopBarController().displayMessage("Select the destination station", Color.BLACK);
					drawCancelButton();
				} 
			}
		});
	}

	public void beginCreating(PioneerTrain train) {
		this.train = train;
		this.firstStation = train.getLastStation();
		controller = new PioneerTrainController(train, context);
		controller.beginCreating();
		context.getGameLogic().setState(GameState.CREATING_CONNECTION);
	}

	public void endCreating(Connection connection) {
		if (connection != null){
			connections.add(connection);
		} else {
			train.getActor().setVisible(false);
		}
		this.train = null;
		this.firstStation = null;

		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, true);
		cancel.setVisible(false);
		cancelImage.setVisible(false);

		context.getGameLogic().setState(GameState.NORMAL);
	}

	private Station createNewStation(String string, Position location) {
		Station station = new Station(string, location); 
		StationController.renderStation(station);
		stationAdded(station);
		return station;
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
		//Adds a button to leave the screen
		if (cancel == null) {
			Texture cancelText = new Texture(Gdx.files.internal("btn_cancel.png"));
			cancelImage = new Image(cancelText);
			cancelImage.setWidth(106);
			cancelImage.setHeight(37);
			cancelImage.setPosition(TaxeGame.WIDTH - 120, TaxeGame.HEIGHT - 56);

			cancel = new ImageButton(context.getSkin());
			cancel.setPosition(TaxeGame.WIDTH - 120, TaxeGame.HEIGHT - 56);
			cancel.setWidth(106);
			cancel.setHeight(37);
			cancel.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					endCreating(null);
					controller.setActive(true);
					
				}
			});

			context.getStage().addActor(cancelImage);
			context.getStage().addActor(cancel);
		} else {
			cancel.setVisible(true);
			cancelImage.setVisible(true);
		}
	}

	public void showStationNameEntry(final Position location) {
		final InputProcessor ip = Gdx.input.getInputProcessor();
		Image image = new Image(new Texture(Gdx.files.internal("NewStationDialog.png")));
		nameip = new TextEntryDialog(context, image) {
			@Override
			public boolean keyDown(int keycode) {
				super.keyDown(keycode);
				if (keycode == Keys.ENTER){
					String text = textEntryBar.getLabelValue();
					if (context.getGameLogic().getMap().isUniqueName(text)) {
						Station station = createNewStation(text, location);
						controller.endCreating(station);
						textEntryBar.clear();
						setVisible(false);
						Gdx.input.setInputProcessor(ip);

					} else {
						context.getTopBarController().displayFlashMessage("Please enter a unique station name", Color.RED);
					}
				}
				if (keycode == Input.Keys.ESCAPE) {
					context.getGameLogic().setState(GameState.CREATING_CONNECTION);
					setVisible(false);
					Gdx.input.setInputProcessor(ip);
				}
				return true;
			}
		};
		Gdx.input.setInputProcessor(nameip);
	}

	public void addNewConnections(ArrayList<Tuple<Connection, Position>> collidedPositions, Connection connection) {
		CollisionStation prevJunction = null;
		Station startStation = connection.getStation1();
		Station endStation = connection.getStation2();
		for (int i = 0; i < collidedPositions.size(); i++) {
			Tuple<Connection, Position> pair = collidedPositions.get(i);
			Connection collidedConn = pair.getFirst();
			Position position = pair.getSecond();
			CollisionStation junction = context.getGameLogic().getMap().addJunction(ConnectionController.getNextJunctionNum(), position);
			StationController.renderCollisionStation(junction);

			connectionRemoved(collidedConn);

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
}
