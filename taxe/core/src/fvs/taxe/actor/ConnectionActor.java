package fvs.taxe.actor;

import gameLogic.map.IPositionable;
import gameLogic.map.Position;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**This class is a type of image specifically for creating connections between stations.*/
public class ConnectionActor extends Image{
	// Wraps up the shapeRenderer of the connections into an image to sllow correct z-ordering 
	// (as can be added to stage in specific order)
	
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

	// By "partially draw", it means to draw the whole connection in COlor, then draw the partial section in black
	
	/** Boolean saying whether the connection should be partly drawn */
	private boolean partialDraw;

	/** If the connection is being partially drawn, where the partial connection starts from */
	private IPositionable partialStart;
	
	/** If the connection is being partially drawn, where the partial connection ends */
	private IPositionable partialNext;

	// the partial fields are required as connections are defined in one way only, 
	// therefore calculating which end that the partial route ends at requires further calculations and another field anyway

	/** Instantation method for the connection actor
	 * @param color Color that the connection should be displayed as
	 * @param start The start position of the connection onscreen
	 * @param end The end position of the connection onscreen
	 * @param connectionWidth The width of the connection to display in pixels
	 */
	public ConnectionActor(Color color, IPositionable start, IPositionable end, float connectionWidth)  {
		shapeRenderer = new ShapeRenderer();
		this.color = color;
		this.start = start;
		this.end = end;
		this.connectionWidth = connectionWidth;
	}

	/** Draw the connections using a shapeRenderer with the colours, positions and width defined
	 * will also draw the partial route from the partial positions defined is partialDraw is true
	 */
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(color);
		shapeRenderer.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), connectionWidth);
		if (partialDraw) {
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rectLine(partialStart.getX(), partialStart.getY(), partialNext.getX(), partialNext.getY(), connectionWidth);
		} 
		shapeRenderer.end();
		batch.begin();
	}

	public Color getConnectionColor(){
		return this.color;
	}
	
	public void setConnectionColor(Color color) {
		this.color = color;
	}
	
	/** Clear the previous partial drawing values, and disable the drawing of partial routes until reenabled with setPartialPosition() */
	public void clearPartialPosition(){
		partialDraw = false;
		partialStart = null;
		partialNext = null;
	}

	/** Set the connection to be partially drawn 
	 * @param x The starting location's x value
	 * @param y The starting location's y value
	 * @param position The end position to draw the partial route to
	 */
	public void setPartialPosition(float x, float y, IPositionable position) {
		partialDraw = true;
		partialStart = new Position((int)x, (int)y);
		partialNext = position;
	}
}
