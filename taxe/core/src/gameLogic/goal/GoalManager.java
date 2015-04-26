package gameLogic.goal;

import fvs.taxe.GameScreen;
import gameLogic.Game;
import gameLogic.map.CollisionStation;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.Random;

import adb.taxe.record.GoalEvent;
import adb.taxe.record.RecordingScreen;

/**Class to handle all of the goals in the game*/
public class GoalManager {
	/** Constant to say the number of goals a player can have*/
    public final static int CONFIG_MAX_PLAYER_GOALS = 3;
    
    /** The resourceManager associated with the game*/
    private ResourceManager resourceManager;

    public GoalManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /** Generate a random goal and calculate the points that should be awarded for completion of the goal
     * @param turn The turn number the game is currently on
     * @return The goal that has been generated
     */
    public Goal generateRandom(int turn) {
        Map map = Game.getInstance().getMap();
        Station origin;
        Station intermediary;
        int forTurns;
        int bonus = 0;
        Train train = null;

        do {
            //This generates a suitable random station to be the origin node of the goal
            origin = map.getRandomStation();
        } while (origin instanceof CollisionStation);

        Station destination;
        do {
            //This generates a suitable random station to be the destination node of the goal (i.e not equal to the origin)
            destination = map.getRandomStation();
        } while (destination == origin || destination instanceof CollisionStation);

        double shortestDist = map.getShortestDistance(origin, destination); //Stores the shortest distance between two points
        int score = (int) (shortestDist * (Math.pow(1.0001, shortestDist))); //Score by default = shortestDist*1.0001^shortestDist.
        // This rewards players for completing longer goals, but not too much. If the scaling value was too large then it would be unfair to players that only receive shorter goals.

        Random random = new Random();
        int rand = random.nextInt(3);
        if (rand == 0) {
            //decide if goal has intermediary station; if not, initiate  the intermediary station as origin
            do {
                //Generates a suitable intermediary station (i.e not equal to the other two nodes and not in the shortest path
                intermediary = map.getRandomStation();
            }
            while (intermediary == origin || intermediary == destination || intermediary instanceof CollisionStation || map.inShortestPath(origin, destination, intermediary));
            //Calculates the bonus to be equal to the two distances between origin/intermediary and intermediary/destination added together
            bonus = (int) (map.getShortestDistance(origin, intermediary) + map.getShortestDistance(intermediary, destination));
            if (bonus < (score + (30 * Math.pow(1.0001, shortestDist)))) {
                //If the bonus is below what the score would be otherwise (can happen in some cases) then the bonus is set to equal the score plus a slight additional scaling to reward the player for the bonus
                bonus = (int) (score + (30 * Math.pow(1.0001, shortestDist)));
            }
        } else intermediary = origin;

        if (rand == 1) {
            //decides if goal can be competed in a number of turns for bonus;
            double expectedTurns;
            expectedTurns = Math.ceil(shortestDist / 60.0/2); //This is the number of turns a goal is expected to be completed in using an average speed train
            double lowerBound = Math.floor(0.75*expectedTurns); //These two variables are bounds for the generation of a suitable forTurns variable.
            double upperBound = Math.ceil(1.25 * expectedTurns);
            //By creating these bounds it adds an element of randomisation to the goals which SHOULD increase the enjoyment of the game
            do {
                forTurns = random.nextInt((int) upperBound + 1);
                //Continuously generates a new value until it fits within the bounds
            } while (forTurns < lowerBound);
            double diffFromExpected = forTurns - expectedTurns; //Difference between the expected and actual value
            bonus = score + (int) ((0.5 + (diffFromExpected / (2.0 * (upperBound - lowerBound)))) * score);
            //This bonus is calculated to reward the player more for completing a goal in a number of turns below the expected more, if they are above the average then they still receive a bonus but it is smaller
            //This is designed to work in the form: score + (25-75% based on the diffFromExpected)*score. This ensures that the bonus always exceeds the score, but is no more than 175% of the score.
        } else forTurns = 0;

        if (rand == 2) {
            train = resourceManager.getRandomTrain();
            //This will now never be less than the base score as it includes it in the calculation
            //This bonus equals (100-trainSpeed)% of the shortestDist + score
            //Higher speeds will result in lower bonus, lower speeds result in higher bonus. Rewards for difficulty
            bonus = (int) ((((100 - (float) train.getSpeed()) / 100) * shortestDist) + score);
        }

        Goal goal = new Goal(origin, destination, intermediary, turn, forTurns, score, bonus, train);

        return goal;
    }

    /** Add a random goal to a player, if they haven't been forced to skip a turn for that turn*/
    public void addRandomGoalToPlayer(Player player, boolean firstTurn) {
        //Firstly check if this is a game or a recording
    	//Needs to check whether the player is skipping their turn, if they are then they should not be given a goal
		if (!player.getSkip()) {
			if(firstTurn)
			{
				//It is a game, so we generate a new goal and add it to the player
    			Goal g = generateRandom(player.getPlayerManager().getTurnNumber());
    			player.addGoal(g);
			}
			else if(!GameScreen.instance.getClass().equals(RecordingScreen.class))
			{
				//It is a game, so we generate a new goal and add it to the player
    			Goal g = generateRandom(player.getPlayerManager().getTurnNumber());
    			player.addGoal(g);
    			if(GameScreen.instance.isRecording())
    			{
    				GameScreen.instance.record.recordGoal(g);
    			}
			}
			else
			{
				//It is a recording, so we load a goal from the playback
				RecordingScreen rec = (RecordingScreen)GameScreen.instance;
				
				GoalEvent goalE = rec.eventPlayer.getNextGoalEvent();
				Goal g = goalE.asGoal(Game.getInstance().getMap(), resourceManager);
				player.addGoal(g);
			}
    	}
    }

    /** This updates the score when a train arrives at a station by checking if the goals are complete 
     * @param train The train that has completed its route
     * @param player The player who owns the train/ will get the points
     * @return The string to display if a goal has been completed, otherwise null
     */
    public ArrayList<String> trainArrived(Train train, Player player) {
        ArrayList<String> completedString = new ArrayList<String>();
        for (Goal goal : player.getGoals()) {
            if (goal.isComplete(train)) {
                if (goal.isBonusCompleted(train)) {
                    player.updateScore(goal.getBonus());
                } else {
                    player.updateScore(goal.getScore());
                }
                player.completeGoal(goal);
                player.removeResource(train);
                completedString.add("player " + player.getPlayerNumber() + " completed a goal to " + goal.toString() + "!");
            }
        }
        return completedString;
    }
}
