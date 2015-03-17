package fvs.taxe.actor;

import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**This class is a type of image specifically for creating connections between stations.*/
public class ConnectionActor extends Image{

	/**This variable stores the width of the connection between stations in pixels.*/
	private float connectionWidth;

	/**The shapeRenderer variable is used to render a line from the start to the end of the given color and connectionWidth.*/
	private ShapeRenderer shapeRenderer;

	/**The color of the connection between stations.*/
	private Color color;

	/**The start position of the connection, where the line is drawn from.*/
	private IPositionable start;

	/**The end position of the connection, where the line is drawn to.*/
	private IPositionable end;

	private boolean partialDraw;

	private float partialX;

	private float partialY;

	private IPositionable partialNext;

	public ConnectionActor(Color color, IPositionable start, IPositionable end, float connectionWidth)  {
		shapeRenderer = new ShapeRenderer();
		this.color = color;
		this.start = start;
		this.end = end;
		this.connectionWidth = connectionWidth;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(color);
		shapeRenderer.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), connectionWidth);
		if (partialDraw) {
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rectLine(partialX, partialY, partialNext.getX(), partialNext.getY(), connectionWidth);
		} 
		shapeRenderer.end();
		batch.begin();
	}

	public void setConnectionColor(Color color) {
		this.color = color;
	}
	
	public void clearPartialPosition(){
		partialDraw = false;
		partialX = -1f;
		partialY = -1f;
		partialNext = null;
	}

	public Color getConnectionColor(){
		return this.color;
	}

	public void setPartialPosition(float x, float y, IPositionable iPositionable) {
		partialDraw = true;
		partialX = x;
		partialY = y;
		partialNext = iPositionable;
	}
}
