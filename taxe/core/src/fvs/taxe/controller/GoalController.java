package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import fvs.taxe.TaxeGame;
import fvs.taxe.clickListener.GoalClickListener;
import gameLogic.player.Player;
import gameLogic.listeners.PlayerChangedListener;
import gameLogic.player.PlayerManager;
import gameLogic.goal.Goal;

import java.text.DecimalFormat;

public class GoalController {
	
    //This class is in control of drawing all the goals
    private Context context;
    private Group goalButtons = new Group();
    private Color[] colours = new Color[3];

    public GoalController(Context context) {
        this.context = context;
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
        float y = top - 530.0f - TopBarController.CONTROLS_HEIGHT;
        
        // Draw score labels
        game.batch.begin();
        
        TextBounds currentPlayerNameBounds = game.fontTinyBold.getBounds(currentPlayerName());
        TextBounds currentPlayerScoreBounds = game.fontLight.getBounds(currentPlayerScore());
        TextBounds otherPlayerNameBounds = game.fontTinyBold.getBounds(otherPlayerName());
        TextBounds otherPlayerScoreBounds = game.fontLight.getBounds(otherPlayerScore());
        
        // If player 1 is current player
        if (context.getGameLogic().getPlayerManager().getOtherPlayer().getPlayerNumber() == 2) {
            
        	// Draw left player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, currentPlayerName(), 60 - currentPlayerNameBounds.width/2, y);
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, currentPlayerScore(), 60 - currentPlayerScoreBounds.width/2, y+50);
            //----------------
            
            // Draw right player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, otherPlayerName(), 228 - otherPlayerNameBounds.width/2, y);
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, otherPlayerScore(), 228 - otherPlayerScoreBounds.width/2, y+50);
            //----------------
        }
        else {
            
        	// Draw left player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, otherPlayerName(), 60 - otherPlayerNameBounds.width/2, y);
            
            game.fontLight.setColor(Color.WHITE);
            game.fontLight.draw(game.batch, otherPlayerScore(), 60 - otherPlayerScoreBounds.width/2, y+50);
            //----------------
            
            // Draw right player score labels
            game.fontTinyBold.setColor(Color.WHITE);
            game.fontTinyBold.draw(game.batch, currentPlayerName(), 228 - currentPlayerNameBounds.width/2, y);
            
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

    private String currentPlayerName() {
        //This method is used to draw the current player's name
        return "PLAYER " +
                context.getGameLogic().getPlayerManager().getCurrentPlayer().getPlayerNumber();
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
        return "PLAYER " +
                context.getGameLogic().getPlayerManager().getOtherPlayer().getPlayerNumber();
    }
    
    private String otherPlayerScore() {
        //This method is used to draw the current player's name and their score
        //It was necessary to apply a decimal format to the score as it is stored a double which by default is "0.0", however that is not intuitive for scoring as it should only be integer values.
        DecimalFormat integer = new DecimalFormat("0");
        return integer.format(
                context.getGameLogic().getPlayerManager().getOtherPlayer().getScore());
    }
}
