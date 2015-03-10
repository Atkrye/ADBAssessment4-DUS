package fvs.taxe.clickListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import fvs.taxe.Button;
import fvs.taxe.GameScreen;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.ConnectionController;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.StationController;
import fvs.taxe.controller.TrainController;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;
import gameLogic.resource.Skip;
import gameLogic.resource.Train;

public class DialogButtonClicked implements ResourceDialogClickListener {
	//This class is huge and seemingly complicated because it handles the events based off of any button being clicked
	private Context context;
	private Player currentPlayer;
	private Train train;
	private Skip skip;

	public DialogButtonClicked(Context context, Player player, Train train) {
		//This constructor is used when a train dialog button is clicked.
		//Train is set to the train that the dialog was associated with and the other variables are set to null
		this.currentPlayer = player;
		this.train = train;
		this.context = context;
		this.skip = null;
	}

	public DialogButtonClicked(Context context, Player player, Skip skip) {
		//This constructor is used when an skip dialog button is clicked.
		//skip is set to the skip that the dialog was associated with and the other variables are set to null
		this.currentPlayer = player;
		this.train = null;
		this.skip = skip;
		this.context = context;
	}

	@Override
	public void clicked(Button button) {
		switch (button) {
		case TRAIN_DROP:
			//If a TRAIN_DROP button is pressed then the train is removed from the player's resources
			currentPlayer.removeResource(train);
			train.getActor().remove();
			break;

			//The reason that all the placement case statements are in their own scope ({}) is due to the fact that switch statements do not create their own scopes between cases.
			//Instead these must be manually defined, which was done to allow for instantiation of new TrainControllers.
		case TRAIN_PLACE: {
			//If the TRAIN_PLACE button is pressed then the game is set up so that the train can be placed

			//This sets the cursor to be the one associated with the train loaded from the assets folder
			Pixmap pixmap = new Pixmap(Gdx.files.internal(train.getCursorImage()));
			Gdx.input.setCursorImage(pixmap, 0, 0); // these numbers will need tweaking
			pixmap.dispose();

			//Begins the placement of a train
			Game.getInstance().setState(GameState.PLACING_TRAIN);

			//Hides all trains currently on the map
			TrainController trainController = new TrainController(context);
			trainController.setTrainsVisible(null, false);

			//A station click listener is generated to handle the placement of the train
			final StationClickListener stationListener = new StationClickListener() {
				@Override
				public void clicked(Station station) {
					//Checks whether a node is a junction or not. If it is then the train cannot be placed there and the user is informed
					if (station instanceof CollisionStation) {
						context.getTopBarController().displayFlashMessage("Trains cannot be placed at junctions.", Color.RED);

					} else {
						//This puts the train at the station that the user clicks and adds it to the trains visited history
						train.setPosition(station.getPosition());
						train.addHistory(station, Game.getInstance().getPlayerManager().getTurnNumber());

						//Resets the cursor
						Gdx.input.setCursorImage(null, 0, 0);

						//Hides the current train but makes all moving trains visible
						TrainController trainController = new TrainController(context);
						TrainActor trainActor = trainController.renderTrain(train);
						trainController.setTrainsVisible(null, true);
						train.setActor(trainActor);
						
						//Unsubscribes from the listener so that it does not call this code again when it is obviously not necessary, without this placing of trains would never end
						StationController.unsubscribeStationClick(this);
						Game.getInstance().setState(GameState.NORMAL);
					}
				}
			};

			final InputListener keyListener = new InputListener() {
				@Override
				public boolean keyDown(InputEvent event, int keycode) {
					//If the Escape key is pressed while placing a train then it is cancelled
					//This is a new addition as the original code did not allow the user to cancel placement of trains once they had begun which was frustrating
					if (keycode == Input.Keys.ESCAPE) {
						//Sets all of the currently placed trains back to visible
						TrainController trainController = new TrainController(context);
						trainController.setTrainsVisible(null, true);

						//Resets the cursor
						Gdx.input.setCursorImage(null, 0, 0);

						//Unsubscribes from the listener so that it does not call the code when it is not intended to
						StationController.unsubscribeStationClick(stationListener);
						Game.getInstance().setState(GameState.NORMAL);

						//Removes itself from the keylisteners of the game as otherwise there would be a lot of null pointer exceptions and unintended behaviour
						context.getStage().removeListener(this);
					}

					//keyDown requires you to return the boolean true when the function has completed, so this ends the function
					return true;
				}
			};

			//Adds the keyListener to the game
			context.getStage().addListener(keyListener);

			//Adds the stationClick listener to the stationController's listeners
			StationController.subscribeStationClick(stationListener);
			break;
		}

		case TRAIN_ROUTE:
			//Begins routing a train if the TRAIN_ROUTE button is clicked
			context.getRouteController().begin(train);
			break;

		case VIEW_ROUTE:
			//Shows the user the train's current route if they click on VIEW_ROUTE button
			context.getRouteController().viewRoute(train);
			break;
			
		case SKIP_RESOURCE:
			//If SKIP_RESOURCE is pressed then this finds the other player's playerNumber and sets their skipped boolean to true
			//If you wish to add more than 2 players then extra checking would have to be added here to ensure that the right player has their turn skipped
			//For our implementation just checking the two binary values is enough
			int p = context.getGameLogic().getPlayerManager().getCurrentPlayer().getPlayerNumber() - 1;
			if (p == 0) {
				p = 1;
			} else {
				p = 0;
			}

			context.getGameLogic().getPlayerManager().getAllPlayers().get(p).setSkip(true);
			//Removes the resource after it has been used
			currentPlayer.removeResource(skip);
			break;

		case SKIP_DROP:
			//Removes the resource from the player if they press the SKIP_DROP button
			currentPlayer.removeResource(skip);
			break;

		case TRAIN_CHANGE_ROUTE:
			//Begins the change route feature when TRAIN_CHANGE_ROUTE is pressed by the player
			context.getRouteController().begin(train);
			break;
			
		case TRAIN_CREATE_CONNECTION:
			// Begin creating the connection between 2 points
			context.getConnectionController().beginCreating((PioneerTrain) train);
			break;
		
		case TRAIN_KAMIKAZE:
			// Kamikaze, destroy the connection
			context.getConnectionController().destroyConnection((KamikazeTrain) train);
			((KamikazeTrain) train).selfDestruct();
			break;
			
		default:
			break;
		}
	}
}
