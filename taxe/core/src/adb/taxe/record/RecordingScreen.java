package adb.taxe.record;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.GameScreen;
import fvs.taxe.GameSetupScreen;
import fvs.taxe.MusicPlayer;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import fvs.taxe.clickListener.StationClickListener;
import fvs.taxe.controller.ConnectionController;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.GoalController;
import fvs.taxe.controller.ObstacleController;
import fvs.taxe.controller.ResourceController;
import fvs.taxe.controller.RouteController;
import fvs.taxe.controller.StationController;
import fvs.taxe.controller.TopBarController;
import fvs.taxe.controller.TrainController;
import fvs.taxe.dialog.DialogEndGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.listeners.GameStateListener;
import gameLogic.listeners.TurnListener;
import gameLogic.map.BlankMapActor;
import gameLogic.map.Station;

public class RecordingScreen extends GameScreen{
	
	// Texture for playback controls
	Texture playbackTexture;
	
	// Rectangles from play, pause and forward buttons on playback control
	private Rectangle playBounds;
    private Rectangle pauseBounds;
    private Rectangle forwardBounds;
    private Vector3 touchPoint;
    private OrthographicCamera camera;

	/**The rate at which ticks build up. Ticks increases by the value of replaySpeed every frame*/
	public float replaySpeed = 1.0f;
	/**The number of ticks built up since the last event was injected*/
	public float ticks = 0.0f;
	/**The number of ticks that must build up for an event to be played*/
	public static final int tickClock = 60;
	/**The playback device that will be injecting events into the game*/
	public Playback eventPlayer;
	/**The initial speed the replaySpeed is set to when play is pressed*/
	public final static float baseRate = 1.0f;
	/**The rate at which replaySpeed increases when fast forward is pressed*/
	public final static float multiplier = 2.0f;
	/**The maximum speed of playback*/
	public final static float replaySpeedCap = 16.0f;
	/**Constructor constructs as normal, but specifically changes inputs so that all inputs come from the record Recorder instance
	 * @param game The instance of TaxeGame this screen is running from
	 * @param loadedGame The game that has been loaded
	 */
	public RecordingScreen(TaxeGame game, ArrayList<Event> events) {
		super(game);
		Game loadedGame = ((EmbeddedSaveData)events.get(0)).getGame();
		events.remove(0);
		instance = this;
		//Set up stage
		stage = new Stage();

		//Sets the skin
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		//Initialises the game
		Game.setInstance(loadedGame, false);
		gameLogic = loadedGame;
		context = new Context(stage, skin, game);
		
		//Redirect the input processor to a blank stage so that the player cannot interfere with the game
		Gdx.input.setInputProcessor(new Stage());
		
		//Set up the play back device
		eventPlayer = new Playback();
		eventPlayer.setEvents(events);
		eventPlayer.setPlaybackProcessor(stage);

		//Draw background
		dayMapTexture = new Texture(Gdx.files.internal("DaytimeMap.png"));
		nightMapTexture = new Texture(Gdx.files.internal("NightMap.png"));
		blankMapActor = new BlankMapActor();
		stage.addActor(blankMapActor);
		map = gameLogic.getMap();
		map.setMapActor(blankMapActor);
		
		// Playback Controls
		playbackTexture = new Texture(Gdx.files.internal("playback_controls.png"));
		
		// Set bounds of buttons
		playBounds = new Rectangle(325, 552, 45, 45);
        pauseBounds = new Rectangle(378, 552, 45, 45);
        forwardBounds = new Rectangle(432, 552, 45, 45);
        touchPoint = new Vector3();
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);

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

	    MusicPlayer.playTrack();
		
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
	
	/**This method contains all of the base logic that must be called every frame*/
	private void update() {
		if(!this.gameLogic.getState().equals(GameState.ANIMATING))
		{
			ticks = ticks + replaySpeed;
			if(ticks > tickClock)
			{
				ticks = 0;
				eventPlayer.nextEvent();
			}
		}
    	
        if (Gdx.input.justTouched()) {
        	//detects which area of the screen is touched
        	//If rectangles are touch then relevant action is taken
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
            	//Play has been pressed, so replay speed is set to the base speed
            	setReplaySpeed(baseRate);
            }
            if (pauseBounds.contains(touchPoint.x, touchPoint.y)) {
            	//Pause has been pressed, so the replay speed is set to 0 to pause the playback
            	setReplaySpeed(0);
            }
            if (forwardBounds.contains(touchPoint.x, touchPoint.y)) {
            	setReplaySpeed(replaySpeed * multiplier);
            }

       }
	}
	
	/**Sets the speed of replay and updates the UI (where 1.0f is 1 event per second*/
	private void setReplaySpeed(float newSpeed) {
		if(newSpeed > replaySpeedCap)
		{
			replaySpeed = replaySpeedCap;
		}
		else
		{
			replaySpeed = newSpeed;
		}
	}

	/**Draws any graphs exclusive to the replayScreen, e.g. the replay buttons*/
	private void draw() {
    	//This method draws the mainScreen Texture 
    	camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(playbackTexture, 305, 490);       

        game.fontLight.setScale(0.6f);
        TextBounds replaySpeedBounds = game.fontLight.getBounds("x" + replaySpeed);
        game.fontLight.setColor(Color.WHITE);
        game.fontLight.draw(game.batch, "x" + replaySpeed, 450 - replaySpeedBounds.width/2, 550 - replaySpeedBounds.height / 2);
        game.batch.end();
        game.fontLight.setScale(1.0f);
        
    }
	
	@Override
	public void render(float delta)
	{
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render(delta);
		
		update();
		draw();
	}

}
