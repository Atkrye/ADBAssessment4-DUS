package gameLogic.map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Position extends IPositionable {
	//This is the class that implements IPositionable and allows you to create new Positions that can be compared to existing ones
	private int x;
	private int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Position) {
			Position pos = (Position) o;
			return (x == pos.getX() && y == pos.getY());
		}
		return false;
	}

	@Override
	public String toString(){
		return "( " + x + " , " + y + " )";
	}

	public static float getDistance(IPositionable a, IPositionable b) {
		//This method returns the absolute distance from point A to point B in pixels
		return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
	}
	
	public static float getAngle(IPositionable position1, IPositionable position2) {
		float dx = position2.getX() - position1.getX();
		float dy = position2.getY() - position1.getY();
		float angle = MathUtils.atan2(dy, dx);
		return angle;
	}

	// Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
	// intersect the intersection point may be stored in the floats i_x and i_y.
	public static Position getLineIntersect(float p0_x, float p0_y, float p1_x, float p1_y, 
			float p2_x, float p2_y, float p3_x, float p3_y) {
		float s1_x, s1_y, s2_x, s2_y;
		s1_x = p1_x - p0_x;     
		s1_y = p1_y - p0_y;
		s2_x = p3_x - p2_x;     
		s2_y = p3_y - p2_y;

		float s, t;
		s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
		t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
			// Collision detected
			float i_x = p0_x + (t * s1_x);
			float i_y = p0_y + (t * s1_y);
			return new Position((int) i_x, (int) i_y);
		}
		return null; // No collision
	}
}

