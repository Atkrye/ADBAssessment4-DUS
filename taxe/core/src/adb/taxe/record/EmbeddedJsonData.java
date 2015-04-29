package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

/**This class is a subclass of Event that is used to embed a json format string. This is done so that we can save a Game.java
 * instance as a json format string, embed it in the event and then add this event to the events array produced by
 * recording in Record.java. This allows us to save the entire Recording as a single object (the events array with the game save embedded inside
 * it, all embedded within a EventArrayContainer) that is more compatible with LibGDX's Json features.
 */
public class EmbeddedJsonData extends Event{
	/**The json format data that makes up the saved Game instance*/
	private String data;
	
	/**This constructor simply loads a passed set of data into the data variable so that it can be written
	 * to a saved recording when toJson is called.
	 * @param val The json format data to embed.
	 */
	public EmbeddedJsonData(String val)
	{
		data = val;
	}
	
	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Game Data");
		json.writeValue("Data", data);
		json.writeObjectEnd();
	}

}
