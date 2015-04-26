package gameLogic.resource;

import Util.Tuple;
import fvs.taxe.GameScreen;
import gameLogic.player.Player;
import gameLogic.map.JSONImporter;

import java.util.ArrayList;
import java.util.Random;

import adb.taxe.record.RecordingScreen;

/**This class creates and stores the Trains specified from trains.json*/
public class ResourceManager {
	/** The maximum number of resources a Player can own */
    public final int CONFIG_MAX_RESOURCES = 6;
    
    /** Random instance for generating random resources*/
    private Random random = new Random();
    
    /** List of pairs of train names and the trains associated speed*/
    private ArrayList<Tuple<String, Integer>> trains;
    
    /** Global, static instance of the resourceManager*/
    public static ResourceManager global = new ResourceManager();

    /** Instantiation- Parse all of the resources from the appropritae json file*/
    public ResourceManager() {
        //This calls the JSON importer which sets the train
        @SuppressWarnings("unused")
		JSONImporter jsonImporter = new JSONImporter(this);
    }

    public ArrayList<Tuple<String, Integer>> getTrains() {
        return trains;
    }

    public void setTrains(ArrayList<Tuple<String, Integer>> trains) {
        this.trains = trains;

    }

    /** Return one random Resource from the created Trains
	 * @return A randomly selected Train object from the list of created trains, with the speed and image set
	 * according to the properties of the train defined in trains.json
	 */
    private Resource getRandomResource() {
        //Returns a random resource
        int idx = random.nextInt(11);
        if (idx == 1) {
            //1 in 10 chance to return a skip
            return new Skip();
        } else if (idx<3){ 
        	return new KamikazeTrain();	
        } else if (idx<5){
        	return new PioneerTrain();
        } else {
            //Otherwise randomly selects a train to give the player.
            //We decided not to use the value of idx to choose the train as this allows us to change the number of trains in the system independently of this routine
            //i.e we could have 30 trains, but still retain a 1 in 10 chance to get an skip
            
        	return getRandomTrain();
        }
    }

    /** Return a random instance of train (NOT PIONEER/KAMIKAZETRAIN) */
    public Train getRandomTrain() {
        //Uses a random number generator to pick a random train and return the complete train class for that train.
        int index = random.nextInt(trains.size());
        Tuple<String, Integer> train = trains.get(index);
        return new Train(train.getFirst(), train.getFirst().replaceAll(" ", "") + ".png", train.getSecond());
    }
    
    /** Return an instance of hte train given by the name
     * @param trainName The name of the required train
     * @return The train with the name trainName
     */
    public Train getTrainByName(String trainName)
    {
    	for(Tuple<String, Integer> train : trains)
    	{
    		if(train.getFirst().equals(trainName))
    		{
    	        return new Train(train.getFirst(), train.getFirst().replaceAll(" ", "") + ".png", train.getSecond());
    		}
    	}
    	return null;
    }
    
    /** Return an instance of the resource given by the name
     * @param resourceName Name of the required resource
     * @return The resource with the name resourceName
     */
    public Resource getResourceByName(String resourceName)
    {
    	if(resourceName.equals("Skip Turn"))
    	{
    		return new Skip();
    	}
    	else if(resourceName.equals("Pioneer"))
    	{
    		return new PioneerTrain();
    	}
    	else if(resourceName.equals("Kamikaze"))
    	{
    		return new KamikazeTrain();
    	}
    	else
    	{
    		return getTrainByName(resourceName);
    	}
    }

    /** Add one randomly generated Train to the given Player
     * @param player The player that will have a randomly generated resource added to it
     * @param firstTurn boolean to say whether it is the firstTurn of the player
     * */
    public void addRandomResourceToPlayer(Player player, boolean firstTurn) {
        

        //Need to check whether the player is skipping their turn as they should not receive a resource if they are
        if (!player.getSkip()) {
        	if(firstTurn)
        	{
        		//Generates random resource
        		Resource resource = getRandomResource();

        		//If player has a particular resource it will generate a new one until they do not have the generated resource.
        		//This is to prevent a build up of obstacles/skips/engineers
        		//Note: This method does not take into account trains, hence the player can have 7 of the same train in theory
        		while (player.hasResource(resource)) {
        			resource = getRandomResource();
        		}
        		addResourceToPlayer(player, resource);
        	}
			else if(!GameScreen.instance.getClass().equals(RecordingScreen.class))
			{
        		//Generates random resource
        		Resource resource = getRandomResource();

        		//If player has a particular resource it will generate a new one until they do not have the generated resource.
        		//This is to prevent a build up of obstacles/skips/engineers
        		//Note: This method does not take into account trains, hence the player can have 7 of the same train in theory
        		while (player.hasResource(resource)) {
        			resource = getRandomResource();
        		}
        		addResourceToPlayer(player, resource);
        		if(GameScreen.instance.isRecording())
    			{
    				GameScreen.instance.record.recordResource(resource);
    			}
			}
			else
			{
				addResourceToPlayer(player, getResourceByName(((RecordingScreen)GameScreen.instance).eventPlayer.getNextResourceEvent().getName()));
			}
        }
    }

    /** Add the given Resource to the given Player if they havent exceeded limit of resources
     * @param player The player with which to add the resource
     * @param resource The resource that will be added to the player
     */
    private void addResourceToPlayer(Player player, Resource resource) {
        if (player.getResources().size() < CONFIG_MAX_RESOURCES) {
            //If the player has less than the max number of resources then the resource is given to the player.
            resource.setPlayer(player);
            player.addResource(resource);
        }
    }
}