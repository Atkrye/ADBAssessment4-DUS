package Util;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import fvs.taxe.TaxeGame;


public class TextEntryBar {
	   private int x;
	   private int y;
	   protected boolean clicked;
	   protected String label;
	   protected String startLabel;
	   private TaxeGame game;
	   private OrthographicCamera camera;
	   private	Rectangle textFieldBounds;
	   private	BitmapFont font;
	   protected static int active;
	   protected int activeVal;
		
		
	   
		public TextEntryBar(int x, int y, int activeVal, TaxeGame game) {
			this.x = x;
			this.y = y;
			this.label = "";
			this.game = game;			
			this.clicked = false;
			this.activeVal = activeVal;
			this.startLabel = "Enter Name";
			active = 0;
			font = game.fontMediumLight;
			camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
	        camera.setToOrtho(false);
	        textFieldBounds = new Rectangle(x, y, 300, 80 );
	    } 
	
		
   public String getLabelValue(){
	   //returns the string label unless it is empty in which case the startLabel string returned
	   if (label.isEmpty()){
			return startLabel;
	   } return label;
	}
   
   public static void changeActive(){
	//changes the active TextEntryBar   
	if(active < 2 ){
		active = active + 1;} 
	else {
		active = 0;
		}	
		
	}	
	
    public void checkActive(){
    //Checks to see if it is the active TextEntryBar	   	
    	if (activeVal == active){
    		font.setColor(Color.BLUE);
    		}
    	} 
		
	public void makeLabel (char character){
	//If this TextEntryBar is active and the label length is less than ten
	//Takes a character, if the character is a letter, the character is appended to label
		
		if (activeVal == active && label.length() < 10){	
			if(Character.isLetter(character)){
			   clicked = true;
			   label = label + character;
			   }
		   }   
	}
	
	public void deleteLetter (){
	//If this TextEntryBar is active and the label length is at least 0
	//deletes a character from the label string	
		if (activeVal == active && label.length() >= 0){
			if (label.length() == 0){
				clicked = true;
			} 
			else{
				label = label.substring(0, label.length()-1);}
		}
	}
	
	public void update(Vector3 touchPoint){
	//detects if the TextEntryBar is touched, if so set the TextEntryBar as active	
	if (textFieldBounds.contains(touchPoint.x, touchPoint.y)) {
    	active = activeVal;
        return;
    	}
    }
	
	public void draw() {
		//draws startLabel or label depending on whether clicked is true
		//font colour is dark grey if the TextEntryBox is active
			camera.update();
			game.batch.setProjectionMatrix(camera.combined);
			game.batch.begin();
			font.setColor(Color.GRAY);
			
			
			if (activeVal == active){
				font.setColor(Color.DARK_GRAY);
			}
			if (clicked == true){
				font.draw(game.batch, label, x, y + 45 );
			}
			else{
			font.draw(game.batch, startLabel, x, y + 45 );
			} 
			game.batch.end();
		}
	
	
}