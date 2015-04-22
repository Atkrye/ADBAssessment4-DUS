package adb.taxe.record;

import com.badlogic.gdx.utils.Json;

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
