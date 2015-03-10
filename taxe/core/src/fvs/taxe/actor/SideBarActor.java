package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SideBarActor extends Image {

	public SideBarActor() {
		super(new Texture(Gdx.files.internal("Sidebar.png")));
	}
}
