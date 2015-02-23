package gameLogic.obstacle;

/** Interface for listening to an obstacle starting and ending, 
 * allowing actions to occur when an obstacle has started or ended in classes that implement this listener*/
public interface ObstacleListener {
	public void started(Obstacle obstacle);
	public void ended(Obstacle obstacle);
}
