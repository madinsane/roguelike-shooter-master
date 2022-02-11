package groupproject;

import org.jsfml.graphics.RenderWindow;

/**
 * Holds the instances of the manager classes for access between other classes
 */
public class Manager {
	private StateMachine states; //State manager
	private RenderWindow window; //Game window
	private AssetManager assets; //Asset manager
	private InputManager inputs; //Input manager
	private DataManager data; //Data manager
	private EntityManager entities; //Entity manager

	public Manager() {
		window = new RenderWindow();
		states = new StateMachine();
		assets = new AssetManager();
		inputs = new InputManager();
		entities = new EntityManager(this);
		data = new DataManager();
	}

	/**
	 * Gets the State manager
	 * @return State manager
	 */
	public StateMachine getStates() {
		return states;
	}

	/**
	 * Gets the Game window
	 * @return Game window
	 */
	public RenderWindow getWindow() {
		return window;
	}

	/**
	 * Gets the Asset manager
	 * @return Asset manager
	 */
	public AssetManager getAssets() {
		return assets;
	}

	/**
	 * Gets the Input manager
	 * @return Input manager
	 */
	public InputManager getInputs() {
		return inputs;
	}

	/**
	 * Gets the Data manager
	 * @return Data manager
	 */
	public DataManager getData() {
		return data;
	}

	/**
	 * Gets the Entity manager
	 * @return Entity manager
	 */
	public EntityManager getEntities() { return entities; }
}
