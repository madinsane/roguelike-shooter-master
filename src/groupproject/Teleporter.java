package groupproject;

/**
 * Extension of Entity
 * Describes an object that regenerates the map when touched
 */
public class Teleporter extends Entity {
	private boolean enabled;
	private boolean spawning;
	
	public Teleporter(Manager manager, float xpos, float ypos, float scale, int width, int height) {
		super(manager, xpos, ypos, scale, width, height);
		super.setTexture(super.getManager().getAssets().getTexture("teleporter"));
		super.setSprite(0, false);
		super.setDepth(-1);
		this.enabled = false;
		this.spawning = false;
	}

	/**
	 * Checks if teleporter is enabled
	 * @return is teleporter enabled
	 */
	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets if the teleporter is enabled and plays an animation
	 * @param enabled is enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			super.tryAddAnimation("teleporter_spawn");
			spawning = true;
		}
	}

	/**
	 * Plays animations and updates position
	 */
	@Override
	public void ai() {
		if (enabled && !spawning && super.isAnimationQueueEmpty())
			super.addAnimation("teleporter_idle");
		super.updatePosition();
		super.animations();
		if (spawning && super.isAnimationQueueEmpty()) {
			spawning = false;
		}
	}

	/**
	 * Causes player to leave current level and generate a new level and be placed in new level
	 * @param player player
	 */
	public void leave(Player player) {
		// Generate new map
		State maybeGameState = getManager().getStates().getActiveState();


		if(maybeGameState instanceof GameState)
		{
			GameState gameState = (GameState)maybeGameState;
			// move player to room 0, which we don't delete
			getManager().getEntities().setCurrentRoom(gameState.getRoomIds()[Constants.DUNGEON_WIDTH / 2][Constants.DUNGEON_HEIGHT / 2]);
			getManager().getEntities().getCurrentRoomObject().addEntity(player);
			getManager().getEntities().removeOtherRooms();

			if(gameState != null)
			{
				gameState.incDifficultyLevel();
				gameState.generateForestDungeon(Constants.DUNGEON_WIDTH, Constants.DUNGEON_HEIGHT);
			}
		}
	}

	public void collide(Entity collidedEntity) {

	}
}
