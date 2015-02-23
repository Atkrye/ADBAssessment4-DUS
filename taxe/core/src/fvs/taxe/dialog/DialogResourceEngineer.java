package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.resource.Engineer;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceEngineer extends Dialog {
    private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();
	private Context context;

    public DialogResourceEngineer(Context context, Engineer engineer, Skin skin) {
        super(engineer.toString(), skin);
        //Generates all the buttons that allow the user to interact with the dialog
        this.context = context;
        text("What do you want to do with this engineer?");
        button("Repair a blocked connection", "PLACE");
        button("Drop", "DROP");
        button("Cancel", "CLOSE");
    }

    @Override
    public Dialog show(Stage stage) {
        //Displays the dialog on screen
        show(stage, null);
        context.getGameLogic().setState(GameState.WAITING);
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
        //Informs all listeners which button has been clicked
        for (ResourceDialogClickListener listener : clickListeners) {
            listener.clicked(button);
        }
    }

    public void subscribeClick(ResourceDialogClickListener listener) {
        //Adds an external listener to the result of the dialog
        clickListeners.add(listener);
    }

    @Override
    protected void result(Object obj) {
        //Does things based on which button the user presses
        if (obj == "CLOSE") {
            this.remove();
        } else if (obj == "DROP") {
            clicked(Button.ENGINEER_DROP);
        } else if (obj == "PLACE") {
            clicked(Button.ENGINEER_USE);
        }
    }
}
