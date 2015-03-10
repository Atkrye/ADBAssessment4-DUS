package fvs.taxe.actor;

import gameLogic.map.IPositionable;
import gameLogic.obstacle.Obstacle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**This class is a type of image specifically for creating Obstacles.*/
public class ObstacleActor extends Image {

	/**The width of an obstacle in pixels.*/
	private static final int width = 50;
	
	/**The height of an obstacle in pixels.*/
    private static final int height = 50;

    /**The obstacleActor instantiation method. It is set up using the obstacle parameter.
     * @param obstacle The obstacle to create the actor from.
     */
	public ObstacleActor(Obstacle obstacle) {
		super(getTexture(obstacle));
		obstacle.setActor(this);
		setSize(width, height);
		IPositionable position = obstacle.getPosition();
		setPosition(position.getX() - (width/2), position.getY() - (height/2));
	}

	/**Static method for getting a texture depending on the type of obstacle passed.
	 * @param obstacle the obstacle to base the return texture of.
	 * @return A texture for the parameter obstacle's type.
	 */
	private static Texture getTexture(Obstacle obstacle) {
		switch(obstacle.getType()){
		case VOLCANO:
			return new Texture(Gdx.files.internal("obstacles/volcano.png"));
		case BLIZZARD:
			return new Texture(Gdx.files.internal("obstacles/blizzard.png"));
		case FLOOD:
			return new Texture(Gdx.files.internal("obstacles/flood.png"));
		case EARTHQUAKE:
			return new Texture(Gdx.files.internal("obstacles/earthquake.png"));
		default:
			return null;
		}
	}
}
