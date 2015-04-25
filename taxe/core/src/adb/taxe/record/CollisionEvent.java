package adb.taxe.record;

import gameLogic.resource.Train;

import com.badlogic.gdx.utils.Json;

public class CollisionEvent extends Event{
	/**The id of the first train involved in the collision*/
	private int train1ID;
	/**The id of the second train involved in the collision*/
	private int train2ID;
	/**The id of the train that was destroyed in the collision*/
	private int destroyedID;
	
	/**Constructor for a new collision event between 2 trains, where one train has been destroyed.
	 * @param train1ID The ID of the first train
	 * @param train2ID The ID of the second train
	 * @param destroyedID The ID of the train that was destroyed in the collision
	 */
	public CollisionEvent(int train1ID, int train2ID, int destroyedID)
	{
		this.train1ID = train1ID;
		this.train2ID = train2ID;
		this.destroyedID = destroyedID;
	}
	
	@Override
	public void toJson(Json json)
	{
		json.writeObjectStart();
		json.writeValue("Type", "Collision");
		json.writeValue("Train1ID", train1ID);
		json.writeValue("Train2ID", train2ID);
		json.writeValue("DestroyedID", destroyedID);
		json.writeObjectEnd();
	}
	
	/**This method checks whether this event represents the collision between to trains
	 * @param train1 The first train involved in a collision
	 * @param train2 The second train involved in a collision
	 * @return Whether the event represents the collision between the 2 parameter trains (true if it does!)
	 */
	public boolean isCollision(Train train1, Train train2)
	{
		if((train1.getID() == train1ID && train2.getID() == train2ID) || (train2.getID() == train1ID && train1.getID() == train2ID))
		{
			return true;
		}
		return false;
	}
	
	/**Gets the id of the train that was destroyed in this collision event
	 * @return The destroyed train's id, corresponds to train.getID()
	 */
	public int getDestroyedID()
	{
		return destroyedID;
	}
			

}
