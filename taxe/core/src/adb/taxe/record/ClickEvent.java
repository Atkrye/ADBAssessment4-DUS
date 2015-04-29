package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

/**This class is a subclass of event. It is used to describe a player clicking in the game. It stores this click
 * as coordinates that can be injected back in to a recording by a Playback.java instance.
 */
public class ClickEvent extends Event{
	private int x;
	private int y;
	
	public ClickEvent(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Click");
		json.writeValue("x", x);
		json.writeValue("y", y);
		json.writeObjectEnd();
	}
	
	

}
