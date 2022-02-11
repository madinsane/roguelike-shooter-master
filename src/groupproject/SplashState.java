package groupproject;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Clock;
import org.jsfml.window.event.Event;

/**
 * Splash screen state
 */
public class SplashState extends State {

	private Manager manager;
	private Clock clock;
	private Sprite background;

	public SplashState(Manager manager) {
		this.manager = manager;
		background = new Sprite();
		clock = new Clock();
	}

	public void init() {
		manager.getAssets().loadTexture("SplashScreen", Constants.SPLASH_SCREEN_FILEPATH);
		background.setTexture(manager.getAssets().getTexture("SplashScreen"));
		background.setScale(
				manager.getWindow().getSize().x / background.getLocalBounds().width,
				manager.getWindow().getSize().y / background.getLocalBounds().height
		);
	}

	public void handleInput() {
		for (Event event : manager.getWindow().pollEvents()) {
			if (Event.Type.CLOSED == event.type) {
				manager.getWindow().close();
			}
		}
	}

	public void update(float dt) {
		if (clock.getElapsedTime().asSeconds() > Constants.SPLASH_STATE_SHOW_TIME) {
			//Switch to menu
			manager.getStates().addState(new MainMenuState(manager), true);
		}
	}

	public void draw(float dt) {
		manager.getWindow().clear(Color.WHITE);
		manager.getWindow().draw(background);
		manager.getWindow().display();
	}

	public void pause() {

	}

	public void resume() {

	}
}
