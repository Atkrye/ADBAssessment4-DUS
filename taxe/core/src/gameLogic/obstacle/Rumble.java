package gameLogic.obstacle;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

// adapted from https://carelesslabs.wordpress.com/2014/05/08/simple-screen-shake/
/** Class for creating the shaking effect for when an earthquake occurs*/
public class Rumble {
	/** The amount of time the shake should occur*/
	public float time;

	/** The random generator for creating a randomised shake effect*/
	Random random;

	/** The amount of movement in pixels that will occur in that tick (movement) */
	float x, y;

	/** The current amount of time that the shake has elapsed for */
	float currentTime;

	/** The maximum amount of movement that will occur in the shake*/
	float power;

	/** The current amount of movement that will occur in the shake*/
	float currentPower;

	/** Constructor initialises time, currenttime, power and current power*/
	public Rumble(){
		time = 0;
		currentTime = 0;
		power = 0;
		currentPower = 0;
	}

	/** Initialise the power and duration of the shake, required before calling tick()   
	 * @param power The amount of force applied in the calculations for the movement
	 * @param time The amount in seconds that the shake should occur
	 */
	public void rumble(float power, float time) {
		random = new Random();
		this.power = power;
		this.time = time;
		this.currentTime = 0;
	}

	/** Generate the new vectors that represent the amount of x, y movement based upon a random multiple of the power
	 * @param delta The amount of time between frames
	 * @return The amount of movement in pixels that has occurred for that frame - (0,0) if no time left 
	 */
	public Vector2 tick(float delta){
		if(currentTime <= time) {
			currentPower = power * ((time - currentTime) / time);
			// generate random new x and y values taking into account
			// how much force was passed in
			x = (random.nextFloat() - 0.5f) * 2 * currentPower;
			y = (random.nextFloat() - 0.5f) * 2 * currentPower;


			currentTime += delta;
			return new Vector2 (-x, -y);
		} else {
			time = 0;
			return new Vector2 (0f, 0f);
		}
	}      
}