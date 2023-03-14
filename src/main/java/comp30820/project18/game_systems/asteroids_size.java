package comp30820.project18.game_systems;
import java.awt.Polygon; // Using the polygon package
// This java file contains information on the different sizes of the asteroids
public enum asteroids_size {
	// Small asteroid, we set radius to 15 and it has 100 points (i.e., you get 100 pts)
	Small(15.0, 100),	
	// The medium sized asteroid, we set radius to 25 and you get 50 points
	Medium(25.0, 50),
	// Large asteroid, we set radius to 40 and it has 20 points (i.e., you get 20 pts)
	Large(40.0, 20);
	// The number of points on an asteroid
	private static final int ValueOfAsteroid = 5;
	public final Polygon polygon; // Shape of a polygon
	public final double radius;
	public final int killValue; // points you get for killing a polygon asteroid
	
 // Creates a new type of asteroid, takes 2 parameters, both the radius and the kill value
	private asteroids_size(double radius, int value) {
		this.polygon = generatePolygon(radius);
		this.radius = radius + 1.0;
		this.killValue = value;
	}
// Generates a regular polygon of size radius, the parameter radius is the radius of the polygon and the return will give us the newly spawned polygon
	private static Polygon generatePolygon(double radius) {
		//Create an array to store the coordinates.
		int[] x = new int[ValueOfAsteroid];
		int[] y = new int[ValueOfAsteroid];
		// Generate the points in the polygon.
		double angle = (2 * Math.PI / ValueOfAsteroid);
		for(int i = 0; i < ValueOfAsteroid; i++) {
			x[i] = (int) (radius * Math.sin(i * angle));
			y[i] = (int) (radius * Math.cos(i * angle));
		}
		// Create a new polygon from the generated points and return it
		return new Polygon(x, y, ValueOfAsteroid);
	}

}
