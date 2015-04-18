package Util;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import fvs.taxe.controller.Context;

public class TextEntryDialog extends InputAdapter {

	protected TextEntryBar textEntryBar;
	protected Image nameBackground;
	private Context context;
	private Actor actor;
	
	public TextEntryDialog(Context context, Image image) {
		this.nameBackground = image;
		this.context = context;
		onCreate();
	}
	
	public void onCreate() {
		int midx = Math.round(context.getStage().getWidth() / 2 - nameBackground.getWidth()/2); 
		int midy = Math.round(context.getStage().getHeight() / 2- nameBackground.getHeight()/2);
		nameBackground.setPosition(midx , midy);
		context.getStage().addActor(nameBackground);

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
	
	public void setVisible(boolean visible){
		nameBackground.setVisible(visible);
		actor.setVisible(visible);
	}
	
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACKSPACE){
			//backspace deletes a character from the active entry bar
			textEntryBar.deleteLetter();
		}
		return true;
	}

	public boolean keyTyped (char character) {
		//This adds a character to the active entry bar  
		textEntryBar.makeLabel(character);
		return true;
	}

};

