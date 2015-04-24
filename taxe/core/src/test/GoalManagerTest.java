package test;

import junit.framework.TestCase;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

public class GoalManagerTest extends TestCase {
	ResourceManager rs = new ResourceManager();
	GoalManager goalManager=new GoalManager(rs);
	PlayerManager playerManager = new PlayerManager();


	//  playerManager.createPlayers(2);
	//  Player player1 = playerManager.getCurrentPlayer();

	Train train = new Train("Green", "", 100);

	Station station1 = new Station("station1", new Position(5, 5));
	Station station2 = new Station("station2", new Position(2, 2));
	Station station3 = new Station("station3", new Position(6, 2));

	Goal goal = new Goal(station1, station2, station3, 0, 0, 0, 0, train);

	public void testGenerateRandom() throws Exception {

		System.out.println(goal.toString());
		//player1.addGoal(goal);
		//player1.addResource(train);

		Goal newGoal1 = goalManager.generateRandom(1);
		System.out.println(newGoal1.toString());
		Goal newGoal2 = goalManager.generateRandom(3);
		System.out.println(newGoal2.toString());
		Goal newGoal3 = goalManager.generateRandom(5);
		System.out.println(newGoal3.toString());
		Goal newGoal4 = goalManager.generateRandom(7);
		System.out.println(newGoal4.toString());
		Goal newGoal5 = goalManager.generateRandom(9);
		System.out.println(newGoal5.toString());
		Goal newGoal6 = goalManager.generateRandom(11);
		System.out.println(newGoal6.toString());
		Goal newGoal7 = goalManager.generateRandom(13);
		System.out.println(newGoal7.toString());


	}

	public void testAddRandomGoalToPlayer() throws Exception {
		playerManager.createPlayers(2);
		Player player1 = playerManager.getCurrentPlayer();
		assertEquals(0,player1.getGoals().size());

		goalManager.addRandomGoalToPlayer(player1, false);
		goalManager.addRandomGoalToPlayer(player1, false);
		goalManager.addRandomGoalToPlayer(player1, false);


		assertEquals(3, player1.getGoals().size());



	}
}