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
	
	private ShapeRenderer shapeRenderer;
	private float obstacleWidth = 0;
	private Color controlsColor = Color.LIGHT_GRAY;
    private Color obstacleColor = Color.LIGHT_GRAY;
	private int controlsHeight;

	public TopBarActor(){
		super(new Texture(Gdx.files.internal("Topbar.png")));
		this.shapeRenderer = new ShapeRenderer();
		this.controlsHeight = TopBarController.CONTROLS_HEIGHT;
		setPosition(290, TaxeGame.HEIGHT - TopBarController.CONTROLS_HEIGHT);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		// previously used method, allowed colouring of top bar
		//batch.end();
       /* game.batch.draw(topbarTexture, 290, TaxeGame.HEIGHT - CONTROLS_HEIGHT);
        game.batch.end();*/
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
       /* // main topBar
        shapeRenderer.setColor(controlsColor);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, TaxeGame.WIDTH, controlsHeight);
       
        // obstacle topBar 
        shapeRenderer.setColor(obstacleColor);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, obstacleWidth, controlsHeight);
        
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, TaxeGame.WIDTH, 1);*/
        
       // shapeRenderer.end();
        //batch.begin();
	}
	
	public void setObstacleColor(Color color) {
		this.obstacleColor = color;
	}
	
	public void setControlsColor(Color color) {
		this.controlsColor = color;
	}

	public void setObstacleWidth(float width) {
		this.obstacleWidth = width;
	}
}
