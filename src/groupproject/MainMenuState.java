package groupproject;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.system.Clock;

/**
 * Main menu state
 */
public class MainMenuState extends State {
	private Manager manager;

	private String[] options;
	private Text menuOption;
	private int menuSelection;
	private Color grey;
	Clock opOscClock;
	
	public MainMenuState(Manager manager) {
		this.manager = manager;
		grey = new Color(150, 150, 150);
		opOscClock = new Clock();
	}

	/**
	 * Loads font and options for main menu
	 */
	public void init() {
		manager.getAssets().loadFont("MainMenuFont", Constants.MAIN_MENU_SCREEN_FONT);
		
		options = new String[] {"Begin", "Quit"};
		menuOption = new Text("Noop", manager.getAssets().getFont("MainMenuFont"));
		menuOption.setCharacterSize(16);
		menuOption.setColor(Color.BLACK);
		manager.getEntities().setRoomWidth(manager.getWindow().getSize().x);
		manager.getEntities().setRoomHeight(manager.getWindow().getSize().y);
		
		menuSelection = 0;
	}

	/**
	 * Handles input for main menu
	 */
	public void handleInput() {
		for (Event event : manager.getWindow().pollEvents()) {
			if (Event.Type.CLOSED == event.type) {
				manager.getWindow().close();
			}
			else if(Event.Type.KEY_PRESSED == event.type)
			{
				if(event.asKeyEvent().key == Constants.PLAYER_KEY_UP)
				{
					menuSelection--;
					opOscClock.restart();
				}
				else if(event.asKeyEvent().key == Constants.PLAYER_KEY_DOWN)
				{
					menuSelection++;
					opOscClock.restart();
				}
				else if(event.asKeyEvent().key == Constants.PLAYER_KEY_DASH)
				{
					optionSelected();
				}
				
				menuSelection = Math.max(0, Math.min(menuSelection, options.length-1));
			}
		}
	}

	/**
	 * Performs action for currently selected option
	 */
	public void optionSelected()
	{
		if(menuSelection == 0)
		{
			manager.getStates().addState(new GameState(manager), true);
		}
		else
		{
			manager.getWindow().close();
		}
	}

	/**
	 * Updates logic every dt
	 * @param dt time of 1 frame
	 */
	public void update(float dt) {

	}

	/**
	 * Draws all drawables every dt
	 * @param dt time of 1 frame
	 */
	public void draw(float dt) {
		manager.getWindow().clear(Color.WHITE);

		menuOption.setColor(Color.BLACK);
		
		for(int i = 0; i < options.length; i++)
		{
			menuOption.setScale(1.f, 1.f);
			menuOption.setString(options[i]);
			
			if(menuSelection == i)
			{
				float sc = 1.f;
				
				sc += (float)Math.abs(Math.sin((opOscClock.getElapsedTime().asSeconds()+0.5f)*2.f) / 4.f);
				
				menuOption.setScale(sc, sc);
			}
			
			menuOption.setPosition(
				(float)Math.floor(manager.getEntities().getRoomWidth() / 2) - (float)Math.floor(menuOption.getGlobalBounds().width / 2.f),
				(float)Math.floor(manager.getEntities().getRoomHeight() / 2) + (float)(i * 26.f)
			);
			
			manager.getWindow().draw(menuOption);
		}
		
		menuOption.setScale(1.f, 1.f);
		menuOption.setColor(grey);
		
		menuOption.setString("[Pick: W/S]  [Select: Space]");
		menuOption.setPosition(
			(float)Math.floor(manager.getEntities().getRoomWidth() / 2) - (float)Math.floor(menuOption.getGlobalBounds().width / 2.f),
			(float)Math.floor(manager.getEntities().getRoomHeight()) - 64.f
		);
		
		manager.getWindow().draw(menuOption);
		
		manager.getWindow().display();
	}

	/**
	 * Pauses main menu
	 */
	public void pause() {

	}

	/**
	 * Resumes main menu
	 */
	public void resume() {

	}
}
