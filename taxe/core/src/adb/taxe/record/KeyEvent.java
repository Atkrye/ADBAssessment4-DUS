package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

public class KeyEvent extends Event{
	private int keyCode;
	public KeyEvent(int keyCode)
	{
		this.keyCode = keyCode;
	}
	
	public int getKeyCode()
	{
		return keyCode;
	}
	
	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Key");
		json.writeValue("Keycode", keyCode);
		json.writeObjectEnd();
	}

}
