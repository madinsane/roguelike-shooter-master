package groupproject;

/**
 * Abstract class describing a general state
 */
public abstract class State {

	/**
	 * Initializes the state
	 */
	public abstract void init();

	/**
	 * Handles input, called every frame
	 */
	public abstract void handleInput();

	/**
	 * Handles logic updates, called every frame
	 * @param dt time of 1 frame
	 */
	public abstract void update(float dt);

	/**
	 * Handles drawing to the screen, called every frame
	 * @param dt time of 1 frame
	 */
	public abstract void draw(float dt);

	/**
	 * Called when the state is paused
	 */
	public abstract void pause();

	/**
	 * Called when the state is resumed
	 */
	public abstract void resume();

}
