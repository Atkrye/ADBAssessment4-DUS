package gameLogic.trong;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.TaxeGame;

/**This class is a type of Image that describes the 2 bars the make up the walls of the game*/
public class BarActor extends Image{
	/**The height of a bar in the game*/
	private final static float height = 20;
	/**The instantiation method sets up the texture and sive of the Bar, as well as positioning it based
	 * on whether it is the top bar or the bottom bar
	 * @param top Whether this bar actor is the top bar or the bottom bar
	 */
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
