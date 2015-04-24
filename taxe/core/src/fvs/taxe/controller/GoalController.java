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

public class GoalController {
	
    //This class is in control of drawing all the goals
    private static Context context;
    private Group goalButtons = new Group();
    private Color[] colours = new Color[3];
    
    public boolean exitPressed;
    private Group exitMenu = new Group();
	private GameState prevState;

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

    public void drawHeaderText() {
        //This method draws the header for the goals, this is called at the beginning of every turn
        TaxeGame game = context.getTaxeGame();
        float top = (float) TaxeGame.HEIGHT;
        float y = top - 550.0f - TopBarController.CONTROLS_HEIGHT;
        
        // Draw score labels
        game.batch.begin();
        
        // If player 1 is current player
        if (context.getGameLogic().getPlayerManager().getOtherPlayer().getPlayerNumber() == 2) {
        	
        	TextBounds currentPlayerNameBounds = game.fontTinyBold.getBounds(currentPlayerName());
            
        	// Draw left player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, currentPlayerName(), 60 - currentPlayerNameBounds.width/2, y);
            
            TextBounds currentPlayerScoreBounds = game.fontLight.getBounds(currentPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, currentPlayerScore(), 60 - currentPlayerScoreBounds.width/2, y+50);
            //----------------
            
            TextBounds otherPlayerNameBounds = game.fontTinyBold.getBounds(otherPlayerName());
            
            // Draw right player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, otherPlayerName(), 228 - otherPlayerNameBounds.width/2, y);
            
            TextBounds otherPlayerScoreBounds = game.fontLight.getBounds(otherPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, otherPlayerScore(), 228 - otherPlayerScoreBounds.width/2, y+50);
            //----------------

        }
        else {
        	
        	TextBounds otherPlayerNameBounds = game.fontTinyBold.getBounds(otherPlayerName());
            
        	// Draw left player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, otherPlayerName(), 60 - otherPlayerNameBounds.width/2, y);
            
            TextBounds otherPlayerScoreBounds = game.fontLight.getBounds(otherPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, otherPlayerScore(), 60 - otherPlayerScoreBounds.width/2, y+50);
            //----------------
            
            TextBounds currentPlayerNameBounds = game.fontTinyBold.getBounds(currentPlayerName());
            
            // Draw right player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, currentPlayerName(), 228 - currentPlayerNameBounds.width/2, y);
            
            TextBounds currentPlayerScoreBounds = game.fontLight.getBounds(currentPlayerScore());
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, currentPlayerScore(), 228 - currentPlayerScoreBounds.width/2, y+50);
            //----------------

        }
        
        // Draw player turn label at top
        TextBounds lightBounds = game.fontSmallLight.getBounds("Your Turn, ");
        TextBounds boldBounds = game.fontSmallBold.getBounds(currentPlayerName());
        
        game.fontSmallLight.setColor(Color.WHITE);
        game.fontSmallLight.draw(game.batch, "Your Turn, ", (290/2) - (lightBounds.width + boldBounds.width)/2, (top - (75/2) + (lightBounds.height/2)));
        
        game.fontSmallBold.setColor(Color.WHITE);
        game.fontSmallBold.draw(game.batch, currentPlayerName(), (290/2) - (lightBounds.width + boldBounds.width)/2 + lightBounds.width, (top - (75/2) + (lightBounds.height/2)));
        //---------------
        
        game.batch.end();
    }

    public void setColours(Color[] colours) {
        //This method sets the button colours to be whatever is passed in the parameters
        this.colours = colours;
        //then redraws the current player goals with the new colours
        showCurrentPlayerGoals();
    }

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
    
    public void resumePressed() {
    	System.out.println("resume");
    	context.getGameLogic().setState(prevState);
    	exitMenu.remove();
        exitMenu.clear();
        
        exitPressed = false;
    }

    private String currentPlayerName() {
        //This method is used to draw the current player's name
        return context.getGameLogic().getPlayerManager().getCurrentPlayer().getName();
    }
    
    private String currentPlayerScore() {
        //This method is used to draw the current player's name and their score
        //It was necessary to apply a decimal format to the score as it is stored a double which by default is "0.0", however that is not intuitive for scoring as it should only be integer values.
        DecimalFormat integer = new DecimalFormat("0");
        return integer.format(
                context.getGameLogic().getPlayerManager().getCurrentPlayer().getScore());
    }
    
    private String otherPlayerName() {
        //This method is used to draw the current player's name
        return context.getGameLogic().getPlayerManager().getOtherPlayer().getName();
    }
    
    private String otherPlayerScore() {
        //This method is used to draw the current player's name and their score
        //It was necessary to apply a decimal format to the score as it is stored a double which by default is "0.0", however that is not intuitive for scoring as it should only be integer values.
        DecimalFormat integer = new DecimalFormat("0");
        return integer.format(
                context.getGameLogic().getPlayerManager().getOtherPlayer().getScore());
    }
}
