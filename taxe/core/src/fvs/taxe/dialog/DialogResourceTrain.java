package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.resource.PioneerTrain;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceTrain extends Dialog {
	private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();
	private Context context;

	public DialogResourceTrain(Context context, Train train, Skin skin, boolean trainPlaced) {
		super(train.toString(), skin);
		this.context = context;
		text("What do you want to do with this train?");

		boolean isPioneer = false;
		if (train.getClass().equals(PioneerTrain.class)) {
			isPioneer = true;
		}

		//Generates the buttons required to allow the user to interact with the dialog
		if (!trainPlaced) {
			//If the train is not placed, generate button allowing placement
			button("Place at a station", "PLACE");

		} else if (!train.isMoving()) {
			if ((isPioneer && !((PioneerTrain) train).isCreating())) {
				//If the train is not moving then generate button to specify a route
				button("Choose a route", "ROUTE");
				button("Create a connection", "CREATE_CONNECTION");

			} else if (!isPioneer){
				//Generate button to view the route
				button("Choose a route", "ROUTE");
			} 

		} else if (train.getRoute() != null) {
			//If the train has a route then generate button to change the route
			button("Change route", "CHANGE_ROUTE");
			//Generate button to view the route
			button("View Route", "VIEW_ROUTE");
		}

		button("Drop", "DROP");

		button("Cancel", "CLOSE");
	}

	@Override
	public Dialog show(Stage stage) {
		show(stage, null);
		context.getGameLogic().setState(GameState.WAITING);
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}

	@Override
	public void hide() {
		hide(null);

	}

	private void clicked(Button button) {
		for (ResourceDialogClickListener listener : clickListeners) {
			listener.clicked(button);
		}
	}

	public void subscribeClick(ResourceDialogClickListener listener) {
		clickListeners.add(listener);
	}

	@Override
	protected void result(Object obj) {
		context.getGameLogic().setState(GameState.NORMAL);
		if (obj == "CLOSE") {
			this.remove();
		} else if (obj == "DROP") {
			clicked(Button.TRAIN_DROP);
		} else if (obj == "PLACE") {
			clicked(Button.TRAIN_PLACE);
		} else if (obj == "ROUTE") {
			clicked(Button.TRAIN_ROUTE);
		} else if (obj == "VIEW_ROUTE") {
			clicked(Button.VIEW_ROUTE);
		} else if (obj == "CHANGE_ROUTE") {
			clicked(Button.TRAIN_CHANGE_ROUTE);
		} else if (obj == "CREATE_CONNECTION") {
			clicked(Button.TRAIN_CREATE_CONNECTION);
		} 
	}
}
