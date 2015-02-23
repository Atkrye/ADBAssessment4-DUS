package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;

public class DialogTurnSkipped extends Dialog {

    private Context context;

	public DialogTurnSkipped(Context context, Skin skin) {
        super("Miss a turn", skin);
        this.context = context;
        //Informs player that they have missed their turn.
        text("Due to circumstances outside our control \n Network Rail would like to apologise for you missing your turn.");
        button("OK", "EXIT");
        align(Align.center);
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
        hide(null);
        context.getGameLogic().setState(GameState.NORMAL);
    }

    @Override
    protected void result(Object obj) {
        //When the button is clicked
        Game.getInstance().getPlayerManager().turnOver(null);
        this.remove();
    }

}
