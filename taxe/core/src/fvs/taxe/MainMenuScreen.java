package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainMenuScreen extends ScreenAdapter {
    TaxeGame game;
    OrthographicCamera camera;
    Rectangle playBounds;
    Rectangle loadBounds;
    Rectangle exitBounds;
    Vector3 touchPoint;
    Texture mapTexture;
    Image mapImage;

    public MainMenuScreen(TaxeGame game) {
        //This sets all the relevant variables for the menu screen
        //Did not understand this fully so did not change anything
        this.game = game;
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);

        playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 310, 480, 660, 133);
        loadBounds = new Rectangle(TaxeGame.WIDTH / 2 - 310, 285, 660, 133);
        exitBounds = new Rectangle(TaxeGame.WIDTH / 2 - 310, 90, 660, 133);
        touchPoint = new Vector3();

        //Loads the gameMap in
        mapTexture = new Texture(Gdx.files.internal("launchscreen.png"));
        mapImage = new Image(mapTexture);
    }

    public void update() {
        //Begins the game or exits the application based on where the user presses
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new GameScreen(game));
                return;
            }
            if (loadBounds.contains(touchPoint.x, touchPoint.y)) {
                System.out.println("Load Pressed");
            }
            if (exitBounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.exit();
            }
        }
    }

    public void draw() {
        //This method draws the menu

        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw transparent map in the background
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