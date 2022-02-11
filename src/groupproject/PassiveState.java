package groupproject;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.system.Clock;

import java.util.ArrayList;

/**
 * Passive tree state
 */
public class PassiveState extends State {
	private Manager manager;

	private String[] options;
	private Text menuOption;
	private int menuSelection;
	private Color grey;
	Clock opOscClock;
	private ArrayList<String>[] passives;
	private int gold;
	private Color goldColor;

	public PassiveState(Manager manager, int gold) {
		this.manager = manager;
		this.gold = gold;
		grey = new Color(150, 150, 150);
		goldColor = new Color(208, 180, 32);
		opOscClock = new Clock();
	}

	/**
	 * Loads data and assets
	 */
	public void init() {
		manager.getEntities().cleanUp();
		manager.getAssets().loadFont("MainMenuFont", Constants.MAIN_MENU_SCREEN_FONT);
		manager.getData().readFromFile("passives", Constants.DATA_PASSIVES_FILEPATH);

		getPassives();
		options = new String[] {"Restart", "Quit"};
		menuOption = new Text("Noop", manager.getAssets().getFont("MainMenuFont"));
		menuOption.setCharacterSize(16);
		menuOption.setColor(Color.BLACK);

		menuSelection = 0;
	}

	/**
	 * Gets passives from passives in data
	 */
	public void getPassives() {
		ArrayList<String[]> passivesTemp = manager.getData().getData("passives");
		passives = new ArrayList[passivesTemp.size()-1];
		for (int i=1; i<passivesTemp.size(); i++) {
			passives[i-1] = new ArrayList<>();
			//Name
			passives[i-1].add(passivesTemp.get(i)[4]);
			//Count
			passives[i-1].add(passivesTemp.get(i)[1]);
			//Cost
			passives[i-1].add(passivesTemp.get(i)[3]);
			//Description
			passives[i-1].add(passivesTemp.get(i)[5]);
		}
	}

	/**
	 * Handles input
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

				menuSelection = Math.max(0, Math.min(menuSelection, options.length+passives.length-1));
			}
		}
	}

	/**
	 * Handles action for currently selected option
	 */
	public void optionSelected()
	{
		if(menuSelection == passives.length) {
			manager.getData().savePassives(passives);
			manager.getStates().addState(new GameState(manager), true);
		} else if (menuSelection == passives.length + 1) {
			manager.getData().savePassives(passives);
			manager.getWindow().close();
		} else {
			if (Integer.parseInt(passives[menuSelection].get(2)) <= gold) {
				gold -= Integer.parseInt(passives[menuSelection].get(2));
				int count = Integer.parseInt(passives[menuSelection].get(1)) + 1;
				passives[menuSelection].set(1, Integer.toString(count));
			}
		}
	}

	public void update(float dt) {

	}

	/**
	 * Draws all drawables each dt
	 * @param dt time of 1 frame
	 */
	public void draw(float dt) {
		manager.getWindow().clear(Color.WHITE);

		menuOption.setScale(1.f, 1.f);
		menuOption.setColor(Color.BLACK);

		menuOption.setString("Name\tCount\tCost\tDescription");
		menuOption.setPosition(
				(float)Math.floor(manager.getEntities().getRoomWidth() / 2) - (float)Math.floor(menuOption.getGlobalBounds().width / 2.f),200
		);

		manager.getWindow().draw(menuOption);

		menuOption.setColor(Color.BLACK);

		for (int i = 0; i < (options.length + passives.length); i++) {
			if (i < passives.length) {
				menuOption.setString(passives[i].get(0) + "\t" + passives[i].get(1) + "\t" + passives[i].get(2) + "\t" + passives[i].get(3));
			} else {
				menuOption.setString(options[i - passives.length]);
			}
			menuOption.setScale(1.f, 1.f);
        	if (menuSelection == i) {
				float sc = 1.f;
        		sc += (float) Math.abs(Math.sin((opOscClock.getElapsedTime().asSeconds() + 0.5f) * 2.f) / 4.f);
        		menuOption.setScale(sc, sc);
			}
        	menuOption.setPosition(
					(float) Math.floor(manager.getEntities().getRoomWidth() / 2) - (float) Math.floor(menuOption.getGlobalBounds().width / 2.f),
					(float) Math.floor(manager.getEntities().getRoomHeight() / 2) + (float) (i * 26.f)
			);
			manager.getWindow().draw(menuOption);
		}

		menuOption.setScale(1.f, 1.f);
		menuOption.setColor(goldColor);

		menuOption.setString("Gold: " + gold);
		menuOption.setPosition(
				(float)Math.floor(manager.getEntities().getRoomWidth() / 2) - (float)Math.floor(menuOption.getGlobalBounds().width / 2.f),
				(float)Math.floor(manager.getEntities().getRoomHeight()) - 88
		);

		manager.getWindow().draw(menuOption);

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

	public void pause() {

	}

	public void resume() {

	}
}
