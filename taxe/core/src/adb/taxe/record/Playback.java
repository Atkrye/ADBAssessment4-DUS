package adb.taxe.record;

import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;

public class Playback {
	  /**The array of Events that makes up the recording*/
	  private ArrayList<Event> events;
	  /**The index of the events that the Playback has reached up to*/
	  private int eventIndex = 0;
	  /**The playback input processor to inject click, key and char events into*/
	  private InputProcessor playbackProcessor;
	  /**Whether the playback direction has reversed (so going backwards through the recording, if true)*/
	  private boolean reverse = false;
	  
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

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}
}
