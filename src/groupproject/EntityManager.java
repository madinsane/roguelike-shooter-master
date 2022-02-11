package groupproject;

import org.jsfml.window.event.*;
import org.jsfml.window.event.Event;


import java.awt.*;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager class for handling Entity
 */
public class EntityManager {
	private int idCounter; //IdCounter to automatically assign new ids to new Entity
	private int roomIdCounter;
	private Manager manager; //Manager
	private int currentRoom; // Current room
	private boolean availableWeapon;
	private float scale;
	// Map of rooms
	private Map<Integer, Room> rooms;
	private boolean checkDoors;
	private int roomWidth;
	private int roomHeight;
	private String[] itemList;
	private Random random;
	private Pair<String, Integer>[] enemyGold;
	private int difficultyLevel;
	private int shopRoomId;

	public EntityManager(Manager manager) {
		this.manager = manager;
		scale = 0;
		idCounter = 0;
		roomIdCounter = 0;
		random = new Random();
		
		rooms = new ConcurrentHashMap<>();

	}

	/**
	 * Initializes current room
	 */
	public void initRoom() {
		currentRoom = addRoom();
	}

	/**
	 * Sets the current difficulty level
	 * @param difficultyLevel new difficulty level
	 */
	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	/**
	 * Gets the current difficulty level
	 * @return current difficulty level
	 */
	public int getDifficultyLevel() {
		return difficultyLevel;
	}

	/**
	 * Sets the item list, containing the names of all droppable items
	 * @param itemList item list containing names of all droppable items
	 */
	public void setItemList(String[] itemList) {
		this.itemList = itemList;
	}

	/**
	 * Gets the item list, containing the names of all droppable items
	 * @return item list, containing the names of all droppable items
	 */
	public String[] getItemList() {
		return itemList;
	}

	/**
	 * Sets enemy gold list, containing a pair of (enemy name, gold amount)
	 * @param enemyGold enemy gold list, containing a pair of (enemy name, gold amount)
	 */
	public void setEnemyGold(Pair<String, Integer>[] enemyGold) {
		this.enemyGold = enemyGold;
	}

	/**
	 * Gets enemy gold list, containing a pair of (enemy name, gold amount)
	 * @return enemy gold list, containing a pair of (enemy name, gold amount)
	 */
	public Pair<String, Integer>[] getEnemyGold() {
		return enemyGold;
	}

	/**
	 * Sets the room width
	 * @param roomWidth new room width
	 */
	public void setRoomWidth(int roomWidth) {
		this.roomWidth = roomWidth;
	}

	/**
	 * Sets the room height
	 * @param roomHeight new room height
	 */
	public void setRoomHeight(int roomHeight) {
		this.roomHeight = roomHeight;
	}

	/**
	 * Gets the room width
	 * @return room width
	 */
	public int getRoomWidth() {
		return roomWidth;
	}

	/**
	 * Gets the room height
	 * @return room height
	 */
	public int getRoomHeight() {
		return roomHeight;
	}

	/**
	 * Gets the current room object
	 * @return current room object
	 */
	public Room getCurrentRoomObject()
	{
		return rooms.get(currentRoom);
	}

	/**
	 * Gets the room by id
	 * @param id room id
	 * @return room with id
	 */
	public Room getRoom(int id) {
	    return rooms.get(id);
    }

	/**
	 * Gets the id of the current room
	 * @return current room id
	 */
	public int getCurrentRoom()
	{
		return currentRoom;
	}

	/**
	 * Sets the id of current room
	 * @param Key new id of current room
	 */
	public void setCurrentRoom(int Key)
	{
		currentRoom = Key;
	}

	/**
	 * Gets room by id, if no room is found returns null
	 * @param Key id of room
	 * @return room with id, if no room is found returns null
	 */
	public Room getRoomOrNull(int Key)
	{
		if(rooms.containsKey(Key))
			return rooms.get(Key);
		else
			return null;
	}
	
	/**
	 * Removes a room with a given id to be cleared by garbage collection
	 * @param id id of room to be removed
	 */
	public void removeRoom(int id) {
		if(rooms.containsKey(id))
			rooms.remove(id);
	}
	
	/**
	 * Removes all rooms but the active one.
	 */
	public void removeOtherRooms() {
		Room r = getCurrentRoomObject();
		
		rooms.clear();
		rooms.put(currentRoom, r);
	}

	/**
	 * Drops a new weapon instance onto the map in the current room
	 */
	public int dropRandomWeapon(int weaponId) {

		ArrayList<String[]> projectileData = manager.getData().getData("projectiles");
		Random r = new Random();
		String weaponName = "";
		int randomWeaponIndex = r.nextInt(projectileData.size()-2) + 2;
		//System.out.println(randomWeaponIndex);
		for (int i = 1; i < projectileData.size(); i++) {
			if (i == randomWeaponIndex) {
				String[] row = projectileData.get(i);
				weaponName = row[0];
			}
		}
		Room currentRoom = manager.getEntities().getCurrentRoomObject();

		int weaponWidth = 32;
		int weaponHeight = 32;

		float minXPos = (Constants.ROOM_BOUNDS_PIXELS * scale) + weaponWidth;
		float maxXPos = currentRoom.getWidth() - (Constants.ROOM_BOUNDS_PIXELS * scale) - weaponWidth;
		float minYPos = (Constants.ROOM_BOUNDS_PIXELS * scale) + weaponHeight;
		float maxYPos = currentRoom.getHeight() - (Constants.ROOM_BOUNDS_PIXELS * scale) - weaponHeight;

		float randomXPos = minXPos + (int)(Math.random() * ((maxXPos - minXPos) + 1));
		float randomYPos = minYPos + (int)(Math.random() * ((maxYPos - minYPos) + 1));

		//Checks the state of current weapons, if no weapons are found drops a weapon, if a weapon is found,
		// deletes old weapon and drops a new weapon
		availableWeapon = true;
		if (weaponId == 0) {
			return manager.getEntities().addEntity(new Weapon(manager, weaponName, currentRoom, randomXPos, randomYPos, scale, weaponWidth, weaponHeight));
		} else {
			Entity weapon = manager.getEntities().getEntityOrNull(weaponId);
			if (weapon != null)
				weapon.kill();
			return manager.getEntities().addEntity(new Weapon(manager, weaponName, currentRoom, randomXPos, randomYPos, scale, weaponWidth, weaponHeight));
		}
	}

	/**
	 * Gets if there is a currently available weapon
	 * @return is there a currently available weapon
	 */
	public boolean getAvailableWeapon() { return this.availableWeapon; }

	/**
	 * Sets if there is a currently available weapon
	 * @param availableWeapon is there a currently available weapon
	 */
	public void setAvailableWeapon(boolean availableWeapon) {
		this.availableWeapon = availableWeapon;
	}

	/**
	 * Adds an empty room
	 * @return id of new Room
	 */
	public int addRoom() {
		return addRoom(new Room(manager, roomWidth, roomHeight));
	}

	/**
	 * Adds the given Room to the map and assigns the Room an id
	 * @param newRoom Room to be added to the map
	 * @return id of new Room
	 */
	public int addRoom(Room newRoom) {
		int newId = nextRoomId();
		
		rooms.put(newId, newRoom);
		newRoom.setId(newId);
		
		return newId;
	}
	
	/**
	 * Returns next free GUID for rooms.
	 */
	private int nextRoomId()
	{
		return roomIdCounter++;
	}
	
	/**
	 * Returns next free GUID for entities.
	 */
	public int nextId()
	{
		return idCounter++;
	}
	
	/**
	 * Returns the global manager.
	 */
	public Manager getManager() {
		return manager;
	}

	/**
	 * Inserts all entities into the quadtree
	 */
	public void insertQuad() {
		getCurrentRoomObject().insertQuad();
	}

	/**
	 * Checks collision for an Entity
	 */
	public void checkCollision() {
		getCurrentRoomObject().checkCollision();
	}

	/**
	 * Adds an empty entity
	 * @return id of new Entity
	 */
	public int addEntity() {
		return getCurrentRoomObject().addEntity(new Entity(manager, 0,0,1,16,16));
	}

	/**
	 * Adds the given Entity to the map and assigns the Entity an id
	 * @param newEntity Entity to be added to the map
	 * @return id of new Entity
	 */
	public int addEntity(Entity newEntity) {
		return getCurrentRoomObject().addEntity(newEntity);
	}

	/**
	 * Replaces an entity at a given id
	 * @param id id of entity to be replaced
	 * @param xpos x position
	 * @param ypos y position
	 * @param scale scale
	 * @param width width
	 * @param height height
	 */
	public void replaceEntity(int id, float xpos, float ypos, float scale, int width, int height) {
		for (Map.Entry<Integer,Room> entry : rooms.entrySet()) {
			entry.getValue().replaceEntity(id, xpos, ypos, scale, width, height);
		}
	}

	/**
	 * Removes an entity with a given id to be cleared by garbage collection
	 * @param id id of entity to be removed
	 */
	public void removeEntity(int id) {
		for (Map.Entry<Integer,Room> entry : rooms.entrySet()) {
			entry.getValue().removeEntity(id);
		}
	}

	/**
	 * Gets the entity with the given id
	 * @param id id of entity
	 * @return entity with given id
	 */
	public Entity getEntityOrNull(int id) {
		for (Map.Entry<Integer,Room> entry : rooms.entrySet()) {
			Entity ent = entry.getValue().getEntityOrNull(id);
			
			if(ent != null)
			{
				return ent;
			}
		}
		
		return null;
	}

	/**
	* Drops an item instance onto the map in the current room
	*/

	public void dropItem(float xPos, float yPos, String enemyName, boolean isChampion) {
		String itemName;
		//System.out.println("Size of array list: " + itemData.size());
		int chance = random.nextInt(100);
		int value = 0;
		//Rolls to decide if to spawn a special item in place of gold
		if (chance < Constants.ITEM_DROP_CHANCE) {
			int randomItemIndex = random.nextInt(itemList.length);
			//System.out.println(randomItemIndex);
			itemName = itemList[randomItemIndex];
		} else {
			//If the item is gold gets the enemies gold value and scales it
			itemName = "gold";
			int i=0;
			for (; i<enemyGold.length; i++) {
				if (enemyGold[i].getL().equals(enemyName)) {
					break;
				}
			}
			if (i == enemyGold.length) {
				value = 1;
			} else {
				value = (int)(enemyGold[i].getR() * (Constants.ITEM_GOLD_SCALING * (getDifficultyLevel() + 1)));
			}
			//If enemy is a champion further scales value
			if (isChampion) {
				value *= Constants.CHAMPION_MULTIPLIER;
			}
		}
		//System.out.println("spawned in " + itemName + value);
		Room currentRoom = manager.getEntities().getCurrentRoomObject();
		int itemWidth = 16;
		int itemHeight = 16;
		manager.getEntities().addEntity(new Item(manager, itemName, currentRoom, xPos, yPos, scale, itemWidth, itemHeight, value, false));
	}

	/**
	 * Deletes current game state being replaced by passive state
	 * @param gold player's gold at time of death
	 */
	public void playerDied(int gold) {
		manager.getStates().addState(new PassiveState(manager, gold), true);
	}

	/**
	 * Clean up gamestate before deletion
	 */
	public void cleanUp() {
		clearEntities(false);
		rooms.clear();
	}

	/**
	 * Clears all the entities in all rooms
	 * @param keepPlayer set to true to keep only the player
	 */
	public void clearEntities(boolean keepPlayer) {
		for (Map.Entry<Integer,Room> entry : rooms.entrySet()) {
			entry.getValue().clearEntities(keepPlayer);
		}
	}
	
	/**
	 * Removes the player
	 */
	public void removePlayer() {
		getCurrentRoomObject().removePlayer();
	}

	/**
	 * Sets the texture of an Entity, will set texture to default if none is found
	 * @param id id of Entity
	 * @param key Key corresponding to a loaded texture
	 */
	public void setEntityTexture(int id, String key) {
		for (Map.Entry<Integer,Room> entry : rooms.entrySet()) {
			entry.getValue().setEntityTexture(id, key);
		}
	}

	/**
	 * Draws all the entities, called every frame
	 */
	public void draw() {
		getCurrentRoomObject().draw();
	}

	/**
	 * Calls the logic for each entity, called every frame
	 */
	public void update() {
		getCurrentRoomObject().update();
	}

	/**
	 * Calls the handleInput method for every entity
	 * @param event event from window.pollEvents
	 */
	public void handleInput(Event event) {
		getCurrentRoomObject().handleInput(event);
	}

	/**
	 * Gets scale of game
	 * @return scale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Sets scale of game
	 * @param scale scale
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * Sets whether to force check doors on next frame
	 * @param checkDoors should check doors
	 */
	public void setCheckDoors(boolean checkDoors) {
		this.checkDoors = checkDoors;
	}

	/**
	 * Checks if doors should be checked
	 * @return should doors be checked
	 */
	public boolean isCheckDoors() {
		return checkDoors;
	}

	/**
	 * Gets id of shop room
	 * @return id of shop room
	 */
	public int getShopRoomId() {
		return shopRoomId;
	}

	/**
	 * Sets id of shop room
	 * @param shopRoomId id of shop room
	 */
	public void setShopRoomId(int shopRoomId) {
		this.shopRoomId = shopRoomId;
	}
}
