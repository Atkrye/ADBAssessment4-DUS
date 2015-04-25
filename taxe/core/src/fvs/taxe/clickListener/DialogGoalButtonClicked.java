package fvs.taxe.clickListener;

import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import gameLogic.player.Player;
import gameLogic.goal.Goal;

/** Class used for registering goal dialog clicks*/
public class DialogGoalButtonClicked implements ResourceDialogClickListener {
    /** The player that is currently clicking on the goal*/
	private Player currentPlayer;
	
	/** The goal that has been clicked */
    private Goal goal;

    /** Instantiation
     * @param player The current player that have selected the goal
     * @param goal The goal that has been clicked
     */
    public DialogGoalButtonClicked(Player player, Goal goal) {
        this.currentPlayer = player;
        this.goal = goal;
    }

    /** Method called when a goal's dialog has been clicked*/
    @Override
    public void clicked(Button button) {
        switch (button) {
            case GOAL_DROP:
                currentPlayer.removeGoal(goal);
                //simulate mouse exiting goal button to remove tooltips

                break;
        }
    }
}
