package comp30820.project18.game_systems;
import java.awt.Graphics2D; // Graphics 2D package import
import java.util.Random; // Random package generator, this will be used for our basis of generating asteroids and etc
import comp30820.project18.main_game; // Importing our game
import comp30820.project18.game_functions.vectors_shapes;
import comp30820.project18.game_menus; // The HUD

public class asteroids extends game_system {	
	private static final double MinimumRotationSpeed = 0.0075;  // We set the speed here, this is the minimum speed that the asteroid can rotate
	private static final double MaximumRotationSpeed = 0.0175; // The max rotation
	private static final double VarianceBetweenRotations = MaximumRotationSpeed - MinimumRotationSpeed; // The variation between them is just the both minus out
	private static final double MinimumVelocity = 0.75; // The minimum velocity at which the asteroid can move
	private static final double MaximumVelocity = 1.65; // The max
	private static final double VarianceBetweenVelocity = MaximumVelocity - MinimumVelocity; // Variation
	private static final double MinimumDistance = 200.0; // This is set as the min distance a player can spawn from an asteroid. // We obviously do not want to spawn right next to one
	private static final double MaximumDistance = game_menus.GameResolution / 2.0; // The maximum
	private static final double VarianceBetweenDistance = MaximumDistance - MinimumDistance; // Difference between them is the variance
	private asteroids_size size; // Size of asteroid
	private double RotationSpeedOfAsteroids; // Rotation speed
	
	public asteroids(Random random) { // Here we create a random asteroid, we are using the random package here to do this, imported above
		super(PositionCalculation(random), VelocityCalculation(random), asteroids_size.Large.radius, asteroids_size.Large.killValue);
		this.RotationSpeedOfAsteroids = - ( MinimumRotationSpeed + (random.nextDouble() * VarianceBetweenRotations ) );
		this.size = asteroids_size.Large; // Simply here adding to generate an asteroid
	}
	public asteroids(asteroids parent, asteroids_size size, Random random) { 	// Takes 3 parameters, parent asteroid, size of the asteroid and the random. It is creating a new asteroid from the parent asteroid
		super(new vectors_shapes(parent.position), VelocityCalculation( random ), size.radius, size.killValue);
		this.RotationSpeedOfAsteroids = ( MinimumRotationSpeed + ( random.nextDouble() * VarianceBetweenRotations ) );
		this.size = size;
	}
	private static vectors_shapes PositionCalculation(Random random) { // Here we are calculating a random spawn point for an asteroid
		vectors_shapes vector = new vectors_shapes(game_menus.GameResolution / 2.0, game_menus.GameResolution / 2.0);
		return vector.add(new vectors_shapes(random.nextDouble()*Math.PI *2).scale ( MinimumDistance + random.nextDouble() * VarianceBetweenDistance ) );
	}	
	private static vectors_shapes VelocityCalculation(Random random) {	// Calculates a random velocity for an Asteroid, again using the random package
		return new vectors_shapes(random.nextDouble()*Math.PI*2).scale( ( MinimumVelocity + random.nextDouble() * VarianceBetweenVelocity ) );
	}
	@Override
	public void update(main_game game) {
		super.update(game);
		rotate(RotationSpeedOfAsteroids); // Rotate the image each frame
	}
	@Override // A polygon is a flat two-dimensional shape with straight sides that are fully closed, we are using this to represent our ship
	public void draw(Graphics2D g, main_game game) {
		g.drawPolygon(size.polygon); // Draw the Asteroid, i.e., drawing the polygon
	}
	@Override // Children = smaller asteroids & parent = big asteroids
	public void CollisionHandling(main_game game, game_system other) {
		if(other.getClass() != asteroids.class) { // Prevent a collision with other asteroid 
			// Only spawn "children" if we're not a small asteroid
			if (size != asteroids_size.Small)
			 {
				asteroids_size AsteroidSpawnSize = asteroids_size.values()[size.ordinal() - 1]; // Determine the size of the children
				for( int i = 0; i < 2; i++ ) { // Create the children asteroids & Initiate a for loop to to create a new asteroid (smaller child)
					game.registerEntity(new asteroids(this, AsteroidSpawnSize, game.getRandom()));
				}
			}	
			DeleteAsteroid(); // We delete an asteroid once it gets shot
			game.Highscore(ScoreFromKilledAsteroid()); // The players score gets updated, and added once they kill an asteroid
		}
	}	
}