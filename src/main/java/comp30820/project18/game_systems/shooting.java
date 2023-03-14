package comp30820.project18.game_systems;

import java.awt.Graphics2D;

import comp30820.project18.main_game;
import comp30820.project18.game_functions.vectors_shapes;

// In this section, we are essentially representing a bullet in the game, i.e., the actual object that we shoot with
public class shooting extends game_system {
	//The force/magnitude of the velocity of a shooting object, this is what sets the actual shooting in terms of what it can destruct
	private static final double VelocityForce = 6.75;
	private static final int MaximumBulletLifeSpan = 60; // We need a method to determine the lifespan of a bullet
	private int lifespan; 	// 	The number of cycles this bullet has existed, not the lifespan but how many bullets in the screen at a given time once shot
	public shooting(game_system owner, double angle) { 	// We are creating a bullet instance
		super(new vectors_shapes(owner.position), new vectors_shapes(angle).scale(VelocityForce), 2.0, 0);
		this.lifespan = MaximumBulletLifeSpan;
	}
	@Override
	public void update(main_game game) { // Here we increment the lifespan of the bullet and when shot, it removes (if lifespan less than or equal to 0)
		super.update(game);
		
		this.lifespan--;
		if(lifespan <= 0) {
			DeleteAsteroid(); // Remove the asteroid
		}
	}
	@Override
	public void CollisionHandling(main_game game, game_system other) {
		if(other.getClass() != user.class) {
			DeleteAsteroid();
		}
	}
	@Override
	public void draw(Graphics2D g, main_game game) {
		g.drawOval(-1, -1, 2, 2);
	}
}