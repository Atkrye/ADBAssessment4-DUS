package fvs.taxe;





import gameLogic.Game;
import adb.taxe.record.SaveManager;
import fvs.taxe.SoundPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;




public class MainMenuScreen extends ScreenAdapter {
    private TaxeGame game;
    private OrthographicCamera camera;
    private Rectangle playBounds;
    private Rectangle loadBounds;
    private Rectangle recordingBounds;
    private Rectangle exitBounds;
    private Vector3 touchPoint;
    private Texture mapTexture;
    private Image mapImage;
    private Texture mainScreenTexture;
    private boolean disabled = false;
    public SoundPlayer soundPlayer;
    
    public MainMenuScreen(TaxeGame game) {
        //This sets all the relevant variables for the menu screen
        //Did not understand this fully so did not change anything
        this.game = game;
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);

        playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 310, 520, 660, 133);
        loadBounds = new Rectangle(TaxeGame.WIDTH / 2 - 310, 360, 660, 133);
        recordingBounds = new Rectangle(TaxeGame.WIDTH / 2 - 310, 202, 660, 133);
        exitBounds = new Rectangle(TaxeGame.WIDTH / 2 - 310, 45, 660, 133);
        touchPoint = new Vector3();

        //Loads the gameMap in
        mapTexture = new Texture(Gdx.files.internal("launchscreen.png"));
        mapImage = new Image(mapTexture);
        
        //Creates three rectangles which act as buttons in the screen
        playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 485, 650, 125);
        //loadBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 290, 650, 125);
        exitBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 95, 650, 125);
        touchPoint = new Vector3();
        
        //loads in the background image for the screen
        mainScreenTexture = new Texture(Gdx.files.internal("launch_screen.png"));
        
        soundPlayer = new SoundPlayer();
        
    }

    
    private void update() {
    	
    	
        if (Gdx.input.justTouched() && !disabled) {
        	//detects which area of the screen is touched
        	//If rectangles are touch then relevant action is taken
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
            	SoundPlayer.playSound(1);
            	//If the touch is within the boundaries of the rectangle playBounds the GameSetupScreen is set
            	
            	game.setScreen(new GameSetupScreen(game));
                return;
            }
            //Load a game
            if (loadBounds.contains(touchPoint.x, touchPoint.y)) {
            	SoundPlayer.playSound(1);
            	//Disabled all click ability
            	disabled = true;
            	//Load a game using the SaveManager
                Game loadedGame = SaveManager.load();
                //If the loadedGame is null, the load was cancelled or failed
                if(loadedGame == null)
                {
                	disabled = false;
                }
                else
                {
                	//If the loadedGame is not null, we set the game to a new GameScreen
                	game.setScreen(new GameScreen(game, loadedGame));
                }
            }
            if (recordingBounds.contains(touchPoint.x, touchPoint.y)) {
            	//If the touch is within the boundaries of the rectangle recordingBounds, load recordings
            	SaveManager.loadRecordingFromChooser();
            }
            if (exitBounds.contains(touchPoint.x, touchPoint.y)) {
            	SoundPlayer.playSound(2);
            	//If the touch is within the boundaries of the rectangle exitBounds the game exits
                Gdx.app.exit();
            }
       }
  }
    private void draw() {
    	//This method draws the mainScreen Texture 
    	camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(mainScreenTexture, 0, 0);
        game.batch.end();
        
        }

    @Override
    public void render(float delta) {
        update();
        draw();
    }
}