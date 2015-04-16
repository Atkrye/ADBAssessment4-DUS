package fvs.taxe.actor;

import gameLogic.map.IPositionable;
import gameLogic.map.Station;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**This class is a type of image specifically for creating Station actors.*/
public class StationActor extends Image {
	/**The width of the StationActor in pixels.*/
	private static int width = 20;
	
	/**The height of the StationActor in pixels.*/
	private static int height = 20;
	
	/** Polygon (rectangle shaped) that corresponds to the rectangle around the station */
    private Polygon bounds;
    
    /** The station that the StationActor is linked to */
    private Station station;
    
    /** The drawable that corresponds to the look of the station in the daytime */
	private Drawable dayTexture;

	/** The drawable that corresponds to the look of the station at night */
	private Drawable nightTexture;

	/**Instantiation method
	 * @param location the location of the station actor
	 */
    public StationActor(IPositionable location, Station station) {
    	super(new Texture(Gdx.files.internal("stations/DayStation.png"))); // assume start at daytime- will be changed if created at night
        dayTexture = getDrawable();
        nightTexture = new Image(new Texture(Gdx.files.internal("stations/NightStation.png"))).getDrawable();
        
        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
        
        bounds = new Polygon(new float[] {getX(),getY(),getX(), getY() + height, getX() + width, getY()+ height, getX() + width, getY()});
        this.station = station;
    }
    
    public Station getStation() {
        return station;
    }

    public Polygon getBounds() {
        return this.bounds;
    }

    public static int getStationHeight() {
    	return height;
    }
    
    public static int getStationWidth() {
    	return width;
    }
    
    /** Change whether the collisionStation shows its nightTexture or dayTexture
	 * @param isNight Boolean saying whether to display the nightTexture (True) or dayTexture (False)
	 */
	public void setNight(Boolean isNight) {
		if (isNight){
			setDrawable(nightTexture);
		} else {
			setDrawable(dayTexture);
		}
	}
}
