package groupproject;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.window.event.Event;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager class for handling Entities in a specific room
 */
public class Room {
	private Map<Integer,Entity> entities; //HashMap for mapping Entity to an id
	private Quadtree quadtree; //Quadtree for collision detection
	private Manager manager;
	private int width;
	private int height;
	private int id;
	private ArrayList<Entity> drawList;
	private Sprite tileset;
	private ArrayList<Text> textList;

	public Room(Manager manager, int width, int height) {
		this.manager = manager;
		this.width = width;
		this.height = height;
		
		entities = new ConcurrentHashMap<>(128);
		drawList = new ArrayList<>();
		tileset = new Sprite();
		textList = new ArrayList<>();
		
		initQuadtree();
	}

	/**
	 * Gets room width
	 * @return room width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets room height
	 * @return room height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets room id
	 * @return room id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets room id
	 * @param id room id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Initialises quadtree
	 */
	public void initQuadtree() {
		quadtree = new Quadtree(0, new Rectangle(0,0, width, height)); //Set quadtree to size of room
	}

	/**
	 * Finds and returns the player
	 * @return player
	 */
	public Player getPlayer() {
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			if (entry.getValue() instanceof Player) {
				return (Player) entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Finds and returns the boss
	 * @return boss
	 */
	public Boss getBoss() {
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			if (entry.getValue() instanceof Boss) {
				return (Boss) entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Inserts all entities into the quadtree
	 */
	public void insertQuad() {
		quadtree.clear();
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			quadtree.insert(entry.getValue());
		}
	}

	/**
	 * Checks collision for an Entity
	 */
	public void checkCollision() {
		List<Entity> returnObjects = new ArrayList<>(128);
		insertQuad();
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			returnObjects.clear();
			quadtree.retrieve(returnObjects, entry.getValue());
			for (Entity returnEntity : returnObjects) {
				if (manager.getAssets().boundingBoxTest(entry.getValue().getSprite(),returnEntity.getSprite()) && !entry.getValue().equals(returnEntity)) {
					returnEntity.collide(entry.getValue());
				}
			}
		}
	}

	/**
	 * Adds an empty entity
	 * @return id of new Entity
	 */
	public int addEntity() {
		return addEntity(new Entity(manager, 0,0,1,16,16));
	}

	/**
	 * Adds the given Entity to the map and assigns the Entity an id
	 * @param newEntity Entity to be added to the map
	 * @return id of new Entity
	 */
	public int addEntity(Entity newEntity) {
		if(newEntity.getRoom() == this)
		{
			return newEntity.getId();
		}
		
		// Entity in a room already?
		if(newEntity.getRoom() != null)
		{
			newEntity.getRoom().removeEntity(newEntity.getId());
		}
		
		int newId = newEntity.getId();
		
		if(newId == -1)
			newId = manager.getEntities().nextId();
	
		entities.put(newId,newEntity);
		entities.get(newId).setId(newId);
		entities.get(newId).setRoom(this);
		return (newId);
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
		if(entities.containsKey(id)) {
			entities.replace(id, new Entity(manager, xpos, ypos, scale, width, height));
			entities.get(id).setId(id);
			entities.get(id).setRoom(this);
		}
	}

	/**
	 * Removes an entity with a given id to be cleared by garbage collection
	 * @param id id of entity to be removed
	 */
	public void removeEntity(int id) {
		if(entities.containsKey(id))
			entities.remove(id);
	}

	/**
	 * Gets the entity with the given id
	 * @param id id of entity
	 * @return entity with given id
	 */
	public Entity getEntityOrNull(int id) {
		if(entities.containsKey(id))
			return entities.get(id);
		else
			return null;
	}

	/**
	 * Clears all the entities on the screen
	 * @param keepPlayer set to true to keep only the player
	 */
	public void clearEntities(boolean keepPlayer) {
		if (keepPlayer) {
			Entity player = entities.get(0);
			entities.clear();
			addEntity(player);
		}
		entities.clear();
	}

	/**
	 * Removes the player
	 */
	public void removePlayer() {
		Entity player = entities.get(0);
		removeEntity(player.getId());
	}
	
	/**
	 * Sets the texture of an Entity, will set texture to default if none is found
	 * @param id id of Entity
	 * @param key Key corresponding to a loaded texture
	 */
	public void setEntityTexture(int id, String key) {
		entities.get(id).setTexture(manager.getAssets().getTexture(key));
	}

	/**
	 * Draws all the entities, called every frame
	 */
	public void draw() {
		for (int i = 0; i < textList.size(); i++)
		{
			manager.getWindow().draw(textList.get(i));
		}
		for (int i = 0; i < drawList.size(); i++)
		{
			manager.getWindow().draw(drawList.get(i).getSprite());
		}
		//for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			//manager.getWindow().draw(entry.getValue().getSprite());
		//}
	}

	/**
	 * Aggravates the shopkeeper making all items free and locking doors
	 */
	public void aggro() {
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			if (entry.getValue() instanceof Item) {
				if (((Item) entry.getValue()).isShop()) {
					((Item) entry.getValue()).aggro();
				}
			}
			if (entry.getValue() instanceof Shopkeeper) {
				((Shopkeeper) entry.getValue()).aggro();
			}
		}
		checkDoors();
	}

	/**
	 * Adds text to the drawables list
	 * @param text text to add
	 */
	public void addText(Text text) {
		textList.add(text);
	}

	/**
	 * Calls the ai for each entity, called every frame
	 */
	public void update() {
		// update draw order
		drawList.clear();
		
		for (Map.Entry<Integer,Entity> entry : entities.entrySet())
		{
			drawList.add(entry.getValue());
		}
		
		Collections.sort(drawList);
		
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			entry.getValue().ai();
		}
	}

	/**
	 * Calls the handleInput method for every entity
	 * @param event event from window.pollEvents
	 */
	public void handleInput(Event event) {
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			entry.getValue().handleInput(event);
		}
	}

	/**
	 * Gets current tileset
	 * @return tileset
	 */
	public Sprite getTileset() {
		return tileset;
	}

	/**
	 * Sets current tileset and calculates scale based on difference between tileset and room size
	 * @param tileset tileset texture
	 */
	public void setTileset(Texture tileset) {
		this.tileset.setTexture(tileset);
		float scale = width / (float) tileset.getSize().x;
		//System.out.println(scale + " " + scale + " " + tileset.getSize().x);
		manager.getEntities().setScale(scale);
		this.tileset.setScale(manager.getEntities().getScale(), manager.getEntities().getScale());
	}

	/**
	 * Checks if any enemies are in the current room, if so locks doors, else unlocks doors
	 */
	public void checkDoors() {
		boolean enabled = true;
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			if (entry.getValue() instanceof Enemy && !(entry.getValue() instanceof Shopkeeper)) {
				enabled = false;
				break;
			}
			if (entry.getValue() instanceof Shopkeeper) {
				if (((Shopkeeper) entry.getValue()).isAngry()) {
					enabled = false;
					break;
				}
			}
		}
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			if (entry.getValue() instanceof Door) {
				((Door) entry.getValue()).setEnabled(enabled);
			}
		}
	}

	/**
	 * Opens the floor exit when boss is defeated
	 */
	public void openExit() {
		for (Map.Entry<Integer,Entity> entry : entities.entrySet()) {
			if (entry.getValue() instanceof Teleporter) {
				((Teleporter) entry.getValue()).setEnabled(true);
			}
		}
	}
}
