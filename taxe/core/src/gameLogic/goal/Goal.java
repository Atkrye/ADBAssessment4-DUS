package gameLogic.goal;

import Util.Tuple;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;

public class Goal {
    private Station origin;
    private Station destination;
    private int turnIssued;
    private boolean complete = false;
    private boolean goingThrough = false;
    private boolean inTurns = false;
    private int turnsTime;
    private int score;
    private int bonus;
    private boolean withTrain;
    private Station intermediary;
    private Train train = null;

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

    public boolean isComplete(Train train) {
        //This checks whether or not a goal has been completed
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

    public boolean isBonusCompleted(Train train) {
        //This method returns whether or not a bonus has been completed by checking which bonus the goal has, then passing it to the relevant checking method for that bonus
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

    public boolean wentThroughStation(Train train) {
        //checks if a train has passed through the intermediary station if it exists
        boolean passedThrough = false;
        if (this.isComplete(train))
            //One issue with this could be that the intermediary station could have been visited before the goal was issued
            if (goingThrough && train.routeContains(intermediary)) passedThrough = true;
        return passedThrough;
    }


    public boolean completedWithinMaxTurns(Train train) {
        //Checks if a train has completed the goal in a certain number of turns
        boolean completed = false;
        if (this.isComplete(train) && this.inTurns)
            //Checks whether the turnsTime and turnIssued is greater than the currentTurn.
            //This indicates whether it was completed in time for the bonus
            if ((turnsTime + this.turnIssued) >= gameLogic.Game.getInstance().getPlayerManager().getTurnNumber()) {
                completed = true;
            }
        return completed;

    }

    public boolean completedWithTrain(Train train) {
        //Checks whether the train passed to it has the same name as the train required by the goal's bonus
        if (this.train.getName().equals(train.getName())) {
            return true;
        }
        return false;
    }

    public String baseGoalString() {
        //This generates the string for the base goal in the form "A to B : <points> points"
        return origin.getName() + " to " + destination.getName() + ": " + this.score + " points";
    }

    public String bonusString() {
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