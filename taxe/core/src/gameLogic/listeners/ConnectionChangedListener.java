package gameLogic.listeners;

import gameLogic.map.Connection;

/** Interface to use if a class must take action if a connection has changed in ConnectionCOntroller due to track destruction/creation*/
public interface ConnectionChangedListener {
	public void added(Connection connection);
	public void removed(Connection connection);
}
