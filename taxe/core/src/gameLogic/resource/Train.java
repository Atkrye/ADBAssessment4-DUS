package gameLogic.resource;

import fvs.taxe.actor.TrainActor;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.List;

import Util.Tuple;

/** The class that represents a train- defined by its name and speed */
public class Train extends Resource {
	/**Id field used to ensure every train has a unique id, for tracking in collisions in the replay system*/
	public static int idVal = 0;
	
	/**The id of the train*/
	private int id;
	
	/** The string location of the png file that represents this train's top down view */
    private String image;
    
    /** The position of the train - (-1,-1) if moving */
    private IPositionable position;
    
    /** The TrainActor that is associated with the Train object */
    private TrainActor actor;
    
    /** The number of pixels the train will move per turn */
    private int speed;
    
    // Final destination should be set to null after firing the arrival event
    /** If the train is set on a route, the final station in that route, otherwise null*/
    private Station finalDestination;

    // Should NOT contain current position!
    /** If a route has been set for the train, the list of stations that make up that route 
     * (starting from station after start station */
    private List<Station> route;

    /** The history of where the train has travelled- list of Station names 
     * and the turn number they arrived at that station */
    private List<Tuple<Station, Integer>> history;

    /** Constructor for train initialises the names, images, speed, history and route
     * @param name The string that represents this train
     * @param leftImage The file name in assets/trains that corresponds to the topdown image
     * @param speed The number of pixels the train moves per turn
     */
    public Train(String name, String image, int speed) {
        this(getFreshId(), name, image, speed);
    }
    
    /** Constructor for train initialises the names, images, speed, history and route
     * @param id Id for the train, used for playback collisions
     * @param name The string that represents this train
     * @param leftImage The file name in assets/trains that corresponds to the topdown image
     * @param speed The number of pixels the train moves per turn
     */
    public Train(int id, String name, String image, int speed)
    {
    	this.id = id;
    	this.name = name;
        this.image = image;
        this.speed = speed;
        history = new ArrayList<Tuple<Station, Integer>>();
        route = new ArrayList<Station>();
    }
    
    public int getID()
    {
    	return id;
    }

    public String getName() {
        return name;
    }

    /** Get the filepath associated with the image of the top down train
     * @return String representing filepath of top down image, in assets/ directory
     */
    public String getImage() {
        return "trains/" + image;
    }

    /** Get the filepath associated with the image of the cursor for the train
     * @return String representing filepath of cursor image, in assets/ directory
     */
    public String getCursorImage() {
        return "trains/cursor/" + image;
    }

    /** Set the position of the train to be the Ipositionable given
     * Doesn't affect actor position
     * @param position The Ipositionable position the Train will be set to
     */
    public void setPosition(IPositionable position) {
        this.position = position;
        changed();
    }

    /** Whether or not the route it has contains the station 
     * @param station Station to test whether the route contains it
     * @return Whether the station is in the route the train is on
     */
    public boolean routeContains(Station station) {
        if (this.route.contains(station)) return true;
        return false;
    }

    public IPositionable getPosition() {
        return position;
    }

    public void setActor(TrainActor actor) {
        this.actor = actor;
    }

    public TrainActor getActor() {
        return actor;
    }

    /** Set the route (represented at list of stations) of the train to be the given route and set the finalDestination to be last station in route
     * @param route Route that the train will take (as a list of stations)
     */
    public void setRoute(List<Station> route) {
        // Final destination should be set to null after firing the arrival event
        if (route != null && route.size() > 0) finalDestination = route.get(route.size() - 1);

        this.route = route;
    }

    /** Return whether the train is currently moving
     * @return True if the train is moving or False if train is not moving
     */
    public boolean isMoving() {
        return finalDestination != null;
    }

    public List<Station> getRoute() {
        return route;
    }

    public Station getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(Station station) {
        finalDestination = station;
    }

    public int getSpeed() {
        return speed;
    }

    //Station name and turn number
    public List<Tuple<Station, Integer>> getHistory() {
        return history;
    }

    /** Add a new history pairing of station and the turn the station was arrived at
     * @param stationName The name of the station that the train has arrived at
     * @param turn What turn number the train arrived at that given station
     */
    public void addHistory(Station station, int turn) {
        history.add(new Tuple<Station, Integer>(station, turn));
    }

    @Override
    public void dispose() {
        if (actor != null) {
            actor.remove();
        }
    }

    /** Return the station that the train has most recently visited 
     * @return The station that has most recently visited
     */
    public Station getLastStation() {
        return this.history.get(history.size() - 1).getFirst();
    }

    /** Return the next station along the route
     * @return The next station along the trains current route
     */
    public Station getNextStation() {
        Station last = getLastStation();
        for (int i = 0; i < route.size() - 1; i++) {
            Station station = route.get(i);
            if (last.getName().equals(station.getName())) {
                return route.get(i + 1);
            }
        }
        if(route.size() > 0)
        {
        	return route.get(0);
        }
        return null;
    }
    
    /** Get a fresh id for the train */
    public static int getFreshId()
    {
    	int id = idVal;
    	idVal++;
    	return id;
    }
}
