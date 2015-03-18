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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TrainActor extends Image {
    public static int width = 36;
    public static int height = 36;
    public Train train;

    private Polygon bounds;
    private Rectangle rect;
    public boolean facingLeft;
    protected Context context;
    private boolean paused;
    private boolean recentlyPaused;
    
	public TrainActor(Train train, Context context) {
        //The constructor initialises all the variables and gathers the relevant image for the actor based on the train it is acting for.
        super(new Texture(Gdx.files.internal(train.getImage())));
       
        this.context = context;

        IPositionable position = train.getPosition();

        train.setActor(this);
        this.train = train;
        setSize(width, height);
        
        bounds = new Polygon(new float[] {0,0,0, height, width, height, width, 0});
        bounds.setOrigin(width/2, height/2);
        
        setPosition(position.getX() - width / 2, position.getY() - height / 2);
        paused = false;
        recentlyPaused = false;
        setOrigin(getWidth()/2, getHeight()/2);
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
            		//Make a new trong game and add it to the stack. Determine which player is player 1 and ensure that that train is passed as the first train
            		TrongScreen trongGame;
            		if(this.train.getPlayer().getPlayerNumber() < collidedTrain.getPlayer().getPlayerNumber())
            		{
            			trongGame = GameScreen.makeTrongGame(this.train, collidedTrain);
            		}
            		else
            		{
            			trongGame = GameScreen.makeTrongGame(this.train, collidedTrain);
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
            		collidedTrain.getActor().remove();
            		collidedTrain.getPlayer().removeResource(collidedTrain);
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
        bounds.setPosition(getX(), getY());
    	bounds.setRotation(getRotation());
    }

    public Rectangle getBounds() {
        return rect;
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


                                    //if ((this.rect.overlaps(otherTrain.getActor().getBounds())) && !((this.recentlyPaused) || (otherTrain.getActor().isRecentlyPaused()))) {
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