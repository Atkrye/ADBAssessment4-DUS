package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import fvs.taxe.TaxeGame;

public class TopBarActor extends Actor {
	
	private ShapeRenderer shapeRenderer;
	private float obstacleWidth = 0;
	private Color controlsColor = Color.LIGHT_GRAY;
    private Color obstacleColor = Color.LIGHT_GRAY;
	private int controlsHeight;

	public TopBarActor(){
		super();
		this.shapeRenderer = new ShapeRenderer();
		this.controlsHeight = TopBarController.CONTROLS_HEIGHT;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // main topBar
        shapeRenderer.setColor(controlsColor);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, TaxeGame.WIDTH, controlsHeight);
       
        // obstacle topBar 
        shapeRenderer.setColor(obstacleColor);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, obstacleWidth, controlsHeight);
        
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, TaxeGame.WIDTH, 1);
        
        shapeRenderer.end();
        batch.begin();
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
