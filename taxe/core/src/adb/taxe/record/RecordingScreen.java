package adb.taxe.record;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.GameScreen;
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

	/**The rate at which ticks build up. Ticks increases by the value of replaySpeed every frame*/
	public float replaySpeed = 1.0f;
	/**The number of ticks built up since the last event was injected*/
	public float ticks = 0.0f;
	/**The number of ticks that must build up for an event to be played*/
	public static final int tickClock = 60;
	/**The playback device that will be injecting events into the game*/
	public Playback eventPlayer;
	/**Constructor constructs as normal, but specifically changes inputs so that all inputs come from the record Recorder instance
	 * @param game The instance of TaxeGame this screen is running from
	 * @param loadedGame The game that has been loaded
	 */
	public RecordingScreen(TaxeGame game, ArrayList<Event> events) {
		super(game);
		Game loadedGame = ((EmbeddedSaveData)events.get(0)).getGame();
		events.remove(0);
		instance = this;
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
		};

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
	
	@Override
	public void render(float delta)
	{
		super.render(delta);
		if(!this.gameLogic.getState().equals(GameState.ANIMATING))
		{
			ticks = ticks + replaySpeed;
			if(ticks > tickClock)
			{
				ticks = 0;
				eventPlayer.nextEvent();
			}
		}
	}

}
