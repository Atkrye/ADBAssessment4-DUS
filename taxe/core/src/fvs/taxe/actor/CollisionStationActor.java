package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

public class CollisionStationActor extends Image {
    private static int width = 16;
    private static int height = 16;
	private CollisionStation station;

    public CollisionStationActor(IPositionable location, CollisionStation station) {
        //Places the actor
        super(new Texture(Gdx.files.internal("junction_dot.png")));
        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
        
        this.station = station;
    }
    
    public Station getStation() {
        return station;
    }
}
