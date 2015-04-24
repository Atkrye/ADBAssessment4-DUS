package adb.taxe.record;


import gameLogic.resource.Resource;

import com.badlogic.gdx.utils.Json;

public class ResourceEvent extends Event{
	/**The name of the resource that has been added to the player*/
	String name;
	/**Constructor saves the name of the resource relevant to the event
	 * @param res The resource that has been added to a player
	 */
	public ResourceEvent(Resource res)
	{
		name = res.toString();
	}
	
	/**Constructor for loading a resource back in from Json*/
	public ResourceEvent(String name) {
		this.name = name;
	}

	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Resource");
		json.writeValue("Name", name);
		json.writeObjectEnd();
	}
	
	/**Gets the name of the resource
	 * @return The name of the resource
	 */
	public String getName()
	{
		return name;
	}
}