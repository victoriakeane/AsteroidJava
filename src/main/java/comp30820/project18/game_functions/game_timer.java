package comp30820.project18.game_functions;
// This is a clock feature, it will allow us to keep an eye on the amount of cycles per gameplay
// This clock feature was implemented using the help of YouTube tutorials, reference contained in the pdf report
public class game_timer {
	private float MsPerCycle; // The number of milliseconds that make up one cycle.
	private long LastUpdateSinceCycle; // Delta time is calculated and this checks for the last update
	private int elapsedCycles; // How many cycles have went by
	private float excessCycles; // time left in excess of the cycle
	private boolean GameIsPaused; // Checks if paused
	public game_timer(float cyclesPerSecond) {
		setCyclesPerSecond(cyclesPerSecond);
		reset();
	}
	public void setCyclesPerSecond(float cyclesPerSecond) {
		this.MsPerCycle = (1.0f / cyclesPerSecond) * 1000;
	}
	public void reset() {
		this.elapsedCycles = 0;
		this.excessCycles = 0.0f;
		this.LastUpdateSinceCycle = getCurrentTime();
		this.GameIsPaused = false;
	}
	public void update() {
		// Get the current time and calculate the delta time
		long TheCurrentTimeUpdate = getCurrentTime();
		float delta = (float)(TheCurrentTimeUpdate - LastUpdateSinceCycle) + excessCycles;
		// Update the number of elapsed and excess ticks if we're not paused
		if(!GameIsPaused) {
			this.elapsedCycles += (int)Math.floor(delta / MsPerCycle);
			this.excessCycles = delta % MsPerCycle;
		}
		// Set the last update time for the next update cycle.
		this.LastUpdateSinceCycle = TheCurrentTimeUpdate;
	}
	public void setPaused(boolean paused) { // Pauses and unapuses the game
		this.GameIsPaused = paused;
	}
	public boolean GameIsPaused() { //isPaused function to check if the game is paused
		return GameIsPaused;
	}
	public boolean hasElapsedCycle() { // If condition to check if the cycle has elapsed, i.e., the time has elapsed
		if(elapsedCycles > 0) {
			this.elapsedCycles--;
			return true;
		}
		return false;
	}	
	public boolean peekElapsedCycle() { // Checks elapsed time
		return (elapsedCycles > 0);
	}
	private static final long getCurrentTime() { // Gets the current time in nanoseconds
		return (System.nanoTime() / 1000000L);
	}

}