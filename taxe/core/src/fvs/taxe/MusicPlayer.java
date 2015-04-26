package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicPlayer {
	private static Music track1;
	private static Music track2;
	
	
	public MusicPlayer(){
		
	}
	public static void playTrack(){
	
	 track1 = Gdx.audio.newMusic(Gdx.files.internal("winter.mp3"));	
	 track1.setVolume(0.3f);
	 track1.setLooping(true);
	 track1.play();
	 }
	public static void playTrack2(){
		 
		 track2 = Gdx.audio.newMusic(Gdx.files.internal("adrenaline.mp3"));	
		 track2.setVolume(1f);
		 //if (track2.isPlaying() == false){
			 track2.play();
		 //}
	}
	public static void dispose(){
	 track1.dispose();
	 track2.dispose();
	}
	public static void resumeTrack1(){
		track1.play();
	  }
	public static void stopTrack1(){
		track1.stop();
	  }
	public static void stopTrack2(){
		
		
		System.out.println("stop");
		System.out.println(track2.isPlaying());
	if (track2.isPlaying()){
		track2.stop();
		track2.dispose();
		}
	}
		
}
