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


public class GameScreen extends ScreenAdapter {
	public static TaxeGame game;
	public static GameScreen instance;
	protected Stage stage;
	protected BlankMapActor blankMapActor;
	protected Game gameLogic;
	protected Skin skin;
	protected Map map;
	private float timeAnimated = 0;
	public static final int ANIMATION_TIME = 2;
	protected Tooltip tooltip;
	protected Context context;

	protected StationController stationController;
	protected TopBarController topBarController;
	protected ResourceController resourceController;
	protected GoalController goalController;
	protected RouteController routeController;
	protected ObstacleController obstacleController;
	protected Rumble rumble;
	public TrongScreen trongScreen = null;
	protected ConnectionController connectionController;
	protected Texture dayMapTexture;
	protected Texture nightMapTexture;
	private int i;
	protected TrainController trainController;
	
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
					topBarController.displayFlashMessage(str, Color.GREEN, Color.BLACK, 2f);
				}
			}
		});
		//Adds a listener that checks certain conditions at the end of every turn
		gameLogic.subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if ((gameLogic.getPlayerManager().getTurnNumber() == gameLogic.TOTAL_TURNS || (gameLogic.getPlayerManager().getCurrentPlayer().getScore() >= gameLogic.MAX_POINTS && gameLogic.MAX_POINTS != -1))  && state == GameState.NORMAL) {
					//If the game should end due to the turn number or points total then the appropriate dialog is displayed
					DialogEndGame dia = new DialogEndGame(context, GameScreen.game, gameLogic.getPlayerManager(), skin);
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

		Texture texture = dayMapTexture;
		
		if (context.getGameLogic().getPlayerManager().isNight()){
			texture = nightMapTexture;
		};

		if (rumble.time > 0){
			Vector2 mapPosition = rumble.tick(delta);
			game.batch.begin();
			game.batch.draw(texture, 290+ mapPosition.x, mapPosition.y);
		} else {
			game.batch.begin();
			game.batch.draw(texture, 290, 0);
		}
		game.batch.end();


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

		if (gameLogic.getState() == GameState.CREATING_CONNECTION){
			connectionController.drawMouse();
		}
		
		//Causes all the actors to perform their actions (i.e trains to move)
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();


		stationController.drawRoutingInfo(map.getConnections());
		//Draw the number of trains at each station
		if (gameLogic.getState() == GameState.NORMAL || gameLogic.getState() == GameState.PLACING_TRAIN) {
			stationController.displayNumberOfTrainsAtStations();
		}

		if (goalController.exitPressed == false) {

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
		if (i == 0) {
			//We only render this once a turn, this allows the buttons generated to be clickable.
			//Initially some of this functionality was in the draw() routine, but it was found that when the player clicked on a button a new one was rendered before the input could be handled
			//This is why the header texts and the buttons are rendered separately, to prevent these issues from occurring
			obstacleController.drawObstacles();
			stationController.addConnections(map.getConnections(), Color.GRAY);
			stationController.renderStations();
			obstacleController.drawObstacleEffects();
			if (gameLogic.getPlayerManager().getTrainsToAdd().isEmpty()) {
				trainController.setupTrainActors();
			}
			trainController.drawTrains(this.stage);
			//We have to add in any necessary trains loaded to the train controller now
			/*for(Train t : gameLogic.getPlayerManager().getTrainsToAdd())
			{
				System.out.println("Train Add!");
				trainController.addTrainToActors(t);
			}*/
			topBarController.drawBackground();
			topBarController.drawLabels();
			topBarController.addEndTurnButton();
			//connectionController.drawStationNamingDialog();
			drawSidebar();
			resourceController.drawPlayerResources(gameLogic.getPlayerManager().getCurrentPlayer());
			goalController.showCurrentPlayerGoals();
			goalController.showControls();
			i+=1;
			gameLogic.getPlayerManager().finishLoad(context);
		}
	}

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