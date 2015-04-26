package fvs.taxe.controller;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import fvs.taxe.MusicPlayer;
import fvs.taxe.SoundPlayer;
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

/**Controller for the Top Bar of the GUI, changes the Top Bar.*/
public class TopBarController {
	
	/**The height of the Top Bar.*/
	public final static int CONTROLS_HEIGHT = 75;

	/**The Game Context.*/
	private Context context;
	
	/**The end Turn Button used for the player to End the Turn.*/
	private ImageButton endTurnButton;
	
	/** Image that represents the ending turn button*/
	private Image endTurnImage;
	
	/**Label for displaying a message to the player.*/
	private Label flashMessage;
    
	/**Label for display obstacle events to the player.*/
	private Label obstacleLabel;

	/**Actor for the background to the Top Bar*/
	private TopBarActor topBarBackground;

	/**Instantiation method sets up a listener for Events starting to display the Event message in the Top Bar.
	 * @param context The game Context.
	 */
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
				if(!obstacle.getStartFlag())
				{
					displayObstacleMessage(obstacle.getType().toString() + " in " + obstacle.getStation().getName(), color);
				}
				else
				{
					obstacle.setStartFlag(false);
				}
			}

			@Override
			public void ended(Obstacle obstacle) {
			}		        	
		});

        drawFlashLabel();
    }

    /**This method calls the label drawing methods*/
    public void drawLabels() {
		drawFlashLabel();
		drawObstacleLabel();
	}
    
    /**This method draws a label for a message*/
	public void drawFlashLabel() {
		flashMessage = new Label("", context.getSkin());
		flashMessage.setPosition(690, TaxeGame.HEIGHT - 44);
		flashMessage.setAlignment(0);
		context.getStage().addActor(flashMessage);
	}

	/**This method draws a label for obstacle messages*/
	public void drawObstacleLabel() {
		obstacleLabel = new Label("", context.getSkin());
		obstacleLabel.setColor(Color.BLACK);
		obstacleLabel.setPosition(300,TaxeGame.HEIGHT - 44);
		context.getStage().addActor(obstacleLabel);
	}
    
	/**This method displays a message in the Top Bar with a specified background Color.
	 * @param message The message to be displayed.
	 * @param color The background color to be displayed behind the message.
	 */
    public void displayObstacleMessage(String message, Color color) {
		// display a message to the obstacle topBar label, with topBarBackground color color and given message
		obstacleLabel.clearActions();
		obstacleLabel.setText(message);
		obstacleLabel.setColor(color);
		obstacleLabel.addAction(sequence(delay(2f),fadeOut(0.25f)));
	}
    
    /**This method displays a message of a certain color in the Top Bar.
	 * @param message The message to be displayed.
	 * @param color The color of the message to be displayed.
	 */
    public void displayFlashMessage(String message, Color color) {
		displayFlashMessage(message, color, 2f);
	}

    /**This method displays a message of a certain color in the Top Bar for a certain amount of time.
	 * @param message The message to be displayed.
	 * @param color The color of the message to be displayed.
	 * @param time The length of time to display the message, in seconds.
	 */
	public void displayFlashMessage(String message, Color color, float time) {
		flashMessage.setText(message);
		flashMessage.setColor(color);
		flashMessage.addAction(sequence(delay(time), fadeOut(0.25f)));
	}
	
	/** This method sets a permanent message until it is overwritten
	 * @param message Message to set the top bar to
	 * @param color Color of the text 
	 */
    public void displayMessage(String message, Color color){
        flashMessage.setText(message);
        flashMessage.setColor(color);
    }

    /** Method clears the current message on the top bar background */
    public void clearMessage(){
        flashMessage.setText("");
        flashMessage.setColor(Color.LIGHT_GRAY);
    }

    /**This method adds the background to the game.*/
    public void drawBackground() {
		topBarBackground = new TopBarActor();
		context.getStage().addActor(topBarBackground);
	}

    /**This method adds an End Turn button to the game that captures an on click event and notifies the game when the turn is over.*/
	
    public void addEndTurnButton() {
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
            	//plays button sound
            	SoundPlayer.playSound(1);
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
