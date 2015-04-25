package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.goal.Goal;

import java.util.ArrayList;
import java.util.List;

/** Dialog to display when a goal has been selected */
public class DialogGoal extends Dialog {
	
	/** List of resource dialog click listeners */
    private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();
	
    /** Context that the dialog is in */
    private Context context;

    /** Instantiation method
     * @param context Context that the dialog in
     * @param goal Goal that the dialog corresponds to
     * @param skin Skin to display the dialog with
     */
    public DialogGoal(Context context, Goal goal, Skin skin) {
        //Generates a dialog allowing the player to select what they want to do with the goal
        super(goal.toString(), skin);
        this.context = context;
        text("What do you want to do with this goal?");

        button("Drop", "DROP");
        button("Cancel", "CLOSE");
    }

    /** Show the dialog in the center of the stage */
    @Override
    public Dialog show(Stage stage) {
    	context.getGameLogic().setState(GameState.WAITING);
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }


    /** Hide the dialog */
    @Override
    public void hide() {
    	context.getGameLogic().setState(GameState.NORMAL);
        hide(null);
    }

    private void clicked(Button button) {
        //Informs all listeners that the dialog has been pressed, and which button has been pressed
        for (ResourceDialogClickListener listener : clickListeners) {
            listener.clicked(button);
        }
    }

    public void subscribeClick(ResourceDialogClickListener listener) {
        //Adds listeners to the dialog, which want to know which button the user pressed
        clickListeners.add(listener);
    }

    /** What to do as a result of the button being pressed */
    @Override
    protected void result(Object obj) {
        //Does things based on which button was pressed
        if (obj == "CLOSE") {
            //Closes the dialog if close was pressed
            this.remove();

        } else if (obj == "DROP") {
            //Removes the goal if the drop button is pressed
            clicked(Button.GOAL_DROP);
        }
    }
}
