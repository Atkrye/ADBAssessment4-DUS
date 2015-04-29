package gameLogic.trong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.TaxeGame;
import gameLogic.resource.Train;

/**This class is a type of image used to show the paddles within the game. These are updated each frame
 * so that the controls can be used to move the paddle. The rate at which the paddle moves is based on the speed
 * of a train in game - so the faster a train, the faster the paddle moves.
 */
public class PaddleActor extends Image{
	/**The train that this Paddle represents*/
	private Train train;
	/**The width of a paddle, in pixels*/
	private final static float width = 20;
	/**The height of a paddle, in pixels*/
	private final static float height = 100;
	/**The width of the train icon, in pixels*/
	private final static float iconWidth = 40;
	/**The train icon that moves with the paddle*/
	private Image trainIcon;
	
	/**This instantiation sets up the paddle actor, as well as assigning it's train, producing a trainOcon from that
	 * train, and settting the position based on whether it is the left or right paddle
	 * @param train The train that this paddle is playing for
	 * @param leftPaddle Whether this paddle is the left or right paddle in the pong game
	 */
    public PaddleActor(Train train, boolean leftPaddle) {
        //The constructor initialises all the variables and gathers the relevant image for the actor based on the train it is acting for.
        super(new Texture(Gdx.files.internal("trong/paddle.png")));
        //Resize the paddle
        this.setSize(width,  height);
        //Position the paddle
        float y = ((TrongScreen.gameBottom + ((TrongScreen.gameTop - TrongScreen.gameBottom) / 2)) * TaxeGame.HEIGHT) - this.getHeight() / 2;
        if(leftPaddle)
        {
        	setPosition(TrongScreen.gameLeft * TaxeGame.WIDTH - (getWidth() / 2), y);
        }
        else
        {
        	setPosition(TrongScreen.gameRight * TaxeGame.WIDTH - (getWidth() / 2), y);
        }
        trainIcon = new Image(new Texture(Gdx.files.internal(train.getImage())));
        float ratio = (trainIcon.getHeight() / trainIcon.getWidth());
        trainIcon.setSize(iconWidth, ratio * iconWidth);
        //Determine the x position of the train Icon and position it
        float xD;
        if(leftPaddle)
        {
        	xD = -trainIcon.getWidth();
        }
        else
        {
        	xD = this.getWidth();
        }
        trainIcon.setPosition(this.getX() + xD, (this.getHeight() / 2) + this.getY() - (trainIcon.getHeight() / 2));
        this.train = train;
    }

    /**This method moves the paddle up based off the train's speed*/
	public void up() {
		//Move the paddle up
		this.setY(this.getY() + (train.getSpeed() / 10));
		if(this.getY() > TrongScreen.gameTop * TaxeGame.HEIGHT - this.getHeight())
		{
			this.setY(TrongScreen.gameTop * TaxeGame.HEIGHT - this.getHeight());
		}
		trainIcon.setY((this.getHeight() / 2) + this.getY() - (trainIcon.getHeight() / 2));
	}
	
	/**This method moves the paddle down based off the train's speed*/
	public void down() {
		//Move the paddle down
		this.setY(this.getY() - (train.getSpeed() / 10));
		if(this.getY() < TrongScreen.gameBottom * TaxeGame.HEIGHT)
		{
			this.setY(TrongScreen.gameBottom * TaxeGame.HEIGHT);
		}
		trainIcon.setY((this.getHeight() / 2) + this.getY() - (trainIcon.getHeight() / 2));
	}
	
	/**This method get's the paddle's train icon that moves with it
	 * @return The icon that represents the paddle's train
	 */
	public Image getTrainIcon()
	{
		return trainIcon;
	}

	/**This method gets the train that this paddle is playing for
	 * @return The train involved in the collision that this paddle corresponds to
	 */
	public Train getTrain() {
		return this.train;
	}
}
