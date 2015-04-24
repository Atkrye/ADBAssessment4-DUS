package fvs.taxe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.TaxeGame;

/**Type of Actor specifically for implementing the Top bar in the game GUI*/
public class TopBarActor extends Image {

	/**Instantiation method sets up image and at correct position*/
	public TopBarActor(){
		super(new Texture(Gdx.files.internal("Topbar.png")));
		setPosition(290, TaxeGame.HEIGHT - TopBarController.CONTROLS_HEIGHT);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
}
