package Util;

import fvs.taxe.TaxeGame;

public class IntegerEntryBar extends TextEntryBar {

	public IntegerEntryBar(int x, int y,  boolean lastClicked, int activeVal, TaxeGame game) {
		super(x, y, lastClicked, activeVal, game);
		this.startLabel = "Enter Number...";
	}
	
	@Override
	public void makeLabel (char character){
		
		if (lastClicked == true && label.length() < 5){
			if(Character.isDigit(character)){
			   clicked = true;	
			   label = label + character;
			   
		   }   }
	}
	public void clearLabel(){
		label = "";
		lastClicked = true;
	}
}
