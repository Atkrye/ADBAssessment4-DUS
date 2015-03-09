package fvs.taxe.controller;

import fvs.taxe.TaxeGame;
import fvs.taxe.actor.StationActor;
import fvs.taxe.clickListener.StationClickListener;
import gameLogic.GameState;
import gameLogic.listeners.ConnectionChangedListener;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConnectionController {
	private static ArrayList<ConnectionChangedListener> listeners = new ArrayList<ConnectionChangedListener>();
	private Context context;

	// when currently selecting a connection
	private PioneerTrain train;
	private Station firstStation;
	private TextButton back;
	
	private static int junctionNumber = 0;
	
	
	// class used for creating connections when in creating_connection mode mode


	public ConnectionController(final Context context) {
		this.context = context;
		//connections = new ArrayList<Tuple<PioneerTrain,Connection>>();
		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION) {
					if (station != firstStation){
						if (!connectionOverlaps(station)){
							if (!context.getGameLogic().getMap().doesConnectionExist(firstStation.getName(), station.getName())) {
								endCreating(station);
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
	}

	protected boolean connectionOverlaps(Station station) {
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
		shapeRenderer.setColor(Color.BLACK);
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

	public static String getNextJunctionNum() {
		String string = Integer.toString(junctionNumber);
		junctionNumber+=1;
		return string;
	}
}
