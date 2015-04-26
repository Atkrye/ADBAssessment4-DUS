package fvs.taxe;

import Util.Tuple;
import adb.taxe.record.Recorder;
import adb.taxe.record.RecordingWindow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.actor.SideBarActor;
import fvs.taxe.clickListener.StationClickListener;
import fvs.taxe.controller.*;
import fvs.taxe.dialog.DialogEndGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.listeners.GameStateListener;
import gameLogic.listeners.TurnListener;
import gameLogic.map.Map;
import gameLogic.map.BlankMapActor;
import gameLogic.map.Station;
import gameLogic.obstacle.Rumble;
import gameLogic.resource.Train;
import gameLogic.trong.TrongScreen;

/** This class displays the Game.java game state graphically to the player.*/
public class GameScreen extends ScreenAdapter {
	/**Stores the main instance of TaxeGame.java.*/
	public static TaxeGame game;
	
	
	public static GameScreen instance;
	
	/**Stores the instance of Stage.java that is used to hold the actors used in the Game, and is setup in the Class instantiation method.*/ 
	protected Stage stage;
	protected BlankMapActor blankMapActor;
	
	/**Stores the instance of Game.java used to hold the game variable's GameLogic. This variable exists as a reference point to the instance set in
     * the Game.java class, which can be accessed statically.
     */
	protected Game gameLogic;
    
    /**Stores resources for the UI, such as font, color etc.*/
    protected Skin skin;
    
    /**Holds an instance of the Game map. This exists as a reference to the gameLogic variable's map instance.*/
    protected Map map;
    
    /**This float tracks how long the game has been in the Animating state for. If it's value passes the constant ANIMATION_TIME then the Game stops animating and returns to it's normal state.*/
    private float timeAnimated = 0;
    
    /**This constant integer value holds how long the Game can stay in the animating state for before moving to it's next state.*/
    public static final int ANIMATION_TIME = 2;
    
    /**The instance of Tooltip used to display notifications to the player.*/
    protected Tooltip tooltip;
    
    /**The Context in which the game runs. This collects the Game and all of it's controllers.*/
    protected Context context;

    /**Controller for handling stations.*/
    protected StationController stationController;
    
    /**Controller for handling the graphical bar at the top of the game.*/
    protected TopBarController topBarController;
    
    /**Controller for handling resources.*/
    protected ResourceController resourceController;
    
    /**Controller for handling each of the players' goals.*/
    protected GoalController goalController;
    
    /**Controller for handling routing between stations.*/
    protected RouteController routeController;
    
    /**Controller for handling and placing obstacles.*/
    protected ObstacleController obstacleController;
	
	/** Controller for handling created and deleted connections */
	protected ConnectionController connectionController;
	
	/** Controller for handling the displaying of trains */
	protected TrainController trainController;
	
	/**Variable that is used to visibly "rumble" the game when an obstacle is placed.*/
	protected Rumble rumble;
	
	/**The trongscreen used for when a collision has occurred */
	public TrongScreen trongScreen = null;
	
	/** Texture for the day time map*/
	protected Texture dayMapTexture;
	
	/** Texture for the night time map*/
	protected Texture nightMapTexture;
	
	/** Boolean to say whether the screen has been initially loaded - used for ensuring actors added to stage only once */
	private boolean initiallyLoaded = false;
	
	
	
	/**The recorder that is attached to this game screen*/
	public Recorder record;
	
	/**Whether the mouse was keyed down on the last tick. Use for detecting events in recording*/
	private boolean isMouseDown = false;

	/**Implicit constructor for extension to RecordingScreen.java*/
	public GameScreen(TaxeGame game)
	{
		GameScreen.game = game;
	}
	
	public GameScreen(TaxeGame game, String p1, String p2, String MODE, int val)
	{
		this(game, Game.getInstance(p1, p2, MODE, val));
	}

	/**Instantiation method. Sets up the game using the passed TaxeGame argument. 
	 *@param game The instance of TaxeGame to be passed to the GameScreen to display.
	*/
	public GameScreen(TaxeGame game, Game loadedGame) {
		instance = this;
		GameScreen.game = game;
		//Set up stage with an adapted 
		stage = new Stage()
		{
			@Override
			public boolean keyDown(int keycode)
			{
				if(record.isRecording())
				{
					record.recordKeyPressed(keycode);
				}
				return super.keyDown(keycode);
			}
			
			@Override
			public boolean keyTyped(char key)
			{
				if(record.isRecording())
				{
					record.recordCharTyped(key);
				}
				return super.keyTyped(key);
			}
		};

		//Sets the skin
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		//Initialises the game
		Game.setInstance(loadedGame, false);
		gameLogic = loadedGame;
		context = new Context(stage, skin, game);
		Gdx.input.setInputProcessor(stage);

		//Draw background
		dayMapTexture = new Texture(Gdx.files.internal("DaytimeMap.png"));
		nightMapTexture = new Texture(Gdx.files.internal("NightMap.png"));
		blankMapActor = new BlankMapActor();
		stage.addActor(blankMapActor);
		map = gameLogic.getMap();
		map.setMapActor(blankMapActor);

		tooltip = new Tooltip(skin);
		stage.addActor(tooltip);

		//Initialises all of the controllers for the UI
		stationController = new StationController(context, tooltip);
		topBarController = new TopBarController(context);
		resourceController = new ResourceController(context);
		goalController = new GoalController(context);
		routeController = new RouteController(context);
		obstacleController = new ObstacleController(context);
		connectionController = new ConnectionController(context);
		trainController = new TrainController(context);
		context.setRouteController(routeController);
		context.setTopBarController(topBarController);
		context.setConnectionController(connectionController);
		
		
	     MusicPlayer.playTrack();
	     
		
		rumble = obstacleController.getRumble();
		
		record = new Recorder();

		show();
		
		Game.getInstance().getObstacleManager().activateIdleObstacles();
		//Adds a listener that displays a flash message whenever the turn ends
		gameLogic.getPlayerManager().subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
				//The game will not be set into the animating state for the first turn to prevent player 1 from gaining an inherent advantage by gaining an extra turn of movement.
				if (context.getGameLogic().getPlayerManager().getTurnNumber()!=1) {
					gameLogic.setState(GameState.ANIMATING);

					String str = "Time is Passing";
					topBarController.displayFlashMessage(str, Color.BLACK, 2f);
				}
			}
		});
		//Adds a listener that checks certain conditions at the end of every turn
		gameLogic.subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if ((gameLogic.getPlayerManager().getTurnNumber() == gameLogic.TOTAL_TURNS || (gameLogic.getPlayerManager().getCurrentPlayer().getScore() >= gameLogic.MAX_POINTS && gameLogic.MAX_POINTS != -1))  && state == GameState.NORMAL) {
					//If the game should end due to the turn number or points total then the appropriate dialog is displayed
					DialogEndGame dia = new DialogEndGame(context, gameLogic.getPlayerManager(), skin);
					dia.show(stage);
				} else if (gameLogic.getState() == GameState.ROUTING || gameLogic.getState() == GameState.PLACING_TRAIN) {
					//If the player is routing or place a train then the goals and nodes are colour coded
					goalController.setColours(StationController.colours);
				} else if (gameLogic.getState() == GameState.NORMAL) {
					//If the game state is normal then the goal colour are reset to grey
					goalController.setColours(new Color[3]);
				}

			}
		});

		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				// if the game is routing, set the route black when a new station is clicked
				if(gameLogic.getState() == GameState.ROUTING) {
					if (context.getGameLogic().getPlayerManager().isNight()) {
						routeController.drawRoute(Color.WHITE);
					} else {
						routeController.drawRoute(Color.BLACK);
					}
				}
			}
		});


	}

	// called every frame
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(!Game.getInstance().equals(gameLogic))
		{
			gameLogic = Game.getInstance();
			
			//Notify the Game once all controllers are set up
		}

		// ensure the correct texture is currently being selected
		Texture texture = dayMapTexture;
		if (context.getGameLogic().getPlayerManager().isNight()){
			texture = nightMapTexture;
		};

		// if the rumble has been activated, displace the correct texture
		if (rumble.time > 0){
			Vector2 mapPosition = rumble.tick(delta);
			game.batch.begin();
			game.batch.draw(texture, 290+ mapPosition.x, mapPosition.y);
		} else {
			game.batch.begin();
			game.batch.draw(texture, 290, 0);
		}
		game.batch.end();

		// if you are placing trains dont show the obstacles and show the goal highlights
		if (gameLogic.getState() == GameState.PLACING_TRAIN || gameLogic.getState() == GameState.ROUTING) {
			obstacleController.setObstacleVisibility(false);
			stationController.renderStationGoalHighlights();
			//This colours the start and end nodes of each goal to allow the player to easily see where they need to route
		} else if (!obstacleController.isVisible()) {
			obstacleController.setObstacleVisibility(true);
		}

		if (gameLogic.getState() == GameState.ANIMATING) {
			timeAnimated += delta;
			if (timeAnimated >= ANIMATION_TIME) {
				gameLogic.setState(GameState.NORMAL);
				timeAnimated = 0;
			}
		}

		// draw the line from train -mouse position if creating connection
		if (gameLogic.getState() == GameState.CREATING_CONNECTION){
			connectionController.drawMouse();
		}
		
		//Causes all the actors to perform their actions (i.e trains to move)
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();


		stationController.drawRoutingInfo(map.getConnections());

		if (goalController.exitPressed == false) {
			//Draw the number of trains at each station
			if (gameLogic.getState() == GameState.NORMAL || gameLogic.getState() == GameState.PLACING_TRAIN) {
				stationController.displayNumberOfTrainsAtStations();
			}
			game.batch.begin();

			
			
			if(Game.getInstance().getMode().equals(GameSetupScreen.MODETURNS))
			{
				//Bounds for text
				TextBounds lightBounds = game.fontTinyLight.getBounds("Turn");
				TextBounds boldBounds = game.fontTinyBold.getBounds(((gameLogic.getPlayerManager().getTurnNumber() + 1 < gameLogic.TOTAL_TURNS) ? gameLogic.getPlayerManager().getTurnNumber() + 1 : gameLogic.TOTAL_TURNS) + " / " + gameLogic.TOTAL_TURNS);

				// Draw 'Turn'
				game.fontTinyLight.setColor(Color.WHITE);
				game.fontTinyLight.draw(game.batch, "Turn", 290/2 - (lightBounds.width/2), 112);


				// Draw turn number i.e '1/30'
				game.fontTinyBold.setColor(Color.WHITE);
				game.fontTinyBold.draw(game.batch, ((gameLogic.getPlayerManager().getTurnNumber() + 1 < gameLogic.TOTAL_TURNS) ? gameLogic.getPlayerManager().getTurnNumber() + 1 : gameLogic.TOTAL_TURNS) + " / " + gameLogic.TOTAL_TURNS, 290/2 - (boldBounds.width/2), 85.0f);
				game.batch.end();
			}
			else if(Game.getInstance().getMode().equals(GameSetupScreen.MODEPOINTS))
			{
				//Bounds for text
				TextBounds lightBounds = game.fontTinyLight.getBounds("Target");
				TextBounds boldBounds = game.fontTinyBold.getBounds(String.valueOf(Game.getInstance().MAX_POINTS));
				// Draw 'Turn'
				game.fontTinyLight.setColor(Color.WHITE);
				game.fontTinyLight.draw(game.batch, "Target", 145 - (lightBounds.width/2), 112);

				// Draw turn number i.e '1/30'
				game.fontTinyBold.setColor(Color.WHITE);
				game.fontTinyBold.draw(game.batch, String.valueOf(Game.getInstance().MAX_POINTS), 290/2 - (boldBounds.width/2), 85.0f);
				game.batch.end();
			}

			goalController.drawHeaderText();
		}
		
		//Hard coded back button
		//if(Gdx.input.isKeyJustPressed(Keys.BACKSPACE))
		//{
		//	Game.undoTurn();
		//	System.out.println("UNDO!");
		//	RecordingWindow.createNewRecordingWindow();
		//}
		if(Gdx.input.isButtonPressed(Buttons.LEFT))
		{
			if(!isMouseDown && record.isRecording())
			{
				record.recordMouseClick(Gdx.input.getX(), Gdx.input.getY());
			}
			isMouseDown = true;
		}
		else
		{
			isMouseDown = false;
		}
	}

	@Override
	// Called when GameScreen becomes current screen of the game
	public void show() {
		if (!initiallyLoaded) {
			//We only render this once, this allows the buttons generated to be clickable.
			//Initially some of this functionality was in the draw() routine, but it was found that when the player clicked on a button a new one was rendered before the input could be handled
			//This is why the header texts and the buttons are rendered separately, to prevent these issues from occurring
			obstacleController.drawObstacles();
			stationController.addConnections(map.getConnections(), Color.GRAY);
			stationController.renderStations();
			obstacleController.drawObstacleEffects();
			// if game has been loaded, trainActors must be set up
			if (gameLogic.getPlayerManager().getTrainsToAdd().isEmpty()) {
				trainController.setupTrainActors();
			}
			trainController.drawTrains(this.stage);
			topBarController.drawBackground();
			topBarController.drawLabels();
			topBarController.addEndTurnButton();
			drawSidebar();
			resourceController.drawPlayerResources(gameLogic.getPlayerManager().getCurrentPlayer());
			goalController.showCurrentPlayerGoals();
			goalController.showControls();
			initiallyLoaded = true;
			gameLogic.getPlayerManager().finishLoad(context);
		}
	}

	/** Add the sidebarActor to the stage*/
	private void drawSidebar() {
		SideBarActor sb = new SideBarActor();
		context.getStage().addActor(sb);
	}

	@Override
	public void dispose() {
		dayMapTexture.dispose();
		stage.dispose();
	}

	public void setScreen(ScreenAdapter screen) {
		game.setScreen(screen);
	}

	public static Tuple<TaxeGame, GameScreen> getInstance() {
		return new Tuple<TaxeGame, GameScreen>(game, instance);
	}
	
	public Context getContext()
	{
		return context;
	}

	@Override
	public void resume() {
		//if (trongScreen == null) {
		//context.getGameLogic().setState(GameState.NORMAL);
		trongScreen = null;
		//}
		super.resume();
	}

	public static TrongScreen makeTrongGame(Train t1, Train t2){
		return new TrongScreen(game, t1, t2);
	}
	
	public void startRecording()
	{
		record.startRecording();
	}
	
	public void stopRecording()
	{
		record.stopRecording();
	}

	public boolean isRecording() {
		return record.isRecording();
	}
}