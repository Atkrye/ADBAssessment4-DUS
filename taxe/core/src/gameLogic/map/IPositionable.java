package gameLogic.map;

/**This class describes an abstract class of a 2 Dimensional Vector Coordinate with coordinates x and y.*/
abstract public class IPositionable {
    //This abstract class allows custom positions to be defined throughout the program
    //If you want to generate a new position to compare to other objects' positions then use the Position class instead!
    public abstract int getX();

    public abstract int getY();

    public abstract void setX(int x);

    public abstract void setY(int y);

    public abstract boolean equals(Object o);
}