package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.clickListener.TrainClicked;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.resource.Train;

import java.util.ArrayList;

public class DialogStationMultitrain extends Dialog {
    //This class is used to create a dialog for when there are multiple trains in one location
    private Context context;

    public DialogStationMultitrain(ArrayList<Train> trains, Skin skin, Context context) {
    	
    	
        //This constructor is called when there are multiple blocked trains sitting on top of each other
        super("Select Train", skin);
        this.context = context;
        text("Choose which train you would like");

        //Generates the text string for each of the trains passed to the method and creates a button for them
        for (Train train : trains) {
            String destination = "";
            if (train.getFinalDestination() != null) {
                destination = " to " + train.getFinalDestination().getName();
            }
            button(train.getName() + destination + " (Player " + train.getPlayer().getPlayerNumber() + ")", train);
            getButtonTable().row();
        }

        button("Cancel", "CANCEL");
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

    @Override
    protected void result(Object obj) {
        if (obj == "CANCEL") {
            //If the user clicks cancel then it deletes the dialog
            this.remove();
        } else {
            //Simulate click on the train
            TrainClicked clicker = new TrainClicked(context, (Train) obj);
            //This is a small hack, by setting the value of the simulated x value to -1, we can use this to check whether or not
            //This dialog has been opened before. If this was not here then this dialog and trainClicked would get stuck in an endless loop!
            clicker.clicked(null, -1, 0);
        }
    }
}
