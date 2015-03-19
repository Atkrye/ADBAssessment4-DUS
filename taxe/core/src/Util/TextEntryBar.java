package Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import fvs.taxe.TaxeGame;


public class TextEntryBar {
	   private int x;
	   private int y;
	   protected boolean lastClicked;
	   protected boolean clicked;
	   protected String label;
	   protected String startLabel;
	   private TaxeGame game;
	   private OrthographicCamera camera;
	   private	Rectangle textFieldBounds;
	   private	BitmapFont font;
	   //Vector3 touchPoint;
	   private static int active;
	   private	int activeVal;
		
		
	   
		public TextEntryBar(int x, int y, boolean lastClicked, int activeVal, TaxeGame game) {
			this.x = x;
			this.y = y;
			this.label = "";
			this.game = game;
			this.lastClicked = lastClicked;
			this.clicked = false;
			this.activeVal = activeVal;
			this.startLabel = "Enter Name";
			active = 0;
			//font = new BitmapFont(Gdx.files.internal("open_sans1.fnt"),Gdx.files.internal("open_sans1.png"),false);
			font = game.fontMediumLight;
			camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
	        camera.setToOrtho(false);
	        
			textFieldBounds = new Rectangle(x, y, 300, 80 );
	        
	       } 
	
		
   public String getLabelValue(){
	   if (label.isEmpty()){
			return startLabel;
	   } return label;
	}
   
   public static void changeActive(){
	if(active < 2 ){
		active = active + 1;
		
	} else {
		active = 0;
		}	
		
	}	
	
    public void checkActive(){
    	
    	lastClicked = false;
    	if (activeVal == active){
    		clicked = true;
    		lastClicked = true;
    	}
    	
    } 
		
	public void makeLabel (char character){
		
		if (lastClicked == true && label.length() < 10){
			if(Character.isLetter(character)){
			   clicked = true;
			   label = label + character;
			   
			}
		   }   
	}
	
	public void deleteLetter (){
		if (lastClicked == true && label.length() > 0){
			label = label.substring(0, label.length()-1);
			
		}
	}
	
	public void update(Vector3 touchPoint){
		
		lastClicked = false;
    
    if (textFieldBounds.contains(touchPoint.x, touchPoint.y)) {
    	active = activeVal;
    	clicked = true;
    	lastClicked = true;
    	return ;
    	}
    }
	
	public void draw() {
		
			camera.update();
			game.batch.setProjectionMatrix(camera.combined);
			game.batch.begin();
			font.setColor(Color.GRAY);
            
			if (clicked == true){
				
				font.draw(game.batch, label, x, y + 45 );
			} else{
			font.draw(game.batch, startLabel, x, y + 45 );
			}
			game.batch.end();
		}
	
	
}