package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.TaxeGame;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;

public class DialogEndGame extends Dialog {
    //private TaxeGame game;

    private Context context;

	public DialogEndGame(Context context, TaxeGame game, PlayerManager pm, Skin skin) {
        super("GAME OVER", skin);
       // this.game = game;
        this.context = context;
        double highScore = 0;
        int playerNum = 0;
        for (Player player : pm.getAllPlayers()) {
            //Checks each player's score
            if (player.getScore() > highScore) {
                highScore = player.getScore();
                //Need to add one as playerNumber is 0-based indexing
                playerNum = player.getPlayerNumber();
            }else if (player.getScore() == highScore){
                playerNum =0;
            }
        }

        //Declares the winner based on who received the highest score
        //If adding multiple players then this would need to be changed to reflect that
        if (playerNum != 0) {
            text("PLAYER " + playerNum + " WINS!");
        } else {
            //If no player has the high score then a tie is declared
            text("IT'S A TIE!");
        }

        button("Exit", "EXIT");
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
        context.getGameLogic().setState(GameState.NORMAL);
    }

    @Override
    protected void result(Object obj) {
        if (obj == "EXIT") {
            //Closes the app and disposes any machine resources used
            Gdx.app.exit();
        }
    }
}
