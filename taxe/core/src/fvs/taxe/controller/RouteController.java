package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Util.Tuple;
import fvs.taxe.clickListener.StationClickListener;
import fvs.taxe.TaxeGame;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;

public class RouteController {
    private Context context;
    private Group routingButtons = new Group();
    private List<IPositionable> positions;
    private boolean isRouting = false;
    private Train train;
    private boolean canEndRouting = true;
    private boolean editingRoute = false;
    private double distance = 0;
	private ArrayList<Connection> connections;

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
        }else{
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

    private void addStationToRoute(Station station) {
        // the latest position chosen in the positions so far
        if (positions.size() == 0) {
         if (editingRoute) {
             //Checks whether the train's actor is paused due to a bug with blocked trains
             if (train.getActor().isPaused()){
                 Station lastStation = train.getLastStation();
                 //Checks if a connection exists between the station the train is paused at and the clicked station
                 if (context.getGameLogic().getMap().doesConnectionExist(lastStation.getName(),station.getName())){
                     positions.add(station.getLocation());

                     //Sets the relevant boolean checking if the last node on the route is a junction or not
                     canEndRouting = !(station instanceof CollisionStation);
                 }else {
                     context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
                 }
             }else {
                 Station lastStation = train.getLastStation();
                 Station nextStation = train.getNextStation();
                 if (station.getName() == lastStation.getName() || nextStation.getName() == station.getName()) {
                     //If the connection exists then the station passed to the method is added to the route
                     positions.add(station.getLocation());

                     //Sets the relevant boolean checking if the last node on the route is a junction or not
                     canEndRouting = !(station instanceof CollisionStation);
                 } else {
                     context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
                 }
             }
         }else{
             positions.add(station.getLocation());
         }
        }
        else {
            //Finds the last station in the current route
            IPositionable lastPosition = positions.get(positions.size() - 1);
            Station lastStation = context.getGameLogic().getMap().getStationFromPosition(lastPosition);

            //Check whether a connection exists using the function in Map
            boolean hasConnection = context.getGameLogic().getMap().doesConnectionExist(station.getName(), lastStation.getName());

            if (!hasConnection) {
                //If the connection doesn't exist then this informs the user
                context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);

            } else {
                distance+= context.getGameLogic().getMap().getDistance(lastStation, station);
                DecimalFormat integer = new DecimalFormat("0");

                context.getTopBarController().displayMessage("Total Distance: " + integer.format(distance) + ". Will take " + integer.format(Math.ceil(distance / train.getSpeed() / 2)) + " turns.", Color.BLACK);
                //If the connection exists then the station passed to the method is added to the route
                positions.add(station.getLocation());
                
                connections.add(context.getGameLogic().getMap().getConnection(station, lastStation));
                
                //Sets the relevant boolean checking if the last node on the route is a junction or not
                canEndRouting = !(station instanceof CollisionStation);
            }
        }
    }

    private void addRoutingButtons() {
        TextButton doneRouting = new TextButton("Route Complete", context.getSkin());
        TextButton cancel = new TextButton("Cancel", context.getSkin());

        doneRouting.setPosition(TaxeGame.WIDTH - 250, TaxeGame.HEIGHT - 33);
        cancel.setPosition(TaxeGame.WIDTH - 100, TaxeGame.HEIGHT - 33);

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

        //Adds the buttons to the screen
        routingButtons.addActor(doneRouting);
        routingButtons.addActor(cancel);

        context.getStage().addActor(routingButtons);
    }

    private void confirmed() {
        //Passes the positions to the backend to create a route
        train.setRoute(context.getGameLogic().getMap().createRoute(positions));

        //A move controller is created to allow the train to move along its route.
        //Although move is never used later on in the program, it must be instantiated or else the trains will not move.
        //Hence you should not remove this even though it appears useless, I tried and trains do not move at all.
        TrainMoveController move = new TrainMoveController(context, train);
    }

    private void endRouting() {
        //This routine sets the gamescreen back to how it should be for normal operation
        context.getGameLogic().setState(GameState.NORMAL);
        //All buttons are removed and flags set to the relevant values.
        routingButtons.remove();
        isRouting = false;
        editingRoute = false;
        distance = 0;
        context.getTopBarController().clearMessage();
        //This sets all trains currently travelling along their route to be set to visible.
        TrainController trainController = new TrainController(context);
        trainController.setTrainsVisible(train, true);

        drawRoute(Color.GRAY);
        connections.clear();
        
        //Again using the principle that (-1,-1) is a moving train, this sets the train being routed to invisible if not already on a route, but makes it visible if it already had a route previously
        //This was necessary to add as without it, when editing a route and then cancelling, the train would become invisible for the duration of its original journey
        if (train.getPosition().getX() != -1) {
            train.getActor().setVisible(false);
        }
    }

    public void drawRoute(Color color) {
        for (Connection connection : connections) {
        	connection.getActor().setConnectionColor(color);
        }
    }
    /*
	public void drawRoute(Color color) {
		TaxeGame game = context.getTaxeGame();

		IPositionable previousPosition = null;
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		game.shapeRenderer.setColor(color);

		// get a list of all stations that the train has passed so far
		ArrayList<Station> history = Tuple.getFirstsFromList(train.getHistory());
		// iterate through all positions in the route
		for (int i = 0; i < positions.size(); i++) {
			if (i > 0) {
				boolean b = false;
				// check to see if the connection between to 2 positions is one that has been
				// visited, and if it has been visited then change the colour of the connection line
				for (int j = i; j < history.size(); j++) {
					if (history.get(j).getLocation().equals(positions.get(i)) &&
							history.get(j - 1).getLocation().equals(previousPosition)) {
						game.shapeRenderer.setColor(Color.RED);
						b = true;
						break;
					}
				}
				// otherwise, use the default connection line colour
				if (!b) {
					game.shapeRenderer.setColor(color);
				}
			}else if (editingRoute){
                Rectangle bounds = train.getActor().getBounds();
                previousPosition = new Position((int) (bounds.getX()+(bounds.getWidth()/2)),(int) (train.getActor().getBounds().getY()+(bounds.getHeight()/2)));
            }
			if (previousPosition != null) {
				game.shapeRenderer.rectLine(previousPosition.getX(), previousPosition.getY(),
						positions.get(i).getX(), positions.get(i).getY(),
						StationController.CONNECTION_LINE_WIDTH);
			}

			//Need to keep track of the previous node as we are not using an index based for-loop and the previous node is required to find one of the end points of the line
			previousPosition = positions.get(i);
		}

		game.shapeRenderer.end();
	}*/

    public void viewRoute(Train train) {
        //This method is used to draw the trains current route so that the user can see where their trains are going


        routingButtons.clear();

        train.getRoute();

        //This works by simulating the creation of a new route, but without the ability to save the route
        //This will instead draw the route passed to it, which is the one located in train.getRoute()
        positions = new ArrayList<IPositionable>();
        Station prevStation=null;
        for (Station station : train.getRoute()) {
            positions.add(station.getLocation());
            if (prevStation!=null) {
                distance += context.getGameLogic().getMap().getDistance(station,prevStation);
                DecimalFormat integer = new DecimalFormat("0");
                context.getTopBarController().displayMessage("Total Distance: " + integer.format(distance) + ". Will take " + integer.format(Math.ceil(distance / train.getSpeed() / 2)) + " turns.", Color.BLACK);
            }
            prevStation = station;
        }

        context.getGameLogic().setState(GameState.ROUTING);

        //Adds a button to leave the view route screen
        TextButton back = new TextButton("Return", context.getSkin());

        back.setPosition(TaxeGame.WIDTH - 100, TaxeGame.HEIGHT - 33);

        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getGameLogic().setState(GameState.NORMAL);
                context.getTopBarController().clearMessage();
                routingButtons.remove();
                distance = 0;

            }
        });

        routingButtons.addActor(back);

        context.getStage().addActor(routingButtons);
    }

}
