package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

/**This class is a type of event that describes when a keystroke has been detected in the game and recorded. e.g. to record
 * when the Escape Key or the Enter key is pressed when creating a new city. It stores the keycode of the keystroke
 * that can then be injected back into the recording by the PlayBack Instance. 
 */
public class KeyEvent extends Event{
	/**The keycode stored by this event*/
	private int keyCode;
	
	/**This instantation method simply recieves a keycode and assigns it's internal variable keyCode to this value
	 * @param keyCode The key that has been pressed in the game.
	 */
	public KeyEvent(int keyCode)
	{
		this.keyCode = keyCode;
	}
	
	/**This method gets the keycode of the key press that this event represents.*/
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
