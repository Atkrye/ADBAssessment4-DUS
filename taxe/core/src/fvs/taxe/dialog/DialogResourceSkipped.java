package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import fvs.taxe.controller.Context;
import gameLogic.GameState;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceSkipped extends Dialog {
    private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();
	private Context context;

    public DialogResourceSkipped(Context context) {
        super("Skip", context.getSkin());
        this.context = context;
        text("What do you want to do with this resource?");
        //Generates all the buttons required to allow the user to interact with the dialog
        button("Use", "USE");
        button("Drop", "DROP");
        button("Cancel", "CLOSE");
    }

    @Override
    public Dialog show(Stage stage) {
        //Shows the dialog
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

    public void subscribeClick(ResourceDialogClickListener listener) {
        //Adds listeners to the result of the dialog
        clickListeners.add(listener);
    }

    private void clicked(Button button) {
        //Informs all listeners what the result of the dialog is
        for (ResourceDialogClickListener listener : clickListeners) {
            listener.clicked(button);
        }
    }


    @Override
    protected void result(Object obj) {
        //Calls the clicked routine and passes it the button that the user clicked
        if (obj == "EXIT") {
            Gdx.app.exit();
        } else if (obj == "DROP") {
            clicked(Button.SKIP_DROP);
        } else if (obj == "USE") {
            clicked(Button.SKIP_RESOURCE);
        }
    }

}
