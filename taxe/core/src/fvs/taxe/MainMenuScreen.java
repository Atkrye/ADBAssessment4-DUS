package fvs.taxe;



import gameLogic.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;



public class MainMenuScreen extends ScreenAdapter {
    private TaxeGame game;
    private OrthographicCamera camera;
    private Rectangle playBounds;
    private Rectangle exitBounds;
    private Rectangle loadBounds;
    private Vector3 touchPoint;
    private Texture mapTexture;
    private Sound buttonSound;
    

    
    public MainMenuScreen(TaxeGame game) {
        //This sets all the relevant variables for the menu screen
        //Did not understand this fully so did not change anything
        this.game = game;
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);
        playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 485, 650, 125);
        loadBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 290, 650, 125);
        exitBounds = new Rectangle(TaxeGame.WIDTH / 2 - 305, 95, 650, 125);
        touchPoint = new Vector3();
        
        mapTexture = new Texture(Gdx.files.internal("launch_screen.png"));
        
    }

    
    private void update() {
    	
    	
        if (Gdx.input.justTouched()) {
        	
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
            	Sound buttonSound = Gdx.audio.newSound(Gdx.files.internal("buttonSound3.mp3"));	
            	buttonSound.play();
            	game.setScreen(new GameSetupScreen(game));
                return;
            }
            if (exitBounds.contains(touchPoint.x, touchPoint.y)) {
            	buttonSound.play();
                Gdx.app.exit();
            }}
            //buttonSound.dispose();}
    }

    private void draw() {
   
    	
        
        
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(mapTexture, 0, 0);
        game.batch.end();
        
        }

    @Override
    public void render(float delta) {
        update();
        
        draw();
    }
}