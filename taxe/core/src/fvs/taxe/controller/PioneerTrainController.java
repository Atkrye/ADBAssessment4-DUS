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

/** Controller class for a single pioneer train */
public class PioneerTrainController {
	private Context context;
	
	/** Whether the pioneer train controller is completed (if false, taking input)*/
	private boolean completed = false;
	
	/** The firstStation of the corresponding train*/
	private Station firstStation;
	
	/** The train that corresponds to this controller */
	private PioneerTrain train;
	
	/** The connectionController for this controller */ 
	private ConnectionController connectionController;

	/** Constructor for class, takes input immediately 
	 * Class will be created once connectionController begins creating a connection*/
	public PioneerTrainController(PioneerTrain train, final Context context) {
		this.context = context;
		this.train = train;
		this.connectionController = context.getConnectionController();
		final Map map = context.getGameLogic().getMap();
		
		// If a suitable station is clicked, have this as the other station of the connection to be created
		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION && !completed) {
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

		// If a suitable position on map clicked (not station) create a new station
		map.getMapActor().addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				x += 290; // offset for sidebar
				if (context.getGameLogic().getState() == GameState.CREATING_CONNECTION && !completed){
					if (firstStation != null){
						Position location = new Position((int) x,(int)y);
						if (!map.nearStation(location) ) {
							if (!map.nearConnection(location)) {
								// Show station naming dialog
								context.getGameLogic().setState(GameState.WAITING);
								connectionController.showStationNameEntry(location);
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
	
	/** Start the pioneer train creating- display actor correspondingly*/
	public void beginCreating() {
		context.getGameLogic().setState(GameState.CREATING_CONNECTION);
		firstStation = train.getLastStation();
		train.getActor().setVisible(true);
		completed = false;
	}
	
	/** Stop the pioneer train creating- set the train on its course to plant the new connection 
	 * @param station Station at the other end of the connection 
	 */
	protected void endCreating(Station station) {
		Connection connection = new Connection(firstStation, station);
		train.setPosition(new Position(-1, -1));
		train.setCreating(connection);

		train.getActor().setupConnectionPlanting(connection); 
		addPioneerActions(station);
		connectionController.endCreating(connection);
		
		completed = true;
	}
	
	/** Give the pioneer train the actions needed to plant the connections
	 * @param station Station at other end of connection 
	 */
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
				pioneerTrainComplete();
				return true;
			}
		};
		actions.addAction(finishedCreating);

		train.getActor().addAction(actions);
	}
	
	/** Once the pioneerTrain has completed planting the new connection, create the new connections and junctions */
	public void pioneerTrainComplete() {
		PioneerTrainActor actor = train.getActor();
		ArrayList<Tuple<Connection, Position>> collidedPositions = actor.overlappedConnection();
		
		Connection connection = actor.getConnection();

		if (collidedPositions.size() == 0){
			// just add new connection if no overlaps
			context.getConnectionController().connectionAdded(connection);

		} else {
			// if the train has collided with some connections, create the new connections and junctions
			connectionController.addNewConnections(collidedPositions, connection);
		}
		actor.getTrain().creationCompleted();
	}

	public void setActive(boolean active) {
		this.completed = active;
	}
}
