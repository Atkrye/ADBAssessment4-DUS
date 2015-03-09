package fvs.taxe.actor;

import fvs.taxe.controller.ConnectionController;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.StationController;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;
import java.util.List;

import Util.Tuple;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class PioneerTrainActor extends TrainActor {
	private PioneerTrain train;
	private IPositionable position1;
	private IPositionable position2;
	private Connection connection;
	private double angle;
	private ArrayList<Tuple<Connection, Position>> collidedPositions;
	// orderd in order of increasing distance from 1st station in connection

	public PioneerTrainActor(PioneerTrain train, Context context) {
		super(train, context);
		this.train = train;
		collidedPositions = new ArrayList<Tuple<Connection, Position>>();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (train.isCreating() && isVisible()) {
			if (context.getGameLogic().getState() == GameState.ANIMATING) {
				moveBy((float) (train.getSpeed()*Math.cos(angle))*delta, (float) (train.getSpeed()*Math.sin(angle))*delta);

				// tests if reached goal
				float trainx = getX()+ width/2;
				float nextx = position2.getX();
				float trainy = getY() + height/2;
				float nexty = position2.getY();

				CollisionStation prevJunction = null;

				if (Math.abs(trainx-nextx)<=2 && Math.abs(trainy- nexty)<=2) {
					// if the train has reached the target station

					collidedConnection();

					//	float testx = position1.getX();
					//	float testy = position1.getY();


					/*while (!(Math.abs(testx-nextx)<=2 && Math.abs(testy- nexty)<=2))  {
						System.out.println(testx + " , " + testy);
						collidedConnection(testx, testy);

						//if (point != null){
							//collidedPositions.add(new Tuple<Connection, Position>(connection, point));
						//}
						testx += Math.cos(angle)*delta;
						testy += Math.sin(angle)*delta;
					}
					 */
					if (collidedPositions.size() == 0){
						context.getConnectionController().connectionAdded(connection);

					} else {
						// if the train has collided with some connections


						Station startStation = this.connection.getStation1();
						Station endStation = this.connection.getStation2();
						for (int i = 0; i < collidedPositions.size(); i++) {
							Tuple<Connection, Position> pair = collidedPositions.get(i);
							Connection collidedConn = pair.getFirst();
							Position position = pair.getSecond();
							CollisionStation junction = context.getGameLogic().getMap().addJunction(ConnectionController.getNextJunctionNum(), position);
							StationController.renderCollisionStation(junction);

							context.getConnectionController().connectionRemoved(collidedConn);

							Station iStation1 = collidedConn.getStation1();
							Station iStation2 = collidedConn.getStation2();
							if (i == 0 && collidedPositions.size() == 1) {
								System.out.println("1");
								context.getConnectionController().connectionAdded(new Connection(iStation1, junction));
								context.getConnectionController().connectionAdded(new Connection(iStation2, junction));
								context.getConnectionController().connectionAdded(new Connection(startStation, junction));
								context.getConnectionController().connectionAdded(new Connection(endStation, junction));

							} else if (i == 0) {
								System.out.println("2");
								context.getConnectionController().connectionAdded(new Connection(iStation1, junction));
								context.getConnectionController().connectionAdded(new Connection(iStation2, junction));
								context.getConnectionController().connectionAdded(new Connection(startStation, junction));
								prevJunction = junction;
							} else if (i == collidedPositions.size() -1) {
								System.out.println("3");
								context.getConnectionController().connectionAdded(new Connection(iStation1, junction));
								context.getConnectionController().connectionAdded(new Connection(iStation2, junction));
								context.getConnectionController().connectionAdded(new Connection(prevJunction, junction));
								context.getConnectionController().connectionAdded(new Connection(junction, endStation));
								// ((i < collidedPositions.size()-1 && collidedPositions.size() > 1))
							} else  {
								System.out.println("4");
								context.getConnectionController().connectionAdded(new Connection(iStation1, junction));
								context.getConnectionController().connectionAdded(new Connection(iStation2, junction));
								context.getConnectionController().connectionAdded(new Connection(prevJunction, junction));
								prevJunction = junction;
							} 
						}
					}
					train.creationCompleted();
				}
			}
			ShapeRenderer shapeRenderer = context.getTaxeGame().shapeRenderer;
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rectLine(this.getX()+ width/2, this.getY()+ height/2, 
					position1.getX() , position1.getY() , 5);
			shapeRenderer.end();
		}
	}

	private void collidedConnection() {
		// find all connections that collide with the new connection, and where
		List<Connection> connections = context.getGameLogic().getMap().getConnections();
		int x1,x2,x3,x4,y1,y2,y3,y4;

		x3 = (int) (position1.getX() + 10*Math.cos(angle));
		y3 = (int) (position1.getY() + 10*Math.sin(angle));
		x4 = (int) (position2.getX() - 10*Math.cos(angle));
		y4 = (int) (position2.getY() - 10*Math.sin(angle));

		for (Connection connection: connections){
			x1 = connection.getStation1().getPosition().getX();
			y1 = connection.getStation1().getPosition().getY();
			x2 = connection.getStation2().getPosition().getX();
			y2 = connection.getStation2().getPosition().getY();

			Position point = Position.getLineIntersect(x1, y1, x2, y2, x3, y3, x4, y4);
			
			boolean added = false;
			if (point != null){
				// add it to the ordered collidedPositions array
				IPositionable station1pos = this.connection.getStation1().getPosition();
				float pointdist = getDistance(station1pos, point);
				
				for (int i = 0; i < collidedPositions.size(); i++) {
					Tuple<Connection, Position> pair = collidedPositions.get(i);
					if (getDistance(station1pos, pair.getSecond()) > pointdist) {
						collidedPositions.add(i, new Tuple<Connection, Position>(connection, point));
						added = true;
						break;
					}
				}
				if (!added) {
				collidedPositions.add(new Tuple<Connection, Position>(connection, point));
				}
			}
		}
	}

	private void collidedConnection(float trainx, float trainy) {
		Position position = new Position((int) trainx, (int) trainy);
		if (getDistance(position, position1) > 10
				&& getDistance(position, position2) > 10) {
			if (collidedPositions.size() > 0) {
				if (getDistance(position, collidedPositions.get(collidedPositions.size()-1).getSecond()) < 5) {
					// dont create a new junction if its too close to a previous collision
					return;
				}
			}
			List<Connection> connections = context.getGameLogic().getMap().getConnections();
			int x1,x2,x3,y1,y2,y3;

			x3 = (int) ((int) (trainx + Math.cos(angle)));
			y3 = (int) ((int) (trainy + Math.sin(angle)));

			ShapeRenderer shapeRenderer = context.getTaxeGame().shapeRenderer;
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rectLine(trainx, trainy, x3, y3 , 5);
			shapeRenderer.end();

			for (Connection connection: connections){
				x1 = connection.getStation1().getPosition().getX();
				y1 = connection.getStation1().getPosition().getY();
				x2 = connection.getStation2().getPosition().getX();
				y2 = connection.getStation2().getPosition().getY();

				//boolean num = Intersector.intersectLines(x1, y1, x2, y2, (int) trainx, (int) trainy, x4, y4, intersection);

				Position point = Position.getLineIntersect(x1, y1, x2, y2, trainx, trainy, x3, y3);

				// previousCollision ensures repeated collisions dont occur
				if (point != null){
					// add it to the ordered collidedPositions array
					IPositionable station1pos = this.connection.getStation1().getPosition();
					float pointdist = getDistance(station1pos, point);
					for (int i = 0; i < collidedPositions.size(); i++) {
						Tuple<Connection, Position> pair = collidedPositions.get(i);
						if (getDistance(station1pos, pair.getSecond()) > pointdist) {
							collidedPositions.add(i-1, new Tuple<Connection, Position>(connection, point));
							return;
						}
						collidedPositions.add(new Tuple<Connection, Position>(connection, point));
					}

				}
			}
		}

	}

	private float getDistance(IPositionable a, IPositionable b) {
		//This method returns the absolute distance from point A to point B in pixels
		return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
	}

	public void setStationPositions(Connection connection){
		this.connection = connection;
		this.position1 = connection.getStation1().getPosition();
		this.position2 = connection.getStation2().getPosition();
		this.angle = Position.getAngle(position1,position2);
	}
}
