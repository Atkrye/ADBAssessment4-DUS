package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import fvs.taxe.GameScreen;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.player.Player;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Train;
import gameLogic.trong.TrongScreen;

public class TrainActor extends Image {
    public static int width = 36;
    public static int height = 36;
    public Train train;

    private Rectangle bounds;
    public boolean facingLeft;
    private float previousX;
    private Drawable leftDrawable;
    private Drawable rightDrawable;
    private Context context;
    private boolean paused;
    private boolean recentlyPaused;

    public TrainActor(Train train, Context context) {
        //The constructor initialises all the variables and gathers the relevant image for the actor based on the train it is acting for.
        super(new Texture(Gdx.files.internal(train.getLeftImage())));
        leftDrawable = getDrawable();
        rightDrawable = new Image(new Texture(Gdx.files.internal(train.getRightImage()))).getDrawable();
        this.context = context;

        IPositionable position = train.getPosition();

        train.setActor(this);
        this.train = train;
        setSize(width, height);
        bounds = new Rectangle();
        setPosition(position.getX() - width / 2, position.getY() - height / 2);
        previousX = getX();
        facingLeft = true;
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
            updateFacingDirection();

            final Train collision = collided();
            if (collision != null) {
            	if(Game.trongEnabled)
            	{
            		//Make a new trong game and add it to the stack. Determine which player is player 1 and ensure that that train is passed as the first train
            		TrongScreen trongGame;
            		if(this.train.getPlayer().getPlayerNumber() < collision.getPlayer().getPlayerNumber())
            		{
            			trongGame = GameScreen.makeTrongGame(this.train, collision);
            		}
            		else
            		{
            			trongGame = GameScreen.makeTrongGame(this.train, collision);
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
            	else
            	{
            		//If there is a collision then the user is informed, the two trains destroyed and the connection that they collided on is blocked for 5 turns.
            		context.getTopBarController().displayFlashMessage("Two trains collided.  They were both destroyed.", Color.RED, 2);
            		//Game.getInstance().getMap().blockConnection(train.getLastStation(), train.getNextStation(), 5);
            		collision.getActor().remove();
            		collision.getPlayer().removeResource(collision);
            		train.getPlayer().removeResource(train);
            		this.remove();
            	}
            }

        } /*else if (this.paused) {
            //Everything inside this block ensures that the train does not move if the paused variable is set to true.
            //This ensures that trains do not move through blocked connections when they are not supposed to.

            //find station train most recently passed
            Station station = train.getHistory().get(train.getHistory().size() - 1).getFirst();
//            Station station = Game.getInstance().getMap().getStationByName(stationName);

            // find index of this within route
            int index = train.getRoute().indexOf(station);

            // find next station
            Station nextStation = train.getRoute().get(index + 1);

            // check if connection is blocked, if not, unpause
            if (!Game.getInstance().getMap().isConnectionBlocked(station, nextStation)) {
                this.paused = false;
                this.recentlyPaused = true;
            }*/
        //}
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    public void updateFacingDirection() {
        float currentX = getX();
        //This updates the direction that the train is facing and the image representing the train based on which direction it is travelling
        if (facingLeft && previousX < currentX) {
            setDrawable(rightDrawable);
            facingLeft = false;
        } else if (!facingLeft && previousX > currentX) {
            setDrawable(leftDrawable);
            facingLeft = true;
        }

        previousX = getX();
    }

    public Rectangle getBounds() {
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


                                    if ((this.bounds.overlaps(otherTrain.getActor().getBounds())) && !((this.recentlyPaused) || (otherTrain.getActor().isRecentlyPaused()))) {
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