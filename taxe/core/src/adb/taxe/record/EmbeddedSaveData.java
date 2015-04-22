package adb.taxe.record;

import gameLogic.Game;

import com.badlogic.gdx.utils.Json;

/**This class is used exclusively to embed a JsonValue in an event for the sake of saving to a single Json file
 * @author Tim
 *
 */
public class EmbeddedSaveData extends Event{
	private Game val;
	public EmbeddedSaveData(Game val)
	{
		this.val = val;
	}
	
	public Game getGame()
	{
		return val;
	}
	
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Game Data");
		json.writeValue("Data", val);
		json.writeObjectEnd();
	}
	
	
}
