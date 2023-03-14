package comp30820.project18; // Import the packages we need

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;

import comp30820.project18.game_functions.game_timer;
import comp30820.project18.game_systems.asteroids;
import comp30820.project18.game_systems.game_system;
import comp30820.project18.game_systems.user;

public class main_game extends JFrame { // This file is the main file needed to initialize the game
	private static final int Frames = 60; // Frames per second, set it to 60
	private static final long FrameTime = (long) (1000000000.0 / Frames); // The number of nanoseconds that should elapse each frame. This is far moreaccurate than using milliseconds.
	private static final int LevelLimitDisplay = 60; // The number of frames that the "current level" message appears
	private static final int CooldownDeath = 200; // The value that the cooldown after dying (losing) will elapse after set player dies
	private static final int RespawnLimitCooldown = 100; // Respawn time
	private static final int VunerableElapsed = 0; // vulnerability, i..e, time elapse after you are shot out of bullets
	private static final int ResetCooldownValue = 120; // When you lose the game, value
	private game_menus world; // The instance for the WorldPanel
	private game_timer time_calculator; // The instance for the clock utility package (keep track of gameplay)
	private Random random; // The instance for the spawn of entities at random
	private List<game_system> entities; // The instance for the list of objects in the game
	private List<game_system> ClearEntitiesLoadNew; // The instance for the list of objects that are to be added to the game as the player progresses
	private user player; // The instance for the player
	private int DeathCooldown;
	private int LevelCooldown;
	private int RestartTheCooldown;
	private int score; // This is score
	private int lives; // How many lives you have
	private int level; // This is the current level
	private boolean GameOver; // If the game is over or not
	private boolean RestartAsteroids; // If the player has pressed anything to restart the game
	private ArrayList<Integer> scores = new ArrayList<Integer>();

	private main_game() { // New instance of the game
		super("Asteroids - Group 18"); 		// Initialize the window's basic properties
		loadScore();
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		world = new game_menus(main_game.this);
		add(world, BorderLayout.CENTER);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e); //				System.out.println(e);
				if (world.isStartState()) {
					if (e.getX() > 301 && e.getX() < 512 && e.getY() > 497 && e.getY() < 530) {
						world.setGame();
						ResetAsteroids();
					} else if (e.getX() > 281 && e.getX() < 533 && e.getY() > 597 && e.getY() < 630) {
						world.setScore();
					}}}});

		addKeyListener(new KeyAdapter() { // Here we add a key listener to the window so that we can process incoming user input.
			@Override
			public void keyPressed(KeyEvent e) {
				// Determine which key was pressed
				switch (e.getKeyCode()) {
				// Indicate that we want to apply thrust to our ship
				case KeyEvent.VK_W: // W
				case KeyEvent.VK_UP: // UP
					if (!RestartingGame()) {
						player.SetterForHyperThrust(true);
					}
					break;
				// Indicatee that we want to rotate our ship to the left
				case KeyEvent.VK_A: // A key
				case KeyEvent.VK_LEFT: // Or left key
					if (!RestartingGame()) {
						player.SetterForLeftRotation(true);
					}
					break;
				// Indiciate we want to rotate our ship to the right
				case KeyEvent.VK_D: // D key
				case KeyEvent.VK_RIGHT: // Or right key
					if (!RestartingGame()) {
						player.setRotateRight(true);
					}
					break;
				// When the space bar is pressed, we shoot
				case KeyEvent.VK_SPACE: // Space key
					if (!RestartingGame()) {
						player.setFiring(true);
					}
					break;
				// Indicate that we want to pause the game
				case KeyEvent.VK_P: // P key
					if (!RestartingGame()) {
						time_calculator.setPaused(!time_calculator.GameIsPaused()); // Check and pause time when we pause the
																			// game, call function from clock class
					}
					break;
				case KeyEvent.VK_ESCAPE: // Esc key
					world.setStart();
					break;
				default:
					RestartingGame(); // This controls and exception handles all for other key presses not
										// specified/defined by us
					break;
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				// When we let go of the W, this allows us to slow down and to no longer thrust,
				// sets hyperthrust to false
				case KeyEvent.VK_W: // W key
				case KeyEvent.VK_UP: // Up key
					player.SetterForHyperThrust(false); // Set as false, as it is no longer hyper thrusting
					break;
				// Indicate that we no longer want to rotate our ship left
				case KeyEvent.VK_A:
				case KeyEvent.VK_LEFT:
					player.SetterForLeftRotation(false);
					break;
				// Indicate that we no longer want to rotate our ship right
				case KeyEvent.VK_D:
				case KeyEvent.VK_RIGHT:
					player.setRotateRight(false);
					break;
				// Indicate that we no long want to fire bullets
				case KeyEvent.VK_SPACE: // Space bar
					player.setFiring(false);
					break;
				}
			}
		});
		pack();
		setLocationRelativeTo(null); // Resize the screen to position
		setVisible(true);
	}
	private boolean RestartingGame() {
		boolean restart = (GameOver && RestartTheCooldown <= 0);
		if (restart) {
			RestartAsteroids = true;
		}
		return restart;
	}
	private void GameStart_New() { // Starts the game running, and enters the main game loop
		// Initialize the engine's variables.
		this.random = new Random();
		this.entities = new LinkedList<game_system>();
		this.ClearEntitiesLoadNew = new ArrayList<>();
		this.player = new user();
		loadScore();
		ResetAsteroids(); // Set the variables to their default values.
		// Create the logic timer and enter the game loop.
		this.time_calculator = new game_timer(Frames);
		while (true) {
			// Get the time that the frame started.
			long start = System.nanoTime();
			time_calculator.update();
			for (int i = 0; i < 5 && time_calculator.hasElapsedCycle(); i++) {
				UpdateGame();
			}
			world.repaint();
			long delta = FrameTime - (System.nanoTime() - start); // Check how many nanoseconds left after calculating delta
			if (delta > 0) {
				try {
					Thread.sleep(delta / 1000000L, (int) delta % 1000000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void UpdateGame() { // Update the game entities and states.
		entities.addAll(ClearEntitiesLoadNew); // Clear entity, and update new
		ClearEntitiesLoadNew.clear();
		if (RestartTheCooldown > 0) { // If cooldown reached, restart it
			this.RestartTheCooldown--;
		}
		if (LevelCooldown > 0) {
			this.LevelCooldown--;
		}
		if (GameOver && RestartAsteroids) { // Condition to break out of loop if the it is
			ResetAsteroids();
		}
		if (!GameOver && AsteroidsCompleteLevel()) {
			// Increment the current level, and set the show level cooldown
			this.level++;
			this.LevelCooldown = LevelLimitDisplay;

			// Resets all the entity
			ResetScreenEntity();

			// Default state with firing ready enabled (ready to shoot)
			player.reset();
			player.ResetFiring(true);

			// Add the asteroids to the world.
			for (int i = 0; i < level + 2; i++) {
				registerEntity(new asteroids(random));
			}
		}
		if (DeathCooldown > 0) {
			this.DeathCooldown--; // If player dies, decrement the cooldown
			switch (DeathCooldown) {

			// Reset the entity to it's default spawn state, and disable firing.
			case RespawnLimitCooldown:
				player.reset();
				player.ResetFiring(false);
				break;

			// Re-enable the ability to fire, as we're no longer invulnerable.
			case VunerableElapsed:
				player.ResetFiring(true);
				break;

			}
		}
		if (LevelCooldown == 0) {
			for (game_system entity : entities) {
				entity.update(this);
			}
			for (int i = 0; i < entities.size(); i++) { // Handle all collisions by looping around them to ensure no
														// duplicates occur (see pdf report for reference)
				game_system a = entities.get(i);
				for (int j = i + 1; j < entities.size(); j++) {
					game_system b = entities.get(j);
					if (i != j && a.CollisionCrashCheck(b)
							&& ((a != player && b != player) || DeathCooldown <= VunerableElapsed)) {
						a.CollisionHandling(this, b);
						b.CollisionHandling(this, a);
					}
				}
			}
			Iterator<game_system> iter = entities.iterator(); 			// Loop through and remove "dead" entities, i.e., destroyed asteroids
			while (iter.hasNext()) {
				if (iter.next().DestroyedAsteroid()) {
					iter.remove();
				}
			}
		}
	}

	private void ResetAsteroids() { // Default when the game is reset, i.e., the same level 1 paramaters are used again
		this.score = 0; // start with a score of 0
		this.level = 0; // start in first level
		this.lives = 3; // I set lives to 3, but this can be changed, depending on how many lives you want in the game as a default
		this.DeathCooldown = 0;
		this.GameOver = false;
		this.RestartAsteroids = false;
		ResetScreenEntity();
	}
	private void ResetScreenEntity() { // Resets entities, keeps player
		ClearEntitiesLoadNew.clear();
		entities.clear();
		entities.add(player);
	}
	private boolean AsteroidsCompleteLevel() { // Tells us if the asteroids are all dead, if so, the level is complete,
												// if not returns weather dead or not
		for (game_system e : entities) {
			if (e.getClass() == asteroids.class) {
				return false;
			}
		}
		return true;
	}
	public void PlayerDead() { 	// Update the game when player dies
		// If the player dead function is triggered in our player class, this decrements
		// the life by -1
		this.lives--;

		if (lives == 0) { // Condition so that if there is life remaining, the game begins again
			this.GameOver = true;
			this.RestartTheCooldown = ResetCooldownValue;
			this.DeathCooldown = Integer.MAX_VALUE;

		} else {
			this.DeathCooldown = CooldownDeath;
		}
		// Disable the ability to fire.
		player.ResetFiring(false);
	}
	public void Highscore(int score) {
		this.score += score; // High score, the total points you have so far after killing all asteroids
		// save score
		if (GameOver) {
			saveScore();
		}
	}
	public void registerEntity(game_system entity) { // New entity to game
		ClearEntitiesLoadNew.add(entity);
	}

	public boolean GameOver() { // Is game finished (check)
		return GameOver;
	}

	public boolean CheckIfPlayerVunerable() {
		return (DeathCooldown > VunerableElapsed); // If player has used all shooting and has to wait for cooldown of
													// shots
	}

	public boolean NewSpawnDraw() { // Draw player when spawned
		return (DeathCooldown <= RespawnLimitCooldown);
	}

	public int getScore() { // current score getter
		return score;
	}

	public int getLives() { // How many lives left in the game
		return lives;
	}

	public int getLevel() { // Curent level
		return level;
	}

	public boolean isPaused() { // Show user if the game is paused or not, which is initiated by pressing P
		return time_calculator.GameIsPaused();
	}

	public boolean ShowLevel() { // Show the level of the game
		return (LevelCooldown > 0);
	}

	public Random getRandom() { // Gets the random instance
		return random;
	}

	public List<game_system> TotalEntityReturnList() { // Total list of entities in the game
		return entities;
	}

	public user NewPlayerSpawn() { // Player instance
		return player;
	}

	public static void main(String[] args) { // param taken is args, it starts and creates a new game when initiated
		main_game game = new main_game();
		game.GameStart_New();
	}

	public void loadScore() {
		try {
			scores.clear();
			Scanner sc = new Scanner(new File("score.txt")); // Score.txt addition
			while (sc.hasNextInt()) {
				scores.add(sc.nextInt());
			}
			sc.close();
		} catch (IOException e) {
		}

	}
	public void saveScore() {
		try {
			scores.add(score);
			PrintWriter out = new PrintWriter("score.txt"); // Print to a text file
			for (int score : scores) {
				out.println(score);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Integer> getScores() {
		return scores;
	}
}