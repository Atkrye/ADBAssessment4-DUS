package fvs.taxe;

import Util.TextEntryBar;
import Util.IntegerEntryBar;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameSetupScreen extends ScreenAdapter {
    TaxeGame game;
    OrthographicCamera camera;
    Rectangle playBounds;
    Rectangle pointsTabBounds;
    Rectangle turnsTabBounds;
    Vector3 touchPoint;
    Texture mapTexture;
    Image mapImage;
    Sound buttonSound;
    TextEntryBar p1NameEntry;
    TextEntryBar p2NameEntry;
    IntegerEntryBar pointsTurnsBar;

    public GameSetupScreen(TaxeGame game) {
        //This sets all the relevant variables for the menu screen
        //Did not understand this fully so did not change anything
        this.game = game;
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);
        playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 140, 70, 275, 68);
        pointsTabBounds = new Rectangle(370, 255, 270, 65);
        turnsTabBounds = new Rectangle(640, 255, 270, 65);
        touchPoint = new Vector3();
        p1NameEntry = new TextEntryBar(555, 490, true, 0, game);
        p2NameEntry = new TextEntryBar(555, 400, false, 1, game);
        pointsTurnsBar = new IntegerEntryBar(702, 175, false, 2, game);
        //Loads the gameMap in
        mapTexture = new Texture(Gdx.files.internal("setup_max_points1.png"));
        
        
        Gdx.input.setInputProcessor(new InputAdapter () {
        	
        	public boolean keyDown(int keycode) {
      	      
      	      if(keycode == Keys.BACKSPACE){
      	         p1NameEntry.deleteLetter();
      	         p2NameEntry.deleteLetter();
      	         pointsTurnsBar.deleteLetter();
      	       }
      	      if(keycode == Keys.TAB || keycode == Keys.ENTER){
      	    	 TextEntryBar.changeActive();
      	    	 p1NameEntry.checkActive();
      	         p2NameEntry.checkActive();
      	         pointsTurnsBar.checkActive(); 
      	         
      	      }
      	      return true;
        	}
        	
        	public boolean keyTyped (char character) {
        		   
        		p1NameEntry.makeLabel(character);
        		p2NameEntry.makeLabel(character);
        		pointsTurnsBar.makeLabel(character);
        		   
        		  return true;
        		   }
        	
        	});
        
    }

    public void update() {
    	
        //Begins the game or exits the application based on where the user presses
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            p1NameEntry.update(touchPoint);
            p2NameEntry.update(touchPoint);
            pointsTurnsBar.update(touchPoint);
            
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
            	Sound buttonSound = Gdx.audio.newSound(Gdx.files.internal("buttonSound3.mp3"));	
            	buttonSound.play();
                game.setScreen(new GameScreen(game));
                return;
            }
            if(pointsTabBounds.contains(touchPoint.x, touchPoint.y)){ 
            	mapTexture = new Texture(Gdx.files.internal("setup_max_points1.png"));
               pointsTurnsBar.clearLabel();
               }
            if(turnsTabBounds.contains(touchPoint.x, touchPoint.y))
            {
            	mapTexture = new Texture(Gdx.files.internal("setup_max_turns2.png"));
             pointsTurnsBar.clearLabel();
             }
            }
           
            //buttonSound.dispose();}
    }

    public void draw() {
      
    	//Draw transparent map in the background
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(mapTexture, 0, 0);
        game.batch.end();
        p2NameEntry.draw();
        p1NameEntry.draw();
        pointsTurnsBar.draw();
        
  
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }
}