package gameLogic.resource;

import gameLogic.Disposable;
import gameLogic.player.Player;

/** This abstract class represents any resource that can be used in game.*/
public abstract class Resource implements Disposable {

	/**This variable stores the string that represents the instantiated object */
	protected String name;

	/**This variable stores the player that owns the given resource*/
	protected Player player;

	/** Gets the player that is associated with the resource
	 * @return The player that owns the resource
	 */
	public Player getPlayer() {
		return player;
	}

	/**Set the player that the resource is owned by
	 *@param player The player that will now own that resource
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/** Returns whether the given player owns the resource
	 * @param player The player that will be tested to see if they own that resource
	 * @return True if the player owns the resource, false otherwise
	 */
	public boolean isOwnedBy(Player player) {
		return player == this.player;
	}

	/** Notifies the player that owns the resource that the resource has changed */
	protected void changed() {
		player.changed();
	}

	@Override
	public String toString() {
		return name;
	}
}