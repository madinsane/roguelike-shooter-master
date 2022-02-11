package groupproject;

import org.jsfml.system.Clock;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import static groupproject.Constants.TITLE;
import static groupproject.Constants.DT;

/**
 * Main game loop
 */
public class Game {
	private Manager manager;
	private Clock clock;

	public Game() {
		manager = new Manager();
		clock = new Clock();
		manager.getWindow().create(new VideoMode(1280, 720), TITLE, WindowStyle.DEFAULT);
		manager.getStates().addState(new SplashState(manager));
	}

	/**
	 * Forces the loop to run at 60fps on any machine and calls the methods for the current state
	 */
	public void run() {
		float newTime, frameTime, interpolation;
		float currentTime = clock.getElapsedTime().asSeconds();
		float accumulator = 0.0f;

		while(manager.getWindow().isOpen()) {
			manager.getStates().processStateChanges();
			newTime = clock.getElapsedTime().asSeconds();
			frameTime = newTime - currentTime;
			if(frameTime > 0.25f) {
				frameTime = 0.25f;
			}
			currentTime = newTime;
			accumulator += frameTime;
			
			while(accumulator >= DT) {
				float accRuns = accumulator / DT;
				
				manager.getStates().getActiveState().handleInput();
				manager.getStates().getActiveState().update(DT);
				accumulator -= DT;
			}

			interpolation = accumulator / DT;
			manager.getStates().getActiveState().draw(interpolation);
		}
	}
}
