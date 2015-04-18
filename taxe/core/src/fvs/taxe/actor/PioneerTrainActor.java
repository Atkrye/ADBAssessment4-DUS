package fvs.taxe.actor;

import fvs.taxe.controller.Context;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.resource.PioneerTrain;

import java.util.ArrayList;
import java.util.List;

import Util.Tuple;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Actor that represents the PioneerTrain */
public class PioneerTrainActor extends TrainActor {
	// Creating a specific subclass for PioneerTrains allows PioneerTrains to have functionality on top of standard TrainActor
	// Required to allow it to create connections

	/** The PioneerTrain that corresponds to this actor */
	private PioneerTrain train;

	/** Start position used when a PioneerTrain has been given a NEW connection to create only.*/
	private IPositionable startPosition;

	/** End position used when a PioneerTrain has been given a NEW connection to create only.*/
	private IPositionable endPosition;

	/** Connection a PioneerTrain is creating when it has has been given a NEW connection to create only.*/
	private Connection connection;

	/** The angle (in radians) that the train will move in to create the new connection (if it has been given one to make)*/
	private double radAngle;

	private ShapeRenderer shapeRenderer;

	/** Instantiantion method that creates the Actor for a given train
	 * @param train The corresponding PioneerTrain that the actor is linked to
	 * @param context The game's context
	 */
	public PioneerTrainActor(PioneerTrain train, Context context) {
		super(train, context);
		this.train = train;
		this.shapeRenderer = new ShapeRenderer();
	}

	public Connection getConnection() {
		return connection;
	}

	public PioneerTrain getTrain() {
		return train;
	}
	
	@Override
	public void act(float delta) {
		// TODO Auto-generated method stub
		super.act(delta);
		
		if (train.isCreating() && isVisible()) {
			//draw the line from the station to the train 
			//batch.end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

			// line color depends on whether night or day
			if (context.getGameLogic().getPlayerManager().isNight()) {
				shapeRenderer.setColor(Color.WHITE);
			} else {
				shapeRenderer.setColor(Color.BLACK);
			}

			shapeRenderer.rectLine(this.getX()+ width/2, this.getY()+ height/2, 
					startPosition.getX() , startPosition.getY() , 5);
			shapeRenderer.end();
			//batch.begin();
		}
	}

	/** Finds all connections that will collide with the new connection, and where they will collide
	 * Achieved by emulating movement along path and detecting collisions */
	// emulation makes it easier to place the method here, rather than elsewhere
	public ArrayList<Tuple<Connection, Position>> collidedConnection() {
		// find all connections that collide with the new connection, and where
		ArrayList<Tuple<Connection, Position>> collidedPositions = new ArrayList<Tuple<Connection, Position>>();

		List<Connection> connections = context.getGameLogic().getMap().getConnections();
		int x1,x2,x3,x4,y1,y2,y3,y4;

		x3 = (int) (startPosition.getX() + 10*Math.cos(radAngle));
		y3 = (int) (startPosition.getY() + 10*Math.sin(radAngle));
		x4 = (int) (endPosition.getX() - 10*Math.cos(radAngle));
		y4 = (int) (endPosition.getY() - 10*Math.sin(radAngle));

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

	/** Set all of the corresponding things for creating a new connection - the connection, start/end positions and angles.
	 * @param connection The connection that the pioneer train will create
	 */
	public void setStationPositions(Connection connection){
		this.connection = connection;
		this.startPosition = connection.getStation1().getPosition();
		this.endPosition = connection.getStation2().getPosition();
		this.radAngle = Position.getAngle(startPosition,endPosition);
	}
}
