package fvs.taxe.clickListener;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.SoundPlayer;
import fvs.taxe.Tooltip;
import fvs.taxe.actor.StationActor;
import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DialogGoal;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.player.Player;
import gameLogic.goal.Goal;
import gameLogic.map.Station;

/** ClickListener for when a goal button has been clicked */
public class GoalClickListener extends ClickListener {
    /** Context for the click listener */
	private Context context;
	
	/** Goal for the click listener */
    private Goal goal;
    
    /** Tooltips for the displaying of goal information */
    private Tooltip tooltip1;
    private Tooltip tooltip2;
    private Tooltip tooltip3;

    /** Boolean used to check whether tooltips are currently being displayed or not. */
    // Otherwise tooltips got constantly re-rendered
    private boolean showingTooltips;

    /** Instantiation method 
     * @param context Context the goal click listener is in
     * @param goal Goal that the clicklistener corresponds to
     */
    public GoalClickListener(Context context, Goal goal) {
        this.goal = goal;
        this.context = context;
        this.showingTooltips = false;
    }

    /** Event called when goal has been clicked. DIsplays associated information */
    @Override
    public void clicked(InputEvent event, float x, float y) {
    	SoundPlayer.playSound(1);
        //A check was necessary as to whether tooltips were currently being shown
        //This is due to the odd way that the events work
        //When clicking on a goal, it simultaneously performs the enter and exit methods
        //This led to some unintended behaviour where the tooltips were permanently rendered
        //Therefore they are only hidden if they are being shown
        if (showingTooltips) {

            //This hides the currently shown tooltips as otherwise they get stuck
            tooltip1.hide();
            tooltip2.hide();

            //Tooltip3 might not always exist, therefore by enclosing this in a try catch, if tooltip3 is null then the program does not crash
            try {
                tooltip3.hide();
            } catch (Exception e) {
            }

            //Resets the tooltip flag to false
            showingTooltips = false;
        }


        if (Game.getInstance().getState() == GameState.NORMAL) {
            //If the current game state is normal then a dialog is displayed allowing the user to interact with their goal
            Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();
            DialogGoalButtonClicked listener = new DialogGoalButtonClicked(currentPlayer,goal);
            DialogGoal dia = new DialogGoal(context, goal, context.getSkin());
            dia.show(context.getStage());
            dia.subscribeClick(listener);
        }
    }

    /** EVent used for when a mouse enters over the goalClickListener */
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (!showingTooltips) {
            //Need to check whether tooltips are currently being shown as otherwise it redraws them instantly after the clicked routine has ended
            tooltip1 = new Tooltip(context.getSkin());
            Station origin = goal.getOrigin();
            StationActor originActor = origin.getActor();

            //Sets the tooltip to have the origin's name and to be shown to the top right of the station
            tooltip1.setPosition(originActor.getX() + 20, originActor.getY() + 20);
            tooltip1.show(origin.getName());
            context.getStage().addActor(tooltip1);

            //Sets the tooltip to have the destination's name and to be shown to the top right of the station
            tooltip2 = new Tooltip(context.getSkin());
            Station destination = goal.getDestination();
            StationActor destinationActor = destination.getActor();
            context.getStage().addActor(tooltip2);
            tooltip2.setPosition(destinationActor.getX() + 20, destinationActor.getY() + 20);
            tooltip2.show(destination.getName());

            //If there is an intermediary station then a tooltip is also drawn for this station in the same way as the others
            Station intermediary = goal.getIntermediary();
            if (!intermediary.getName().equals(origin.getName())) {
                tooltip3 = new Tooltip(context.getSkin());
                StationActor intermediaryActor = intermediary.getActor();
                context.getStage().addActor(tooltip3);
                tooltip3.setPosition(intermediaryActor.getX() + 20, intermediaryActor.getY() + 20);
                tooltip3.show(intermediary.getName());
            }

            //Indicates that toolTips are currently being displayed
            showingTooltips = true;
        }
    }

    /** Event used for when a mouse exits the goalClickListener */
    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        //If tooltips are currently being displayed then it hides them all
        if (showingTooltips) {
            tooltip1.hide();
            tooltip2.hide();

            //Tooltip3 might not exist and hence be a null value. By enclosing this in a try catch, it prevents the program from crashing if this is the case
            try {
                tooltip3.hide();
            } catch (Exception e) {
            }

            //Indicates that tooltips are currently not being displayed
            showingTooltips = false;
        }
    }
}
