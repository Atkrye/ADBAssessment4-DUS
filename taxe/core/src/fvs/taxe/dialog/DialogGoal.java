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

public class DialogGoal extends Dialog {
    private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();
	private Context context;

    public DialogGoal(Context context, Goal goal, Skin skin) {
        //Generates a dialog allowing the player to select what they want to do with the goal
        super(goal.toString(), skin);
        this.context = context;
        text("What do you want to do with this goal?");

        button("Drop", "DROP");
        button("Cancel", "CLOSE");
    }

    @Override
    public Dialog show(Stage stage) {
        //Shows the dialog in the centre of the screen
    	context.getGameLogic().setState(GameState.WAITING);
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }


    @Override
    public void hide() {
        //Hides the dialog
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
