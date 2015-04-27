package fvs.taxe;

import Util.TextEntryBar;
import Util.IntegerEntryBar;
import fvs.taxe.SoundPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/** This class is used to show the Game setup screen for the GUI */
public class GameSetupScreen extends ScreenAdapter {

	/** String that represents the game finishing on points */
	public static final String MODEPOINTS = "POINTS";

	/** String that represents the game finishing on turns */
	public static final String MODETURNS = "TURNS";

	/** The game instance for the game setup screen */
	private TaxeGame game;

	/** The camera for the game setup screen*/
	private OrthographicCamera camera;

	/** Rectangle that represents the clickable area for the play button*/
	private Rectangle playBounds;

	/** Rectangle that represents the clickable area for the points button*/
	private Rectangle pointsTabBounds;

	/** Rectangle that represents the clickable area for the turns button*/
	private Rectangle turnsTabBounds;

	/**This vector is set to the location of the most recent click on the screen.*/
	private Vector3 touchPoint;

	/** The texture background for the setup screen*/
	private Texture setupScreenTexture;

	/** Text entry bar for player 1's name*/
	private TextEntryBar p1NameEntry;

	/** Text entry bar for player 2's name*/
	private TextEntryBar p2NameEntry;

	/** Integer entry bar for enterign th enumber of points needed to win*/
	private IntegerEntryBar pointsTurnsBar;

	/** String for the mode of the game that has been selected (either turns or points)*/ 
	private String MODE = MODEPOINTS;

	/** Instantiation
	 * @param game TaxeGame instance 
	 */
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

		// start the input processor for the name entry
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

	/** When rendered, update the gameSetup screen to reflect any changes*/
	private void update() {
		//Begins the game or sets an entry bar as active based on where the user presses
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			p1NameEntry.update(touchPoint);
			p2NameEntry.update(touchPoint);
			pointsTurnsBar.update(touchPoint);
			if (playBounds.contains(touchPoint.x, touchPoint.y)) {
            	SoundPlayer.playSound(1);
				game.setScreen(new GameScreen(game, p1NameEntry.getLabelValue(), p2NameEntry.getLabelValue(), MODE, Integer.valueOf(pointsTurnsBar.getLabelValue())));
				return;
			}
			if(pointsTabBounds.contains(touchPoint.x, touchPoint.y) && !MODE.equals(MODEPOINTS)){ 
	               SoundPlayer.playSound(2);	
				MODE = MODEPOINTS;
				pointsTurnsBar.setLabel("3000");
				setupScreenTexture = new Texture(Gdx.files.internal("setup_max_points1.png"));

				pointsTurnsBar.setLastClicked();
			}
			if(turnsTabBounds.contains(touchPoint.x, touchPoint.y) && !MODE.equals(MODETURNS))
			{
	            SoundPlayer.playSound(2);	
				MODE = MODETURNS;
				setupScreenTexture = new Texture(Gdx.files.internal("setup_max_turns2.png"));
				pointsTurnsBar.setLastClicked();
				pointsTurnsBar.setLabel("30");
			}
		}
	}

	/** Draws the background for the screen and the entry bars */
	private void draw() {
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.batch.draw(setupScreenTexture, 0, 0);
		game.batch.end();
		p2NameEntry.draw();
		p1NameEntry.draw();
		pointsTurnsBar.draw();
	}
}