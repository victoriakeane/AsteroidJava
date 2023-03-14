package comp30820.project18.game_systems;

// Import all our packages
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import comp30820.project18.main_game;
import comp30820.project18.game_functions.vectors_shapes;
import comp30820.project18.game_menus;

public class user extends game_system {
	
	private static final double DefaultPositionForRotation = -Math.PI / 2.0;
	private static final double HyperThrustForce = 0.0385; // thrust magnitude (hyper run)
	private static final double MaximummHyperThrust = 6.5; // max speed of thrust
	private static final double RotateSpeed = 0.052; // max thrust
	private static final double SlowRateHyperThrust = 0.995; // how slow can we go when rotating and stopping the thrust
	private static final int MaximumNumberBullets = 6; // max bullets that can be shot from the ship
	private static final int ElapsedTimeBulletRate = 4; // elapsed cycles between shots
	private static final int MaxShots = 8; // max shots when you hold the space bar
	private static final int MaximumShotsOverheatingLimit = 30; // when does overheating stop (i.e., max cycle, so if you keep shooting, it overheats when reaching max and then a time is needed to wait before continuing)
	private boolean HyperThrustPressed; // pressing thrust to rotate when updated (up arrow)
	private boolean RotationLeftKeyPressed; // rotate left when thrust updates (left arrow)
	private boolean RotationRightKeyPressed; // right side of thrust updating (right arrow)
	private boolean FireKeyPress; // when it updates, should it fire (space bar)	
	private boolean EnabledFiring; // enabling fire when it updates
	private int ContiniousShots; // number of shots that can be fire continuously
	private int Cooldown;
	private int Overheating; // Cooldown for overheating function
	private int GraphicFrames;
	private List<shooting> bullets;
	
	public user() {
		super(new vectors_shapes(game_menus.GameResolution / 2.0, game_menus.GameResolution / 2.0), new vectors_shapes(0.0, 0.0), 10.0, 0);
		this.bullets = new ArrayList<>();
		this.rotation = DefaultPositionForRotation;
		this.HyperThrustPressed = false; // Up key on keyboard
		this.RotationLeftKeyPressed = false; // Right arrow on keyboard
		this.RotationRightKeyPressed = false;
		this.FireKeyPress = false; // Space bar
		this.EnabledFiring = true; // Enabling firing
		this.Cooldown = 0;
		this.Overheating = 0;
		this.GraphicFrames = 0;
	}
	
	public void SetterForHyperThrust(boolean state) {
		this.HyperThrustPressed = state;
	}
	public void SetterForLeftRotation(boolean state) {
		this.RotationLeftKeyPressed = state;
	}
	public void setRotateRight(boolean state) {
		this.RotationRightKeyPressed = state;
	}
	public void setFiring(boolean state) {
		this.FireKeyPress = state;
	}
	public void ResetFiring(boolean state) {
		this.EnabledFiring = state;
	}
	// Resets the player to it's default spawn position, speed, and rotation, and clears the list of bullets.
	public void reset() {
		this.rotation = DefaultPositionForRotation;
		position.set(game_menus.GameResolution / 2.0, game_menus.GameResolution / 2.0);
		velocity.set(0.0, 0.0);
		bullets.clear();
	}	
	@Override
	public void update(main_game game) {
		super.update(game);
		// Increment the animation frame.
		this.GraphicFrames++;
		if(RotationLeftKeyPressed != RotationRightKeyPressed) {
			rotate(RotationLeftKeyPressed ? -RotateSpeed : RotateSpeed);
		}
		if(HyperThrustPressed) {

			velocity.add(new vectors_shapes(rotation).scale(HyperThrustForce));
				// Limit our hyperthrust
			if(velocity.getLengthSquared() >= MaximummHyperThrust * MaximummHyperThrust) {
				velocity.normalize().scale(MaximummHyperThrust);
			}
		}
		if(velocity.getLengthSquared() != 0.0) { // Slow down hyperthrust
			velocity.scale(SlowRateHyperThrust);
		}
		Iterator<shooting> iter = bullets.iterator(); // Loop through each of the bullets
		while(iter.hasNext()) {
			shooting bullet = iter.next();
			if(bullet.DestroyedAsteroid()) {
				iter.remove();
			}
		}
		this.Cooldown--;
		this.Overheating--;
		if(EnabledFiring && FireKeyPress && Cooldown <= 0 && Overheating <= 0) {
				// Create a new bullet if max bullets have not been reached
			if(bullets.size() < MaximumNumberBullets) {
				this.Cooldown = ElapsedTimeBulletRate;
				
				shooting bullet = new shooting(this, rotation);
				bullets.add(bullet);
				game.registerEntity(bullet);
			}	
			this.ContiniousShots++;
			if(ContiniousShots == MaxShots) {
				this.ContiniousShots = 0;
				this.Overheating = MaximumShotsOverheatingLimit;
			}
		} else if(ContiniousShots > 0) {
			// Decrement the number of consecutive shots, since we're not trying to fire.
			this.ContiniousShots--;
		}
	}
	@Override
	public void CollisionHandling(main_game game, game_system other) {
		// Kill the player if it collides with an asteroid
		if(other.getClass() == asteroids.class) {
			game.PlayerDead();
		}
	}
	@Override
	public void draw(Graphics2D g, main_game game) {
		if(!game.CheckIfPlayerVunerable() || game.isPaused() || GraphicFrames % 20 < 10) { // Flash if the player is just spawning
				// Drawing the actual ship
			g.drawLine(-10, -8, 10, 0);
			g.drawLine(-10, 8, 10, 0);
			g.drawLine(-6, -6, -6, 6);
			// The flames being drawn to indicate our hyperthrust
			if(!game.isPaused() && HyperThrustPressed && GraphicFrames % 6 < 3) {
				g.drawLine(-6, -6, -12, 0);
				g.drawLine(-6, 6, -12, 0);
			}
		}
	}
	
}
