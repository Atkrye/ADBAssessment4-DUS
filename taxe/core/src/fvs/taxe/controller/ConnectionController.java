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
import gameLogic.resource.PioneerTrain;
import gameLogic.resource.Train;

import java.util.ArrayList;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConnectionController {
	private static ArrayList<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
	private Context context;
	//private ArrayList<Tuple<PioneerTrain,Connection>> connections;	// pairs of station that have not yet been created 

	// when currently selecting a connection
	private PioneerTrain train;
	private Station firstStation;
	private TextButton back;
	// class used for creating connections when in creating_connection mode mode


	public ConnectionController(final Context context) {
		this.context = context;
		//connections = new ArrayList<Tuple<PioneerTrain,Connection>>();
		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION) {
					if (station != firstStation){
						if (!context.getGameLogic().getMap().doesConnectionExist(firstStation.getName(), station.getName())) {
							endCreating(station);
						} else {
							context.getTopBarController().displayFlashMessage("Connection already exists", Color.RED);
						}
					}
				}
			}
		});
	}

	public void begin(PioneerTrain train) {
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
		train.setPosition(new Position(-1, -1));
		train.setCreating(connection);
		context.getGameLogic().setState(GameState.NORMAL);
		back.setVisible(false);
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

	public void drawMouse() {
		ShapeRenderer shapeRenderer = context.getTaxeGame().shapeRenderer;
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		context.getTaxeGame();
		shapeRenderer.rectLine(firstStation.getPosition().getX(), firstStation.getPosition().getY(), 
				Gdx.input.getX(), TaxeGame.HEIGHT- Gdx.input.getY(), 5);
		shapeRenderer.end();
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
