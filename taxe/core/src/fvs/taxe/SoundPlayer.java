package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundPlayer {
	private static Sound sound1;
	private static Sound sound2;
	private static Sound sound3;
	private static Sound sound4;
	private static Sound sound5;
	private static Sound blizzardSound;
	private static Sound floodSound;
	private static Sound volcanoSound;
	private static Sound earthquakeSound;
	private static Sound bounceSound;
	
	public SoundPlayer(){
		sound1 = Gdx.audio.newSound(Gdx.files.internal("sounds/buttonSound3.mp3"));	
		sound2 = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.mp3"));
		sound3 = Gdx.audio.newSound(Gdx.files.internal("sounds/build.mp3"));
		sound4 = Gdx.audio.newSound(Gdx.files.internal("sounds/bomb.mp3"));
		sound5 = Gdx.audio.newSound(Gdx.files.internal("sounds/click.mp3"));
		blizzardSound = Gdx.audio.newSound(Gdx.files.internal("sounds/blizzard.mp3"));
		floodSound = Gdx.audio.newSound(Gdx.files.internal("sounds/flood.mp3"));
		volcanoSound = Gdx.audio.newSound(Gdx.files.internal("sounds/volcano.mp3"));
		earthquakeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/earthquake.mp3"));
		bounceSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bounce.mp3"));
	}

	public static void playSound(int soundChoice){
		switch (soundChoice) {
        case 1:  sound1.play();
                 break;
        case 2:  sound2.play();
                 break;
        case 3:  sound3.play();
                 break;
        case 4:  sound4.play();
        		 break;
        case 5:  sound5.play();
		 		 break;
		case 6:  blizzardSound.play();
                 break;
        case 7:  floodSound.play();
                 break;
        case 8:  volcanoSound.play();
        		 break;
        case 9:  earthquakeSound.play();
		 		 break;
        case 10:  bounceSound.play();
		         break; 		 
		 		 
        
		}
	}
	public static void dispose(){
		sound1.dispose();
		sound2.dispose();
		sound3.dispose();
		sound4.dispose();
		sound5.dispose();
		blizzardSound.dispose();
		floodSound.dispose();
		volcanoSound.dispose();
		earthquakeSound.dispose();
		bounceSound.dispose();
	}	
}
