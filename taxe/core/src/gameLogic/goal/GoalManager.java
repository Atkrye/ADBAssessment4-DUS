package gameLogic.goal;

import gameLogic.Game;
import gameLogic.player.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.Random;

public class GoalManager {
    public final static int CONFIG_MAX_PLAYER_GOALS = 3;
    private ResourceManager resourceManager;

    public GoalManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public Goal generateRandom(int turn) {
        //This routine generates a random goal and calculates the points that should be awarded for completion of the goal

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

    public void addRandomGoalToPlayer(Player player) {
        //Needs to check whether the player is skipping their turn, if they are then they should not be given a goal
        if (!player.getSkip()) {
            player.addGoal(generateRandom(player.getPlayerManager().getTurnNumber()));
        }

		/* Uncomment to test the appropriateness of the generated points for the goals
        for (int i = 0; i<20;i++){
			generateRandom(player.getPlayerManager().getTurnNumber());
		}*/
    }

    public ArrayList<String> trainArrived(Train train, Player player) {
        //This updates the score when a train arrives at a station by checking if the goals are complete
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
