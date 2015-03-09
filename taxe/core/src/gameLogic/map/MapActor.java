package gameLogic.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MapActor extends Image {

	 public MapActor() {
		 super(new Texture(Gdx.files.internal("gamemap.png")));
	    }
}
