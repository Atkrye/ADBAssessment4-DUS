package fvs.taxe.actor;

import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**Class that represents the CollisionStation on screen, extends Image*/
public class CollisionStationActor extends Image {
	/** Width of collision station in pixels*/
	private static int width = 16;

	/** Height of collisionstation in pixels*/
	private static int height = 16;

	/** The collisionstation that the actor is representing*/
	private CollisionStation station;

	/** The drawable that corresponds to the look of the collision station in the daytime */
	private Drawable dayTexture;

	/** The drawable that corresponds to the look of the collision station at night */
	private Drawable nightTexture;

	/** The collisionstationactor instantiation method. Set up using location and a collisionstation
	 * @param location The iPositionable that corresponds to the location of the station onscreen
	 * @param station The collisionStation that the actor is linked to
	 */
	public CollisionStationActor(IPositionable location, CollisionStation station) {
		super(new Texture(Gdx.files.internal("stations/DayJunction.png"))); // assume start at daytime- will be changed if created at night
		dayTexture = getDrawable();
		nightTexture = new Image(new Texture(Gdx.files.internal("stations/NightJunction.png"))).getDrawable();

		setSize(width, height);
		setPosition(location.getX() - width / 2, location.getY() - height / 2);

		this.station = station;
	}

	/** Change whether the collisionStation shows its nightTexture or dayTexture
	 * @param isNight Boolean saying whether to display the nightTexture (True) or dayTexture (False)
	 */
	public void setNight(Boolean isNight) {
		if (isNight){
			setDrawable( nightTexture);
		} else {
			setDrawable(dayTexture);
		}
	}
	
	/** Get the station that corresponds to this actor
	 * @return The CollisionStation that is linked to this actor
	 */
	public Station getStation() {
		return station;
	}
}
