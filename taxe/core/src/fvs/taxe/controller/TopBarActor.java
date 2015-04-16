package fvs.taxe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.TaxeGame;

public class TopBarActor extends Image {

	public TopBarActor(){
		super(new Texture(Gdx.files.internal("Topbar.png")));
		setPosition(290, TaxeGame.HEIGHT - TopBarController.CONTROLS_HEIGHT);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
}
