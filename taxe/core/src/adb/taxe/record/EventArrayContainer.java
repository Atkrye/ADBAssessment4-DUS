package adb.taxe.record;

import java.util.ArrayList;

/**This class is used to embed an Array of Events. This is important because LibGDX's json features do
 * not allow Generic or Inferred arguments (neither Array<T> or Array<Event>) when writing an object to json.
 * By embedding the Array of events within another Java object, we can abstract away from the inferred arguments concept
 * and save this new EventArrayContainer object in one go, by accessing the events array stored within it
 * when using a custom json writer in SaveManager.
 * @author Tim
 */
public class EventArrayContainer {
	/**The array of events stored within this container*/
	public ArrayList<Event> events;
	
	/**This instantiation method simply takes an array of events and sets the internal events variable to this array
	 * @param events The array of events to be embedded within this container
	 */
	public EventArrayContainer(ArrayList<Event> events)
	{
		this.events = events;
	}

}
