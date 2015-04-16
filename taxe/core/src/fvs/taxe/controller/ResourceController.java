package fvs.taxe.controller;


import fvs.taxe.clickListener.SkipClicked;
import fvs.taxe.clickListener.TrainClicked;
import gameLogic.Game;
import gameLogic.listeners.PlayerChangedListener;
import gameLogic.player.Player;
import gameLogic.resource.Resource;
import gameLogic.resource.Skip;
import gameLogic.resource.Train;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class ResourceController {
    private Context context;
    private Group resourceButtons = new Group();
    private Group resourceImages = new Group();

    public ResourceController(final Context context) {
        this.context = context;
        //Subscribes to the listener so that the resources are redrawn whenever the player changes.
        context.getGameLogic().getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                drawPlayerResources(Game.getInstance().getPlayerManager().getCurrentPlayer());
            }
        });
    }

    public void drawPlayerResources(Player player) {
        //This method draws the buttons representing the player's resources, alter this method if you want to change how resources are represented.
        float x = 10.0f;
        //The value of y is set based on how much space the header texts and goals have taken up (assumed that 3 goals are always present for a consistent interface)
        float y = 300.0f;
        
        int xCounter = 0;

        //Clears the resource buttons so that the other player's resources are not displayed
        resourceButtons.remove();
        resourceButtons.clear();
        
        resourceImages.remove();
        resourceImages.clear();

        for (final Resource resource : player.getResources()) {
            
        	//This if statement is used to determine what type of resource is being drawn. This is necessary as each resource needs to have a different click listener assigned to its button.
            // draw train boxes first, then skip box after
        	if (resource instanceof Train) {
                Train train = (Train) resource;

                // Don't show a button for trains that have been placed, trains placed are still part of the 7 total upgrades
                //If a train is not placed then its position is null so this is used to check
                if (train.getPosition() == null) {
                    //Creates a clickListener for the button and adds it to the list of buttons
                    TrainClicked listener = new TrainClicked(context, train);
                    //TextButton button = new TextButton(resource.toString(), context.getSkin());
                    ImageButton button = new ImageButton(context.getSkin());
                    button.setWidth(82);
                    button.setHeight(99);
                    button.addListener(listener);
                    resourceButtons.addActor(button);
                    button.setPosition(x, y);
                    xCounter += 1;
                    
                    System.out.println(resource.toString());
                    
                    Texture buttonText = null;
                    Image buttonImage;
                    
                    if (resource.toString().equals("Bullet Train")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_bullet.png"));
                    }
                    else if (resource.toString().equals("Diesel Train")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_diesel.png"));
                    }
                    else if (resource.toString().equals("Electric Train")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_electric.png"));
                    }
                    else if (resource.toString().equals("Kamikaze")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_kamikaze.png"));
                    }
                    else if (resource.toString().equals("MagLev Train")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_maglev.png"));
                    }
                    else if (resource.toString().equals("Nuclear Train")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_nuclear.png"));
                    }
                    else if (resource.toString().equals("Petrol Train")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_petrol.png"));
                    }
                    else if (resource.toString().equals("Pioneer")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_pioneer.png"));
                    }
                    else if (resource.toString().equals("Steam Train")) {
                    	buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_steam.png"));
                    }
                    
                    buttonImage = new Image(buttonText);
                    buttonImage.setWidth(82);
                    buttonImage.setHeight(99);
                    resourceImages.addActor(buttonImage);
                    buttonImage.setPosition(x, y);
                    
                    if (xCounter == 3) {
                    	y -= 105;
                    	x = 10.0f;
                    	xCounter = 0;
                    }
                    else {
                    	x += button.getWidth() + 10;
                    }
                    
                }

            } 
        }
        
        for (final Resource resource : player.getResources()) {
            
        	//This if statement is used to determine what type of resource is being drawn. This is necessary as each resource needs to have a different click listener assigned to its button.
            // NEED to draw train boxes first, then skip box after
        	if (resource instanceof Skip) {
                //Creates a clickListener for the button and adds it to the list of buttons
                Skip skip = (Skip) resource;
                SkipClicked listener = new SkipClicked(context, skip);
                ImageButton button = new ImageButton(context.getSkin());
                button.setWidth(267);
                button.setHeight(40);
                button.addListener(listener);
                resourceButtons.addActor(button);
                
                if (xCounter == 0) {
                	x = 10.0f;
                	y += 60;
                }
                else {
                	x = 10.0f;
                	y -= 45;
                }
                
                button.setPosition(x, y);
                
                Texture buttonText = new Texture(Gdx.files.internal("Resource Buttons/btn_skip.png"));
                Image buttonImage = new Image(buttonText);
                buttonImage.setWidth(267);
                buttonImage.setHeight(40);
                resourceImages.addActor(buttonImage);
                buttonImage.setPosition(x, y);
                
                
            } 
        }

        //Adds all generated buttons to the stage
        context.getStage().addActor(resourceImages);
        context.getStage().addActor(resourceButtons);
    }

}
