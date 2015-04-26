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

/**This class is used to set up the graphical interface of the main menu for the player. It is first used when the TaxeGame.java is instantiated.*/
public class MainMenuScreen extends ScreenAdapter {

	/**Stores the main instance of TaxeGame.java.*/
	final private TaxeGame game;

	/**Stores an orthographic camera used in the menu to project clicks.*/
	private OrthographicCamera camera;
	
	/**This rectangle stores the bounds of the play button, and is used to detect whether a click has clicked the play button.*/
	private Rectangle playBounds;

	/** This rectangle stores the bounds of the load button, used to detect whether click has clicked load*/
	private Rectangle loadBounds;

	/** This rectangle stores the bounds of the recording button, used to detect whether click has clicked record*/
	private Rectangle recordingBounds;

	/** This rectangle stores the bounds of the exit button, used to detect whether click has clicked exit*/
	private Rectangle exitBounds;

	/**This vector is set to the location of the most recent click on the screen.*/
	private Vector3 touchPoint;

	/** The background texture of the main menu screen */
	private Texture mainScreenTexture;

	/** Whether mainmenu is clickable or not*/
	private boolean disabled = false;

	/**Instantiation method. sets up bounds and camera.
	 *@param game The main TaxeGame instance is assigned to the local variable game.
	 */
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

		//Creates three rectangles which act as buttons in the screen
		playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 485, 650, 125);
		//loadBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 290, 650, 125);
		exitBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 95, 650, 125);
		touchPoint = new Vector3();

		//loads in the background image for the screen
		mainScreenTexture = new Texture(Gdx.files.internal("launch_screen.png"));
	}

	/**This method is called once every frame using the render method. It checks whether there has been a touch, and if so, checks whether this touch is within one of the buttons bounds.*/
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
            	SoundPlayer.playSound(1);
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

	/**This method is called once every frame using the render method. It draws the main menu.*/
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