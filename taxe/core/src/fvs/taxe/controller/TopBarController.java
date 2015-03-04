package fvs.taxe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.TaxeGame;
import gameLogic.GameState;
import gameLogic.listeners.GameStateListener;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TopBarController {
    //This class controls what is displayed in the topBar, the primary method of informing the players of events that occur in game
    //It's very possible to move away from a topBar orientated design and more to dialogs as we have done, but we decided not to entirely due to the work required.
    public final static int CONTROLS_HEIGHT = 75;

    private Context context;
    private Color controlsColor = Color.LIGHT_GRAY;
    private TextButton endTurnButton;
    private Label flashMessage;

    public TopBarController(Context context) {
        this.context = context;
        //This creates a listener that changes the bar colour based on the state that the game is in
        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                switch (state) {
                    case ANIMATING:
                        controlsColor = Color.GREEN;
                        break;

                    default:
                        controlsColor = Color.LIGHT_GRAY;
                        break;
                }
            }
        });

        createFlashActor();
    }

    private void createFlashActor() {
        flashMessage = new Label("", context.getSkin());
        flashMessage.setPosition(690, TaxeGame.HEIGHT - 44);
        context.getStage().addActor(flashMessage);
    }


    public void displayFlashMessage(String message, Color color) {
        //This method displays a message in the topBar for the default 1.75 seconds
        displayFlashMessage(message, color, 1.75f);
    }

    public void displayFlashMessage(String message, Color color, float time) {
        //This method also displays a message in the topBar, but for the amount of time specified in the parameters
        flashMessage.setText(message);
        flashMessage.setColor(color);
        flashMessage.addAction(sequence(delay(time), fadeOut(0.25f)));
    }

    public void displayMessage(String message, Color color){
        //This method sets a permanent message until it is overwritten
        flashMessage.setText(message);
        flashMessage.setColor(color);
    }

    public void clearMessage(){
        //This method clears the current message
        flashMessage.setText("");
        flashMessage.setColor(Color.LIGHT_GRAY);
    }

    public void drawBackground() {
        TaxeGame game = context.getTaxeGame();
        
        Texture topbarTexture = new Texture(Gdx.files.internal("Topbar.png"));
        
        game.batch.begin();
        game.batch.draw(topbarTexture, 290, TaxeGame.HEIGHT - CONTROLS_HEIGHT);
        game.batch.end();
        
    }

    public void addEndTurnButton() {

        //This method adds an endTurn button to the topBar which allows the user to end their turn
        endTurnButton = new TextButton("End Turn", context.getSkin());
        endTurnButton.setPosition(TaxeGame.WIDTH - 120.0f, TaxeGame.HEIGHT - 56.0f);
        endTurnButton.setSize(106, 37);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //This sets the turn to be over in the backend
                context.getGameLogic().getPlayerManager().turnOver(context);
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                //This sets whether or not the endTurn button is displayed based on the state of the game
                //This is important as it prevents players from ending their turn mid placement or mid routing
                if (state == GameState.NORMAL) {
                    endTurnButton.setVisible(true);
                } else {
                    endTurnButton.setVisible(false);
                }
            }
        });

        context.getStage().addActor(endTurnButton);
    }
}
