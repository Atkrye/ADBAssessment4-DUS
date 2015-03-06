package gameLogic.trong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.TaxeGame;
import gameLogic.resource.Train;

public class TrongScreen extends ScreenAdapter{
    final private TaxeGame game;
    private ScreenAdapter nextScreen;
    private Stage stage;
    private Skin skin;
    private PaddleActor paddle1;
    private PaddleActor paddle2;
    private BarActor topBar;
    private BarActor botBar;
    private BallActor ball;
    
    //These 4 variables describe the proportion of the screen dedicated to the game
    public static final float gameBottom = 0.1f;
    public static final float gameTop = 0.7f;
    public static final float gameLeft = 0.1f;
    public static final float gameRight = 0.9f;
    
	public TrongScreen(TaxeGame game, Train t1, Train t2)
	{
		this.game = game;
        stage = new Stage();

        //Sets the skin
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        paddle1 = new PaddleActor(t1, true);
        paddle2 = new PaddleActor(t2, false);
        stage.addActor(paddle1);
        stage.addActor(paddle2);
        stage.addActor(paddle1.getTrainIcon());
        stage.addActor(paddle2.getTrainIcon());
        topBar = new BarActor(true);
        botBar = new BarActor(false);
        stage.addActor(topBar);
        stage.addActor(botBar);
        ball = new BallActor(paddle1, paddle2);
        stage.addActor(ball);
	}
	
	public void finish()
	{
		game.setScreen(nextScreen);
		nextScreen.resume();
	}
	
	public void setNextScreen(ScreenAdapter nextScreen)
	{
		this.nextScreen = nextScreen;
		nextScreen.pause();
	}
	
	// called every frame
    @Override
    public void render(float delta) {
    	update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }
    
    public void update()
    {
    	if(Gdx.input.isKeyPressed(Keys.W)) 
    	   paddle1.up();
    	if(Gdx.input.isKeyPressed(Keys.S)) 
    	   paddle1.down();
    	if(Gdx.input.isKeyPressed(Keys.UP)) 
     	   paddle2.up();
     	if(Gdx.input.isKeyPressed(Keys.DOWN)) 
     	   paddle2.down();
     	if(Gdx.input.isKeyPressed(Keys.ESCAPE))
     	{
     		finish();
     	}
     	ball.update();
     	//Check win conditions
     	if(ball.getX() < 0)
     	{
     		paddle1.getTrain().getPlayer().removeResource(paddle1.getTrain());
     		paddle1.getTrain().getActor().remove();
     		finish();
     	}

     	if(ball.getX() > TaxeGame.WIDTH)
     	{
     		paddle2.getTrain().getPlayer().removeResource(paddle2.getTrain());
     		paddle2.getTrain().getActor().remove();
     		finish();
     	}
    }
}
