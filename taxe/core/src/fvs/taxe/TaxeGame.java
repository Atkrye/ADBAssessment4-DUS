package fvs.taxe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class TaxeGame extends Game {
	



    // Using native res of the map image we are using at the moment
    //Did not change this to allow resizing as this was deemed to be too much work
    public static final int WIDTH = 1022, HEIGHT = 678;

    public SpriteBatch batch;
    public BitmapFont font;
	public BitmapFont fontSmall;
	public BitmapFont fontTiny;
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        //create font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        // font size 50pt
        parameter.size = 50;
        font = generator.generateFont(parameter);

        //font size 20pt
        parameter.size = 20;
        fontSmall = generator.generateFont(parameter);

		//font size 12pt
		parameter.size = 14;
		fontTiny = generator.generateFont(parameter);


        generator.dispose();
        // don't forget to dispose to avoid memory leaks!

        //Sets the main screen to be the menu
        setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }


}