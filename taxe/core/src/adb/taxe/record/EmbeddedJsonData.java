package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

public class EmbeddedJsonData extends Event{
	String data;
	
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
