package gameLogic.trong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.TaxeGame;
import gameLogic.resource.Train;

public class PaddleActor extends Image{
	private Train train;
	private final static float width = 20;
	private final static float height = 100;
	private final static float iconWidth = 40;
	private Image trainIcon;
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
        trainIcon = new Image(new Texture(Gdx.files.internal(train.getLeftImage())));
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

	public void up() {
		//Move the paddle up
		this.setY(this.getY() + (train.getSpeed() / 10));
		if(this.getY() > TrongScreen.gameTop * TaxeGame.HEIGHT - this.getHeight())
		{
			this.setY(TrongScreen.gameTop * TaxeGame.HEIGHT - this.getHeight());
		}
		trainIcon.setY((this.getHeight() / 2) + this.getY() - (trainIcon.getHeight() / 2));
	}
	
	public void down() {
		//Move the paddle down
		this.setY(this.getY() - (train.getSpeed() / 10));
		if(this.getY() < TrongScreen.gameBottom * TaxeGame.HEIGHT)
		{
			this.setY(TrongScreen.gameBottom * TaxeGame.HEIGHT);
		}
		trainIcon.setY((this.getHeight() / 2) + this.getY() - (trainIcon.getHeight() / 2));
	}
	
	public Image getTrainIcon()
	{
		return trainIcon;
	}

	public Train getTrain() {
		return this.train;
	}
}
