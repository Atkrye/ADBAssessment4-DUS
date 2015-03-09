package gameLogic.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

// blank actor of size of map, used to allow clicklisteners.
// simpler then implementing inputprocessor on connectioCOntroller
public class BlankMapActor extends Image {

	 public BlankMapActor() {
		 Texture texture = new Texture(Gdx.files.internal("gamemap.png"));
		 setWidth(texture.getWidth());
		 setHeight(texture.getHeight());
	    }
}
