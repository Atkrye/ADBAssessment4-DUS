package fvs.taxe.controller;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import fvs.taxe.actor.PioneerTrainActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.clickListener.StationClickListener;
import gameLogic.GameState;
import gameLogic.map.Connection;
import gameLogic.map.Map;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;

import Util.Tuple;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PioneerTrainController {
	private boolean active = false;
	private Context context;
	private Station firstStation;
	private PioneerTrain train;
	private ConnectionController connectionController;

	public PioneerTrainController(PioneerTrain train, final Context context) {
		this.context = context;
		this.train = train;
		this.connectionController = context.getConnectionController();
		final Map map = context.getGameLogic().getMap();
		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION && !active) {
					if (station != firstStation){
						if (!map.connectionOverlaps(firstStation, station)){
							if (!map.doesConnectionExist(firstStation.getName(), station.getName())) {
								if (!connectionController.connectionBeingMade(station)){
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
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION && !active){
					if (firstStation != null){
						Position location = new Position((int) x,(int)y);
						if (!map.nearStation(location) ) {
							if (!map.nearConnection(location)) {
								context.getGameLogic().setState(GameState.WAITING);
								connectionController.showStationNameEntry(location);
								//endCreating(station);
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
	
	protected void endCreating(Station station) {
		Connection connection = new Connection(firstStation, station);
		train.setPosition(new Position(-1, -1));
		train.setCreating(connection);

		train.getActor().setStationPositions(connection);
		addPioneerActions(station);
		connectionController.endCreating(connection);
		
		active = true;
	}

	public void beginCreating() {
		context.getGameLogic().setState(GameState.CREATING_CONNECTION);
		firstStation = train.getLastStation();
		//this.train = train;
		train.getActor().setVisible(true);
		//connectionController.beginCreating();
		this.active = false;
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
	
	public void pioneerTrainComplete(PioneerTrainActor actor) {
		ArrayList<Tuple<Connection, Position>> collidedPositions = actor.collidedConnection();
		
		Connection connection = actor.getConnection();

		if (collidedPositions.size() == 0){
			context.getConnectionController().connectionAdded(connection);

		} else {
			// if the train has collided with some connections
			connectionController.addNewConnections(collidedPositions, connection);
		}
		actor.getTrain().creationCompleted();
	}
}
