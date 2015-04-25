package fvs.taxe;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

/**This class is an adapted window specifically for showing text.*/
public class Tooltip extends Window {
	
	/**Instantiation method, sets up the size and skin.
	 * @param skin stores the fonts and colors used in the tooltip.
	 */
    public Tooltip(Skin skin) {
        super("", skin);
        setSize(150, 20);
        setVisible(false);
    }

    /**This method makes the tooltip display a string.
     * @param content the String to be displayed.
     */
    public void show(String content) {
        setTitle(content);
        setVisible(true);
        toFront();
    }

    /**This method hides the tooltip by changing the visibility.*/
    public void hide() {
        setVisible(false);
    }
}
