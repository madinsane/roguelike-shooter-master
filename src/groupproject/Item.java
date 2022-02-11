package groupproject;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Text;

/**
* Describes a single item entity in the game.
*/

public class Item extends Entity {
	private Room room;                  // The room the item exists in
	private String name;
	private int value;
	private boolean isShop;
	private int cost;
	private Text text;
    /**
    * Initialises a new item instance to be dropped onto the map
    *
    * @param manager current instance of manager
    * @param xpos    starting x position (0,0) being top left corner of window
    * @param ypos    starting y position
    * @param scale   initial sprite scale
    * @param width   width of Entity (pixels)
    * @param height  height of Entity (pixels)
	* @param value	value of item
	* @param isShop	is the item in a shop
    */

    public Item(Manager manager, String name, Room room, float xpos, float ypos, float scale, float width, float height, int value, boolean isShop) {
        super(manager, xpos, ypos, scale, width, height);
        this.room = room;
        this.name = name;
        this.value = value;
        this.isShop = isShop;
		super.setDepth(-1);
		super.setXpos(xpos);
		super.setYpos(ypos);
		super.setTexture(super.getManager().getAssets().getTexture(name));
		super.setSprite(0, false);
		text = new Text("", manager.getAssets().getFont("MainMenuFont"));
		//If the item spawned in a shop attaches text and cost to the item
		if (isShop) {
			manager.getEntities().setShopRoomId(room.getId());
			cost = Integer.parseInt(manager.getData().getRowData("items", name)[5]);
			text.setString("$" + cost);
			text.setColor(new Color(208, 180, 32));
			text.setPosition(xpos - 12, ypos + 48);
			room.addText(text);
		}
		if (this.name.equals("gold")) {
			if (value <= 2) {
				super.setSprite(0, false);
			} else if (value <= 4) {
				super.setSprite(1, false);
			} else if (value <= 9) {
				super.setSprite(2, false);
			} else {
				super.setSprite(3, false);
			}
		}
	}

	/**
	 * Checks if the item is in a shop
	 * @return is item in a shop
	 */
	public boolean isShop() {
		return isShop;
	}

	/**
	 * Makes item free
	 */
	public void aggro() {
		text.setString("");
		cost = 0;
	}

	/**
	 * Gives the player the item if the item is free, otherwise attempts to make the player pay for the item
	 * @param collidedEntity other entity that is being collided with
	 */
    @Override
	public void collide(Entity collidedEntity) {
        if (collidedEntity instanceof Player) {
        	if (!isShop) {
				boolean collected = ((Player) collidedEntity).obtainItem(name, value);
				if (collected)
					kill();
			} else {
        		if (cost <= ((Player) collidedEntity).getGoldCounter()) {
					((Player) collidedEntity).setGoldCounter(((Player) collidedEntity).getGoldCounter() - cost);
					boolean collected = ((Player) collidedEntity).obtainItem(name, value);
					if (collected) {
						kill();
					} else {
						((Player) collidedEntity).setGoldCounter(((Player) collidedEntity).getGoldCounter() + cost);
					}
				}
			}
        }
    }
}