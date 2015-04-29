package gameLogic.trong;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.SoundPlayer;
import fvs.taxe.TaxeGame;

/**This class is a type of image describes object specific information for creating and controlling the Pong ball*/
public class BallActor extends Image{
	/**The in game pixel width of a ball*/
	private final static float width = 30;
	/**The in game pixel height of a ball*/
	private final static float height = 30;

	/**The in game multiplying rate at which the ball accelerates*/
	private final static float A = 1.001f;
	
	/**The left paddle in this Trong instance*/
	private PaddleActor leftPaddle;
	/**The right paddle in this Trong instance*/
	private PaddleActor rightPaddle;
	
	/**The x velocity of the ball in pixels per frame*/
	private float velocityX = 0;
	
	/**The y velocity of the ball in pixels per frame*/
	private float velocityY = 0;
	
	/**The start velocity of the ball, in pixels per frame*/
	private final static float startV = 4;
	
	/**This instantiation method sets up the ball actor and assigns the game paddles to it
	 * @param leftPaddle The left paddle in the point of pong
	 * @param rightPaddle The right paddle in the point of pong
	 */
    public BallActor(PaddleActor leftPaddle, PaddleActor rightPaddle) {
        //The constructor initialises all the variables and gathers the relevant image for the actor
        super(new Texture(Gdx.files.internal("trong/ball.png")));
        //Resize the ball
        this.setSize(width,  height);
        this.setPosition((TaxeGame.WIDTH / 2) - this.getWidth() / 2, ((TrongScreen.gameBottom + ((TrongScreen.gameTop - TrongScreen.gameBottom) / 2)) * TaxeGame.HEIGHT) - this.getHeight() / 2);
        this.leftPaddle = leftPaddle;
        this.rightPaddle = rightPaddle;
        velocityX = startV;
        if(new Random().nextBoolean())
        {
        	velocityX = velocityX * -1;
        }
    }
    
    /**This method is called every frame. It updates the ball, bouncing it off the Pong paddles and the wall*/
	public void update() {
		velocityX = velocityX * A;
		velocityY = velocityY * A;
		this.setX(this.getX() + velocityX);
		this.setY(this.getY() + velocityY);
		float velocity = (float) Math.sqrt((velocityX * velocityX) + (velocityY * velocityY));
		if(intersects(leftPaddle))
		{   SoundPlayer.playSound(10);
			float dy = dY(leftPaddle);
			float dx = dX(leftPaddle);
			//displacement;
			float d = (float)Math.sqrt((dy* dy) + (dx * dx));
			this.velocityX = (dx / d) * velocity;
			this.velocityY = (dy / d) * velocity;
		}
		if(intersects(rightPaddle))
		{	
			SoundPlayer.playSound(10);
			float dy = dY(rightPaddle);
			float dx = dX(rightPaddle);
			//displacement;
			float d = (float)Math.sqrt((dy* dy) + (dx * dx));
			this.velocityX = (dx / d) * velocity;
			this.velocityY = (dy / d) * velocity;
		}
		if(velocityY > 0 && this.getY() + this.getHeight() > (TrongScreen.gameTop * TaxeGame.HEIGHT))
		{	
			SoundPlayer.playSound(10);
			velocityY = velocityY * -1;
		}

		if(velocityY < 0 && this.getY() < (TrongScreen.gameBottom * TaxeGame.HEIGHT))
		{	
			SoundPlayer.playSound(10);
			velocityY = velocityY * -1;
		}
	}
	
	/**This method checks whether the ball intersects the bounds another image
	 * @param im The image to check for intersections
	 * @return Whether an intersection exists
	 */
	private boolean intersects(Image im)
	{
		return !((this.getX() > im.getX() + im.getWidth()) || (im.getX() > this.getX() + this.getWidth()) || (this.getY() > im.getY() + im.getHeight()) || (im.getY() > this.getY() + this.getHeight()));
	}
	
	/**This method finds the difference in x coordinates of the ball with another image
	 * @param im The image to find the x displacement to
	 * @return The float value of the difference between the ball and im's x coordinates
	 */
	private float dX(Image im)
	{
		return (this.getX() + (this.getWidth() / 2)) - (im.getX() + (im.getWidth() / 2));
	}
	
	/**This method finds the difference in y coordinates of the ball with another image
	 * @param im The image to find the y displacement to
	 * @return The float value of the difference between the ball and im's y coordinates
	 */
	private float dY(Image im)
	{
		return (this.getY() + (this.getHeight() / 2)) - (im.getY() + (im.getHeight() / 2));
		
	}
    
    

}
