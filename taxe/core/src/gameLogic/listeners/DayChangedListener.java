package gameLogic.listeners;

/** Listener used if a class wants to take action when the game has changed from day - night or vice versa*/
public interface DayChangedListener {
	public void changed(Boolean isNight);
}
