package gameLogic.trong;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.TaxeGame;

public class BarActor extends Image{
	private final static float height = 20;
    public BarActor(boolean top) {
        super(new Texture(Gdx.files.internal("trong/wall.png")));
        //Resize the paddle
        this.setSize((TrongScreen.gameRight - TrongScreen.gameLeft) * TaxeGame.WIDTH,  height);
        float y;
        if(top)
        {
        	y = (TrongScreen.gameTop * TaxeGame.HEIGHT);
        }
        else
        {
        	y = (TrongScreen.gameBottom * TaxeGame.HEIGHT) - this.getHeight();
        }
        this.setPosition(TrongScreen.gameLeft * TaxeGame.WIDTH, y);
    	
    }

}
