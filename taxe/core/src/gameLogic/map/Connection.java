package gameLogic.map;

import fvs.taxe.actor.ConnectionActor;

public class Connection {
	private Station station1;
	private Station station2;
	//Added this variable to the Connection class which indicates how long the connection shall be blocked for
	//We could have used a boolean to indicate whether it was blocked but this implementation makes it very easy to set the connections to be blocked for a certain number of turns
	//private int blocked;
	private ConnectionActor actor;

	public Connection(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
	}

	public Station getStation1() {
		return this.station1;
	}

	public Station getStation2() {
		return this.station2;
	}

	public void setActor(ConnectionActor actor){
		this.actor = actor;
	}

	public ConnectionActor getActor(){
		return this.actor;
	}

	public IPositionable getMidpoint() {
		//This returns the midPoint of the connection, which is useful for drawing the obstacle indicators on to the connection
		return new IPositionable() {
			@Override
			public int getX() {
				return (station1.getPosition().getX() + station2.getPosition().getX()) / 2;
			}

			@Override
			public int getY() {
				return (station1.getPosition().getY() + station2.getPosition().getY()) / 2;
			}

			@Override
			public void setX(int x) {

			}

			@Override
			public void setY(int y) {

			}

			@Override
			public boolean equals(Object o) {
				return false;
			}
		};
	}
}