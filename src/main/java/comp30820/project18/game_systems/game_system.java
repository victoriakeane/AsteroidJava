package comp30820.project18.game_systems;

import java.awt.Graphics2D; // Import the packages we need for the entity

import comp30820.project18.main_game;
import comp30820.project18.game_functions.vectors_shapes;
import comp30820.project18.game_menus;

public abstract class game_system {
	protected vectors_shapes position; // position of entity
	protected vectors_shapes velocity; // velocity
	protected double rotation; // rotation
	protected double radius; // collision radius
	private boolean DestroyedAsteroid; // how many shots required before it is removed
	private int ScoreFromKilledAsteroid; // our score received per killing asteroid
	// Here we take 4 params, this is the position of the entity, the velocity, the radius and the score we receive when we kill them
	public game_system(vectors_shapes position, vectors_shapes velocity, double radius, int ScoreFromKilledAsteroid) {
		this.position = position;
		this.velocity = velocity;
		this.radius = radius;
		this.rotation = 0.0f;
		this.ScoreFromKilledAsteroid = ScoreFromKilledAsteroid;
		this.DestroyedAsteroid = false;
	}
	public void rotate(double amount) { // Rotating the entity, amount = the value of rotation
		this.rotation += amount;
		this.rotation %= Math.PI * 2;
	}
	public int ScoreFromKilledAsteroid() { // Sum up the total points that we get when we will the entity and return us a score
		return ScoreFromKilledAsteroid;
	}
	public void DeleteAsteroid() {
		this.DestroyedAsteroid = true; // True = when an entity is destroyed, it needs to be removed from the players screen
	}
	public vectors_shapes FindPosition() { // Find the position and return the position
		return position;
	}
	public vectors_shapes FindVelocity() { // The total velocity of the entity
		return velocity;
	}
	public double FindRotation() { // This is returning to us the rotation
		return rotation;
	}
	public double TotalRadiusCollision() { // radius of the collision
		return radius;
	}
	public boolean DestroyedAsteroid() { // Returns if the entity needs to be removed
		return DestroyedAsteroid;
	}
	public void update(main_game game) { // Update the status of the entity
		position.add(velocity);
		if(position.x < 0.0f) {
			position.x += game_menus.GameResolution;
		}
		if(position.y < 0.0f) {
			position.y += game_menus.GameResolution;
		}
		position.x %= game_menus.GameResolution;
		position.y %= game_menus.GameResolution;
	}
	public boolean CollisionCrashCheck(game_system entity) { // If two entities collided, takes parameter entity to check against and returns true if collision has happened
		double radius = entity.TotalRadiusCollision() + TotalRadiusCollision(); // Used pythagorean theorem, because we are squaring one variable than using the square root of another
		return (position.getDistanceToSquared(entity.position) < radius * radius);
	}
	public abstract void CollisionHandling(main_game game, game_system other); // Handle a collision with another entity
	public abstract void draw(Graphics2D g, main_game game); // Draw the entity, g is the graphics instance and game is the game instance
}