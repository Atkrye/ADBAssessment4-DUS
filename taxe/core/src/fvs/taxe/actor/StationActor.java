package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

public class StationActor extends Image {
    public static int width = 20;
    public static int height = 20;
    private Rectangle bounds;
    private Station station;

    public StationActor(IPositionable location, Station station) {
        super(new Texture(Gdx.files.internal("station_dot.png")));
    
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
}
