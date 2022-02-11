package groupproject;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Mouse;

/**
 * Manager class for common input methods
 */
public class InputManager {

	public InputManager() {

	}

	/**
	 * Checks if sprite is being clicked
	 * @param sprite sprite to check
	 * @param button mouse button being used to click
	 * @param window game window
	 * @return true if sprite is clicked else false
	 */
	public boolean isSpriteClicked(Sprite sprite, Mouse.Button button, RenderWindow window) {
		if(Mouse.isButtonPressed(button)) {
			IntRect tempRect = new IntRect(new Vector2i(sprite.getPosition()),
					new Vector2i((int)sprite.getGlobalBounds().width,
							(int)sprite.getGlobalBounds().height));
			if(tempRect.contains(Mouse.getPosition(window))) return true;
		}
		return false;
	}
}
