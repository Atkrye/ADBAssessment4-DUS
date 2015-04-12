package fvs.taxe;

import Util.TextEntryBar;
import Util.IntegerEntryBar;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;


public class GameSetupScreen extends ScreenAdapter {

    public static final String MODEPOINTS = "POINTS";
    public static final String MODETURNS = "TURNS";
    private TaxeGame game;
    private OrthographicCamera camera;
    private Rectangle playBounds;
    private Rectangle pointsTabBounds;
    private Rectangle turnsTabBounds;
    private Vector3 touchPoint;
    private Texture setupScreenTexture;
    private TextEntryBar p1NameEntry;
    private TextEntryBar p2NameEntry;
    private IntegerEntryBar pointsTurnsBar;
    private String MODE = MODEPOINTS;

    public GameSetupScreen(TaxeGame game) {
        //This sets all the relevant variables for the menu screen
        this.game = game;
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);
        //Creates three rectangles which act as buttons in the screen
        playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 140, 70, 275, 68);
        pointsTabBounds = new Rectangle(370, 255, 270, 65);
        turnsTabBounds = new Rectangle(640, 255, 270, 65);
        touchPoint = new Vector3();
        //This creates the entry bar instances required for game setup  
        p1NameEntry = new TextEntryBar(555, 490, 0, game);
        p2NameEntry = new TextEntryBar(555, 400,  1, game);
        pointsTurnsBar = new IntegerEntryBar(702, 175, 2, game);
        //Loads the gameMap in
        setupScreenTexture = new Texture(Gdx.files.internal("setup_max_points1.png"));
        
        
        Gdx.input.setInputProcessor(new InputAdapter () {
        	//The input processor which acts upon a user pressing keys
        	public boolean keyDown(int keycode) {
      	      
      	      if(keycode == Keys.BACKSPACE){
      	    	 //backspace deletes a character from the active entry bar
      	         p1NameEntry.deleteLetter();
      	         p2NameEntry.deleteLetter();
      	         pointsTurnsBar.deleteLetter();
      	       }
      	      if(keycode == Keys.TAB || keycode == Keys.ENTER){
      	    	 //if tab or enter is pressed the active entry bar is changed 
      	    	 TextEntryBar.changeActive();
      	    	 p1NameEntry.checkActive();
      	         p2NameEntry.checkActive();
      	         pointsTurnsBar.checkActive(); 
      	         
      	      }
      	      return true;
        	}
        	
        	public boolean keyTyped (char character) {
        		//This adds a character to the active entry bar   
        		p1NameEntry.makeLabel(character);
        		p2NameEntry.makeLabel(character);
        		pointsTurnsBar.makeLabel(character);
        		   
        		  return true;
        		   }
        	
        	});
        
    }

    private void update() {
    	
        //Begins the game or sets an entry bar as active based on where the user presses
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            p1NameEntry.update(touchPoint);
            p2NameEntry.update(touchPoint);
            pointsTurnsBar.update(touchPoint);
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
            	game.setScreen(new GameScreen(game, p1NameEntry.getLabelValue(), p2NameEntry.getLabelValue(), MODE, Integer.valueOf(pointsTurnsBar.getLabelValue())));
                return;
            }
            if(pointsTabBounds.contains(touchPoint.x, touchPoint.y) && !MODE.equals(MODEPOINTS)){ 
               MODE = MODEPOINTS;
               pointsTurnsBar.setLabel("3000");
               setupScreenTexture = new Texture(Gdx.files.internal("setup_max_points1.png"));
               
               pointsTurnsBar.setLastClicked();
               }
            if(turnsTabBounds.contains(touchPoint.x, touchPoint.y) && !MODE.equals(MODETURNS))
            {
                MODE = MODETURNS;
            	setupScreenTexture = new Texture(Gdx.files.internal("setup_max_turns2.png"));
             pointsTurnsBar.setLastClicked();
             pointsTurnsBar.setLabel("30");
             }
            }
           
       
    }

    private void draw() {
      
    	//Draws the background for the screen as well as the entry bars 
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(setupScreenTexture, 0, 0);
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