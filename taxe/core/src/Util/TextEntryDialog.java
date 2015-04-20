package Util;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.controller.Context;

/** Input adapter for tasking text entry*/
public class TextEntryDialog extends InputAdapter {
	private Context context;
	
	/** TextEntryBar for taking the text input */
	protected TextEntryBar textEntryBar;
	
	/** The background of the text entry bar */
	protected Image nameBackground;
	
	/** Actor corresponding to the text entry bar image*/
	private Actor actor;
	
	/** Constructor class for creating a text entry bar with background as given image
	 * @param context Associated context
	 * @param image Image used for background of bar
	 */
	public TextEntryDialog(Context context, Image image) {
		this.nameBackground = image;
		this.context = context;
		onCreate();
	}
	
	/** Set up the positions and drawing of the text entry bar and background */
	public void onCreate() {
		int midx = Math.round(context.getStage().getWidth() / 2 - nameBackground.getWidth()/2); 
		int midy = Math.round(context.getStage().getHeight() / 2- nameBackground.getHeight()/2);
		nameBackground.setPosition(midx , midy);
		context.getStage().addActor(nameBackground);

		// text entry bar wrapped in actgor to allow to be drawn with stage, set visibility
		textEntryBar = new TextEntryBar(midx + 80, midy + 30, 0, context.getTaxeGame());
		actor = new Actor(){
			@Override
			public void draw(Batch batch, float parentAlpha) {
				super.draw(batch, parentAlpha);
				batch.end();
				textEntryBar.draw();
				batch.begin();
			}
		};
		context.getStage().addActor(actor);
		
		setVisible(true);
	}
	
	/** Change the visibility of the background and text entry bar
	 * @param visible Boolean of visibility
	 */
	public void setVisible(boolean visible){
		nameBackground.setVisible(visible);
		actor.setVisible(visible);
	}
	
	/** Method to override for taking input. Backspace removes character*/
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACKSPACE){
			//backspace deletes a character from the active entry bar
			textEntryBar.deleteLetter();
		}
		return true;
	}

	/** When key is typed, update the textEntryBar */
	public boolean keyTyped (char character) {
		//This adds a character to the active entry bar  
		textEntryBar.makeLabel(character);
		return true;
	}
};

