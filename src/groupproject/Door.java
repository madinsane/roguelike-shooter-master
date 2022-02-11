package groupproject;

import org.jsfml.graphics.Color;

/**
 * Extension of Entity
 * Describes a Teleporter for moving between rooms
 */
public class Door extends Entity {
	private int tp_room;
	private boolean tp_exitX, tp_exitY;
	private float tp_threshold = 200.f;
	private boolean enabled;
	
	public Door(Manager manager, float xpos, float ypos, float scale, int width, int height, int tp_room, boolean tp_exitX, boolean tp_exitY, String type) {
		super(manager, xpos, ypos, scale, width, height);
		super.setTexture(super.getManager().getAssets().getTexture(type));
		super.setSprite(4, false);
		setEnabled(false);
		if (super.getYpos() < 100*super.getManager().getEntities().getScale()) {
			super.setDepth(-1);
		}
		this.tp_room = tp_room;
		
		this.tp_exitX = tp_exitX;
		this.tp_exitY = tp_exitY;
		
		if(this.tp_room == -1)
		{
			super.getSprite().setColor(new Color(255, 255, 255, 0));
		}
	}

	/**
	 * Checks if door is enabled
	 * @return is enabled
	 */
	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets if the door is enabled and plays the correct animation
	 * @param enabled is door enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled)
			//super.clearAnimations();
			super.tryAddAnimation("fence_despawn");
		else
			super.tryAddAnimation("fence_spawn");
	}

	/**
	 * Moves player to connected room
	 * @param collidedEntity other entity that is being collided with
	 */
	public void collide(Entity collidedEntity) {
		if (enabled) {
			Room oldRoom = getManager().getEntities().getCurrentRoomObject();
			Room newRoom = getManager().getEntities().getRoomOrNull(tp_room);

			if (collidedEntity instanceof Player) // collision with player
			{
				if (newRoom != null) {
					if (super.getManager().getEntities().getCurrentRoomObject().getPlayer().getRangedAmmo() == -1)
						super.getManager().getEntities().setAvailableWeapon(false);
					// Switch to requested room
					if (getManager().getEntities().getRoomOrNull(tp_room) != null) {
						getManager().getEntities().setCurrentRoom(tp_room);
						getManager().getEntities().getCurrentRoomObject().addEntity(collidedEntity);
					}

					float tp_x = collidedEntity.getXpos();
					float tp_y = collidedEntity.getYpos();

					if (tp_exitX) {
						if (tp_x <= tp_threshold) {
							tp_x = newRoom.getWidth() - tp_threshold - collidedEntity.getWidth();
						} else if (tp_x >= oldRoom.getWidth() - tp_threshold) {
							tp_x = tp_threshold;
						}
					}

					if (tp_exitY) {
						if (tp_y <= tp_threshold) {
							tp_y = newRoom.getHeight() - tp_threshold - collidedEntity.getHeight();
						} else if (tp_y >= oldRoom.getHeight() - tp_threshold) {
							tp_y = tp_threshold;
						}
					}

					collidedEntity.setXpos(tp_x);
					collidedEntity.setYpos(tp_y);

					// if we're in the normal gamestate, discover the room
					State maybeGameState = getManager().getStates().getActiveState();

					if (maybeGameState instanceof GameState) {
						GameState gameState = (GameState) maybeGameState;

						if (gameState != null) {
							gameState.setRoomDiscovery(newRoom.getId(), true);
						}
					}
					super.getManager().getEntities().getCurrentRoomObject().checkDoors();
				} else {
					// move the player to their last position
					collidedEntity.setXpos(Math.min(Math.max(0, collidedEntity.getXpos()), oldRoom.getWidth()));
					collidedEntity.setYpos(Math.min(Math.max(0, collidedEntity.getYpos()), oldRoom.getHeight()));
				}
			}
		}
	}
}
