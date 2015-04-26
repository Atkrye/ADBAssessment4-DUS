package fvs.taxe.clickListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.SoundPlayer;
import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DialogResourceSkipped;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.player.Player;
import gameLogic.resource.Skip;

//Responsible for checking whether the Skip is clicked.
public class SkipClicked extends ClickListener {

    Context context;
    Skip skip;
    private boolean displayingMessage;

    public SkipClicked(Context context, Skip skip) {
        this.context = context;
        this.skip = skip;
        displayingMessage = false;
    }


    public void clicked(InputEvent event, float x, float y) {
        //When skip is clicked it checks whether the game is in the normal state
        if (Game.getInstance().getState() == GameState.NORMAL) {

            // current player can't be passed in as it changes so find out current player at this instant
            Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();
            
            //plays skip button sound
            SoundPlayer.playSound(5);
            //Creates a dialog when skip is clicked allowing the user to select what they want to do with the resource
            DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, skip);
            DialogResourceSkipped dia = new DialogResourceSkipped(context);
            dia.show(context.getStage());
            dia.subscribeClick(listener);
        }
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor trainActor) {
        //This is used for mouseover events for Skips
        //This shows the message if there is not one currently being displayed
        if (!displayingMessage) {
            displayingMessage = true;
            if (Game.getInstance().getState() == GameState.NORMAL) {
                context.getTopBarController().displayMessage("Force your opponent to skip a turn.", Color.BLACK);


            }
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor trainActor) {
        //This is used for mouseover events for Skips
        //This hides the message currently in the topBar if one is being displayed
        if (displayingMessage) {
            displayingMessage = false;
            if (Game.getInstance().getState() == GameState.NORMAL) {
                //If the game state is normal then the topBar is cleared by passing it an empty string to display for 0 seconds
                context.getTopBarController().clearMessage();
            }
        }
    }
}
