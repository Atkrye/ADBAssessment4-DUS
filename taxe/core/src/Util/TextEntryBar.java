package Util;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import fvs.taxe.TaxeGame;

/** Class for taking text entry from keyboard - max 3 per screen */
public class TextEntryBar {
	/** x position for the text entry bar*/
	private int x;

	/** y position for the text entry bar*/
	private int y;

	/** Boolean to say whether the text entry bar has been clicked */
	protected boolean clicked;

	/** Current label being shown on screen*/
	protected String label;

	/** The label to show when the text entry bar has initially been shown on screen, before text entered */
	protected String startLabel;

	/** The game that the textEntryBar is in*/
	private TaxeGame game;

	/** Camera for the textEntry bar*/
	private OrthographicCamera camera;

	/** The rectangle that represents the bounds for the text field*/
	private	Rectangle textFieldBounds;

	/** The font that the text is shown in*/
	private	BitmapFont font;

	/** The number that represents which textEntry bar is active, starting from 0 upwards*/ 
	protected static int active;

	/** The value for the textEntryBar, used to determine which textEntry bar is active 
	 * if more than one textEntry bar on screen */
	protected int activeVal;

	/** Instantiation
	 * @param x X coordinate of the textEntry bar
	 * @param y Y coordinate of the textEntry bar
	 * @param activeVal The value of this textEntryBar
	 * @param game 
	 */
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

	public void setLabel(String label)
	{
		this.startLabel = label;
		this.label = label;
	}

	/** Change the active textEntryBar on the screen*/
	public static void changeActive(){
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

	/** If this TextEntryBar is active and the label length is at least 0, deletes a character from the label string */
	public void deleteLetter (){
		if (activeVal == active && label.length() >= 0){
			if (label.length() == 0){
				clicked = true;
			} 
			else {
				label = label.substring(0, label.length()-1);
			}
		}
	}

	/** detects if the TextEntryBar is touched, if so set the TextEntryBar as active */	
	public void update(Vector3 touchPoint){
		if (textFieldBounds.contains(touchPoint.x, touchPoint.y)) {
			active = activeVal;
			return;
		}
	}

	/** draws startLabel or label depending on whether clicked is true
		font colour is dark grey if the TextEntryBox is active */
	public void draw() {
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

	/** Clear the text entry bar*/
	public void clear(){
		label = "";
		this.clicked = false;
	}
}