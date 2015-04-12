package fvs.taxe.controller;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import fvs.taxe.TaxeGame;
import gameLogic.GameState;
import gameLogic.listeners.GameStateListener;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TopBarController {
    //This class controls what is displayed in the topBar, the primary method of informing the players of events that occur in game
    //It's very possible to move away from a topBar orientated design and more to dialogs as we have done, but we decided not to entirely due to the work required.
    public final static int CONTROLS_HEIGHT = 75;

    private Context context;
    private ImageButton endTurnButton;
    private Image endTurnImage;
    private Label flashMessage;
	private Label obstacleLabel;

	private TopBarActor topBarBackground;

    public TopBarController(Context context) {
        this.context = context;
        //This creates a listener that changes the bar colour based on the state that the game is in
        context.getGameLogic().subscribeObstacleChanged(new ObstacleListener(){

			@Override
			public void started(Obstacle obstacle) {
				ObstacleType type = obstacle.getType();						     
				Color color = null;
				switch(type){
				case BLIZZARD:
					color = Color.WHITE;
					break;
				case FLOOD:
					color = Color.valueOf("1079c1");
					break;
				case VOLCANO:
					color = Color.valueOf("ec182c");
					break;
				case EARTHQUAKE:
					color = Color.valueOf("7a370a");
					break;
				}				
				displayObstacleMessage(obstacle.getType().toString() + " in " + obstacle.getStation().getName(), color);
			}

			@Override
			public void ended(Obstacle obstacle) {
			}		        	
		});

        drawFlashLabel();
    }

    public void drawLabels() {
		drawFlashLabel();
		drawObstacleLabel();
	}
    
	public void drawFlashLabel() {
		flashMessage = new Label("", context.getSkin());
		flashMessage.setPosition(690, TaxeGame.HEIGHT - 44);
		flashMessage.setAlignment(0);
		context.getStage().addActor(flashMessage);
	}

	public void drawObstacleLabel() {
		obstacleLabel = new Label("", context.getSkin());
		obstacleLabel.setColor(Color.BLACK);
		obstacleLabel.setPosition(300,TaxeGame.HEIGHT - 44);
		context.getStage().addActor(obstacleLabel);
	}
    
    public void displayObstacleMessage(String message, Color color) {
		// display a message to the obstacle topBar label, with topBarBackground color color and given message
		// wraps automatically to correct size
		obstacleLabel.clearActions();
		obstacleLabel.setText(message);
		obstacleLabel.setColor(Color.BLACK);
		obstacleLabel.pack();
		topBarBackground.setObstacleColor(color);
		topBarBackground.setObstacleWidth(obstacleLabel.getWidth()+20);
		obstacleLabel.addAction(sequence(delay(2f),fadeOut(0.25f), run(new Runnable() {
			public void run() {
				// run action to reset obstacle label after it has finished displaying information
				obstacleLabel.setText("");
				topBarBackground.setObstacleColor(Color.LIGHT_GRAY);
			}
		})));
	}
    
    public void displayFlashMessage(String message, Color color) {
		displayFlashMessage(message, color, 2f);
	}

	public void displayFlashMessage(String message, Color color, float time) {
		flashMessage.setText(message);
		flashMessage.setColor(color);
		flashMessage.addAction(sequence(delay(time), fadeOut(0.25f)));
	}

	public void displayFlashMessage(String message, Color backgroundColor, Color textColor, float time) {
		topBarBackground.setObstacleColor(backgroundColor);
		topBarBackground.setControlsColor(backgroundColor);
		flashMessage.clearActions();
		flashMessage.setText(message);
		flashMessage.setColor(textColor);
		flashMessage.addAction(sequence(delay(time), fadeOut(0.25f), run(new Runnable() {
			public void run() {
				topBarBackground.setControlsColor(Color.LIGHT_GRAY);
				if (obstacleLabel.getActions().size == 0){
					topBarBackground.setObstacleColor(Color.LIGHT_GRAY);
				}
			}
		})));
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
		topBarBackground = new TopBarActor();
		context.getStage().addActor(topBarBackground);
	}

    public void addEndTurnButton() {
        //This method adds an endTurn button to the topBar which allows the user to end their turn
    	
    	Texture buttonText = new Texture(Gdx.files.internal("btn_endturn.png"));
    	endTurnImage = new Image(buttonText);
    	endTurnImage.setWidth(106);
    	endTurnImage.setHeight(37);
    	endTurnImage.setPosition(TaxeGame.WIDTH - 120.0f, TaxeGame.HEIGHT - 56.0f);
    	
        endTurnButton = new ImageButton(context.getSkin());
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
                    endTurnImage.setVisible(true);
                } else {
                    endTurnButton.setVisible(false);
                    endTurnImage.setVisible(false);
                }
            }
        });
        
        context.getStage().addActor(endTurnImage);
        context.getStage().addActor(endTurnButton);
    }
}
