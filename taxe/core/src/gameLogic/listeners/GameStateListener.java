package gameLogic.listeners;

import gameLogic.GameState;

/**This interface adds a changed method for when the game state changes to any other class.*/
public interface GameStateListener {
    public void changed(GameState state);
}
