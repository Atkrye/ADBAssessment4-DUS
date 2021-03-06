package adb.taxe.record;

import gameLogic.resource.Train;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

/**This method does the heavyweight work for reading events back in and injecting them in to the game. Every turn,
 * the RecordingScreen gets the next event from the PlayBack device and injects it back in to the Game's input
 * processor. When the turn is over and thing must be calculated (Obstacles, new Resources and Goals, collisions), a set of
 * methods allow the Recording to find the events that were calculated in the original game.
 */
public class Playback {
	  /**The array of Events that makes up the recording*/
	  private ArrayList<Event> events;
	  /**The index of the events that the Playback has reached up to*/
	  private int eventIndex = 0;
	  /**The playback input processor to inject click, key and char events into*/
	  private InputProcessor playbackProcessor;
	  /**Whether the playback direction has reversed (so going backwards through the recording, if true)*/
	  private boolean reverse = false;
	  /**Used to store a set input processor to load back from when a temporary processor is dropped*/
	  private InputProcessor storedProcessor;
	  
	  /**Loads a set of events into the Recorder to play back
	 	*@param events The events to be played back, in order
	   */
	  public void setEvents(ArrayList<Event> events)
	  {
		  this.events = events;
		  eventIndex = 0;
	  }
	  
	  /**Injects an event back into the input processor / game
	   * @param ev The event to be inputted
	   * @return Whether the event was injected correctly
	   */
	  public boolean injectEvent(Event ev)
	  {
		  if(ev.getClass().equals(ClickEvent.class))
		  {
			  System.out.println("Click");
			  injectClick(((ClickEvent)ev).getX(), ((ClickEvent)ev).getY());
			  return true;
		  }
		  else if(ev.getClass().equals(KeyEvent.class))
		  {
			  injectKey(((KeyEvent)ev).getKeyCode());
			  return true;
		  }
		  else if(ev.getClass().equals(CharEvent.class))
		  {
			  injectChar(((CharEvent)ev).getChar());
			  return true;
		  }
		  return false;
	  }
	  
	  /**Injects a click back into the current input processor
	   * @param x The x coordinate of the click to inject
	   * @param y The y coordinate of the click to inject
	   */
	  private void injectClick(int x, int y)
	  {
		  playbackProcessor.touchDown(x, y, 0, Buttons.LEFT);
		  playbackProcessor.touchUp(x, y, 0, Buttons.LEFT);
	  }
	  
	  /**Injects a key press back into the current input processor
	   * @param keycode The keycode of the key pressed
	   */
	  private void injectKey(int keycode)
	  {
		  playbackProcessor.keyDown(keycode);
		  playbackProcessor.keyUp(keycode);
	  }
	  
	  /**Injects a character typed back into the current input processor
	   * @param ch The character to be injected.
	   */
	  private void injectChar(char ch)
	  {
		  playbackProcessor.keyTyped(ch);
	  }

	  /**Returns the InputProcessor that is used by this Recorder to inject events into the Game.*/
	  public InputProcessor getPlaybackProcessor() {
		  return playbackProcessor;
	  }

	  /**Sets the InputProcessor to be used for injecting events back in to the Game
	   * @param playbackProcessor The InputProcessor to be used
	   */
	  public void setPlaybackProcessor(InputProcessor playbackProcessor) {
		  this.playbackProcessor = playbackProcessor;
		  this.storedProcessor = playbackProcessor;
	  }

	  /**Injects the next event and returns it, moving eventIndex along
	   * @return The event that has just been injected
	   */
	public Event nextEvent() {
		if((eventIndex < events.size() && !reverse) || (eventIndex > -1 && reverse))
		{
			Event e = events.get(eventIndex);
			injectEvent(e);
			if(!reverse)
			{
				eventIndex++;
			}
			else
			{
				eventIndex--;
			}
			return e;
		}
		return null;
	}
	
	/**This method iterates through the events array to find the next ObstacleEvent stored within it. It removes
	 * this event from the array and returns it - Meaning that each ObstacleEvent is read in sequentially corresponding
	 * to the order that they occurred in in the game.
	 * @return The next ObstacleEvent that occurs within the recording
	 */
	public ObstacleEvent getNextObstacleEvent()
	{
		ObstacleEvent ret = null;
		for(int i = 0; i < events.size(); i++)
		{
			if(events.get(i).getClass().equals(ObstacleEvent.class) && ret == null)
			{
				ret = (ObstacleEvent)events.remove(i);
				break;
			}
		}
		return ret;
	}
	
	/**This method iterates through the events array to find the next GoalEvent stored within it. It removes
	 * this event from the array and returns it - Meaning that each GoalEvent is read in sequentially corresponding
	 * to the order that they occurred in in the game.
	 * @return The next GoalEvent that occurs within the recording
	 */
	public GoalEvent getNextGoalEvent()
	{
		GoalEvent ret = null;
		for(int i = 0; i < events.size(); i++)
		{
			if(events.get(i).getClass().equals(GoalEvent.class) && ret == null)
			{
				ret = (GoalEvent)events.remove(i);
				break;
			}
		}
		return ret;
	}
	
	/**This method iterates through the events array to find the nextResourceEvent stored within it. It removes
	 * this event from the array and returns it - Meaning that each ResourceEvent is read in sequentially corresponding
	 * to the order that they occurred in in the game.
	 * @return The next ResourceEvent that occurs within the recording
	 */
	public ResourceEvent getNextResourceEvent()
	{
		ResourceEvent ret = null;
		for(int i = 0; i < events.size(); i++)
		{
			if(events.get(i).getClass().equals(ResourceEvent.class) && ret == null)
			{
				ret = (ResourceEvent)events.remove(i);
				break;
			}
		}
		return ret;
	}
	
	/**This method iterates through the events array to find the next CollisionEvent stored within it. It removes
	 * this event from the array and returns it - Meaning that each CollisionEvent is read in sequentially corresponding
	 * to the order that they occurred in in the game.
	 * @return The next CollisionEvent that occurs within the recording
	 */
	public CollisionEvent getCollisionEvent(Train train1, Train train2)
	{
		for(Event e : events)
		{
			if(e.getClass().equals(CollisionEvent.class))
			{
				CollisionEvent ce = (CollisionEvent)e;
				if(ce.isCollision(train1, train2))
				{
					return ce;
				}
			}
		}
		return null;
	}

	/**This method checks whether the recording is set to play back in reverse. 
	 * @return True if the playback is in reverse, false otherwise
	 */
	public boolean isReverse() {
		return reverse;
	}

	/**This method sets whether the recorder should play back in reverse. There is however, no compatibility for
	 * this in terms of undoing events within the recording yet, so this currently has no use. This could
	 * be a potential extension in further work.
	 * @param reverse Whether the recording should be in reverse or not
	 */
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	/**Sets a temporary input processor
	 * @param tempProcessor The processor to be used temporarily
	 */
	public void setTempProcessor(InputAdapter tempProcessor) {
		this.playbackProcessor = tempProcessor;	
	}

	/**Drops a temporary input processor in favour of the original*/
	public void dropTempProcessor() {
		this.playbackProcessor = this.storedProcessor;
	}
	
	
}
