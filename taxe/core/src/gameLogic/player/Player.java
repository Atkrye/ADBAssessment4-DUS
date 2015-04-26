package gameLogic.player;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

/**This class holds variables and methods for a single player.*/
public class Player {
	
	/**The game's player manager. This allows the class to access other players.*/
    private PlayerManager pm;
    
    /**The resources that this player owns.*/
    private List<Resource> resources;
    
    /**The goals that this player has available to them.*/
    private List<Goal> goals;
    
    /**The player's current score.*/
    private double score;
    
    /**This player's number, e.g. Player1, Player2.*/
    private int number;
    
    /** This indicates whether the player is skipping their turn.*/
    private boolean skip;
    
    /** The name of the player, input on game creation*/
    private String name;

    /**Instantiation method.
     * @param pm The PlayerManager of the Game that handles this player.
     * @param playerNumber The player number, e.g. Player 1, Player 2.
     * @param name The players name
     */
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

    /** If the player has a free goal slot, add a goal 
     * @param goal Goal to add
     */
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

    /** Complete one of the players goals, remove it from GUI
     * @param goal Goal that has been completed
     */
    public void completeGoal(Goal goal) {
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

    /** Add the score to the players current score
     * @param score Score to add to current score
     */
    public void updateScore(int score) {
        this.score = this.score + score;
    }

    /** Remove a goal from the players inventory*/
    public void removeGoal(Goal goal) {
        
        if (goals.contains(goal))
            goals.remove(goal);
        changed();
    }

    /** Check whether the player has the resource
     * @param resource Resource that is being checked
     * @return True if player has resource
     */
    public boolean hasResource(Resource resource) {
       
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

	public void setScore(int score) {
		this.score = score;
	}
}
