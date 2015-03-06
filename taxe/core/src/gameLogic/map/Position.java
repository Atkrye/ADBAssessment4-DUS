package gameLogic.map;

import com.badlogic.gdx.math.MathUtils;

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

	public static float getAngle(Position p1, Position p2) {
		/*// convert to vector2's instead?
		int dot = p1.getX() * p2.getX() + p1.getY() + p1.getY();
		
		double magp1 = Math.sqrt((p1.getX() * p1.getX() + p2.getY() * p2.getY()));
		double magp2 = Math.sqrt((p2.getX() * p2.getX() + p2.getY() * p2.getY()));
		
		double cosa = (double) (dot/(magp1*magp2));
		
		double angle = Math.acos(cosa);
		return angle;*/
		
		float dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
		float angle = MathUtils.atan2(dy, dx);
		return angle;
	}
}

