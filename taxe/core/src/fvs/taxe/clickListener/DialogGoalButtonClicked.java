package fvs.taxe.clickListener;

import fvs.taxe.Button;
import fvs.taxe.SoundPlayer;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import gameLogic.player.Player;
import gameLogic.goal.Goal;

public class DialogGoalButtonClicked implements ResourceDialogClickListener {
    private Player currentPlayer;
    private Goal goal;

    public DialogGoalButtonClicked(Player player, Goal goal) {
        this.currentPlayer = player;
        this.goal = goal;
    }

    @Override
    public void clicked(Button button) {
    	SoundPlayer.playSound(2);
        switch (button) {
            case GOAL_DROP:
                currentPlayer.removeGoal(goal);
                //simulate mouse exiting goal button to remove tooltips

                break;
        }
    }
}
