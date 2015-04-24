package fvs.taxe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.GameScreen;
import fvs.taxe.TaxeGame;
import fvs.taxe.clickListener.GoalClickListener;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.player.Player;
import gameLogic.listeners.PlayerChangedListener;
import gameLogic.player.PlayerManager;
import gameLogic.goal.Goal;

import java.text.DecimalFormat;

/**Controller for updating UI with goals.*/
public class GoalController {
	
	/**The context of the Game.*/
	private static Context context;
	
	/**A group of buttons used for controlling the goals,*/
	private Group goalButtons = new Group();
	
	/** The array of colours used for drawing the goal station highlights */
    private Color[] colours = new Color[3];
    
    /** Whether the exit button has been pressed */
    public boolean exitPressed;
    
    /** The group of actors for the exit menu */
    private Group exitMenu = new Group();
    
    /** The previous state if exit has been pressed, used to restore the state when exit finished*/
	private GameState prevState;

	/**The instantation method sets up listeners for Goal changes and Player changes so that it can update the UI accordingly,
	 * @param context The context of the game.
	 */
    public GoalController(Context context) {
        GoalController.context = context;
        //Makes the system redraw the currentGoals whenever the player changes.
        context.getGameLogic().getPlayerManager()
                .subscribePlayerChanged(new PlayerChangedListener() {
					@Override
					public void changed() {
						showCurrentPlayerGoals();
					}
				});
    }

    /**This method draws the header text (e.g. the current Player) for the goals.*/
    public void drawHeaderText() {
        //This method draws the header for the goals, this is called at the beginning of every turn
        TaxeGame game = context.getTaxeGame();
        float top = (float) TaxeGame.HEIGHT;
        float y = top - 550.0f - TopBarController.CONTROLS_HEIGHT;
        
        // Draw score labels
        game.batch.begin();
        
        // If player 1 is current player
        if (context.getGameLogic().getPlayerManager().getOtherPlayer().getPlayerNumber() == 2) {
        	
        	TextBounds currentPlayerNameBounds = game.fontTinyBold.getBounds(getCurrentPlayerName());
            
        	// Draw left player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, getCurrentPlayerName(), 60 - currentPlayerNameBounds.width/2, y);
            
            TextBounds currentPlayerScoreBounds = game.fontLight.getBounds(getCurrentPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, getCurrentPlayerScore(), 60 - currentPlayerScoreBounds.width/2, y+50);
            //----------------
            
            TextBounds otherPlayerNameBounds = game.fontTinyBold.getBounds(getOtherPlayerName());
            
            // Draw right player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, getOtherPlayerName(), 228 - otherPlayerNameBounds.width/2, y);
            
            TextBounds otherPlayerScoreBounds = game.fontLight.getBounds(getOtherPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, getOtherPlayerScore(), 228 - otherPlayerScoreBounds.width/2, y+50);
            //----------------

        }
        else {
        	
        	TextBounds otherPlayerNameBounds = game.fontTinyBold.getBounds(getOtherPlayerName());
            
        	// Draw left player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, getOtherPlayerName(), 60 - otherPlayerNameBounds.width/2, y);
            
            TextBounds otherPlayerScoreBounds = game.fontLight.getBounds(getOtherPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, getOtherPlayerScore(), 60 - otherPlayerScoreBounds.width/2, y+50);
            //----------------
            
            TextBounds currentPlayerNameBounds = game.fontTinyBold.getBounds(getCurrentPlayerName());
            
            // Draw right player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, getCurrentPlayerName(), 228 - currentPlayerNameBounds.width/2, y);
            
            TextBounds currentPlayerScoreBounds = game.fontLight.getBounds(getCurrentPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, getCurrentPlayerScore(), 228 - currentPlayerScoreBounds.width/2, y+50);
            //----------------

        }
        
        // Draw player turn label at top
        TextBounds lightBounds = game.fontSmallLight.getBounds("Your Turn, ");
        TextBounds boldBounds = game.fontSmallBold.getBounds(getCurrentPlayerName());
        
        game.fontSmallLight.setColor(Color.WHITE);
        game.fontSmallLight.draw(game.batch, "Your Turn, ", (290/2) - (lightBounds.width + boldBounds.width)/2, (top - (75/2) + (lightBounds.height/2)));
        
        game.fontSmallBold.setColor(Color.WHITE);
        game.fontSmallBold.draw(game.batch, getCurrentPlayerName(), (290/2) - (lightBounds.width + boldBounds.width)/2 + lightBounds.width, (top - (75/2) + (lightBounds.height/2)));
        //---------------
        
        game.batch.end();
    }

    public void setColours(Color[] colours) {
        //This method sets the button colours to be whatever is passed in the parameters
        this.colours = colours;
        //then redraws the current player goals with the new colours
        showCurrentPlayerGoals();
    }

    /**This method draws the current player's goals in the game UI.*/
    public void showCurrentPlayerGoals() {
        //This method displays the player's current goals
        //First the current goals are cleared so that the other player's goals are not displayed too.
        goalButtons.remove();
        goalButtons.clear();

        PlayerManager pm = context.getGameLogic().getPlayerManager();
        Player currentPlayer = pm.getCurrentPlayer();

        float top = (float) TaxeGame.HEIGHT;
        float x = 10.0f;
        //This value is set by subtracting the total height of the player header and the goal header, change this if you want to adjust the position of the goals or other elements in the GUI
        float y = top - 25.0f - TopBarController.CONTROLS_HEIGHT;

        int index = 0;

        for (Goal goal : currentPlayer.getGoals()) {
            //Necessary to check whether the goals are complete as completed goals are not removed from the player's list of goals, without this check complete goals would also be displayed.
            if (!goal.getComplete()) {

                y -= 50;
                ImageTextButton button = new ImageTextButton(
                        goal.baseGoalString() + "\n" + goal.bonusString(), context.getSkin());
                button.getLabel().setAlignment(Align.left);
                //The goal buttons are scaled so that they do not overlap nodes on the map, this was found to be necessary after changing the way goals were displayed
                float scaleFactor = 0.8f;
                button.getLabel().setFontScale(scaleFactor, scaleFactor);
                button.setHeight(scaleFactor * button.getHeight());
                button.setWidth(scaleFactor * button.getWidth());

                //Adds the listener to the button so that it will inform the correct parts of the system
                GoalClickListener listener = new GoalClickListener(context, goal);
                button.setPosition(x, y);

                if (colours[index++] != null) {
                    //Sets the colour based on the values in the array. If the train is routing then these colours will match nodes on the map, otherwise they are all grey.
                    button.setColor(colours[index - 1]);
                }
                button.addListener(listener);
                goalButtons.addActor(button);
            }
        }
        context.getStage().addActor(goalButtons);
    }
    
    /** Draw the controls related to recording, saving and exiting */
    public void showControls() {
    	// Draw record, save and exit controls
    	
    	Texture recordButtonText = new Texture(Gdx.files.internal("btn_record.png"));
        Image recordButtonImage = new Image(recordButtonText);
        recordButtonImage.setWidth(77);
        recordButtonImage.setHeight(31);
        recordButtonImage.setPosition(10, 10);
        context.getStage().addActor(recordButtonImage);
        
        Texture saveButtonText = new Texture(Gdx.files.internal("btn_save.png"));
        Image saveButtonImage = new Image(saveButtonText);
        saveButtonImage.setWidth(77);
        saveButtonImage.setHeight(31);
        saveButtonImage.setPosition(107, 10);
        context.getStage().addActor(saveButtonImage);
        
        Texture exitButtonText = new Texture(Gdx.files.internal("btn_exit.png"));
        Image exitButtonImage = new Image(exitButtonText);
        exitButtonImage.setWidth(77);
        exitButtonImage.setHeight(31);
        exitButtonImage.setPosition(203, 10);
        context.getStage().addActor(exitButtonImage);
        
        ImageButton recordButton = new ImageButton(context.getSkin());
        recordButton.setWidth(77);
        recordButton.setHeight(31);
        recordButton.setPosition(10, 10);
        recordButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		if(!GameScreen.instance.isRecording())
        		{
        			GameScreen.instance.startRecording();
        		}
        		else
        		{
                    GameScreen.instance.stopRecording();
        		}
            };
        } );
        context.getStage().addActor(recordButton);
        
        ImageButton saveButton = new ImageButton(context.getSkin());
        saveButton.setWidth(77);
        saveButton.setHeight(31);
        saveButton.setPosition(107, 10);
        saveButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("save");
            };
        } );
        context.getStage().addActor(saveButton);
        
        ImageButton exitButton = new ImageButton(context.getSkin());
        exitButton.setWidth(77);
        exitButton.setHeight(31);
        exitButton.setPosition(203, 10);
        exitButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
                exitPressed();
            };
        } );
        context.getStage().addActor(exitButton);
    	
    }
    
    /** What occurs when the exit button has pressed, setup the exit menu and register clicks */
    private void exitPressed() {
    	System.out.println("exit");
    	prevState = context.getGameLogic().getState();
    	context.getGameLogic().setState(GameState.WAITING);
    	exitPressed = true;
    	
    	exitMenu.remove();
        exitMenu.clear();
    	
    	Texture exitTexture = new Texture(Gdx.files.internal("exitgame.png"));
    	Image exitImage = new Image(exitTexture);
    	exitImage.setPosition(0, 0);
    	exitMenu.addActor(exitImage);
    	
    	ImageButton resumeButton = new ImageButton(context.getSkin());
    	resumeButton.setWidth(183);
    	resumeButton.setHeight(54);
    	resumeButton.setPosition(350, 292);
    	resumeButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		resumePressed();
            };
        } );
        exitMenu.addActor(resumeButton);
        
        ImageButton saveButton = new ImageButton(context.getSkin());
        saveButton.setWidth(183);
        saveButton.setHeight(54);
        saveButton.setPosition(552, 292);
        saveButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		Game.getInstance().save();
            };
        } );
        exitMenu.addActor(saveButton);
        
        ImageButton exitButton = new ImageButton(context.getSkin());
        exitButton.setWidth(183);
        exitButton.setHeight(54);
        exitButton.setPosition(754, 292);
        exitButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		System.out.println("exit");
        		exitPressed = false;
        		Gdx.app.exit();
            };
        } );
        exitMenu.addActor(exitButton);
        
    	context.getStage().addActor(exitMenu);
    }
    
    /** If resume is pressed when the game has the exit menu pressed, restore the game to its previous state*/
    public void resumePressed() {
    	System.out.println("resume");
    	context.getGameLogic().setState(prevState);
    	exitMenu.remove();
        exitMenu.clear();
        
        exitPressed = false;
    }

    private String getCurrentPlayerName() {
        return context.getGameLogic().getPlayerManager().getCurrentPlayer().getName();
    }
   
     /**This method is used to draw the current player's name and their score */
    private String getCurrentPlayerScore() {
       //It was necessary to apply a decimal format to the score as it is stored a double which by default is "0.0", however that is not intuitive for scoring as it should only be integer values.
        DecimalFormat integer = new DecimalFormat("0");
        return integer.format(
                context.getGameLogic().getPlayerManager().getCurrentPlayer().getScore());
    }
    
    private String getOtherPlayerName() {
        return context.getGameLogic().getPlayerManager().getOtherPlayer().getName();
    }
    
    private String getOtherPlayerScore() {
        //This method is used to draw the current player's name and their score
        //It was necessary to apply a decimal format to the score as it is stored a double which by default is "0.0", however that is not intuitive for scoring as it should only be integer values.
        DecimalFormat integer = new DecimalFormat("0");
        return integer.format(
                context.getGameLogic().getPlayerManager().getOtherPlayer().getScore());
    }
}
