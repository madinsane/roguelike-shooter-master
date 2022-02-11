package groupproject;

/**
 * Describes a splash effect created by a projectile
 */
public class Splash extends Projectile {

    public Splash(Manager manager, float xpos, float ypos, float scale, int width, int height, int damage, int lifeTime, Entity sourceEntity, String texture, boolean isAnimated) {
        super(manager, xpos, ypos, scale*manager.getEntities().getScale(), width, height, 0, damage, 0, lifeTime, sourceEntity, texture, isAnimated, false, false, "");
        super.setDepth(-1);

    }

    /**
     * Force collides with other entity
     * @param collidedEntity other entity that is being collided with
     */
    @Override
    public void collide(Entity collidedEntity) {
        if (!collidedEntity.equals(super.getSourceEntity()) && !(collidedEntity instanceof Projectile) && !(collidedEntity instanceof Teleporter) && !(collidedEntity instanceof Weapon) && !(collidedEntity instanceof Item)) {
            collidedEntity.collide(this);
        }
    }
}
