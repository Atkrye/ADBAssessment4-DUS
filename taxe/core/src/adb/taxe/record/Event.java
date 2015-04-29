package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

/**This abstract class is used to define the necessary method that must be shared across all events - toJson.
 * This method is called by the SaveManager class when saving a recording to convert each event to a json format.
 */
public abstract class Event {
	
	/**This method is called by SaveManager. It takes a Json object and writes the event's data to it
	 * @param json The json object that is being written to
	 */
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Empty");
		json.writeObjectEnd();
	}

}
