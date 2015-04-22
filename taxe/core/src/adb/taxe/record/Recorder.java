package adb.taxe.record;


import java.util.ArrayList;

import com.badlogic.gdx.utils.JsonReader;

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
}
