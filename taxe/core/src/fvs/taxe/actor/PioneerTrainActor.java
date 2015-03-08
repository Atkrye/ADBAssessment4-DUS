package fvs.taxe.actor;

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

public class PioneerTrainActor extends TrainActor {
	private PioneerTrain train;
	private IPositionable position1;
	private IPositionable position2;
	private Connection connection;
	private double angle;
	private ArrayList<Tuple<Connection, Position>> collidedPositions;

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

				Station prevJunction = null;

				if (Math.abs(trainx-nextx)<=2 && Math.abs(trainy- nexty)<=2) {
					// if the train has reached the target station

					collidedConnection();

					if (collidedPositions.size() == 0){
						context.getConnectionController().connectionAdded(connection);

					} else {
						// if the train has collided with some connections
						for (int i = 0; i <collidedPositions.size(); i++) {
							Tuple<Connection, Position> pair = collidedPositions.get(i);
							Connection connection = pair.getFirst();
							Position position = pair.getSecond();
							CollisionStation junction = context.getGameLogic().getMap().addJunction("1", position);

							Station station1 = connection.getStation1();
							Station station2 = connection.getStation2();
							Station station3 = this.connection.getStation1();
							Station station4 = this.connection.getStation2();

							StationController.renderCollisionStation(junction);

							context.getConnectionController().connectionRemoved(connection);

							if (i == 0 && collidedPositions.size() == 1) {
								context.getConnectionController().connectionAdded(new Connection(station1, junction));
								context.getConnectionController().connectionAdded(new Connection(station2, junction));
								context.getConnectionController().connectionAdded(new Connection(station3, junction));
								context.getConnectionController().connectionAdded(new Connection(station4, junction));

							} else if (i == collidedPositions.size() -1) {
								context.getConnectionController().connectionAdded(new Connection(station1, junction));
								context.getConnectionController().connectionAdded(new Connection(station2, junction));
								context.getConnectionController().connectionAdded(new Connection(prevJunction, junction));
								context.getConnectionController().connectionAdded(new Connection(station4, junction));
								// ((i < collidedPositions.size()-1 && collidedPositions.size() > 1))
							} else if (i == 0) {
								context.getConnectionController().connectionAdded(new Connection(station1, junction));
								context.getConnectionController().connectionAdded(new Connection(station2, junction));
								context.getConnectionController().connectionAdded(new Connection(station3, junction));
								prevJunction = junction;
							} else  {
								context.getConnectionController().connectionAdded(new Connection(station1, junction));
								context.getConnectionController().connectionAdded(new Connection(station2, junction));
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

			if (point != null){
				collidedPositions.add(new Tuple<Connection, Position>(connection, point));
			}
		}
	}

	public void setStationPositions(Connection connection){
		this.connection = connection;
		this.position1 = connection.getStation1().getPosition();
		this.position2 = connection.getStation2().getPosition();
		this.angle = Position.getAngle(position1,position2);
	}
}
