package fvs.taxe.controller;

import fvs.taxe.SoundPlayer;
import fvs.taxe.TaxeGame;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.clickListener.StationClickListener;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.Train;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**Controller for using routing, with GUI*/
public class RouteController {

	/**The context of the Game.*/
	private Context context;

	/**The positions selected in routing.*/
	private List<IPositionable> positions;

	/**The connections selected in routing.*/
	private List<Connection> connections;

	/**Whether or not the player is currently using routing*/
	private boolean isRouting = false;

	/**The train currently having a route selected*/
	private Train train;

	/**Whether or not the currently selected route is at a point where the routing can be completed*/
	private boolean canEndRouting = true;

	/** Whether the route is currently being edited (true if it is, else its a new route being made */
	private boolean editingRoute = false;

	/** Distance of the route currently beng made in pixels */
	private double distance = 0;

	/** Image button corresponding to the done routing */
	private ImageButton doneRouting;

	/** Image burron corresponding to the cancellation of routing */
	private ImageButton cancel;

	/** Image related to the doneRouting button */
	private Image doneRoutingImage;

	/** Image related to the cancel button */
	private Image cancelImage;

	/** If view route has been selected, the index into the connections array of which connection the train is partly across */
	private int indexPartial = -1;

	/**Instantiation method. Sets up a listener for when a train is selected. If the RouteController is routing, that station is then added to the route,
	 * @param context The context of the game.
	 */
	public RouteController(Context context) {
		this.context = context;

		connections = new ArrayList<Connection>();

		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (isRouting) {
					addStationToRoute(station);
				}
			}
		});
	}

	/**This method is called when a train is selected for routing,
	 * @param train The train to produce a route for.
	 */
	public void begin(Train train) {
		//This method is called when the user wants to create a route
		this.train = train;

		//sets the relevant flags to show that a route is being created
		isRouting = true;
		context.getGameLogic().setState(GameState.ROUTING);

		//Creates a new list and adds the station that the train is currently on as the first node.
		positions = new ArrayList<IPositionable>();

		//When a train has been placed at a station its position is equal to that of the station that it is located.
		//When a train already has a route and is moving, the position of train is (-1,-1).
		//This is checked here as we do not wish to route the train from its position to (-1,-1), hence this is only done when the train is at a station
		if (train.getPosition().getX() != -1) {
			positions.add(train.getPosition());
		} else{
			editingRoute = true;
		}

		//Generates all the buttons necessary to complete routing
		addRoutingButtons();

		//This makes all trains except the currently routed train to be invisible.
		//This makes the screen less cluttered while routing and prevents overlapping trainActors from stopping the user being able to click stations.
		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, false);
		train.getActor().setVisible(true);
	}

	/**This method adds a station to the route if its suitable. Its location is added, and the appropriate connection is stored.
	 * @param station The station to be added.
	 */
	private void addStationToRoute(Station station) {
		// the latest position chosen in the positions so far
		if (positions.size() == 0) {
			if (editingRoute) {
				//Checks whether the train's actor is paused due to a bug with blocked trains
				if (train.getActor().isPaused()){
					Station lastStation = null;
					lastStation = train.getLastStation();
					//Checks if a connection exists between the station the train is paused at and the clicked station
					if (Game.getInstance().getMap().doesConnectionExist(lastStation.getName(),station.getName())){
						positions.add(station.getPosition());

						// kamikaze trains can end on a junction
						if (!(this.train.getClass().equals(KamikazeTrain.class))) {
							//Sets the relevant boolean checking if the last node on the route is a junction or not
							canEndRouting = !(station instanceof CollisionStation);
						} else {
							canEndRouting = true;
						}
					} else {
						context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
					}
				} else {
					Station lastStation = train.getLastStation();
					Station nextStation = train.getNextStation();
					if (station.getName() == lastStation.getName() || nextStation.getName() == station.getName()) {
						//If the connection exists then the station passed to the method is added to the route
						positions.add(station.getPosition());

						if (!(this.train.getClass().equals(KamikazeTrain.class))) {
							//Sets the relevant boolean checking if the last node on the route is a junction or not
							canEndRouting = !(station instanceof CollisionStation);
						} else {
							canEndRouting = true;
						}
					} else {
						context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
					}
				}
			} else {
				positions.add(station.getPosition());
			}
		} else {
			//Finds the last station in the current route
			Station lastStation  = Game.getInstance().getMap().getStationFromPosition(positions.get(positions.size() - 1));

			System.out.println(lastStation.getName());
			//Check whether a connection exists using the function in Map
			boolean hasConnection = Game.getInstance().getMap().doesConnectionExist(station.getName(), lastStation.getName());
			if (!hasConnection) {
				//If the connection doesn't exist then this informs the user
				context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
			} else {
				distance+= Game.getInstance().getMap().getStationDistance(lastStation, station);
				DecimalFormat integer = new DecimalFormat("0");

				context.getTopBarController().displayMessage("Total Distance: " + integer.format(distance) + ". Will take " + integer.format(Math.ceil(distance / train.getSpeed() / 2)) + " turns.", Color.BLACK);
				//If the connection exists then the station passed to the method is added to the route
				positions.add(station.getPosition());
				connections.add(Game.getInstance().getMap().getConnection(station, lastStation));

				if (!(this.train.getClass().equals(KamikazeTrain.class))) {
					//Sets the relevant boolean checking if the last node on the route is a junction or not
					canEndRouting = !(station instanceof CollisionStation);
				} else {
					canEndRouting = true;
				}
			}
		}
	}

	/**This method is called when Routing commences for a Train. It sets up buttons for cancelling and finishing the routing,*/
	private void addRoutingButtons() {
		if (doneRouting == null){

			Texture doneRoutingText = new Texture(Gdx.files.internal("btn_routecomplete.png"));
			doneRoutingImage = new Image(doneRoutingText);
			doneRoutingImage.setWidth(150);
			doneRoutingImage.setHeight(37);
			doneRoutingImage.setPosition(TaxeGame.WIDTH - 285, TaxeGame.HEIGHT - 56);

			Texture cancelText = new Texture(Gdx.files.internal("btn_cancel.png"));
			cancelImage = new Image(cancelText);
			cancelImage.setWidth(106);
			cancelImage.setHeight(37);
			cancelImage.setPosition(TaxeGame.WIDTH - 120, TaxeGame.HEIGHT - 56);

			doneRouting = new ImageButton(context.getSkin());

			doneRouting.setPosition(TaxeGame.WIDTH - 285, TaxeGame.HEIGHT - 56);
			doneRouting.setWidth(150);
			doneRouting.setHeight(37);


			cancel = new ImageButton(context.getSkin());
			cancel.setPosition(TaxeGame.WIDTH - 120, TaxeGame.HEIGHT - 56);
			cancel.setWidth(106);
			cancel.setHeight(37);

			//If the cancel button is clicked then the routing is ended but none of the positions are saved as a route in the backend
			cancel.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					endRouting();
				}
			});

			//If the finished button is pressed then the routing is ended and the route is saved in the backend
			doneRouting.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundPlayer.playSound(1);
					//Checks whether or not the route is legal and can end
					if (!canEndRouting) {
						//If not, informs the user of what they must do to make the route legal
						context.getTopBarController().displayFlashMessage("Your route must end at a station", Color.RED);
					} else {
						//If the route is legal then the route is saved and routing ended
						confirmed();
						endRouting();
					}
				}
			});

			//Adds the images to the screen
			context.getStage().addActor(cancelImage);
			context.getStage().addActor(doneRoutingImage);

			//Adds the buttons to the screen
			context.getStage().addActor(cancel);
			context.getStage().addActor(doneRouting);
		} else {
			cancelImage.setVisible(true);
			doneRoutingImage.setVisible(true);

			cancel.setVisible(true);
			doneRouting.setVisible(true);
		}
	}

	/**This method is called when a Route has been finalised by the player. The route is created from the positions, and the Train is set along this route
	 * using a TrainController.*/
	private void confirmed() {
		//Passes the positions to the backend to create a route
		train.setRoute(Game.getInstance().getMap().createRoute(positions));

		//A move controller is created to allow the train to move along its route.
		new TrainMoveController(context, train);
	}

	/**This method is called when the routing is finalised by the player or cancelled. The existing route is dropped and the RouteController is set up for the next Routing.*/
	private void endRouting() {
		//This routine sets the gamescreen back to how it should be for normal operation
		context.getGameLogic().setState(GameState.NORMAL);
		//All buttons are removed and flags set to the relevant values.
		cancelImage.setVisible(false);
		doneRoutingImage.setVisible(false);
		cancel.setVisible(false);
		doneRouting.setVisible(false);
		isRouting = false;
		editingRoute = false;
		distance = 0;
		context.getTopBarController().clearMessage();
		//This sets all trains currently travelling along their route to be set to visible.
		TrainController trainController = new TrainController(context);
		trainController.setTrainsVisible(train, true);

		if (indexPartial != -1){
			connections.get(indexPartial).getActor().clearPartialPosition();
		}
		indexPartial = -1;
		drawRoute(Color.GRAY);
		connections.clear();

		//Again using the principle that (-1,-1) is a moving train, this sets the train being routed to invisible if not already on a route, but makes it visible if it already had a route previously
		//This was necessary to add as without it, when editing a route and then cancelling, the train would become invisible for the duration of its original journey
		if (train.getPosition().getX() != -1) {
			train.getActor().setVisible(false);
		}
	}

	/** Method is used to draw the trains current route so that the user can see where their trains are going */
	public void viewRoute(Train train) {
		//This works by simulating the creation of a new route, but without the ability to save the route
		//This will instead draw the route passed to it, which is the one located in train.getRoute()
		//Because of the nature of save load, this can actually be called before a route has been chosen, so we have to ensure the buttons are created
		addRoutingButtons();
		doneRoutingImage.setVisible(false);
		doneRouting.setVisible(false);
		isRouting = false;
		editingRoute = false;
		cancel.setVisible(true);
		cancelImage.setVisible(true);
		this.train = train;
		positions = new ArrayList<IPositionable>();
		Station prevStation=null;
		for (Station station : train.getRoute()) {
			positions.add(station.getPosition());
			if (prevStation!=null) {
				distance += context.getGameLogic().getMap().getStationDistance(station,prevStation);
				DecimalFormat integer = new DecimalFormat("0");
				context.getTopBarController().displayMessage("Total Distance: " + integer.format(distance) + ". Will take " + integer.format(Math.ceil(distance / train.getSpeed() / 2)) + " turns.", Color.BLACK);
				connections.add(context.getGameLogic().getMap().getConnection(station, prevStation));
			}
			prevStation = station;
		}

		context.getGameLogic().setState(GameState.ROUTING);
		drawPartialRoute();
	}

	/**This method draws the currently selected Route for the player to view, using a different Color.
     * @param color The Color of the Route.
     */
	public void drawRoute(Color color) {
		for (Connection connection : connections) {
			connection.getActor().setConnectionColor(color);
		}
	}

	/** draws a route with a train partially on it */
	private void drawPartialRoute() {
		// calculate where train is
		Station next = train.getNextStation();
		Connection partialConnection = context.getGameLogic().getMap().getConnection(next, train.getLastStation());
		indexPartial  = connections.indexOf(partialConnection);
		// set the connection actor to render a portion of it (from train to station)
		partialConnection.getActor().setPartialPosition(train.getActor().getX()+ TrainActor.width/2, train.getActor().getY() + TrainActor.height/2, next.getPosition());

		for (int i = connections.indexOf(partialConnection)+1; i<connections.size(); i++){
			connections.get(i).getActor().setConnectionColor(Color.BLACK);
		}
	}
}