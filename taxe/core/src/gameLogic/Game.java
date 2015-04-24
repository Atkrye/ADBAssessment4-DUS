package gameLogic;

import fvs.taxe.GameScreen;
import fvs.taxe.GameSetupScreen;
import gameLogic.goal.GoalManager;
import gameLogic.listeners.GameStateListener;
import gameLogic.listeners.TurnListener;
import gameLogic.map.Map;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleManager;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Util.Tuple;
import adb.taxe.record.ObstacleEvent;
import adb.taxe.record.RecordingScreen;
import adb.taxe.record.SaveManager;

import com.badlogic.gdx.math.MathUtils;

/**Main Game class of the Game. Handles all of the game logic.*/
public class Game{
	public static final boolean trongEnabled = true;
	//This is sort of a super-class that can be accessed throughout the system as many of its methods are static
	//This is a useful tool to exploit to make implementing certain features easier
	/**The instance that the game is running in.*/
	private static Game instance;

	/**The game's PlayerManager that handles both of the players.*/
	private PlayerManager playerManager;

	/**The game's GoalManager that handles goals for the players.*/
	private GoalManager goalManager;

	/**The game's ResourceManager that handles resources for the players.*/
	private ResourceManager resourceManager;

	/**The game's ObstacleManager that handles Obstacles in the game.*/
	private ObstacleManager obstacleManager;

	/**The game map for this instance.*/
	private Map map;

	/**The game state.*/
	private GameState state;

	/**List of listeners that listen to changes in game state.*/
	private List<GameStateListener> gameStateListeners = new ArrayList<GameStateListener>();

	/**List of listeners that listen to changes in obstacles.*/
	private List<ObstacleListener> obstacleListeners = new ArrayList<ObstacleListener>();

	/** Mode saying whether the game is finished by number of turns or number of points */
	private String MODE;

	private static String previousTurn = "";
	private static String nextPreviousTurn = "";
	//Default values
	/** The number of turns until the game is finished */
	public int TOTAL_TURNS = 30;

	/**The score a player must reach to win the game.*/
	public int MAX_POINTS = 3000;

	/**The Instantiation method, sets up the players and game listeners.*/
	private Game(String p1Name, String p2Name, String MODE, int count) {
		this.MODE = MODE;
		if(MODE.equals(GameSetupScreen.MODETURNS))
		{
			TOTAL_TURNS = count;
			MAX_POINTS = -1;
		}
		if(MODE.equals(GameSetupScreen.MODEPOINTS))
		{
			MAX_POINTS = count;
			TOTAL_TURNS = -1;
		}
		//Creates players
		playerManager = new PlayerManager();
		playerManager.createPlayers(p1Name, p2Name);

		//Give them starting resources and goals
		resourceManager = new ResourceManager();
		goalManager = new GoalManager(resourceManager);

		map = new Map(false, null);
		obstacleManager = new ObstacleManager(map);


		state = GameState.NORMAL;

		//Adds all the subscriptions to the game which gives players resources and goals at the start of each turn.
		//Also decrements all connections and blocks a random one
		//The checking for whether a turn is being skipped is handled inside the methods, this just always calls them
		playerManager.subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
				Player currentPlayer = playerManager.getCurrentPlayer();
				goalManager.addRandomGoalToPlayer(currentPlayer, false);
				resourceManager.addRandomResourceToPlayer(currentPlayer, false);
				resourceManager.addRandomResourceToPlayer(currentPlayer, false);
				if (playerManager.getTurnNumber() != 1) {
					// obstacles only occur from first turn onwards
					calculateObstacles();
					decreaseObstacleTime();
				}
			}
		});
	}

	/** Constructor to be used when loading a game from a file */
	public Game(String MODE, int totalTurns, int maxPoints, PlayerManager pm, Map m, ObstacleManager om, final boolean isRecording) {
		this.MODE = MODE;
		TOTAL_TURNS = totalTurns;
		MAX_POINTS = maxPoints;
		//Creates players
		playerManager = pm;
		resourceManager = new ResourceManager();
		goalManager = new GoalManager(resourceManager);

		map = m;
		obstacleManager = om;


		state = GameState.NORMAL;

		//Adds all the subscriptions to the game which gives players resources and goals at the start of each turn.
		//Also decrements all connections and blocks a random one
		//The checking for whether a turn is being skipped is handled inside the methods, this just always calls them
		playerManager.subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
				Player currentPlayer = playerManager.getCurrentPlayer();
				goalManager.addRandomGoalToPlayer(currentPlayer, false);
				resourceManager.addRandomResourceToPlayer(currentPlayer, false);
				resourceManager.addRandomResourceToPlayer(currentPlayer, false);
				if (playerManager.getTurnNumber() != 1) {
					// obstacles only occur from first turn onwards
					calculateObstacles();
					decreaseObstacleTime();
				}
			}
		});
	}

	public void save()
	{
		SaveManager.save();
	}

	/**Returns the main game instance.*/
	public static Game getInstance(String p1Name, String p2Name, String MODE, int count) {
		if (instance == null) {
			instance = new Game(p1Name, p2Name, MODE, count);
			// initialisePlayers gives them a goal, and the GoalManager requires an instance of game to exist so this
			// method can't be called in the constructor
			instance.initialisePlayers();
		}
		return instance;
	}

	/**Sets up the players. Only the first player is given goals and resources initially.*/
	private void initialisePlayers() {
		Player player = playerManager.getAllPlayers().get(0);
		goalManager.addRandomGoalToPlayer(player, true);
		resourceManager.addRandomResourceToPlayer(player, true);
		resourceManager.addRandomResourceToPlayer(player, true);
	}

	public String getMode()
	{
		return MODE;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public GoalManager getGoalManager() {
		return goalManager;
	}

	public ObstacleManager getObstacleManager(){
		return obstacleManager;
	}

	public Map getMap() {
		return map;
	}

	public GameState getState() {
		return state;
	}

	/**Sets the GameState of the Game. Listeners are notified using stateChanged().*/
	public void setState(GameState state) {
		this.state = state;
		//Informs all listeners that the state has changed
		stateChanged();
	}

	public void subscribeStateChanged(GameStateListener listener) {
		gameStateListeners.add(listener);
	}

	private void stateChanged() {
		for (GameStateListener listener : gameStateListeners) {
			listener.changed(state);
		}
	}

	/**This method is called whenever an obstacle starts. All listeners are notified that this has happened.*/
	public void obstacleStarted(Obstacle obstacle) {
		for (ObstacleListener listener : obstacleListeners) {
			listener.started(obstacle);
		}
	}

	/**This method is called whenever an obstacle end. All listeners are notified that this has happened.*/
	private void obstacleEnded(Obstacle obstacle) {
		for (ObstacleListener listener : obstacleListeners) {
			listener.ended(obstacle);
		}
	}

	public void subscribeObstacleChanged(ObstacleListener obstacleListener) {
		obstacleListeners.add(obstacleListener);
	}
	
	/**This method causes one obstacle to happen at random, notifying the listeners.*/
	private void calculateObstacles() {
		//Choose where we get our obstacle based on whether the GameScreen is a recording
		if(!GameScreen.instance.getClass().equals(RecordingScreen.class))
		{
			//We are in a normal GameScreen so we calculate our obstacle as normal
			// randomly choose one obstacle, then make the obstacle happen with its associated probability
			ArrayList<Tuple<Obstacle, Float>> obstacles = obstacleManager.getObstacles();
			int index = MathUtils.random(obstacles.size()-1);


			Tuple<Obstacle, Float> obstacleProbPair = obstacles.get(index);
			boolean obstacleOccured = MathUtils.randomBoolean(obstacleProbPair.getSecond());
			Obstacle obstacle = obstacleProbPair.getFirst();

			// if it has occurred and isnt already active, start the obstacle
			if(obstacleOccured && !obstacle.isActive()){
				obstacleStarted(obstacle);

				//If we're recording, record the obstacle start
				if(GameScreen.instance.isRecording())
				{
					GameScreen.instance.record.recordObstacle(obstacle);
				}
			}
			//We need to record that no obstacle occured if the obstacle was not started
			else if(GameScreen.instance.isRecording())
			{
				GameScreen.instance.record.recordObstacle();
			}
		}
		else
		{
			//We are in a recording and need to load our Obstacle from the playback

			RecordingScreen rec = (RecordingScreen)GameScreen.instance;
			ObstacleEvent obstacleEvent = rec.eventPlayer.getNextObstacleEvent();
			for(Tuple<Obstacle, Float> o : obstacleManager.getObstacles())
			{
				Obstacle foundObstacle = o.getFirst();
				if(foundObstacle.getType().toString().equals(obstacleEvent.getType()) && foundObstacle.getStation().getName().equals(obstacleEvent.getStation()))
				{
					obstacleStarted(foundObstacle);
				}
			}
		}
	}

	/**This method decreases the remaining duration of any remaining obstacles by 1 turn. If the duration has reached 0, the obstacle is removed and all listeners are notified.*/
	private void decreaseObstacleTime() {
		// decreases any active obstacles time left active by 1
		ArrayList<Tuple<Obstacle, Float>> obstacles = obstacleManager.getObstacles();
		for (int i = 0; i< obstacles.size(); i++) {
			Obstacle obstacle = obstacles.get(i).getFirst();
			if (obstacle.isActive()) {
				boolean isTimeLeft = obstacle.decreaseTimeLeft();
				if (!isTimeLeft) {
					// if the time left = 0, then deactivate the obstacle
					obstacleEnded(obstacle);
				}
			}
		}
	}

	public static Game getInstance() {
		return instance;
	}

	public static void setInstance(Game setupGame, boolean newGameScreen) {
		if(newGameScreen)
		{
			GameScreen.getInstance().getSecond().dispose();
			GameScreen.getInstance().getFirst().setScreen(new GameScreen(GameScreen.getInstance().getFirst(), setupGame));
		}
		else
		{
			instance = setupGame;
		}
	}

	//This method clones the current instance and stores it
	public static void storeLastTurn()
	{
		System.out.println("Stored");
		if(nextPreviousTurn != "")
		{
			previousTurn = nextPreviousTurn;
		}
		nextPreviousTurn = SaveManager.getSaveText();
	}

	//If we can, we step the game back a turn using this method
	public static void undoTurn()
	{
		if(previousTurn != "")
		{
			setInstance(SaveManager.loadFromText(previousTurn), true);
			previousTurn = "";
		}
	}

	public void notifyStarted() {
		getObstacleManager().activateIdleObstacles();
	}


}