package fvs.taxe.actor;

import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CollisionStationActor extends Image {
    private static int width = 16;
    private static int height = 16;
	private CollisionStation station;
	private Drawable dayTexture;
	private Drawable nightTexture;

    public CollisionStationActor(IPositionable location, CollisionStation station) {
        //Places the actor
        super(new Texture(Gdx.files.internal("DayJunction.png")));
        dayTexture = getDrawable();
        nightTexture = new Image(new Texture(Gdx.files.internal("NightJunction.png"))).getDrawable();
        
        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
        
        this.station = station;
    }
    
    public Station getStation() {
        return station;
    }
    
    public void setNight(Boolean isNight) {
		if (isNight){
			setDrawable( nightTexture);
		} else {
			setDrawable(dayTexture);
		}
	}
}
