package gameLogic.player;

import fvs.taxe.GameScreen;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.ObstacleController;
import fvs.taxe.controller.TrainController;
import fvs.taxe.dialog.DialogTurnSkipped;
import gameLogic.Game;
import gameLogic.goal.Goal;
import gameLogic.listeners.DayChangedListener;
import gameLogic.listeners.TurnListener;
import gameLogic.listeners.PlayerChangedListener;
import gameLogic.map.IPositionable;
import gameLogic.map.Map;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Skip;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.JsonValue;


public class PlayerManager {
	private ArrayList<Player> players = new ArrayList<Player>();
    private int currentTurn = 0;
    private int turnNumber = 0;
    private List<TurnListener> turnListeners = new ArrayList<TurnListener>();
    private List<PlayerChangedListener> playerListeners = new ArrayList<PlayerChangedListener>();
	private List<DayChangedListener> dayListeners = new ArrayList<DayChangedListener>();
    private boolean isNight = false;
    private TrainController t;
    private ArrayList<Train> trainsToAdd = new ArrayList<Train>();
    /**Instantiation method for when creating a player manager from a saved game's Json data
     * @param jsonData The json data from the save game
     * @param isNight Boolean: Whether the loaded game is a night time or daytime state. true = night, false = day
     * @param turn 
     */
    public PlayerManager(JsonValue jsonData, boolean isNight, int turn, Map map)
    {
    	//Use current Obstacle Controller context
    	t = new TrainController(ObstacleController.getContext());
    	System.out.println("COntext: " + ObstacleController.getContext());
    	this.turnNumber = turn;
    	this.isNight = isNight;
    	JsonValue player1Data = jsonData.get("Player1");
    	JsonValue player2Data = jsonData.get("Player2");
    	Player p1 = loadPlayer(player1Data, 1, map, t);
    	Player p2 = loadPlayer(player2Data, 2, map, t);
    	players.add(p1);
    	players.add(p2);
    }
    
    public void finishLoad()
    {
    	for(Train tr : trainsToAdd)
    	{
    		t.setTrainsVisible(tr, true);
    	}
    }
    
    public ArrayList<Train> getTrainsToAdd()
    {
    	return trainsToAdd;
    }
    
    /**Empty instantiation for new game*/
    public PlayerManager() {
	}

	/**Method used to generate a player from save game Json Data
     * @param jsonData The data containing the player's information
     */
    public Player loadPlayer(JsonValue jsonData, int playerNumber, Map m, TrainController trainController)
    {
    	String name = jsonData.getString("Name");
    	int score = jsonData.getInt("Score");
    	boolean skip = jsonData.getBoolean("Skip");
    	boolean active = jsonData.getBoolean("Active");
    	Player p = new Player(this, playerNumber, name);
    	if(active)
    	{
    		this.currentTurn = playerNumber -1;
    	}
    	p.setSkip(skip);
    	p.setScore(score);
    	trainController.setupTrainActors();
    	for(JsonValue resource = jsonData.get("Resources").child(); resource != null; resource = resource.next())
    	{
    		if(resource.getString("DataType").equals("Skip"))
    		{
    			p.addResource(new Skip());
    		}
    		else
    		{
    			String rName = resource.getString("Name");
    			int speed = resource.getInt("Speed");
    			String rImage = resource.getString("Image");
    			Train t = new Train(rName, rImage, speed);
    			t.setPlayer(p);
    			p.addResource(t);
    			String xS = resource.getString("x");
    			if(!xS.equals("Empty"))
    			{
    				int x = Integer.valueOf(resource.getString("x"));
    				int y = Integer.valueOf(resource.getString("y"));
    				t.setPosition(new Position(x, y));
					TrainActor trainActor = trainController.renderTrain(t);
					t.setActor(trainActor);
    				//There is an actor on the screen
    				if(x == -1 && y == -1)
    				{
    					trainsToAdd.add(t);
    					t.getActor().setVisible(true);
    					t.getActor().getBounds().setRotation(resource.getFloat("actorRot"));
    					t.getActor().getBounds().setPosition(resource.getFloat("actorX"), resource.getFloat("actorY"));
    				}
    			}
    		}
    	}
    	for(JsonValue goal = jsonData.get("Goals").child(); goal != null; goal = goal.next())
    	{
    		Station origin = m.getStationByName(goal.getString("Origin"));
    		Station destination = m.getStationByName(goal.getString("Destination"));
    		Station intermediary = m.getStationByName(goal.getString("Intermediate"));
    		Train t = null;
    		if(!goal.getString("Train").equals("None"))
    		{
    			t = ResourceManager.global.getTrainByName(goal.getString("Train"));
    		}
    		int turn = goal.getInt("Turn");
    		int turnCount = goal.getInt("TurnCount");
    		int scoreV = goal.getInt("ScoreValue");
    		int bonusV = goal.getInt("BonusValue");
    		Goal g = new Goal(origin, destination, intermediary, turn, turnCount, scoreV, bonusV, t);
    		p.addGoal(g);
    	}
    	
    	return p;
    }
    
    public void createPlayers(int count) {
        //Initialises all players (set by count)
        for (int i = 0; i < count; i++) {
            players.add(new Player(this, i + 1, ""));
        }
    }
    
    public void createPlayers(String p1Name, String p2Name) {
        players.add(new Player(this, 1, p1Name));
        players.add(new Player(this, 2, p2Name));
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurn);
    }
    
    public Player getOtherPlayer() {
    	if (currentTurn == 0) {
    		return players.get(currentTurn + 1);
    	}
    	else {
    		return players.get(currentTurn - 1);
    	}
    }

    public List<Player> getAllPlayers() {
        return players;
    }

    public void turnOver(Context context) {
        //Swaps current player
        //This is for two players, if you wish to add more players you will need to increment current turn by 1 and then perform mod MaxPlayers on the result.
        currentTurn = currentTurn == 1 ? 0 : 1;

        //Calls turn listeners
        turnChanged();
        playerChanged();

        //Checks whether or not the turn is being skipped, if it is then it informs the player
        if (this.getCurrentPlayer().getSkip()) {
            DialogTurnSkipped dia = new DialogTurnSkipped(context, context.getSkin());
            dia.show(context.getStage());
            this.getCurrentPlayer().setSkip(false);
        }
    }


    public void subscribeTurnChanged(TurnListener listener) {
        turnListeners.add(listener);
    }

    private void turnChanged() {
    	Game.storeLastTurn();
        turnNumber++;
        if (turnNumber%4 == 0){
        	isNight = !isNight;
        	dayChanged();
        }
        
		// reverse iterate to give priority to calls from Game() (obstacles)
		for(int i = 0; i< turnListeners.size(); i++) {
			turnListeners.get(turnListeners.size()-1-i).changed();
		}
    }

    public void subscribePlayerChanged(PlayerChangedListener listener) {
        playerListeners.add(listener);
    }

    // very general event which is fired when player's goals / resources are changed
    public void playerChanged() {
        for (PlayerChangedListener listener : playerListeners) {
            listener.changed();
        }
    }
    
    public void subscribeDayChanged(DayChangedListener listener){
    	dayListeners.add(listener);
    }
    
    public void dayChanged() {
    	for (DayChangedListener listener : dayListeners) {
            listener.changed(isNight);
        }
    }

    public int getTurnNumber() {
        return turnNumber;
    }

	public boolean isNight() {
		return isNight;
	}

}
