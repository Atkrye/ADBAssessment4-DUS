package adb.taxe.record;


import gameLogic.goal.Goal;
import gameLogic.obstacle.Obstacle;
import gameLogic.resource.Resource;

import java.util.ArrayList;

/**This Class does the heavyweight work for recording. It sets up an array of Events and then adds subsequent events
 * that occur within the Game to this Array. Once the stop button is pressed to end the recording, this array of events
 * is embedded within an EventArrayContainer and stored in a json format within a .taxeR recording file type.
 */
public class Recorder {

	  /**Indicates whether the recorder is currently recording events*/
	  private boolean isRecording = false;
	  /**The array of Events that makes up the recording*/
	  private ArrayList<Event> events;
	  /**Stores the game save data until it is ready to be saved*/
	  private String saveData;
	  
	  /**Starts the recorder accepting recording inputs*/
	  public void startRecording()
	  {
		  saveData = SaveManager.getSaveText();
		  events = new ArrayList<Event>();
		  this.isRecording = true;
	  }
	  
	  /**Stops the recorder and saves the recording*/
	  public void stopRecording()
	  {
		  SaveManager.saveRecording(saveData, events);
		  this.isRecording = false;
	  }
	  
	  /**Checks whether the recorder is recording.
	   * @return Whether the recorder is recording. True if it is, false if not.
	   */
	  public boolean isRecording()
	  {
		  return isRecording;
	  }
	  
	  /**Tells the recorder to record a new mouse click
	   * @param x the x coordinate of the click
	   * @param y the y coordinate of the click*/
	  public void recordMouseClick(int x, int y)
	  {
		  events.add(new ClickEvent(x, y));
	  }

	  /**Tells the recorder to record a new Key pressed
	   * @param keycode The keycode of the key pressed
	   */
	  public void recordKeyPressed(int keycode) {
		  events.add(new KeyEvent(keycode));
	  }
	  
	  /**Tells the recorder to record a new character entered
	   * @param ch The character typed
	   */
	  public void recordCharTyped(char ch) {
		  events.add(new CharEvent(ch));
	  }

	  /**Tells the recorder to record a new obstacle starting
	   * @param obstacle The obstacle started
	   */
	  public void recordObstacle(Obstacle obstacle) {
		  events.add(new ObstacleEvent(obstacle));
	  }
	  
	  /**Duplicate method with no argument indicates to record that no obstacle happened
	   */
	  public void recordObstacle() {
		  events.add(new ObstacleEvent());
	  }

	  /**Tells the recorder to record a new goal that has been given to a player
	   * @param g The goal to be recorded
	   */
	  public void recordGoal(Goal g) {
		  events.add(new GoalEvent(g));
	  }
	  
	  /**Tells the recorder to record a new resource that has been given to a player
	   * @param resource The resource to be recorded
	   */
	  public void recordResource(Resource resource)
	  {
		  events.add(new ResourceEvent(resource));
	  }

	 
	  /**Tells the recorder to record a new collision that has occured between trains
	   * @param train1ID The id of the first train involved in the collision
	   * @param train2ID The id of the second train involved in the collision
	   * @param destroyedID The id of the train that was destroyed in the collision
	   */
	  public void recordCollision(int train1ID, int train2ID, int destroyedID) {
		  events.add(new CollisionEvent(train1ID, train2ID, destroyedID));
	  }
}
