package gameLogic.listeners;

import gameLogic.map.Connection;

public interface ConnectionChangedListener {
	public void added(Connection connection);
	public void removed(Connection connection);
}
