package fvs.taxe.actor;

import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.player.PlayerManager;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;
import java.util.List;

import Util.Tuple;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class PioneerTrainActor extends TrainActor {
	private PioneerTrain train;
	private IPositionable position1;
	private IPositionable position2;
	private Connection connection;
	private double angle;
	//private ArrayList<Tuple<Connection, Position>> collidedPositions;
	// orderd in order of increasing distance from 1st station in connection

	public PioneerTrainActor(PioneerTrain train, Context context) {
		super(train, context);
		this.train = train;
		//collidedPositions = new ArrayList<Tuple<Connection, Position>>();
	}

	public Connection getConnection() {
		return connection;
	}

	public PioneerTrain getTrain() {
		return train;
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

				if (Math.abs(trainx-nextx)<=2 && Math.abs(trainy- nexty)<=2) {
					// if the train has reached the target station
					context.getConnectionController().pioneerTrainComplete(this);
				}
			}
			
			ShapeRenderer shapeRenderer = context.getTaxeGame().shapeRenderer;
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			if (PlayerManager.isNight()) {
				shapeRenderer.setColor(Color.WHITE);
			} else {
				shapeRenderer.setColor(Color.BLACK);
			}

			shapeRenderer.rectLine(this.getX()+ width/2, this.getY()+ height/2, 
					position1.getX() , position1.getY() , 5);
			shapeRenderer.end();
		}
	}

	public ArrayList<Tuple<Connection, Position>> collidedConnection() {
		// find all connections that collide with the new connection, and where
		ArrayList<Tuple<Connection, Position>> collidedPositions = new ArrayList<Tuple<Connection, Position>>();
		
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
				float pointdist = Position.getDistance(station1pos, point);

				for (int i = 0; i < collidedPositions.size(); i++) {
					Tuple<Connection, Position> pair = collidedPositions.get(i);
					if (Position.getDistance(station1pos, pair.getSecond()) > pointdist) {
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
		return collidedPositions;
	}

	public void setStationPositions(Connection connection){
		this.connection = connection;
		this.position1 = connection.getStation1().getPosition();
		this.position2 = connection.getStation2().getPosition();
		this.angle = Position.getAngle(position1,position2);

		float degangle = (float) (MathUtils.radiansToDegrees*angle);
		addAction(Actions.rotateTo((float) degangle));
	}
}
