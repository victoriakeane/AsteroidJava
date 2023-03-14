package comp30820.project18;  // Import all our packages

import java.awt.Color; 
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JPanel;
import comp30820.project18.game_functions.vectors_shapes;
import comp30820.project18.game_systems.game_system;

public class game_menus extends JPanel { // This is responsible for displaying the game to the user
	public static final int STATE_START = 1;
	public static final int STATE_GAME = 2;
	public static final int STATE_SCORE = 3;
	public static final int GameResolution = 800; // This variable is in charge of the actual size of the screen, wechanged it around from 500 to 800, and 800 is a good size for playing, current size = 800 (also makes it easier to play since larger)
	private static final Font GameFont = new Font("Dialog", Font.PLAIN, 30); // larger text/font for the actual game info that is displayed such as score
	private static final Font SecondarySubFont = new Font("Dialog", Font.PLAIN, 40); // font for medium text
	private main_game game; // game instance
	private static final Font Title = new Font("Tahoma", Font.BOLD, 100); // larger text/font for the actual game
	// info that is displayed such as score
	private static final Font Menu = new Font("Tahoma", Font.PLAIN, 40); // font for medium text
	private int state = STATE_START;
	public game_menus(main_game game) { // creates a new world instance, and takes the game as the parameter
		this.game = game;
		setPreferredSize(new Dimension(GameResolution, GameResolution)); // Here we are setting the size of the window and the colour of the background
		setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics g) { // We looked at other projects to determine this, see reference/bibliography for references
		super.paintComponent(g); // This part is required, otherwise rendering gets messy
		// "Cast our Graphics object to a Graphics2D object to make use of the extra capabilities such as anti-aliasing, and transformations"
		Graphics2D g2d = (Graphics2D) g;
		if (state == STATE_START) {
			g2d.setColor(Color.white);
			drawTextCentered("ASTEROIDS", Title, g2d, -100);
			drawTextCentered("PLAY GAME", Menu, g2d, 100);
			drawTextCentered("HIGH SCORES", Menu, g2d, 200);
			return;
		} else if (state == STATE_SCORE) {
			g2d.setColor(Color.white);
			drawTextCentered("TOP 5 SCORES", Menu, g2d, -300);
			g2d.setColor(Color.lightGray);
			ArrayList<Integer> scores = new ArrayList<Integer>(game.getScores());
			Collections.sort(scores); // if (game.getScore() > 0) {scores.add(game.getScore());}
			Collections.reverse(scores);
			for (int i = 0; i < 5; i++) {
				if (i < scores.size()) {
					drawTextCentered("" + scores.get(i), Menu, g2d, -200 + i * 60);
				}
			}
			g2d.setColor(Color.GREEN);
			drawTextCentered("Press Esc back to main menu", GameFont, g2d, 300);
			return;
		}
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.GREEN); // Here we are setting the draw colour, we chose to set it to green (this is the asteroids)

		AffineTransform identity = g2d.getTransform(); 	// Grab a reference to the current "identity" transformation, so we can reset + for each object. Loop through each entity and draw it onto the window
		Iterator<game_system> iter = game.TotalEntityReturnList().iterator();
		while (iter.hasNext()) {
			game_system entity = iter.next();
			if (entity != game.NewPlayerSpawn() || game.NewSpawnDraw()) { // If condition to draw the player if the player dies (i.e., create a new player on screen)
				vectors_shapes pos = entity.FindPosition(); // Get the position of the entity. Draw the entity at it's actual position, and reset the transformation, i.e.  position player died
				drawEntity(g2d, entity, pos.x, pos.y);
				g2d.setTransform(identity); // Here we need to determine whether or not the entity is close enough to the edge of the window to wrap around to the other side
				double radius = entity.TotalRadiusCollision();
				double x = (pos.x < radius) ? pos.x + GameResolution
						: (pos.x > GameResolution - radius) ? pos.x - GameResolution : pos.x;
				double y = (pos.y < radius) ? pos.y + GameResolution
						: (pos.y > GameResolution - radius) ? pos.y - GameResolution : pos.y;
				if (x != pos.x || y != pos.y) { //  Draw the entity at it's wrapped position, and reset the transformation
					drawEntity(g2d, entity, x, y);
					g2d.setTransform(identity);
				}}}
		if (!game.GameOver()) { // Display the score on the screen, position 10,15 for top right
			g.drawString("Score: " + game.getScore(), 25, 20);
		}
		if (game.GameOver()) { // Overlay messages to display on screen when game is over or paused or displaying the level
			drawTextCentered("Sorry! Game is OVER.", GameFont, g2d, -40);
			drawTextCentered("Your final score is: " + game.getScore(), SecondarySubFont, g2d, 30);
			drawTextCentered("Press Esc back to main menu", GameFont, g2d, 80);

		} else if (game.isPaused()) {
			drawTextCentered("Game is paused!", GameFont, g2d, -25); // Pause occurs if P is pressed
		} else if (game.ShowLevel()) {
			drawTextCentered("You are on level: " + game.getLevel(), GameFont, g2d, -25);
		}
		g2d.translate(30, 40); // Here for every single life the player has, we draw ship to display it
		g2d.scale(0.85, 0.85);
		for (int i = 0; i < game.getLives(); i++) {
			g2d.drawLine(-8, 10, 0, -10);
			g2d.drawLine(8, 10, 0, -10);
			g2d.drawLine(-6, 6, 6, 6);
			g2d.translate(30, 0);
		}}
	private void drawTextCentered(String text, Font font, Graphics2D g, int y) { // Takes 4 params, the text, the font, g and y. This draws text onto the center of the window
		g.setFont(font);
		g.drawString(text, GameResolution / 2 - g.getFontMetrics().stringWidth(text) / 2, GameResolution / 2 + y);
	}
	private void drawEntity(Graphics2D g2d, game_system entity, double x, double y) { // Takes 4 params, the text, the font, g and y. This draws an entity onto the center of the window
		g2d.translate(x, y);
		double rotation = entity.FindRotation();
		if (rotation != 0.0f) {
			g2d.rotate(entity.FindRotation());
		}
		entity.draw(g2d, game);
	}
	public void setStart() {
		state = STATE_START;
	}
	public void setGame() {
		state = STATE_GAME;
	}
	public void setScore() {
		state = STATE_SCORE;
	}
	public boolean isStartState() {
		return state == STATE_START;
	}}