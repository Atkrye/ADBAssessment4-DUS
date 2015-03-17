package gameLogic.player;

import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DialogTurnSkipped;
import gameLogic.listeners.DayChangedListener;
import gameLogic.listeners.TurnListener;
import gameLogic.listeners.PlayerChangedListener;
import gameLogic.player.Player;

import java.util.ArrayList;
import java.util.List;


public class PlayerManager {
    private ArrayList<Player> players = new ArrayList<Player>();
    private int currentTurn = 0;
    private int turnNumber = 0;
    private List<TurnListener> turnListeners = new ArrayList<TurnListener>();
    private List<PlayerChangedListener> playerListeners = new ArrayList<PlayerChangedListener>();
	private List<DayChangedListener> dayListeners = new ArrayList<DayChangedListener>();
    private boolean isNight = false;
    
    public void createPlayers(int count) {
        //Initialises all players (set by count)
        for (int i = 0; i < count; i++) {
            players.add(new Player(this, i + 1));
        }
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
