package adb.taxe.record;

import gameLogic.Game;

import com.badlogic.gdx.utils.Json;

/**This class is a type of event that is used when loading an array. Since the Game instance was stored as a json
 * format string within an EmbeddedJsonData event in the events array, it should now be loaded back as an event.
 * This class acts as a container for the Game instance loaded from the Recording so that it can be loaded in
 * by a RecordingScreen instance
 */
public class EmbeddedSaveData extends Event{
	/**The Game instance embedded within this event*/
	private Game val;
	
	/**This constructor sets the embedded game instance to a passed one
	 * @param val The Game instance to embed within the event
	 */
	public EmbeddedSaveData(Game val)
	{
		this.val = val;
	}
	
	/**This method gets the Game embedded within the EmbeddedSaveData event*/
	public Game getGame()
	{
		return val;
	}
	
	/**This class provides functionality for saving an EmbeddedSaveData event back to json, should it be wanted
	 * in the future.
	 */
	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Game Data");
		json.writeValue("Data", val);
		json.writeObjectEnd();
	}
	
	
}
