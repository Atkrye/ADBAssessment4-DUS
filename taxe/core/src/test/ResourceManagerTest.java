package test;

import junit.framework.TestCase;

import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

public class ResourceManagerTest extends TestCase {
	ResourceManager rm = new ResourceManager();

	public void testGetRandomTrain() throws Exception {
		Train train1 = rm.getRandomTrain();
		System.out.println(train1.toString());

		Train train2 = rm.getRandomTrain();
		System.out.println(train2.toString());

		Train train3 = rm.getRandomTrain();
		System.out.println(train3.toString());

		Train train4 = rm.getRandomTrain();
		System.out.println(train4.toString());

		Train train5 = rm.getRandomTrain();
		System.out.println(train5.toString());

		Train train6 = rm.getRandomTrain();
		System.out.println(train6.toString());


	}

	public void testAddRandomResourceToPlayer() throws Exception {
		PlayerManager playerManager = new PlayerManager();
		playerManager.createPlayers(2);
		Player player1 = playerManager.getCurrentPlayer();

		rm.addRandomResourceToPlayer(player1, false);
		rm.addRandomResourceToPlayer(player1, false);
		rm.addRandomResourceToPlayer(player1, false);

		assertEquals(3, player1.getResources().size());

	}
}