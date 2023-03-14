package comp30820.project18.game_functions;
public class vectors_shapes { // This is the class that will give us the 2D shape used in the game (vector)
	public double x; // x value
	public double y; // y value
	public vectors_shapes(double angle) { // Creates a new Vector from an angle. The length of this vector will be 1
		this.x = Math.cos(angle);
		this.y = Math.sin(angle);
	}
	public vectors_shapes(double x, double y) {  // Creates a new Vector with the desired values.
		this.x = x; // 2 parameters, x and y
		this.y = y;
	}
	public vectors_shapes(vectors_shapes vector) { 	// Creates a new Vector and copies the components from the old, takes vector which is the paramater for the vector to copy
		this.x = vector.x;
		this.y = vector.y;
	}
	public vectors_shapes set(double x, double y) { // sets x, y and return the vector for chaining
		this.x = x;
		this.y = y;
		return this;
	}
	public vectors_shapes add(vectors_shapes vector) { // add components to vector (return the vector for chaining)
		this.x += vector.x;
		this.y += vector.y;
		return this;
	}
	public vectors_shapes scale(double scalar) { // Scales the vector ((return the vector for chaining)
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
	public vectors_shapes normalize() { // Normalizes the vector ((return the vector for chaining)
		double length = getLengthSquared();
		if(length != 0.0f && length != 1.0f) {
			length = Math.sqrt(length);
			this.x /= length;
			this.y /= length;
		}
		return this;
	}
	public double getLengthSquared() { // length of the vector
		return (x * x + y * y);
	}
	public double getDistanceToSquared(vectors_shapes vector) { // We are getting the square distance from one vector to another vector
		double dx = this.x - vector.x;
		double dy = this.y - vector.y;
		return (dx * dx + dy * dy);
	}
}