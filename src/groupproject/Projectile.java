package groupproject;

/**
 * Extension of Moby
 * Describes a projectile
 */
public class Projectile extends Moby {
	private float lifeTime;
	private Entity sourceEntity;
	private String name;
	private boolean isAnimated;
	private String splash;
	private boolean canSplash;
	private boolean pierce;
	private boolean rotate;

	public Projectile(Manager manager, float xpos, float ypos, float scale, int width, int height, double angle, int damage, int velocity, int lifeTime, Entity sourceEntity, String texture, boolean isAnimated, boolean pierce, boolean rotate, String splash) {
		super(manager, xpos, ypos, scale, width, height);
		this.name = texture;
		this.splash = splash;
		this.pierce = pierce;
		this.rotate = rotate;
		canSplash = false;
		if (!splash.equals(" ")) {
			sourceEntity.setMasterSplash(splash);
			canSplash = true;
		}
		this.isAnimated = isAnimated;
		super.setAngle(angle);
		super.setXvelocity(velocity * (float)Math.cos(angle));
		super.setYvelocity(velocity * (float)Math.sin(angle));
		this.lifeTime = lifeTime;
		super.setTexture(super.getManager().getAssets().getTexture(texture));
		super.setSprite(0, false);
		super.setDam(damage);
		super.setDepth(1);
		this.sourceEntity = sourceEntity;
		super.setKnockbackMultipler(2);
	}

	/**
	 * Gets the entity that created this projectile
	 * @return entity that created this projectile
	 */
	public Entity getSourceEntity() {
		return sourceEntity;
	}

	/**
	 * Animates/Rotates/Moves and updates counters
	 */
	@Override
	public void ai() {
		if (lifeTime <= 0 || super.getHit()) {
			kill();
		}
		if (isAnimated) {
			chooseAnimation();
			super.animations();
		}
		if (rotate) {
			super.getSprite().setRotation((float) (Math.toDegrees(super.getAngle() + Math.PI / 2)));
		}
		super.updatePosition();
		super.clearTemp();
		lifeTime--;
	}

	/**
	 * Chooses projectile animation
	 */
	public void chooseAnimation() {
		String instruction = "_idle";
		super.tryAddAnimation(name + instruction);
	}

	/**
	 * Creates a splash effect
	 * @param sourceEntity entity that created this projectile
	 */
	public void addSplash(Moby sourceEntity) {
		super.getManager().getEntities().addEntity(new Splash(super.getManager(), super.getXpos() - (sourceEntity.getSplashWidth()/2),
				super.getYpos()-(sourceEntity.getSplashHeight()), super.getScale(), sourceEntity.getSplashWidth(), sourceEntity.getSplashHeight(),
				sourceEntity.getSplashDamage(), sourceEntity.getSplashLifetime(), sourceEntity, sourceEntity.getSplashTexture(), sourceEntity.isSplashIsAnimated()));
	}

	/**
	 * Handles projectile collision
	 * @param collidedEntity other entity that is being collided with
	 */
	@Override
	public void collide(Entity collidedEntity) {
		if (!collidedEntity.equals(sourceEntity) && !(collidedEntity instanceof Projectile) && !(collidedEntity instanceof Teleporter) && !(collidedEntity instanceof Weapon) && !(collidedEntity instanceof Item)) {
			collidedEntity.collide(this);
			if (!pierce) {
                kill();
            }
		}
	}

	/**
	 * Gets if this projectile pierces
	 * @return pierce
	 */
    public boolean getPierce() {
        return pierce;
    }

	/**
	 * Tries to create a splash effect
	 */
	public void trySplash() {
		if (canSplash) {
			if (sourceEntity instanceof Moby && !sourceEntity.getMasterSplash().equals("")) {
				((Moby) sourceEntity).loadSplash(sourceEntity.getMasterSplash());
				//System.out.println(sourceEntity + " " + sourceEntity.getMasterSplash());
				addSplash((Moby) sourceEntity);
			}
		}
	}

	/**
	 * Kills the projectile
	 */
	@Override
	public void kill() {
		hide();
		trySplash();
		super.getManager().getEntities().removeEntity(super.getId());
	}

}
