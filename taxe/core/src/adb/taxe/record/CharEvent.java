package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

/**This subclass of event is used to describe when a character has been typed in the Game. It stores that character
 * and can pass it back to a recording so that the typed character can be injected in to a recording by a Playback.java instance.
 */
public class CharEvent extends Event{
	/**The character stored in this event*/
	private char ch;
	
	public CharEvent(char ch)
	{
		this.ch = ch;
	}
	
	/**Gets the character that was typed when this event was recorded
	 * @return The character that was recorded when the event was created
	 */
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
