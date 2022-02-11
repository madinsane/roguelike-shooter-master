package groupproject;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.event.Event;
import java.util.*;

import java.util.LinkedList;

/**
 * Class to describe an entity in the game
 */
public class Entity implements Comparable<Entity> {
	private Sprite sprite; //Current sprite (based on a backing texture)
	private float xpos; //X position
	private float ypos; //Y position
	private float xor; //X origin
	private float yor; //Y origin
	private float height; //Height
	private float width; //Width
	private float scale; //Scale
	private float depth; //Depth
	private Manager manager; //Manager
	private Room room;
	private int id; //Entity id for use with EntityManager
	private int animationCounter; //Counter for number of frames the current animation frame was displayed for
	private LinkedList<Triplet<Integer, Integer, Boolean>> animationQueue;
	private int spritePos; //Current spritesheet position
	private String currentAnimation; // next animation to load into the queue when the animationQueue has emptied. Set to -1 to not play anything.
	private Boolean hit = false; //Whether the enitity has been hit
	private Boolean stop = false; //Whether the enitity has been hit
	private Entity latestCollision; //Entity last collided with
	private float moveSpeed;
	private ArrayList<String> exceptions = new ArrayList<String>(); //Array of exceptions (where collide does not occur)
	private ArrayList<String> stoppage = new ArrayList<String>(); //Array of entitys which cause it to stop on contact
	private int damage = 0; //Damage dealt
	private boolean moved; //Whether this Entity has made an unchecked movement;
	private String previousAnimation;
	private String masterSplash;

	/**
	 * Creates an Entity
	 * @param manager current instance of manager
	 * @param xpos starting x position (0,0) being top left corner of window
	 * @param ypos starting y position
	 * @param scale initial sprite scale
	 * @param width width of Entity (pixels)
	 * @param height height of Entity (pixels)
	 */
	public Entity(Manager manager, float xpos, float ypos, float scale, float width, float height) {
		sprite = new Sprite();
		this.xpos = xpos;
		this.ypos = ypos;
		this.scale = scale;
		this.width = width;
		this.height = height;
		this.manager = manager;

		// Animation vars
		animationCounter = 0;
		animationQueue = new LinkedList<>();
		currentAnimation = "";
		previousAnimation = "";
		this.room = null;
		this.id = -1;
		this.xor = 0;
		this.yor = 0;
		this.moved = true;
		this.depth = 0;
		masterSplash = "rocket";
	}

	/**
	 * Gets type of splash
	 * @return type of splash
	 */
	public String getMasterSplash() {
		return masterSplash;
	}

	/**
	 * Sets type of splash
	 * @param masterSplash new type of splash
	 */
	public void setMasterSplash(String masterSplash) {
		this.masterSplash = masterSplash;
	}

	/**
	 * Sets previous animation
	 * @param previousAnimation previous animation
	 */
	public void setPreviousAnimation(String previousAnimation) {
		this.previousAnimation = previousAnimation;
	}

	/**
	 * Sets current animation
	 * @param currentAnimation current animation
	 */
	public void setCurrentAnimation(String currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	/**
	 * Gets current animation
	 * @return current animation
	 */
	public String getCurrentAnimation() {
		return currentAnimation;
	}

	/**
	 * Removes this entity from the map ready to be cleaned up by garbage collection
	 */
	public void kill() {
		getManager().getEntities().removeEntity(getId());
	}

	/**
	 * Sets the depth this entity is drawn at.
	 * @param depth The new depth.
	 */
	public void setDepth(float depth) {
		this.depth = depth;
	}
	
	/**
	 * Sets the depth this entity is drawn at.
	 * @return The new depth.
	 */
	public float getDepth() {
		return depth;
	}
	
	/**
	 * Sets the room this entity thinks it's in. (internal use only please)
	 * @param r The new room.
	 */
	public void setRoom(Room r) {
		this.room = r;
	}
	
	/**
	 * Get the room this entity thinks it's in.
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * Sets the x position
	 * @param xpos new x position
	 */
	public void setXpos(float xpos) {
		this.moved = true;
		this.xpos = xpos;
	}

	/**
	 * Sets the y position
	 * @param ypos new y position
	 */
	public void setYpos(float ypos) {
		this.moved = true;
		this.ypos = ypos;
	}
	
	/**
	 * Sets the x origin
	 * @param xpos new x origin
	 */
	public void setXorigin(float xpos) {
		this.moved = true;
		this.xor = xpos;
	}

	/**
	 * Sets the y origin
	 * @param ypos new y origin
	 */
	public void setYorigin(float ypos) {
		this.moved = true;
		this.yor = ypos;
	}

	/**
	 * Sets the height
	 * @param height new height
	 */
	public void setHeight(float height) {
		this.moved = true;
		this.height = height;
	}

	/**
	 * Sets the width
	 * @param width new width
	 */
	public void setWidth(float width) {
		this.moved = true;
		this.width = width;
	}

	/**
	 * This method should only be called by EntityManager to setup the id initially and not called again
	 * @param id The id corresponding to this Entity in the HashMap
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the id to be used with EntityManager
	 * @return Entity id
	 */



	/**
	 * Adds addition to the exceptions list
	 */
    public void addException(String excep){
		exceptions.add(excep);
	}

	/**
	 * Adds addition to the stoppage list
	 */
	public void addStoppage(String attac){
		stoppage.add(attac);
	}

	/**
	 * Changes stop
	 * @return stop
	 */
	public Boolean getStop() {
		return stop;
	}
	/**
	 * Sets stop to a new boolean
	 */
	public void changeStop(Boolean bool) {
		stop = bool;
	}

	/**
	 * Changes damage
	 * @return damage
	 */
	public int getDam() {
		return damage;
	}
	/**
	 * Sets the damage value
	 */
	public void setDam(int damage) {
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	/**
	 * Gets the sprite
	 * @return Sprite
	 */
	public Sprite getSprite() {
		return sprite;
	}

	/**
	 * Gets the sprite position
	 * @return Position of the sprite in the spritesheet
	 */
	public int getSpritePos() { return spritePos; }

  /**
	 * Sets the sprite colour
	 */
	public void setSpriteColour(Sprite sprite) {
		this.sprite = sprite;
	}

	/**
	 * Gets the x position
	 * @return x position
	 */
	public float getXpos() {
		return xpos;
	}

	/**
	 * Gets the y position
	 * @return y position
	 */
	public float getYpos() {
		return ypos;
	}
	
	/**
	 * Gets the x origin
	 * @return x origin
	 */
	public float getXorigin() {
		return xor;
	}

	/**
	 * Gets the y origin
	 * @return y origin
	 */
	public float getYorigin() {
		return yor;
	}

	
	/**
	 * Gets the unscaled height
	 * @return height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * Gets the unscaled width
	 * @return width
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * Gets the scale
	 * @return scale
	 */
	public float getScale() {
		return scale;
	}
	
	/**
	 * Returns true if this entity moved this frame
	 * @return moved?
	 */
	public boolean getHasMoved() {
		return moved;
	}
	
	/**
	 * Gets the height
	 * @return height
	 */
	public float getScaledHeight() {
		return height * scale;
	}

	/**
	 * Gets the width
	 * @return width
	 */
	public float getScaledWidth() {
		return width * scale;
	}

	/**
	 * Gets the manager
	 * @return manager
	 */
	public Manager getManager() {
		return manager;
	}

	/**
	 * Gets the latest entity collided with
	 * @return latestCollision
	 */
	public Entity getlastEnt() {
		return latestCollision;
	}

	/**
	 * Returns hit
	 * @return hit
	 */
	public Boolean getHit() {
		return hit;
	}

	/**
	 * Sets hit to a new boolean
	 */
	public void changeHit(Boolean bool) {
		hit = bool;
	}

	/**
	 * Sets the texture to be used by the sprite
	 * @param texture texture
	 */
	public void setTexture(Texture texture) {
		sprite.setTexture(texture);
	}

	/**
	 * Adds an animation to this entity's animation list.
	 * @param name name mapped to the animation list
	 */
	public void addAnimation(String name)
	{
		for (Triplet<Integer, Integer, Boolean> entry : manager.getData().getAnimations(name)) {
			animationQueue.add(entry);
		}
		previousAnimation = name;
	}

	public String getPreviousAnimation() {
		return previousAnimation;
	}

	public boolean tryClearAnimations(String newAnimation) {
		if (!previousAnimation.equals(newAnimation)) {
			clearAnimations();
			return true;
		}
		return false;
	}

	public void tryAddAnimation(String newAnimation) {
		if (tryClearAnimations(newAnimation))
			addAnimation(newAnimation);
	}

	/**
	 * Adds an animation frame to the queue
	 * @param pos position in the spritesheet to change to
	 * @param duration frames to show the animation, e.g. 1 to show for 1 frame
	 */
	private void queueAnimationFrame(int pos, int duration, boolean flip) {
		animationQueue.addLast(new Triplet<>(pos, duration, flip));
	}

	/**
	 * Clears the animation queue.
	 */
	public void clearAnimations() {
		animationQueue.clear();
	}

	/**
	 * Updates the position of the sprite to the current x position and y position
	 */
	public void updatePosition() {
		if(this.moved)
		{
			sprite.setPosition(xpos, ypos);
			sprite.setOrigin(xor, yor);
			getSprite().setScale(scale, scale);
		}
		
		this.moved = false;
	}

	/**
	 * Method to handle the ai, called every frame
	 */
	public void ai() {
		updatePosition();
		animations();
	}

	/**
	 * Method to handle input to be acted on by an entity, called every frame
	 * @param event event from window.pollEvents
	 */
	public void handleInput(Event event) {

	}

	/**
	 * Checks if the animation queue is empty
	 * @return is animation queue empty
	 */
	public boolean isAnimationQueueEmpty() {
		return animationQueue.isEmpty();
	}

	/**
	 * Method to handle playing animations for the entity, called every frame
	 */
	public void animations() {
		if (!animationQueue.isEmpty()) {
			if (animationQueue.getFirst().getM() > animationCounter) {
				if (spritePos != animationQueue.getFirst().getL()) {
					setSprite(animationQueue.getFirst().getL(), animationQueue.getFirst().getR());
				}
				
				animationCounter++;
			} else {
				animationQueue.removeFirst();
				animationCounter = 0;
			}
		} else {
			if (currentAnimation.equals("")) {
				return;
			}
			addAnimation(currentAnimation);
		}

	}

	/**
	 * Called when this entity is colliding with another entity
	 * @param collidedEntity other entity that is being collided with
	 */
	public void collide(Entity collidedEntity) {
		//Temporary fix
		String temp = String.valueOf(collidedEntity.getClass());

		/*if (stoppage.contains(temp)){
			stop = true;
			hit = true;
		} else if (!exceptions.contains(temp)){
			hit = true;
			latestCollision = collidedEntity;
		}*/
	}

	/**
	 * Sets the sprite rectangle to the specified sprite out of a sprite sheet.
	 * 0 is the first sprite, 8 is the first sprite of the second row
	 * @param pos The integer corresponding to the position of the desired sprite in the current sprite sheet
	 */
	public void setSprite(int pos, boolean flip) {
		spritePos = pos;
		int left = (int)(Math.floorMod(pos, 8) * width);
		int top = (int)(Math.floorDiv(pos, 8) * height);
		if (flip) {
			IntRect test = new IntRect(left+(int)width, top, -(int)width, (int)height);
			sprite.setTextureRect(new IntRect(left+(int)width, top, -(int)width, (int)height));
		} else {
			sprite.setTextureRect(new IntRect(left, top, (int) width, (int) height));
		}
	}

	/**
	 * @return The number of sprites this Entity can display.
	 */
	public int getNumSprites()
	{
		return (int)((sprite.getTexture().getSize().x / width) * (sprite.getTexture().getSize().y / height));
	}

	@Override
	public int compareTo(Entity other)
	{
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		
		if(getDepth() == other.getDepth()) return EQUAL;
		if(getDepth() > other.getDepth()) return AFTER;
		
		return BEFORE;
	}
	
}
