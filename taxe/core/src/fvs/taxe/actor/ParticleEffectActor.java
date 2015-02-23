package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**This class is a type of actor specifically for creating Particles e.g. for obstacles.*/
public class ParticleEffectActor extends Actor {

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
		//Draw the particleEffect in real time
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
