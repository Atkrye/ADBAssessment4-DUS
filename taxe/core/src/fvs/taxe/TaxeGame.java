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
    public static final int WIDTH = 1280, HEIGHT = 700;

    public SpriteBatch batch;
    
    public BitmapFont fontRegular;
	public BitmapFont fontSmallRegular;
	public BitmapFont fontTinyRegular;
	
	public BitmapFont fontLight;
	public BitmapFont fontSmallLight;
	public BitmapFont fontTinyLight;
	
	public BitmapFont fontBold;
	public BitmapFont fontSmallBold;
	public BitmapFont fontTinyBold;
	
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        //Create regular font
        FreeTypeFontGenerator generatorRegular = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        // font size 50pt
        parameter.size = 50;
        fontRegular = generatorRegular.generateFont(parameter);

        //font size 30pt
        parameter.size = 30;
        fontSmallRegular = generatorRegular.generateFont(parameter);

		//font size 15pt
		parameter.size = 15;
		fontTinyRegular = generatorRegular.generateFont(parameter);


		generatorRegular.dispose();
        // don't forget to dispose to avoid memory leaks!
		//--------------------
		
		//Create light font
        FreeTypeFontGenerator generatorLight = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Light.ttf"));
        FreeTypeFontParameter parameterLight = new FreeTypeFontParameter();

        // font size 50pt
        parameterLight.size = 50;
        fontLight = generatorLight.generateFont(parameterLight);

        //font size 30pt
        parameterLight.size = 30;
        fontSmallLight = generatorLight.generateFont(parameterLight);

		//font size 15pt
        parameterLight.size = 15;
		fontTinyLight = generatorLight.generateFont(parameterLight);


		generatorLight.dispose();
        // don't forget to dispose to avoid memory leaks!
		//--------------------
		
		//Create bold font
        FreeTypeFontGenerator generatorBold = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Bold.ttf"));
        FreeTypeFontParameter parameterBold = new FreeTypeFontParameter();

        // font size 50pt
        parameterBold.size = 50;
        fontBold = generatorBold.generateFont(parameterBold);

        //font size 30pt
        parameterBold.size = 30;
        fontSmallBold = generatorBold.generateFont(parameterBold);

		//font size 15pt
        parameterBold.size = 15;
		fontTinyBold = generatorBold.generateFont(parameterBold);


		generatorBold.dispose();
        // don't forget to dispose to avoid memory leaks!
		//--------------------

        //Sets the main screen to be the menu
        setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
        fontRegular.dispose();
        shapeRenderer.dispose();
    }


}