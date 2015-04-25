package fvs.taxe.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import fvs.taxe.TaxeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//Set window size
		config.height = TaxeGame.HEIGHT;
		config.width = TaxeGame.WIDTH;
		config.resizable = false;
		config.addIcon("icon/dus256.png", FileType.Internal);
		config.addIcon("icon/dus128.png", FileType.Internal);
		config.addIcon("icon/dus64.png", FileType.Internal);
		config.addIcon("icon/dus32.png", FileType.Internal);
		config.addIcon("icon/dus16.png", FileType.Internal);
		//config.fullscreen = true;
		if(arg.length > 0)
		{
			config.title = "TaxE Replay";
			new LwjglApplication(new TaxeGame(arg[0]), config);
			
		}
		else
		{
			config.title = "TaxE";
			new LwjglApplication(new TaxeGame(), config);
		}
	}
}
