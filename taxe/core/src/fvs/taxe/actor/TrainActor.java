package fvs.taxe.actor;

import fvs.taxe.GameScreen;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.resource.Train;
import gameLogic.trong.TrongScreen;
import adb.taxe.record.CollisionEvent;
import adb.taxe.record.RecordingScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TrainActor extends Image {
	// train actors now rotate and have a top down view, rather than switching betwene left and right. Simpler, looks better.

	/**The width of a TrainActor in pixels.*/
	public static int width = 40;

	/**The height of a TrainActor in pixels.*/
	public static int height = 20;

	/**The train the TrainActor corresponds to.*/
	public Train train;

	/**The polygon (rectangle) that corresponds to the bounds of the TrainActor.*/
	private Polygon bounds; 
	// polygon used as it allows easy rotation, unlike rectangle

	/** Context relating to this trainActor */
	protected Context context;

	// pausing function isnt in use anymore, but remains as it could be useful ina  future development
	/** Boolean saying whether the actor is paused */
	private boolean paused;

	/** Boolean saying whether the actor has been recently paused "recently" is determined elsewhere */
	private boolean recentlyPaused;

	/** Class for displaying a train on screen as an actor 
	 * @param train The train that corresponds to the actor
	 * @param context The corresponding game context
	 */
	public TrainActor(Train train, Context context) {
		//The constructor initialises all the variables and gathers the relevant image for the actor based on the train it is acting for.
		super(new Texture(Gdx.files.internal(train.getImage())));
		this.context = context;
		train.setActor(this);
		this.train = train;
		setSize(width, height);
		// set the bounds of the train to the size of the train
		bounds = new Polygon(new float[] {0,0,0, height, width, height, width, 0});
		bounds.setOrigin(width/2, height/2); // for rotations


		IPositionable position = train.getPosition();
		setPosition(position.getX() - width / 2, position.getY() - height / 2); // position is center of train
		setOrigin(getWidth()/2, getHeight()/2); // for rotations
		paused = false;
		recentlyPaused = false;

	}

	@Override
	public void act(float delta) {
		if ((Game.getInstance().getState() == GameState.ANIMATING) && (!this.paused)){
			//This function moves the train actors along their routes.
			//It renders everything every 1/delta seconds
			super.act(delta);
			updateBounds();

			final Train collidedTrain = collided();
			if (collidedTrain != null) {
				if(Game.trongEnabled)
				{
					//Firstly determine whether we're in a recording or an actual game
					if(GameScreen.instance.getClass().equals(RecordingScreen.class))
					{
						System.out.println("Recording collision!");
						//It is a recording so we load the collision and destroy the losing train.
						CollisionEvent ce = ((RecordingScreen)GameScreen.instance).eventPlayer.getCollisionEvent(this.train, collidedTrain);
						//If ce is null no collision event was found so the game continues ignoring the collision
						if(ce != null)
						{
							if(this.train.getID() == ce.getDestroyedID())
							{
								train.getPlayer().removeResource(train);
								this.remove();
							}
							else if(collidedTrain.getID() == ce.getDestroyedID())
							{
								collidedTrain.getActor().remove();
								collidedTrain.getPlayer().removeResource(collidedTrain);
							}
						}
					}
					else
					{
						//Make a new trong game and add it to the stack. Determine which player is player 1 and ensure that that train is passed as the first train
						TrongScreen trongGame;
						if(this.train.getPlayer().getPlayerNumber() < collidedTrain.getPlayer().getPlayerNumber())
						{
							trongGame = GameScreen.makeTrongGame(this.train, collidedTrain);
						}
						else
						{
							trongGame = GameScreen.makeTrongGame(collidedTrain, this.train);
						}
						if(GameScreen.instance.trongScreen != null)
						{
							GameScreen.instance.trongScreen.setNextScreen(trongGame);
						}
						else
						{
							GameScreen.instance.setScreen(trongGame);
						}
						trongGame.setNextScreen(GameScreen.instance);
					}
				}
				else
				{
					//If there is a collision then the user is informed, the two trains destroyed
					context.getTopBarController().displayFlashMessage("Two trains collided.  They were both destroyed.", Color.RED, 2);
					collidedTrain.getActor().remove();
					collidedTrain.getPlayer().removeResource(collidedTrain);
					train.getPlayer().removeResource(train);
					this.remove();
				}
			}

		} 
	}

	/** Update the bounds of the actor to match the actors position and rotation */
	private void updateBounds() {
		bounds.setPosition(getX(), getY());
		bounds.setRotation(getRotation());
	}

	public Polygon getBounds() {
		return bounds;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public boolean getPaused() {
		return this.paused;
	}

	public boolean isRecentlyPaused() {
		return recentlyPaused;
	}

	public void setRecentlyPaused(boolean recentlyPaused) {
		this.recentlyPaused = recentlyPaused;
	}

	/** Return the train that the current train has collided with (null if no collision)
	 * @return Opposing players train that the current train has collided with, null if no train collision
	 */
	public Train collided() {
		//The aim of this function is to check whether the train represented by the actor has collided with any other trains on the board
		Station last = train.getLastStation();
		Station next = train.getNextStation();
		if (train.getPosition().getX() == -1 && !paused) {
			//if this train is moving;
			for (Player player : Game.getInstance().getPlayerManager().getAllPlayers()) {
				if(!player.equals(this.train.getPlayer()))
				{
					for (Train otherTrain : player.getTrains()) {
						//This checks every train that is currently present within the game
						if (!otherTrain.equals(train)) {
							//don't check if collided with self
							if (otherTrain.getPosition() != null) {
								//Checks if the other train has been placed on the map
								if (otherTrain.getPosition().getX() == -1 && !otherTrain.getActor().getPaused()) {
									//if other train moving
									//This is because the position of the train when it is in motion (i.e travelling along its route) is (-1,-1) as that is how FVS decided to implement it
									//It is necessary to check whether this is true as if the train is not in motion then it does not have an actor, hence otherTrain.getActor() would cause a null point exception.

									if ((otherTrain.getNextStation() == next && otherTrain.getLastStation() == last)
											|| (otherTrain.getNextStation() == last && otherTrain.getLastStation() == next)) {
										//check if trains on same connection

										if ((Intersector.overlapConvexPolygons(bounds, otherTrain.getActor().bounds)) && !((this.recentlyPaused) || (otherTrain.getActor().isRecentlyPaused()))){
											//Checks whether the two trains are recently paused, if either of them are then no collision should occur
											//This prevents the issue of two paused trains crashing when they shouldn't
											//There is still the potential issue of two blocked trains colliding when they shouldn't, as it is impossible to know which connection a blocked train will occupy. i.e when one train is rerouted but not the other
											return otherTrain;
											//This is slightly limiting as it only allows two trains to collide with each other, whereas in theory more than 2 could collide, this is however very unlikely and due to complications
											//not necessary to factor in to our implementation at this stage. If you need to add more trains then you would have to build up a list of collided trains and then return it.
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
}