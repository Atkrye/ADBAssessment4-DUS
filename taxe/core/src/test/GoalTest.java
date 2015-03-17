package test;

import junit.framework.TestCase;

import gameLogic.goal.Goal;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;

public class GoalTest extends TestCase {

	Station origin = new Station("station1", new Position(5, 5));
	Station destination = new Station("station2", new Position(2, 2));

	Station station3= new Station("station3", new Position(3, 5));
	Station station4 = new Station("station4", new Position(4, 2));
	Station station5= new Station("station5", new Position(5, 1));
	Station station6 = new Station("station6", new Position(6, 2));
	Station station7= new Station("station7", new Position(7, 5));
	Station station8 = new Station("station8", new Position(8, 2));


	Station intermediary = new Station("station3", new Position(5, 5));
	Train train = new Train("RedTrain", "RedTrain.png", 250);
	Train train2 = new Train("RedTrain", "RedTrain.png", 250);
	Goal goal = new Goal(origin, destination, intermediary, 0, 4, 50, 20, train);
	Goal goal2 = new Goal(origin, destination, intermediary, 0, 4, 50, 20, train2);




	public void testIsComplete() throws Exception {
		train.addHistory(origin, 0);
		train.addHistory(station3, 1);
		train.addHistory(station4, 4);
		train.addHistory(station5, 6);
		train.addHistory(station6, 10);
		train.addHistory(station7, 11);
		train.addHistory(station8, 16);

		assertEquals(false, goal.isComplete(train));
		train.setFinalDestination(destination);
		train.addHistory(destination, 18);
		assertEquals(true, goal.isComplete(train));

	}




	public void testCompletedWithinMaxTurns() throws Exception {

		Goal anotherGoal= new Goal (origin, destination, intermediary, 20, 0, 20, 50, train);
		assertEquals(false, anotherGoal.completedWithinMaxTurns(train));

		Goal yetAnotherGoal = new Goal(destination,origin,intermediary, 20, 10, 20, 50,train);
		train.addHistory(origin, 20);
		train.addHistory(station4, 22);
		train.addHistory(station6, 23);
		train.addHistory(station7, 40);
		train.addHistory(station8, 56);
		train.addHistory(destination,60);
		assertEquals(false, yetAnotherGoal.completedWithinMaxTurns(train));

	}

	public void testCompletedWithTrain() throws Exception {
		assertEquals(true, goal.getTrain().getName()==train.getName());
		Train timeOfMyLife = new Train("I just love testing", "RedTrain.png",  250);
		assertEquals(false, goal.getTrain().getName()==timeOfMyLife.getName());

	}


}