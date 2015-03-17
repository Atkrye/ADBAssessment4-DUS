package gameLogic.resource;

import Util.Tuple;
import gameLogic.player.Player;
import gameLogic.map.JSONImporter;

import java.util.ArrayList;
import java.util.Random;

public class ResourceManager {
    public final int CONFIG_MAX_RESOURCES = 7;
    private Random random = new Random();
    private ArrayList<Tuple<String, Integer>> trains;

    public ResourceManager() {
        //This calls the JSON importer which sets the train
        JSONImporter jsonImporter = new JSONImporter(this);
    }

    public ArrayList<Tuple<String, Integer>> getTrains() {
        return trains;
    }

    public void setTrains(ArrayList<Tuple<String, Integer>> trains) {
        this.trains = trains;

    }

    private Resource getRandomResource() {
        //Returns a random resource
        int idx = random.nextInt(11);
        if (idx == 1) {
            //1 in 10 chance to return a skip
            return new Skip();
        } else if (idx<6){
        	return new KamikazeTrain();	
        } else if (idx<11){
        	return new PioneerTrain();
        } else {
            //Otherwise randomly selects a train to give the player.
            //We decided not to use the value of idx to choose the train as this allows us to change the number of trains in the system independently of this routine
            //i.e we could have 30 trains, but still retain a 1 in 10 chance to get an skip
            
        	return getRandomTrain();
        	
        }
    }

    public Train getRandomTrain() {
        //Uses a random number generator to pick a random train and return the complete train class for that train.
        int index = random.nextInt(trains.size());
        Tuple<String, Integer> train = trains.get(index);
        return new Train(train.getFirst(), train.getFirst().replaceAll(" ", "") + ".png", train.getSecond());
    }


    public void addRandomResourceToPlayer(Player player) {
        //This adds a random resource to player

        //Need to check whether the player is skipping their turn as they should not receive a resource if they are
        if (!player.getSkip()) {
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
    }

    private void addResourceToPlayer(Player player, Resource resource) {
        if (player.getResources().size() < CONFIG_MAX_RESOURCES) {
            //If the player has less than the max number of resources then the resource is given to the player.
            resource.setPlayer(player);
            player.addResource(resource);
        }
    }
}