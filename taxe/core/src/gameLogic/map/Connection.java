package gameLogic.map;

import com.badlogic.gdx.math.Vector2;

import fvs.taxe.actor.ConnectionActor;

/**A connection describes the link between 2 stations.*/
public class Connection {
	/**The first station of the connection.*/
	private Station station1;
	
	/**The second station of the connection.*/
	private Station station2;
	
	/**The actor that represents this connection.*/
	private ConnectionActor actor;
	
	/** The vector that corresponds to this connection*/
	private Vector2 vector;

	/**Instantiation method.
	 * @param station1 The first station for the connection.
	 * @param station2 The second station for the connection.
	 */
	public Connection(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
		
		vector = new Vector2(station1.getPosition().getX(), station1.getPosition().getY());
		vector.add(station2.getPosition().getX(), station2.getPosition().getY());
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

	/** Returns the midpoint of the connection */
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
	
	public Vector2 getVector(){
		return vector;
	}
}