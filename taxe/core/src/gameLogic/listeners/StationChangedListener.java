package gameLogic.listeners;

import gameLogic.map.Station;

/** Listener used for when a class wnats to take action due to a station being added or removed in COnnectionCOntroller due to creation/destruction of tracks*/
public interface StationChangedListener {
	public void stationAdded(Station station);
	public void stationRemoved(Station station);
}
