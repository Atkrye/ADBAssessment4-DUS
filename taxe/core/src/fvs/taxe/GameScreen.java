package fvs.taxe;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;




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
	private static TaxeGame game;
	public static GameScreen instance;
	private Stage stage;
	private BlankMapActor blankMapActor;
	private Game gameLogic;
	private Skin skin;
	private Map map;
	private float timeAnimated = 0;
	public static final int ANIMATION_TIME = 2;
	private Tooltip tooltip;
	private Context context;

	private StationController stationController;
	private TopBarController topBarController;
	private ResourceController resourceController;
	private GoalController goalController;
	private RouteController routeController;
	private ObstacleController obstacleController;
	private Rumble rumble;
	public TrongScreen trongScreen = null;
	private ConnectionController connectionController;
	private Texture mapTexture;

	public GameScreen(TaxeGame game) {
        instance = this;
		GameScreen.game = game;
		stage = new Stage();

		//Sets the skin
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		//Initialises the game
		gameLogic = Game.getInstance();
		context = new Context(stage, skin, game, gameLogic);
		Gdx.input.setInputProcessor(stage);

		//Draw background
		mapTexture = new Texture(Gdx.files.internal("gamemap.png"));
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
		context.setRouteController(routeController);
		context.setTopBarController(topBarController);
		context.setConnectionController(connectionController);

		rumble = obstacleController.getRumble();

		//Adds a listener that displays a flash message whenever the turn ends
		gameLogic.getPlayerManager().subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
				//The game will not be set into the animating state for the first turn to prevent player 1 from gaining an inherent advantage by gaining an extra turn of movement.
				if (context.getGameLogic().getPlayerManager().getTurnNumber()!=1) {
					gameLogic.setState(GameState.ANIMATING);
					topBarController.displayFlashMessage("Time is passing...", Color.GREEN, Color.BLACK, 2f);
				}
			}
		});

		//Adds a listener that checks certain conditions at the end of every turn
		gameLogic.subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if ((gameLogic.getPlayerManager().getTurnNumber() == gameLogic.TOTAL_TURNS || gameLogic.getPlayerManager().getCurrentPlayer().getScore() >= gameLogic.MAX_POINTS) && state == GameState.NORMAL) {
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
					routeController.drawRoute(Color.BLACK);
				}
			}
		});
	}

	// called every frame
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (rumble.time > 0){
			Vector2 mapPosition = rumble.tick(delta);
			game.batch.begin();
			game.batch.draw(mapTexture, mapPosition.x, mapPosition.y);
			game.batch.end();
			
		} else {
			game.batch.begin();
			game.batch.draw(mapTexture, 0, 0);
			game.batch.end();
		}

		if (gameLogic.getState() == GameState.PLACING_TRAIN || gameLogic.getState() == GameState.ROUTING) {
			stationController.renderStationGoalHighlights();
			//This colours the start and end nodes of each goal to allow the player to easily see where they need to route
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

		game.batch.begin();
		//If statement checks whether the turn is above 30, if it is then display 30 anyway
		game.fontSmall.draw(game.batch, "Turn " + ((gameLogic.getPlayerManager().getTurnNumber() + 1 < gameLogic.TOTAL_TURNS) ? gameLogic.getPlayerManager().getTurnNumber() + 1 : gameLogic.TOTAL_TURNS) + "/" + gameLogic.TOTAL_TURNS, (float) TaxeGame.WIDTH - 90.0f, 20.0f);
		game.batch.end();

		resourceController.drawHeaderText();
		goalController.drawHeaderText();
	}

	@Override
	// Called when GameScreen becomes current screen of the game
	public void show() {
		//We only render this once a turn, this allows the buttons generated to be clickable.
		//Initially some of this functionality was in the draw() routine, but it was found that when the player clicked on a button a new one was rendered before the input could be handled
		//This is why the header texts and the buttons are rendered separately, to prevent these issues from occuring
		obstacleController.drawObstacles();
		stationController.addConnections(map.getConnections(), Color.GRAY);
		stationController.renderStations();
		obstacleController.drawObstacleEffects();
		resourceController.drawPlayerResources(gameLogic.getPlayerManager().getCurrentPlayer());
		topBarController.drawBackground();
		topBarController.drawLabels();
		topBarController.addEndTurnButton();
		goalController.showCurrentPlayerGoals();
	}

	@Override
	public void dispose() {
		mapTexture.dispose();
		stage.dispose();
	}

	public void setScreen(ScreenAdapter screen) {
		game.setScreen(screen);
	}

	public static Tuple<TaxeGame, GameScreen> getInstance() {
		return new Tuple<TaxeGame, GameScreen>(game, instance);
	}

	@Override
	public void resume() {
		trongScreen = null;
		super.resume();
	}

	public static TrongScreen makeTrongGame(Train t1, Train t2){
		return new TrongScreen(game, t1, t2);
	}
}