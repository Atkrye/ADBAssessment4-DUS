package fvs.taxe.actor;

import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.resource.PioneerTrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PioneerTrainActor extends TrainActor {
	private PioneerTrain train;
	private IPositionable position1;
	private IPositionable position2;
	private Connection connection;
	private double angle;

	public PioneerTrainActor(PioneerTrain train, Context context) {
		super(train, context);
		this.train = train;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (train.isCreating()) {
			if (context.getGameLogic().getState() == GameState.ANIMATING) {
				moveBy((float) (train.getSpeed()*Math.cos(angle))*delta, (float) (train.getSpeed()*Math.sin(angle))*delta);
				
				// tests if reached goal
				float trainx = getX()+ width/2;
				float nextx = position2.getX();
				float trainy = getY() + height/2;
				float nexty = position2.getY();
				if (Math.abs(trainx-nextx)<1 && Math.abs(trainy- nexty)<1){
					context.getConnectionController().connectionAdded(connection);
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



		// test for collisions with connections?



	}

	public void setStationPositions(Connection connection){
		this.connection = connection;
		this.position1 = connection.getStation1().getPosition();
		this.position2 = connection.getStation2().getPosition();
		this.angle = Position.getAngle(position1,position2);;
	}
}
