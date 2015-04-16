package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** Class that represents the sidebar as an Image*/
public class SideBarActor extends Image {
	// essentially is Sidebar texture wrapped in an image to allow it to be added ot stage and have control over the z-order
	
	public SideBarActor() {
		super(new Texture(Gdx.files.internal("Sidebar.png")));
	}
}
