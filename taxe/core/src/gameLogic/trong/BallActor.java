package gameLogic.trong;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.TaxeGame;

public class BallActor extends Image{
	private final static float width = 30;
	private final static float height = 30;
	//Acceleration constant
	private final static float A = 1.001f;
	private PaddleActor leftPaddle;
	private PaddleActor rightPaddle;
	private float velocityX = 0;
	private float velocityY = 0;
	
	//Start velocity in pixels
	private final static float startV = 4;
	
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
	public void update() {
		velocityX = velocityX * A;
		velocityY = velocityY * A;
		this.setX(this.getX() + velocityX);
		this.setY(this.getY() + velocityY);
		float velocity = (float) Math.sqrt((velocityX * velocityX) + (velocityY * velocityY));
		if(intersects(leftPaddle))
		{
			float dy = dY(leftPaddle);
			float dx = dX(leftPaddle);
			//displacement;
			float d = (float)Math.sqrt((dy* dy) + (dx * dx));
			this.velocityX = (dx / d) * velocity;
			this.velocityY = (dy / d) * velocity;
		}
		if(intersects(rightPaddle))
		{
			float dy = dY(rightPaddle);
			float dx = dX(rightPaddle);
			//displacement;
			float d = (float)Math.sqrt((dy* dy) + (dx * dx));
			this.velocityX = (dx / d) * velocity;
			this.velocityY = (dy / d) * velocity;
		}
		if(velocityY > 0 && this.getY() + this.getHeight() > (TrongScreen.gameTop * TaxeGame.HEIGHT))
		{
			velocityY = velocityY * -1;
		}

		if(velocityY < 0 && this.getY() < (TrongScreen.gameBottom * TaxeGame.HEIGHT))
		{
			velocityY = velocityY * -1;
		}
	}
	
	private boolean intersects(Image im)
	{
		return !((this.getX() > im.getX() + im.getWidth()) || (im.getX() > this.getX() + this.getWidth()) || (this.getY() > im.getY() + im.getHeight()) || (im.getY() > this.getY() + this.getHeight()));
	}
	
	//Calculates the x displacement between the centres
	private float dX(Image im)
	{
		return (this.getX() + (this.getWidth() / 2)) - (im.getX() + (im.getWidth() / 2));
	}
	
	//Calculates the y displacement between the centres
	private float dY(Image im)
	{
		return (this.getY() + (this.getHeight() / 2)) - (im.getY() + (im.getHeight() / 2));
		
	}
    
    

}
