package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**Class used to display the particle effects onscreen when an obstacle occurs*/
public class ParticleEffectActor extends Actor {
	// simply a container class that contains the particle effect. Wrapped up as actor to ensure it is displayed in a correct z-order
	
	/**The particle effect to be used in this actor*/
	private ParticleEffect particleEffect;

	/**Instantition method for setting up the actor
	 * @param particleEffect The particleEffect to be used in this particle
	 */
	public ParticleEffectActor(ParticleEffect particleEffect) {
		super();
		this.particleEffect = particleEffect;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//Draw the particleEffect in real time (will stop being visible onscreen based on particle effect parameters)
		super.draw(batch, parentAlpha);
		particleEffect.draw(batch, Gdx.graphics.getDeltaTime());
	}
	
	/**Starts animation of the particle effect*/
	public void start() {
		particleEffect.start();
	}
	
	@Override
	public void setPosition(float x, float y){
		super.setPosition(x, y);
		//We must reposition the particleEffect itself too
		particleEffect.setPosition(x, y);
	}
}
