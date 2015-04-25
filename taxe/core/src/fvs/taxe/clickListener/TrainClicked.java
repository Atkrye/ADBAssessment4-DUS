package fvs.taxe.clickListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.actor.StationActor;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DialogResourceTrain;
import fvs.taxe.dialog.DialogStationMultitrain;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.player.Player;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;

/**This is a special type of ClickListener for when a train is clicked.*/
public class TrainClicked extends ClickListener {
	/**The game context.*/
	private Context context;

	/**The train that the click listener corresponds to.*/
	private Train train;

	/** Whether toolbar message is being displayed */
	private boolean displayingMessage;

	/**Instantiation method.
	 * @param context The context of the game.
	 * @param train The train the TrainClicked corresponds to.
	 */
	public TrainClicked(Context context, Train train) {
		this.train = train;
		this.context = context;
		displayingMessage = false;
	}

	/**This method is called when the TrainClicked is Clicked. The GUI is updated accordingly.*/
	@Override
	public void clicked(InputEvent event, float x, float y) {
		if (Game.getInstance().getState() == GameState.NORMAL || Game.getInstance().getState() == GameState.WAITING) {

			// current player can't be passed in as it changes so find out current player at this instant
			Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

			//This checks whether or not the train is already in motion and has an actor
			if (train.getActor() != null && x != -1) {
				if (train.getActor().getPaused()) {
					//This generates a list of trains located on the same spot as the clicked train
					//This was necessary as when trains are blocked at a station they stack on top of the station and only the uppermost train is clickable
					//This is therefore a workaround to ensure that all trains at that location remain selectable
					ArrayList<Train> stackedTrains = new ArrayList<Train>();
					for (Actor actor : context.getStage().getActors()) {
						if (actor instanceof TrainActor) {
							TrainActor trainActor = (TrainActor) actor;
							//If the actor's bounds (location and size) equal that of the original train's actor then the train is added to the ArrayList
							//if (trainActor.getBounds().overlaps(train.getActor().getBounds())) {
							if ((Intersector.overlapConvexPolygons(trainActor.getBounds(), train.getActor().getBounds()))) {
								stackedTrains.add(trainActor.train);
							}
						}//This checks all station actors and checks whether or not the trainActor overlaps with the station
						//If it does then it is necessary to add all trains at that station to the dialog too
						else if (actor instanceof StationActor) {
							StationActor stationActor = (StationActor) actor;
							//if (stationActor.getBounds().overlaps(train.getActor().getBounds())) {
							if ((Intersector.overlapConvexPolygons(stationActor.getBounds(), train.getActor().getBounds()))) {
								for (Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
									for (Resource resource : player.getResources()) {
										if (resource instanceof Train) {
											if (((Train) resource).getPosition() == ((StationActor) actor).getStation().getPosition()) {
												stackedTrains.add((Train) resource);
											}
										}
									}
								}
							}

						}
					}
					if (stackedTrains.size()==1) {
						clicked(event,-1,-1);
					} else {
						//Creates a new multitrain dialog based on the number of trains at that location
						DialogStationMultitrain dia = new DialogStationMultitrain(stackedTrains, context.getSkin(), context);
						dia.show(context.getStage());
					}
				} else {
					if (train.isOwnedBy(currentPlayer)) {
						DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, train);
						DialogResourceTrain dia = new DialogResourceTrain(context, train, context.getSkin(), train.getPosition() != null);
						dia.show(context.getStage());
						dia.subscribeClick(listener);
					}
				}

			}

			//The rest of the code assumes there is only a single train on that location as that is the only possibility
			else if (!train.isOwnedBy(currentPlayer)) {
				//If the train is not owned by the current player then its details are displayed but nothing more
				context.getTopBarController().displayFlashMessage("Enemy's " + train.getName() + ". Speed: " + train.getSpeed(), Color.RED);
				context.getGameLogic().setState(GameState.NORMAL);
			} else {
				//If the train is owned by the player and has a final destination then a dialog is displayed allowing the user to interact with the train
				DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, train);
				DialogResourceTrain dia = new DialogResourceTrain(context, train, context.getSkin(), train.getPosition() != null);
				dia.show(context.getStage());
				dia.subscribeClick(listener);
			}
		}
	}

	/** Used for exiting the mouseover for trains*/
	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor trainActor) {
		//This is used for mouseover events for trains
		//This hides the message currently in the topBar if one is being displayed
		if (train.getActor() != null && x != -1) {
			Gdx.input.setCursorImage(null, 0, 0);
		}
		if (displayingMessage) {
			displayingMessage = false;
			if (Game.getInstance().getState() == GameState.NORMAL) {
				//If the game state is normal then the topBar is cleared by passing it an empty string to display for 0 seconds
				context.getTopBarController().clearMessage();
			}
		}
	}

	/** Used for entering the mouseover for trains*/
	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor trainActor) {
		//This is used for mouseover events for trains
		//This shows the message if there is not one currently being displayed
		if (train.getActor() != null && x != -1) {
			Gdx.input.setCursorImage(new Pixmap(Gdx.files.internal("cursor.png")), 0, 0);
		}
		if (!displayingMessage) {
			displayingMessage = true;
			if (Game.getInstance().getState() == GameState.NORMAL) {

				// current player can't be passed in as it changes so find out current player at this instant
				Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

				if (!train.isOwnedBy(currentPlayer)) {
					//If the train isn't owned by the current player then basic information is displayed about it
					//By passing the displayFlashMessage a very large value for time, this acts almost as a permanent message until it is cleared (suitable for mouseover)
					context.getTopBarController().displayMessage("Opponent's " + train.getName() + ". Speed: " + train.getSpeed(), Color.RED);
				} else if (train.getFinalDestination() == null) {
					//If the train is not placed then the name and speed of the train are displayed
					//By passing the displayFlashMessage a very large value for time, this acts almost as a permanent message until it is cleared (suitable for mouseover)
					context.getTopBarController().displayMessage("Your " + train.getName() + ". Speed: " + train.getSpeed(), Color.BLACK);

				} else {
					//If the train is the player's and placed then the name, speed and destination of the train are all displayed
					//By passing the displayFlashMessage a very large value for time, this acts almost as a permanent message until it is cleared (suitable for mouseover)
					context.getTopBarController().displayMessage("Your " + train.getName() + ". Speed: " + train.getSpeed() + ". Destination: " + train.getFinalDestination().getName(), Color.BLACK);
				}
			}
		}
	}
}
