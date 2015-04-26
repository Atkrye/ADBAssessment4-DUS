package Util;

import fvs.taxe.TaxeGame;

/** Class for the integer entry bar, extending textentrybar */
public class IntegerEntryBar extends TextEntryBar {

	/** Instantiation
	 * @param x X coordinate of bar
	 * @param y Y coordinate of bar
	 * @param activeVal ActiveVal of the integer entry bar
	 * @param game Game instance to show the entry bar in
	 */
	public IntegerEntryBar(int x, int y, int activeVal, TaxeGame game) {
		super(x, y, activeVal, game);
		this.startLabel = "3000";
	}

	@Override
	public void makeLabel (char character){
		//if the character is a digit the character is appended to label
		if (active == activeVal && label.length() < 5){
			if(Character.isDigit(character)){
				if(label.length() == 0 && character == '0'){
					return;
				} else {
					clicked = true;	
					label = label + character;
				}
			}
		}
	}

	public void setLastClicked(){
		//sets the IntegerEntryBar as active
		active = activeVal;
	}
}
