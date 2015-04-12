package fvs.taxe.actor;

import gameLogic.map.IPositionable;
import gameLogic.map.Station;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class StationActor extends Image {
    public static int width = 20;
    public static int height = 20;
    private Rectangle bounds;
    private Station station;
	private Drawable dayTexture;
	private Drawable nightTexture;

    public StationActor(IPositionable location, Station station) {
    	super(new Texture(Gdx.files.internal("DayStation.png")));
        dayTexture = getDrawable();
        nightTexture = new Image(new Texture(Gdx.files.internal("NightStation.png"))).getDrawable();
        
        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
        bounds = new Rectangle();
        bounds.set(getX(), getY(), getWidth(), getHeight());
        this.station = station;
    }
    
    public Station getStation() {
        return station;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

	public void setNight(Boolean isNight) {
		if (isNight){
			setDrawable(nightTexture);
		} else {
			setDrawable(dayTexture);
		}
	}
}
