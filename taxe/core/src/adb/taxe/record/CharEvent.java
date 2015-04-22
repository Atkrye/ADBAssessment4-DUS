package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

public class CharEvent extends Event{
	private char ch;
	public CharEvent(char ch)
	{
		this.ch = ch;
	}
	
	public char getChar()
	{
		return ch;
	}
	
	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Char");
		json.writeValue("CharValue", ch);
		json.writeObjectEnd();
	}

}
