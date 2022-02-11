package groupproject;

import java.util.Random;

/**
 * Extension of Entity
 * Describes a single weapon entity in the game.
 */
public class Weapon extends Entity {

    private Random r = new Random();
    private Room room;                  // The room this weapon exists in
    private int ammo;                   // Stores the current amount of ammo
    private String name;
    private boolean spawning;
    private int deathTimer;

    /**
     * Initialises a new weapon instance to be dropped onto the map
     * @param manager current instance of manager
     * @param xpos starting x position (0,0) being top left corner of window
     * @param ypos starting y position
     * @param scale initial sprite scale
     * @param width width of Entity (pixels)
     * @param height height of Entity (pixels)
     */
    public Weapon(Manager manager, String name, Room room, float xpos, float ypos, float scale, float width, float height) {
        super(manager, xpos, ypos, scale, width, height);
        this.room = room;
        this.name = name;

        super.setXpos(xpos);
        super.setYpos(ypos);
        super.setTexture(super.getManager().getAssets().getTexture("weapon"));
        super.setSprite(0, false);
        super.addAnimation("barrel_teleport_in");
        super.setDepth(-1);
        spawning = true;
        deathTimer = -1;
    }

    /**
     * Handles weapon ai
     */
    @Override
    public void ai() {
        if (!spawning && super.isAnimationQueueEmpty())
            super.addAnimation("barrel_idle");
        super.updatePosition();
        super.animations();
        if (spawning && super.isAnimationQueueEmpty()) {
            spawning = false;
        }
        if (deathTimer > 0) {
            deathTimer--;
        } else if (deathTimer == 0) {
            kill();
        }
    }

    /**
     * Handles weapon collision
     * @param collidedEntity other entity that is being collided with
     */
    @Override
    public void collide(Entity collidedEntity) {
        if (collidedEntity instanceof Player && deathTimer == -1) {
            ((Moby) collidedEntity).changeWeapon(name);
            super.clearAnimations();
            super.addAnimation("barrel_death");
            spawning = true;
            deathTimer = 45;
        }
    }


    /**
     * Returns true if this weapon currently has any ammo.
     * @return true with ammo, false otherwise.
     */
    public boolean hasAmmo() {
        return (ammo != 0);
    }

    /**
     * Gets weapon name
     * @return weapon name
     */
    public String getName() { return this.name; }

}
