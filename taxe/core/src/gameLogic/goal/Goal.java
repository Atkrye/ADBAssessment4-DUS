package gameLogic.goal;

import Util.Tuple;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;

/** Class that represents a goal for the player to complete*/
public class Goal {
	/** The origin station of the goal*/
    private Station origin;
    
    /** The destination station of the goal*/
    private Station destination;
    
    /** The turn the goal was given to the player */
    private int turnIssued;
    
    /** Boolean to say whether goal has been completed */
    private boolean complete = false;
    
    /** Boolean to say whether the goal has a bonus (go via..) */
    private boolean goingThrough = false;
    
    /** The station the train must go through for bonus, if goingThrough is true */
    private Station intermediary;
    
    /** Boolean to say whether the goal has bonus of completing it within a turn limit */
    private boolean inTurns = false;
    
    /** Number of turns the goal must be completed, if inTurns true */
    private int turnsTime;
    
    /** Boolean to say whether the goal has to be completed with a specfic type of train */
    private boolean withTrain;
    
    /** The train the player must finish goal with for bonus, if withTrain is true*/
    private Train train = null;
    
    /** The score for completing the goal (without any bonuses) */
    private int score;
    
    /** The score for completing the bonus goal for the goal*/
    private int bonus;
    
    public int getScore() {
        return this.score;
    }

    public int getBonus() {
        return this.bonus;
    }
    
    public int getTurnsTime()
    {
    	return turnsTime;
    }
    
    public Train getTrain()
    {
    	if(train == null)
    	{
    		return null;
    	}
    	else
    	{
    		return train;
    	}
    }

    /** Instantation only one bonus can active at once.
     * @param origin Start station of the goal
     * @param destination Destination station of the goal
     * @param intermediary The station to go via for bonus (null if not via bonus)
     * @param turn The turn number the goal was issued to the player
     * @param turnsTime The number of turns the goal must be completed in (null if not turn bonus)
     * @param score The base score for completing the goal
     * @param bonus The score for completing the bonus goal
     * @param train The train that must complete the goal for the bonus (null if not train bonus)
     */
    public Goal(Station origin, Station destination, Station intermediary, int turn, int turnsTime, int score, int bonus, Train train) {
        //If a train is passed to the constructor then the appropriate flag is set as well as the train variable.
        //This indicates that the bonus is to complete the goal with a specific train
        if (train != null) {
            this.train = train;
            withTrain = true;
        }

        this.origin = origin;
        this.destination = destination;
        this.score = score;

        //set the amount of points to give if a bonus goal is completed
        this.bonus = bonus;

        //If there is no 'via' bonus then intermediary equals the origin, by checking this we set the appropriate flags and variables
        if (intermediary != destination && intermediary != origin) {
            goingThrough = true;
            this.intermediary = intermediary;
        } else {
            this.intermediary = intermediary;
        }

        //This variable is set so that we can check whether or not the train visited the relevant nodes for the goal before or after the goal had been issued
        this.turnIssued = turn;

        //If turnsTime is greater than 0 then the bonus is to complete a goal in a certain number of turns, sets the relevant variables for this
        this.turnsTime = turnsTime;
        if (turnsTime != 0) {
            this.inTurns = true;
        }
        System.out.println(this.toString() + " for " + this.score + "/" + this.bonus + " points");

    }

    /** Checks whether or not the goal has been completed
     * @param train The train that is being tested against
     * @return Whether the train has completed this goal
     */
    public boolean isComplete(Train train) {
        //This is interesting because this method in itself doesn't check whether a goal is completed
        //The fact that this method is only called when a train reaches its destination allows this to work
        boolean passedOrigin = false;
        for (Tuple<Station, Integer> history : train.getHistory()) {
            //Checks whether or not the station is the origin and if it was visited after the goal was issued
            if (history.getFirst().getName().equals(origin.getName()) && history.getSecond() >=
                    turnIssued) {
                passedOrigin = true;
            }
        }
        //This checks whether or not the final destination is the destination of the goal, if it has then returns true
        if (train.getFinalDestination() == destination && passedOrigin) {
            return true;
        } else {
            return false;
        }
    }

    /** Checks whether or not a bonus has been completed by checking which bonus the goal has, 
     *  then passing it to the relevant checking method for that bonus
     * @param train The train that has completed the goal
     * @return Whether the train has completed the bonus for this goal
     */
    public boolean isBonusCompleted(Train train) {
        if (goingThrough) {
            return wentThroughStation(train);
        }
        if (inTurns) {
            return completedWithinMaxTurns(train);
        }
        if (withTrain) {
            return completedWithTrain(train);
        }
        return false;
    }

    /** Checks if a train has passed through the intermediary station 
     * @param train The train that completed goal
     * @return Whether the train passed through the intermediary
     */
    public boolean wentThroughStation(Train train) {
        boolean passedThrough = false;
        if (this.isComplete(train))
            //One issue with this could be that the intermediary station could have been visited before the goal was issued
            if (goingThrough && train.routeContains(intermediary)) passedThrough = true;
        return passedThrough;
    }

    /** Checks if a train has completed the goal within the bonus number of turns
     * @param train The train that has completed the goal
     * @return Whether the train completed the goal within the turn limit
     */
    public boolean completedWithinMaxTurns(Train train) {
        boolean completed = false;
        if (this.isComplete(train) && this.inTurns)
            //Checks whether the turnsTime and turnIssued is greater than the currentTurn.
            //This indicates whether it was completed in time for the bonus
            if ((turnsTime + this.turnIssued) >= gameLogic.Game.getInstance().getPlayerManager().getTurnNumber()) {
                completed = true;
            }
        return completed;

    }

    /** Checks if a train that has completed the goal is the bonus train
     * @param train The train that ahs completed the goal
     * @return Whether the train is a the type of the bonus train
     */
    public boolean completedWithTrain(Train train) {
        if (this.train.getName().equals(train.getName())) {
            return true;
        }
        return false;
    }

    public String getBaseGoalString() {
        //This generates the string for the base goal in the form "A to B : <points> points"
        return origin.getName() + " to " + destination.getName() + ": " + this.score + " points";
    }

    public String getBonusString() {
        //This builds the string to return by concatenating the relevant information to the string. The string is then returned.
        String output = "Bonus - ";
        if (withTrain) {
            output = output.concat("Using " + train.getName());
        }
        if (inTurns) {
            output = output.concat("Within " + turnsTime);
            if (turnsTime > 1) {
                output = output.concat(" turns (" + String.valueOf(turnIssued + turnsTime) + ")");
            } else {
                output = output.concat(" turn (" + String.valueOf(turnIssued + turnsTime) + ")");
            }
        }
        if (goingThrough) {
            output = output.concat("Via " + intermediary.getName());
        }
        output = output.concat(": " + this.bonus + " points");
        return output;
    }

    public String toString() { // based on the type of goal
        //This routine is only used for printing to the console for debugging
        //Not used in the actual game
        String trainString = "train";
        ArrayList<String> vowels = new ArrayList<String>();
        vowels.add("A");
        vowels.add("E");
        vowels.add("I");
        vowels.add("O");
        vowels.add("U");

        if (train != null) {
            trainString = train.getName();
        }
        if (withTrain) {
            if (vowels.contains(trainString.substring(0, 1))) {
                return "Send an " + trainString + " from " + origin.getName() + " to " + destination.getName();
            } else {
                return "Send a " + trainString + " from " + origin.getName() + " to " + destination.getName();
            }
        }
        if (inTurns) {
            return "Send a train from " + origin.getName() + " to " + destination.getName() + " in " + this.turnsTime + " turns";
        }
        if (goingThrough) {
            return "Send a train from " + origin.getName() + " to " + destination.getName() + " through " + intermediary.getName();
        }
        return "Send a train from " + origin.getName() + " to " + destination.getName();
    }

    public void setComplete() {
        complete = true;
    }

    public boolean getComplete() {
        return complete;
    }

    public Station getOrigin() {
        return this.origin;
    }

    public Station getDestination() {
        return this.destination;
    }

    public Station getIntermediary() {
        return this.intermediary;
    }

	public int getTurn() {
		return this.turnIssued;
	}
}