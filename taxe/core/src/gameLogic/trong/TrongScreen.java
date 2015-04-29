package gameLogic.trong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.MusicPlayer;
import fvs.taxe.GameScreen;
import fvs.taxe.TaxeGame;
import gameLogic.resource.Train;

/**This class is a type of GameScreen specifically for the Trong (Train Pong) implementation. It pauses the 
 * original Game while the point plays out. Once the game is done, it moves the Screen along to the next
 * Screen that was assigned to it*/
public class TrongScreen extends ScreenAdapter{
	/**The instance of TaxeGame that controls the screens*/
    final private TaxeGame game;
    /**The next screen that should be loaded once the point of pong has completed*/
    private ScreenAdapter nextScreen;
    /**The stage used to visually display the actors*/
    private Stage stage;
    /**The skin to be used in the Trong game. Currently this is unused, but future UI changes may use it*/
    @SuppressWarnings("unused")
	private Skin skin;
    /**The left hand paddle in the pong point*/
    private PaddleActor paddle1;
    /**The right hand paddle in the pong point*/
    private PaddleActor paddle2;
    /**The wall along the top of the game*/
    private BarActor topBar;
    /**The wall along the bottom of the game*/
    private BarActor botBar;
    /**The ball moving around the game*/
    private BallActor ball;
    
    /**The bottom-most dimension of the game - 0.1 of the screen is the lowest the ball can go*/
    public static final float gameBottom = 0.1f;
    /**The top-most dimension of the game - 0.7 of the screen is the highest the ball can go*/
    public static final float gameTop = 0.7f;
    /**The left-most dimension of the game - 0.1 of the screen is the furthest left the ball can go*/
    public static final float gameLeft = 0.1f;
    /**The right-most dimension of the game - 0.9 of the screen is the furthest right the ball can go*/
    public static final float gameRight = 0.9f;
    
    /**The instantiation method sets up the game
     * @param game The TaxeGame instance used to set screens
     * @param t1 The first train involved in the collision
     * @param t2 The second train involved in the collision
     */
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
        System.out.println("play");
        MusicPlayer.stopTrack1();
        MusicPlayer.playTrack2();
	}
	
	/**This method is called once the winner has been decided and finalised*/
	public void finish()
	{	
		MusicPlayer.stopTrack2();
		MusicPlayer.resumeTrack1();
		game.setScreen(nextScreen);
		nextScreen.resume();
	}
	
	/**This method sets up the screen to be shown once this point of pong has completed*/
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
        
        //Draw our UI
        game.batch.begin();
        game.fontLight.setColor(Color.WHITE);
        
        //Draw the screen title
        game.fontLight.setScale(0.8f);
        TextBounds infoBounds = game.fontLight.getBounds("Play to survive the collision!");
        game.fontLight.draw(game.batch, "Play to survive the collision!", TaxeGame.WIDTH / 2 - infoBounds.width/2, TaxeGame.HEIGHT - infoBounds.height / 2);
        
        //Draw the player instructions
        game.fontLight.setScale(0.6f);
        String info2Text = paddle1.getTrain().getPlayer().getName() + " use W and S. " + paddle2.getTrain().getPlayer().getName() + " use UP and DOWN.";
        TextBounds info2Bounds = game.fontLight.getBounds(info2Text);
        game.fontLight.draw(game.batch, info2Text, TaxeGame.WIDTH / 2 - info2Bounds.width/2, TaxeGame.HEIGHT * 9/10 - info2Bounds.height / 2);
        game.fontLight.setScale(1.0f);
        game.batch.end();
    }
    
    /**This method is called every frame. It updates the controls of the paddles*/
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
     		recordCollision(paddle1.getTrain());
     	}

     	if(ball.getX() > TaxeGame.WIDTH)
     	{   
     		paddle2.getTrain().getPlayer().removeResource(paddle2.getTrain());
     		paddle2.getTrain().getActor().remove();
     		finish();
     		recordCollision(paddle2.getTrain());
     	}
    }
    
    /**This method is called once the point has finished. It attempts to record the collision if the game is recording
     * @param loser The losing train of the collision
     */
    public void recordCollision(Train loser)
    {
    	if(GameScreen.instance.isRecording())
    	{
    		GameScreen.instance.record.recordCollision(paddle1.getTrain().getID(), paddle2.getTrain().getID(), loser.getID());
    	}
    }
}
