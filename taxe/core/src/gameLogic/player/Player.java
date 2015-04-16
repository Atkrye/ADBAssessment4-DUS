package gameLogic.player;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player{
    private PlayerManager pm;
    private List<Resource> resources;
    private List<Goal> goals;
    private int number;
    private double score;
    //This indicates whether the player is skipping their turn.
    private boolean skip;
    private String name;

    public Player(PlayerManager pm, int playerNumber) {
        goals = new ArrayList<Goal>();
        resources = new ArrayList<Resource>();
        this.pm = pm;
        number = playerNumber;
        score = 0;
    }

    public Player(PlayerManager pm, int playerNumber, String name) {
        goals = new ArrayList<Goal>();
        resources = new ArrayList<Resource>();
        this.pm = pm;
        number = playerNumber;
        score = 0;
		this.name = name;
	}
    
    public String getName()
    {
    	return name;
    }

	public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean getSkip() {
        return skip;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<Train> getTrains() {
        //Returns all of the player's trains
        ArrayList<Train> trains = new ArrayList<Train>();
        for (Resource resource : resources) {
            if (resource instanceof Train) {
                Train train = (Train) resource;
                trains.add(train);
            }
        }
        return trains;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
        changed();
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);

        //Disposes the resource to avoid memory leaks
        resource.dispose();
        changed();
    }

    public void addGoal(Goal goal) {
        int incompleteGoals = 0;
        //Iterates through every goal and counts each goal that has not already been completed
        for (Goal existingGoal : goals) {
            if (!existingGoal.getComplete()) {
                incompleteGoals++;
            }
        }

        //If the number of incomplete goals is less than the maximum number of goals then the player is given a new goal
        if (incompleteGoals < GoalManager.CONFIG_MAX_PLAYER_GOALS) {
            goals.add(goal);
            changed();
        }

    }

    public void completeGoal(Goal goal) {
        //This sets the goal to complete and hence removes it from being displayed on the GUI
        goal.setComplete();
        changed();
    }

    /**
     * Method is called whenever a property of this player changes, or one of the player's resources changes
     */
    public void changed() {
        pm.playerChanged();
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public PlayerManager getPlayerManager() {
        return pm;
    }

    public int getPlayerNumber() {
        return number;
    }

    public double getScore() {
        return score;
    }

    public void updateScore(int score) {
        this.score = this.score + score;
    }

    public void removeGoal(Goal goal) {
        //Removes a goal from a player's inventory
        if (goals.contains(goal))
            goals.remove(goal);
        changed();
    }

    public boolean hasResource(Resource resource) {
        //Returns whether or not the player has the resource passed to the method

        //This method ignores the resource if it is a train as we did not want to stop the player receiving the same train more than once
        if (!(resource instanceof Train)) {
            for (Resource ownedResource : resources) {
                if (resource.toString().equals(ownedResource.toString())){
                    return true;
                }
            }
        }
        return false;
    }
}
