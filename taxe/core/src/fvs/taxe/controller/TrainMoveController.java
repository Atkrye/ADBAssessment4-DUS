package fvs.taxe.controller;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import fvs.taxe.actor.TrainActor;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

/**Controller for moving trains.*/
public class TrainMoveController {
	/**The context of the game.*/
	private Context context;
	
	/**The train being controlled by the controller.*/
	private Train train;

	/**Instantiation adds a turn listener to interrupt the train's action when a turn changes.
	 * @param context The game context.
	 * @param train The train to be controlled.
	 */
	public TrainMoveController(Context context, Train train) {
		this.context = context;
		this.train = train;

		//This adds the movement for the train
		addMoveActions();
	}

	/**This method produces an action for the train to run before moving on the screen.
	 * @return An action where the train is set to visible and off the screen.
	 */
	private RunnableAction beforeAction() {
		return new RunnableAction() {
			public void run() {
				train.getActor().setVisible(true);

				//This is where the (-1,-1) principle comes from as it is set to (-1,-1) before every action.
				//I don't understand exactly why this is, but it is how it was initially implemented and we did not understand enough to change it
				//Instead we now exploit this fact to determine whether a train is already in motion
				train.setPosition(new Position(-1, -1));
			}
		};
	}

	/**This method produces an action to run every time a train reaches a station on it's route.
	 * @param station The station reached.
	 * @return An action which adds the train movement to the move history and continues the journey of the train.
	 */
	private RunnableAction perStationAction(final Station station) {
		return new RunnableAction() {
			// pausing mechanism not currently in  use, but kept for future use
			public void run() {
				if (!obstacleCollision(station)) {	
					if (!train.getRoute().get(0).equals(station)) {
						train.getActor().setRecentlyPaused(false);
					}

					train.addHistory(station, context.getGameLogic().getPlayerManager().getTurnNumber());

					//Uncomment to test whether or not the train is correctly adding stations to its history.
					/*                System.out.println("Added to history: passed " + station.getName() + " on turn "
                        + context.getGameLogic().getPlayerManager().getTurnNumber());*/

					int stationIndex = train.getRoute().indexOf(station); //find this station in route
					int nextIndex = stationIndex + 1;


					//This checks whether or not the train is at its final destination by checking whether the index is still less than the list size
					if (nextIndex < train.getRoute().size()) {
						Station nextStation = train.getRoute().get(nextIndex);

						//Checks whether the next connection is blocked, if so the train is paused, if not the train is unpaused.
						if (train.getActor().isPaused()) {
							train.getActor().setPaused(false);
							train.getActor().setRecentlyPaused(true);
						}

						// check that the connection hasnt been destroyed, if so then stop the train
						if (!context.getGameLogic().getMap().doesConnectionExist(station.getName(), nextStation.getName())) {
							train.setFinalDestination(station);
							train.getActor().clearActions();
							train.getActor().addAction(afterAction());
							train.getActor().setPaused(false);
						}
					} else {
						//If the train is at its final destination then the train is set to unpaused so that it does not cause issues elsewhere in the program.
						train.getActor().setPaused(false);
					}
				}
			}
		};
	}

	/**This method produces an action for when the train has reached it's final destination.
	 * @return A runnable action that displays a message and notifies the goal manager.
	 */
	private RunnableAction afterAction() {
		return new RunnableAction() {
			public void run() {
				//This informs the user that their train has completed a goal, if it has
				ArrayList<String> completedGoals = context.getGameLogic().getGoalManager().trainArrived(train, train.getPlayer());
				for (String message : completedGoals) {
					context.getTopBarController().displayFlashMessage(message, Color.WHITE, 2);
				}

				// set actor position for if train has moved beyond track (occurs if track destroyed)
				IPositionable position = train.getFinalDestination() .getPosition();
				train.getActor().setPosition(position.getX() - TrainActor.width/2, position.getY() - TrainActor.height/2);

				//Sets the train's position to be equal to its final destination's position so that it is appropriately hidden and linked to the station
				train.setPosition(train.getFinalDestination().getPosition());
				train.getActor().setVisible(false);
				train.setFinalDestination(null);
			}
		};
	}

	/**This method uses the current's train's routes to create a set of move actions for the train.*/
	public void addMoveActions() {
		SequenceAction actions = Actions.sequence();
		IPositionable current = train.getPosition();

		//If the train is moving then the position is (-1,-1), this led to very high durations for small distances in edited routes
		//Instead this is checked and if the train is found to be moving then instead the location of the trainActor is used.
		//It is not possible to always use the train actor as if a train is not moving then trainActor is null.
		if (train.getPosition().getX() == -1) {
			current = new Position((int) train.getActor().getBounds().getX(), (int) train.getActor().getBounds().getY());
		}
		actions.addAction(beforeAction());

		// movement no longer relies on the trains position for setting the angles, this allows loading a train to be partly along a route to work 
		for (int i = 0; i < train.getRoute().size(); i++){
			Station station = train.getRoute().get(i);
			IPositionable next = station.getPosition();
	
			IPositionable prev; // the previous station of the route
			if (i > 0){
				prev = train.getRoute().get(i-1).getPosition();
			} else {
				prev = train.getLastStation().getPosition();
			}
			
			// rotate the train to face forwards
			float angle = MathUtils.radiansToDegrees*Position.getAngle(prev, next);
			actions.addAction(Actions.rotateTo(angle));

			//This calculates how long it will take for the train to travel to the next station on the route
			float duration = Position.getDistance(current, next) / train.getSpeed();

			//This adds the action to the actor which makes it move from point A to point B in a certain amount of time, calculated using duration and the two station positions.
			actions.addAction(moveTo(next.getX() - TrainActor.width / 2, next.getY() - TrainActor.height / 2, duration));
			actions.addAction(perStationAction(station));


			current = next;
		}

		actions.addAction(afterAction());

		//Remove all previous actions from the actor so that it does not travel along its original path before the new path
		train.getActor().clearActions();

		//Adds the new actions to the actor
		train.getActor().addAction(actions);
	}

	/**This method checks if the train has collided with an obstacle when it reaches a station. If it has, the train is destroyed.
	 * @return boolean that says whether the train has been deleted
	 */
	private boolean obstacleCollision(Station station) {
		if (station.hasObstacle()){
			train.getActor().remove();
			train.getPlayer().removeResource(train);
			context.getTopBarController().displayFlashMessage(train.getPlayer().getName() + "'s train was hit by a "  + station.getObstacle().getType().toString().toLowerCase() + " in " + station.getName(), Color.RED, 4);
			return true;
		}
		return false;
	}

	//We removed collisions from here as it was more appropriate for how we wanted collisions to work to test it every time the trains were rendered
}