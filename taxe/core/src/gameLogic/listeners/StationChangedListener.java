package gameLogic.listeners;

import gameLogic.map.Station;

public interface StationChangedListener {
	public void stationAdded(Station station);
	public void stationRemoved(Station station);
}
