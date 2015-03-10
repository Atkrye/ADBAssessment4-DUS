package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import fvs.taxe.TaxeGame;
import fvs.taxe.clickListener.EngineerClicked;
import fvs.taxe.clickListener.ObstacleClicked;
import fvs.taxe.clickListener.SkipClicked;
import fvs.taxe.clickListener.TrainClicked;
import gameLogic.player.Player;
import gameLogic.listeners.PlayerChangedListener;
import gameLogic.resource.*;

public class ResourceController {
    private Context context;
    private Group resourceButtons = new Group();

    public ResourceController(final Context context) {
        this.context = context;
        //Subscribes to the listener so that the resources are redrawn whenever the player changes.
        context.getGameLogic().getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                drawPlayerResources(context.getGameLogic().getPlayerManager().getCurrentPlayer());
            }
        });
    }

    public void drawHeaderText() {
        //This draws the header text for the resources, alter this method if you want to change what is displayed
//        TaxeGame game = context.getTaxeGame();
//
//        game.batch.begin();
//        game.fontSmall.setColor(Color.BLACK);
//        game.fontSmall.draw(game.batch, "Unplaced Resources:", 10.0f, (float) TaxeGame.HEIGHT - 250.0f);
//        game.batch.end();
    }

    public void drawPlayerResources(Player player) {
        //This method draws the buttons representing the player's resources, alter this method if you want to change how resources are represented.
        float top = (float) TaxeGame.HEIGHT;
        float x = 10.0f;
        //The value of y is set based on how much space the header texts and goals have taken up (assumed that 3 goals are always present for a consistent interface)
        float y = top - 280.0f;
        y -= 50;

        //Clears the resource buttons so that the other player's resources are not displayed
        resourceButtons.remove();
        resourceButtons.clear();

        for (final Resource resource : player.getResources()) {
            //This if statement is used to determine what type of resource is being drawn. This is necessary as each resource needs to have a different click listener assigned to its button.
            if (resource instanceof Train) {
                Train train = (Train) resource;

                // Don't show a button for trains that have been placed, trains placed are still part of the 7 total upgrades
                //If a train is not placed then its position is null so this is used to check
                if (train.getPosition() == null) {
                    //Creates a clickListener for the button and adds it to the list of buttons
                    TrainClicked listener = new TrainClicked(context, train);
                    TextButton button = new TextButton(resource.toString(), context.getSkin());
                    button.setPosition(x, y);
                    button.addListener(listener);
                    resourceButtons.addActor(button);
                    y -= 40;
                }

            } else if (resource instanceof Obstacle) {
                //Creates a clickListener for the button and adds it to the list of buttons
                Obstacle obstacle = (Obstacle) resource;
                ObstacleClicked listener = new ObstacleClicked(context, obstacle);
                TextButton button = new TextButton("Obstacle", context.getSkin());
                button.setPosition(x, y);
                button.addListener(listener);
                resourceButtons.addActor(button);

                y -= 30;

            } else if (resource instanceof Skip) {
                //Creates a clickListener for the button and adds it to the list of buttons
                Skip skip = (Skip) resource;
                SkipClicked listener = new SkipClicked(context, skip);
                TextButton button = new TextButton("Skip", context.getSkin());
                button.setPosition(x, y);
                button.addListener(listener);
                resourceButtons.addActor(button);

                y -= 30;

            } else if (resource instanceof Engineer) {
                //Creates a clickListener for the button and adds it to the list of buttons
                Engineer engineer = (Engineer) resource;
                EngineerClicked listener = new EngineerClicked(context, engineer);
                TextButton button = new TextButton("Engineer", context.getSkin());
                button.setPosition(x, y);
                button.addListener(listener);
                resourceButtons.addActor(button);

                y -= 30;
            }

        }
        //Adds all generated buttons to the stage
        context.getStage().addActor(resourceButtons);
    }

}
