package groupproject;

import org.jsfml.graphics.*;

/**
 * Extension of Entity
 * Describes a moving Entity
 */
public class Moby extends Entity {
	private float xvelocity;
	private float yvelocity;
	private float tempXvelocity;
	private float tempYvelocity;
	private float currentXvelocity;
	private float currentYvelocity;
	private double angle;
	private boolean isAnimLocked;
	private Sprite mobySprite;
	private float xAcceleration;
	private float yAcceleration;
	private int health;
	private int resistance;
	private int flashTimer;
	private Color currentColor;
	private int deadCounter;
	private float knockbackMultiplier;
	private int moveSpeed;
	private int range;
	private int rangedDamage;
	private int rangedFireRate;
	private int rangedSpeed;
	private int rangedLifetime;
	private int rangedProjectiles;
	private int rangedAmmo;
	private int rangedFireCounter;
	private double rangedSpread;
	private String weaponName;
	private int left;
	private int right;
	private int iFrames;
	private String direction;
	private String name;
	private float damageMultiplier;
	private int weaponId;
	private int rangedWidth;
	private int rangedHeight;
	private String rangedTexture;
	private boolean rangedIsAnimated;
	private Color baseColor;
	private int directions;
	private String rangedSplash;
	private int splashDamage;
	private int splashLifetime;
	private int splashWidth;
	private int splashHeight;
	private String splashTexture;
	private boolean splashIsAnimated;
	private boolean rangedPierce;
	private boolean rangedRotate;
	private float rangedScale;

	public Moby(Manager manager, float xpos, float ypos, float scale, float width, float height) {
		super(manager, xpos, ypos, scale, width, height);
		tempXvelocity = 0;
		rangedFireCounter = 0;
		tempYvelocity = 0;
		isAnimLocked = false;
		xAcceleration = 0;
		yAcceleration = 0;
		xvelocity = 0;
		yvelocity = 0;
		currentXvelocity = 0;
		currentYvelocity = 0;
		health = 0;
		resistance = 1;
		flashTimer = 0;
		currentColor = Color.RED;
		deadCounter = -1;
		knockbackMultiplier = 1;
		iFrames = 2;
		damageMultiplier = 1;
		weaponId = 0;
		baseColor = Color.WHITE;
		directions = 2;
	}

	/**
	 * Updates the logic each frame
	 */
	@Override
	public void ai() {
		updatePosition();
		clearTemp();
	}

	/**
	 * Sets name of moby
	 * @param name name of moby
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets name of moby
	 * @return name of moby
	 */
	public String getName() {
		return name;
	}

	/**
	 * Loads all enemy data from enemies
	 * @param name name of enemy
	 * @param difficulty current difficulty
	 */
	public void loadEnemyData(String name, int difficulty) {
		String[] enemyData = super.getManager().getData().getRowData("enemies", name);
		if (enemyData == null) {
			enemyData = super.getManager().getData().getRowData("enemies", "skinman");
		}
		health = Integer.parseInt(enemyData[1]);
		moveSpeed = Integer.parseInt(enemyData[2]);
		super.setDam(Integer.parseInt(enemyData[3]));
		range = Integer.parseInt(enemyData[4]);
		weaponName = enemyData[5];
		super.setWidth(Integer.parseInt(enemyData[6]));
		super.setHeight(Integer.parseInt(enemyData[7]));
		left = Integer.parseInt(enemyData[8]);
		right = Integer.parseInt(enemyData[9]);
		iFrames = Integer.parseInt(enemyData[10]);
		super.setCurrentAnimation(enemyData[11]);
		super.setPreviousAnimation(super.getCurrentAnimation());
		health += difficulty * Integer.parseInt(enemyData[13]);
		moveSpeed += difficulty * Float.parseFloat(enemyData[14]);
		super.setDam(super.getDam() + (difficulty * Integer.parseInt(enemyData[15])));
		directions = Integer.parseInt(enemyData[19]);
	}

	/**
	 * Gets current weapon name
	 * @return current weapon name
	 */
	public String getWeaponName() {
		return weaponName;
	}

	/**
	 * Gets range (distance between player and moby before it can attack)
	 * @return range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * Sets range (distance between player and moby before it can attack)
	 * @param range new range
	 */
	public void setRange(int range) {
		this.range = range;
	}

	/**
	 * Changes weapon to weapon with corresponding name from projectiles
	 * @param name name of weapon
	 */
	public void changeWeapon(String name) {
		String[] projectileData = super.getManager().getData().getRowData("projectiles", name);
		if (projectileData == null) {
			projectileData = super.getManager().getData().getRowData("projectiles", "player_pistol");
		}
		if (!(this instanceof Player)) {
			damageMultiplier += (float)getDam() / 10;
		}
		rangedDamage = (int)((float)Integer.parseInt(projectileData[1]) * damageMultiplier);
		rangedFireRate = Integer.parseInt(projectileData[2]);
		rangedSpeed = Integer.parseInt(projectileData[3]);
		rangedLifetime = Integer.parseInt(projectileData[4]);
		rangedProjectiles = Integer.parseInt(projectileData[5]);
		rangedAmmo = Integer.parseInt(projectileData[6]);
		rangedSpread = Math.toRadians(Integer.parseInt(projectileData[7]));
		weaponId = Integer.parseInt(projectileData[8]);
		rangedWidth = Integer.parseInt(projectileData[9]);
		rangedHeight = Integer.parseInt(projectileData[10]);
		rangedTexture = projectileData[11];
		rangedIsAnimated = Boolean.parseBoolean(projectileData[12]);
		rangedSplash = projectileData[13];
		rangedPierce = Boolean.parseBoolean(projectileData[14]);
		rangedRotate = Boolean.parseBoolean(projectileData[15]);
		rangedScale = Float.parseFloat(projectileData[16]);
	}

	/**
	 * Loads splash data for corresponding name from splash
	 * @param name name of splash
	 */
	public void loadSplash(String name) {
		String[] splashData = super.getManager().getData().getRowData("splash", name);
		if (splashData == null) {
			splashData = super.getManager().getData().getRowData("splash", "rocket");
		}
		splashDamage = (int)((float)Integer.parseInt(splashData[1]) * damageMultiplier);
		splashLifetime = Integer.parseInt(splashData[2]);
		splashWidth = Integer.parseInt(splashData[3]);
		splashHeight = Integer.parseInt(splashData[4]);
		splashTexture = splashData[5];
		splashIsAnimated = Boolean.parseBoolean(splashData[6]);
	}

	/**
	 * Sets if ranged attack is animated
	 * @param rangedIsAnimated is ranged attack animated
	 */
	public void setRangedIsAnimated(boolean rangedIsAnimated) {
		this.rangedIsAnimated = rangedIsAnimated;
	}

	/**
	 * Sets ranged splash to spawn on death
	 * @param rangedSplash name of splash
	 */
	public void setRangedSplash(String rangedSplash) {
		this.rangedSplash = rangedSplash;
	}

	/**
	 * Gets if ranged attack pierces
	 * @return can pierce
	 */
	public boolean isRangedPierce() {
		return rangedPierce;
	}

	/**
	 * Gets amount of directions a moby has (2 is left and right, 4 for up, down, left, right)
	 * @return amount of directions
	 */
	public int getDirections() {
		return directions;
	}

	/**
	 * Sets width of ranged projectile
	 * @param rangedWidth width of ranged projectile
	 */
	public void setRangedWidth(int rangedWidth) {
		this.rangedWidth = rangedWidth;
	}

	/**
	 * Sets height of ranged projectile
	 * @param rangedHeight height of ranged projectile
	 */
	public void setRangedHeight(int rangedHeight) {
		this.rangedHeight = rangedHeight;
	}

	/**
	 * Sets texture of ranged projectile
	 * @param rangedTexture texture of ranged projectile
	 */
	public void setRangedTexture(String rangedTexture) {
		this.rangedTexture = rangedTexture;
	}

	/**
	 * Gets width of ranged projectile
	 * @return width of ranged projectile
	 */
	public int getRangedWidth() {
		return rangedWidth;
	}

	/**
	 * Gets height of ranged projectile
	 * @return height of ranged projectile
	 */
	public int getRangedHeight() {
		return rangedHeight;
	}

	/**
	 * Gets texture of ranged projectile
	 * @return texture of ranged projectile
	 */
	public String getRangedTexture() {
		return rangedTexture;
	}

	/**
	 * Gets id of current weapon
	 * @return id of current weapon
	 */
	public int getWeaponId() {
		return weaponId;
	}

	/**
	 * Gets current damage multiplier
	 * @return damage multiplier
	 */
	public float getDamageMultiplier() {
		return damageMultiplier;
	}

	/**
	 * Sets current damage multiplier
	 * @param damageMultiplier new damage multiplier
	 */
	public void setDamageMultiplier(float damageMultiplier) {
		this.damageMultiplier = damageMultiplier;
	}

	/**
	 * Adds a projectile at a given angle with current ranged properties
	 * @param angle angle to spawn projectile at
	 */
	public void addProjectile(double angle) {
		super.getManager().getEntities().addEntity(new Projectile(super.getManager(), super.getXpos() - super.getWidth() / 2, super.getYpos() - super.getHeight() / 2,
				rangedScale, rangedWidth, rangedHeight,
				angle, getRangedDamage(), getRangedSpeed(), getRangedLifetime(), this, rangedTexture, rangedIsAnimated, rangedPierce, rangedRotate, rangedSplash));
	}

	/**
	 * Gets scale of ranged projectile
	 * @return scale of ranged projectile
	 */
	public float getRangedScale() {
		return rangedScale;
	}

	/**
	 * Sets scale of ranged projectile
	 * @param rangedScale scale of ranged projectile
	 */
	public void setRangedScale(float rangedScale) {
		this.rangedScale = rangedScale;
	}

	/**
	 * Gets if the ranged projectile can rotate
	 * @return can ranged projectile rotate
	 */
	public boolean isRangedRotate() {
		return rangedRotate;
	}

	/**
	 * Sets if ranged projectile can rotate
	 * @param rangedRotate can ranged projectile rotate
	 */
	public void setRangedRotate(boolean rangedRotate) {
		this.rangedRotate = rangedRotate;
	}

	/**
	 * Gets splash damage
	 * @return splash damage
	 */
	public int getSplashDamage() {
		return splashDamage;
	}

	/**
	 * Gets splash lifetime
	 * @return splash lifetime
	 */
	public int getSplashLifetime() {
		return splashLifetime;
	}

	/**
	 * Gets splash width
	 * @return splash width
	 */
	public int getSplashWidth() {
		return splashWidth;
	}

	/**
	 * Gets splash height
	 * @return splash height
	 */
	public int getSplashHeight() {
		return splashHeight;
	}

	/**
	 * Gets splash texture
	 * @return splash texture
	 */
	public String getSplashTexture() {
		return splashTexture;
	}

	/**
	 * Checks if splash is animated
	 * @return is splash animated
	 */
	public boolean isSplashIsAnimated() {
		return splashIsAnimated;
	}

	/**
	 * Gets if ranged projectile can splash and if so which splash will it create
	 * @return splash to spawn
	 */
	public String getRangedSplash() {
		return rangedSplash;
	}

	/**
	 * Gets if ranged projectile is animated
	 * @return is ranged projectile is animated
	 */
	public boolean isRangedIsAnimated() {
		return rangedIsAnimated;
	}

	/**
	 * Sets ranged damage
	 * @param rangedDamage ranged damage
	 */
	public void setRangedDamage(int rangedDamage) {
		this.rangedDamage = rangedDamage;
	}

	/**
	 * Sets ranged fire rate
	 * @param rangedFireRate ranged fire rate
	 */
	public void setRangedFireRate(int rangedFireRate) {
		this.rangedFireRate = rangedFireRate;
	}

	/**
	 * Sets ranged projectile speed
	 * @param rangedSpeed ranged projectile speed
	 */
	public void setRangedSpeed(int rangedSpeed) {
		this.rangedSpeed = rangedSpeed;
	}

	/**
	 * Sets ranged projectile lifetime
	 * @param rangedLifetime ranged projectile lifetime
	 */
	public void setRangedLifetime(int rangedLifetime) {
		this.rangedLifetime = rangedLifetime;
	}

	/**
	 * Sets amount of projectiles to spawn
	 * @param rangedProjectiles amount of projectiles to spawn
	 */
	public void setRangedProjectiles(int rangedProjectiles) {
		this.rangedProjectiles = rangedProjectiles;
	}

	/**
	 * Sets ammo
	 * @param rangedAmmo ammo
	 */
	public void setRangedAmmo(int rangedAmmo) {
		this.rangedAmmo = rangedAmmo;
	}

	/**
	 * Sets counter for time between ranged attacks
	 * @param rangedFireCounter counter for time between ranged attacks
	 */
	public void setRangedFireCounter(int rangedFireCounter) {
		this.rangedFireCounter = rangedFireCounter;
	}

	/**
	 * Sets ranged spread between multiple projectile attacks
	 * @param rangedSpread ranged spread
	 */
	public void setRangedSpread(double rangedSpread) {
		this.rangedSpread = rangedSpread;
	}

	/**
	 * Gets ranged damage
	 * @return ranged damage
	 */
	public int getRangedDamage() {
		return rangedDamage;
	}

	/**
	 * Gets ranged fire rate (frames)
	 * @return ranged fire rate (frames)
	 */
	public int getRangedFireRate() {
		return rangedFireRate;
	}

	/**
	 * Gets projectile speed
	 * @return projectile speeed
	 */
	public int getRangedSpeed() {
		return rangedSpeed;
	}

	/**
	 * Gets lifetime of ranged projectiles (frames)
	 * @return lifetime of ranged projectiles (frames)
	 */
	public int getRangedLifetime() {
		return rangedLifetime;
	}

	/**
	 * Gets amount of ranged projectiles to spawn each attack
	 * @return amount of ranged projectiles to spawn each attack
	 */
	public int getRangedProjectiles() {
		return rangedProjectiles;
	}

	/**
	 * Gets ranged ammo
	 * @return ranged ammo
	 */
	public int getRangedAmmo() {
		return rangedAmmo;
	}

	/**
	 * Gets counter for time between ranged attacks
	 * @return counter for time between ranged attacks
	 */
	public int getRangedFireCounter() {
		return rangedFireCounter;
	}

	/**
	 * Gets ranged spread between multiple projectile attacks
	 * @return ranged spread between multiple projectile attacks
	 */
	public double getRangedSpread() {
		return rangedSpread;
	}

	/**
	 * Sets moveSpeed to given value
	 */
	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	/**
	 * Gets the moveSpeed
	 * @return moveSpeed
	 */
	public int getMoveSpeed() {
		return moveSpeed;
	}

	/**
	 * Gets knockback multiplier
	 * @return knockback multiplier
	 */
	public float getKnockbackMultipler() {
		return knockbackMultiplier;
	}

	/**
	 * Sets knockback multiplier
	 * @param knockbackMultiplier knockback multiplier
	 */
	public void setKnockbackMultipler(float knockbackMultiplier) {
		this.knockbackMultiplier = knockbackMultiplier;
	}

	/**
	 * Sets x acceleration to given value
	 * @param xAcceleration new value of x acceleration
	 */
	public void setXacceleration(float xAcceleration) {
		this.xAcceleration = xAcceleration;
	}

	/**
	 * Sets y acceleration to given value
	 * @param yAcceleration new value of y acceleraton
	 */
	public void setYacceleration(float yAcceleration) {
		this.yAcceleration = yAcceleration;
	}

	/**
	 * Sets x velocity to given value
	 * @param xvelocity new value of x velocity
	 */
	public void setXvelocity(float xvelocity) {
		this.xvelocity = xvelocity;
	}

	/**
	 * Sets y velocity to given value
	 * @param yvelocity new value of y velocity
	 */
	public void setYvelocity(float yvelocity) {
		this.yvelocity = yvelocity;
	}

	/**
	 * Sets temp x velocity to given value, cleared every frame
	 * @param tempXvelocity new value of temp x velocity
	 */
	public void setTempXvelocity(float tempXvelocity) {
		this.tempXvelocity = tempXvelocity;
	}

	/**
	 * Sets temp y velocity to given value, cleared every frame
	 * @param tempYvelocity new value of temp y velocity
	 */
	public void setTempYvelocity(float tempYvelocity) {
		this.tempYvelocity = tempYvelocity;
	}

	/**
	 * Sets health to given value
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * Removes health when hit
	 */
	public void damage (Entity collidedEntity){
		health = health - collidedEntity.getDam();
		//System.out.println(health);
		if (health <= 0) {
			if (deadCounter < 0)
				setKill();
		}
	}

	/**
	 * Sets an entity to die in the next 20 frames
	 */
	public void setKill() {
		deadCounter = 20;
	}

	/**
	 * Kills a moby
	 */
	@Override
	public void kill() {
		hide();
		super.getManager().getEntities().removeEntity(super.getId());
	}

	/**
	 * Sets angle to given value
	 * @param angle new value of angle
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * Gets the health
	 * @return health
	 */
	public int getHealth() { return health; }

	/**
	 * Gets the left sprite location
	 * @return left
	 */
	public int getLeft() { return left; }

	/**
	 * Gets the right sprite location
	 * @return right
	 */
	public int getRight() { return right; }

	/**
	 * Sets the moby's state of being locked in an animation
	 * @param animLocked true if locked
	 */
	public void setAnimLocked(boolean animLocked) {
		isAnimLocked = animLocked;
	}

	/**
	 * Gets the angle
	 * @return angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Gets temp x velocity, cleared every frame
	 * @return temp x velocity
	 */
	public float getTempXvelocity() {
		return tempXvelocity;
	}

	/**
	 * Gets temp y velocity, cleared every frame
	 * @return temp y velocity
	 */
	public float getTempYvelocity() {
		return tempYvelocity;
	}

	public void setDead(int deadCounter) {
		this.deadCounter = deadCounter;
	}

	/**
	 * Gets x velocity
	 * @return x velocity
	 */
	public float getXvelocity() {
		return xvelocity;
	}

	/**
	 * Gets y velocity
	 * @return y velocity
	 */
	public float getYvelocity() {
		return yvelocity;
	}

	/**
	 * Checks if the moby is currently locked in an animation
	 * @return true if locked
	 */
	public boolean isAnimLocked() {
		return isAnimLocked;
  }
  
  	/**
	 * Gets x acceleration
	 * @return x acceleration
	 */
	public float getXacceleration() {
		return xAcceleration;
	}

	/**
	 * Gets y acceleration
	 * @return y acceleration
	 */
	public float getYacceleration() {
		return yAcceleration;
	}

	/**
	 * Gets resistance to knockback
	 * @param resistance resistance to knockback
	 */
	public void setResistance(int resistance) {
		this.resistance = resistance;
	}

	/**
	 * Gets frames till death
	 * @return frames till death
	 */
	public int getDeadCounter() {
		return deadCounter;
	}

	/**
	 * Clears temp values, checks if moby should die and updates counters
	 */
	public void clearTemp() {
		tempXvelocity = 0;
		tempYvelocity = 0;
		if (xvelocity == 0) {
			if (currentXvelocity > 0) xAcceleration = -Constants.MOBY_NATURAL_ACCELERATION;
			else if (currentXvelocity < 0) xAcceleration = Constants.MOBY_NATURAL_ACCELERATION;
		}
		if (yvelocity == 0) {
			if (currentYvelocity > 0) yAcceleration = -Constants.MOBY_NATURAL_ACCELERATION;
			else if (currentYvelocity < 0) yAcceleration = Constants.MOBY_NATURAL_ACCELERATION;
		}
		updateFlashTimer();
		if (deadCounter == 0) {
			kill();
		} else if (deadCounter > 0) {
			deadCounter--;
		}
		if (getRangedFireCounter() > 0) {
			setRangedFireCounter(getRangedFireCounter() - 1);
		}
	}

	/**
	 * Makes sprite invisible until deletion
	 */
	public void hide() {
		super.getSprite().setColor(Color.TRANSPARENT);
	}

	/**
	 * Accelerates the moby by acceleration
	 */
	public void accelerate() {
		if (Math.abs(currentXvelocity) < Math.abs(xvelocity + tempXvelocity) || Math.abs(currentXvelocity) > Math.abs(xvelocity + tempXvelocity)) {
			if (Math.abs(currentXvelocity + xAcceleration) > Math.abs(xvelocity + tempXvelocity)) {
				currentXvelocity = (xvelocity + tempXvelocity);
				xAcceleration = 0;
			}
			currentXvelocity += xAcceleration;
		}
		if (Math.abs(currentYvelocity) < Math.abs(yvelocity + tempYvelocity) || Math.abs(currentYvelocity) > Math.abs(yvelocity + tempYvelocity)) {
			if (Math.abs(currentYvelocity + yAcceleration) > Math.abs(yvelocity + tempYvelocity)) {
				currentYvelocity = (yvelocity + tempYvelocity);
				yAcceleration = 0;
			}
			currentYvelocity += yAcceleration;
		}
	}

	/**
	 * Gets current direction
	 * @return current direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * Sets current direction
	 * @param directions amount of directions
	 */
	public void setDirection(int directions) {
		if (directions == 4) {
			//System.out.println(Math.toDegrees(getAngle()));
			double directionAngle = Math.toDegrees(getAngle() + Math.PI/2);
			if (directionAngle >= 0 && directionAngle < 90) {
				//Back
				direction = "_back";
			} else if (directionAngle >= 90 && directionAngle < 180) {
				//Right
				direction = "_right";
			} else if (directionAngle < 0 && directionAngle > -135) {
				//Left
				direction = "_left";
			} else {
				//Forward
				direction = "_forward";
			}
		} else {
			if (Math.abs(Math.toDegrees(getAngle())) >= 0 && Math.abs(Math.toDegrees(getAngle())) < 90) {
				//Left
				direction = "_right";
			} else {
				//Right
				direction = "_left";
			}
		}
	}

	/**
	 * Chooses animation to play
	 * @param directions amount of directions
	 */
	public void chooseAnimation(int directions) {
		setDirection(directions);
		String instruction = "_idle";
		if (deadCounter >= 0) {
			instruction = "_death";
		}
		else if (Math.abs(getXvelocity()) > 0 || Math.abs(getYvelocity()) > 0) {
			instruction = "_moving";
		} else if (getRangedFireCounter() >= (int)((getRangedFireRate()) * Constants.ENEMY_FIRERATE_MULTIPLIER) - 5) {
			instruction = "_shoot";
		}
		//System.out.println(name + direction + instruction);
		super.tryAddAnimation(name + direction + instruction);
	}

	/**
	 * Moves the moby in accordance to the knockback
	 */
	public void knockback(Moby collidedEntity) {
		double knockbackAngle = Math.atan2((collidedEntity.getXpos() + collidedEntity.getWidth()/2) - (super.getXpos() + getWidth()/2), (super.getYpos() + getHeight()/2) - (collidedEntity.getYpos() + collidedEntity.getHeight()/2)) - Math.PI/2;
		this.setTempXvelocity(-(getMoveSpeed() * (float) Math.cos(knockbackAngle) * resistance * collidedEntity.getKnockbackMultipler()));
		this.setTempYvelocity(-(getMoveSpeed() * (float) Math.sin(knockbackAngle) * resistance * collidedEntity.getKnockbackMultipler()));
	}

	/**
	 * Flashes current colour with current counter
	 */
	public void flashColour() {
		flashColour(flashTimer, currentColor);
	}

	/**
	 * Sets base color for sprite
	 * @param baseColor base color
	 */
	public void setBaseColor(Color baseColor) {
		this.baseColor = baseColor;
	}

	/**
	 * Makes the moby flash when hit
	 */
	public void flashColour(int startValue, Color color){
		currentColor = color;
		mobySprite = this.getSprite();
		flashTimer = startValue;
		if (flashTimer % iFrames == 0) {
			if (mobySprite.getColor() == color || flashTimer == 0) {
				mobySprite.setColor(baseColor);
			} else {
				mobySprite.setColor(color);
			}
			this.setSpriteColour(mobySprite);
		}
	}

	/**
	 * Gets the current flash timer
	 * @return current flash timer
	 */
	public int getFlashTimer() {
		return flashTimer;
	}

	/**
	 * Updates the flash timer counter and flashes if multiple of iframes
	 */
	public void updateFlashTimer() {
		if (flashTimer > 0) {
			flashTimer--;
		}
		if (flashTimer % iFrames == 0) {
			flashColour();
		}
	}

	/**
	 * Damages entity if collided with damaging player projectile and creates splash if can
	 * Knocks back moby if colliding with a non-ground effect
	 * @param collidedEntity other entity that is being collided with
	 */
	@Override
	public void collide(Entity collidedEntity) {
		if (collidedEntity instanceof Moby) {
			if (collidedEntity instanceof Projectile) {
				if (((Projectile) collidedEntity).getSourceEntity().equals(this)) {
					return;
				} else if (((Projectile) collidedEntity).getSourceEntity() instanceof Player) {
					if (getFlashTimer() < iFrames)
						damage(collidedEntity);
					if (getFlashTimer() == 0) {
						flashColour(iFrames+1, Color.RED);
					}
					if (!(collidedEntity instanceof Splash)) {
						if (!((Projectile) collidedEntity).getPierce()) {
							collidedEntity.kill();
						}
					}
				}
			}
			if (!(collidedEntity instanceof Splash)) {
				knockback((Moby) collidedEntity);
			}
		}
	}

	/**
	 * Updates the position of the sprite
	 */
	@Override
	public void updatePosition() {
		if (xAcceleration != 0 && yAcceleration != 0) {
			accelerate();
		} else {
			currentXvelocity = (xvelocity + tempXvelocity);
			currentYvelocity = (yvelocity + tempYvelocity);
		}
		float newXpos = super.getXpos() + currentXvelocity;
		float newYpos = super.getYpos() + currentYvelocity;
		int minRoomWidth = (int)(Constants.ROOM_BOUNDS_PIXELS * super.getManager().getEntities().getScale());
		int minRoomHeight = (int)(Constants.ROOM_BOUNDS_PIXELS * super.getManager().getEntities().getScale());
		int maxRoomWidth = super.getRoom().getWidth() - (int)(Constants.ROOM_BOUNDS_PIXELS * super.getManager().getEntities().getScale());
		int maxRoomHeight = super.getRoom().getHeight() - (int)(Constants.ROOM_BOUNDS_PIXELS * super.getManager().getEntities().getScale()) + (int)super.getManager().getEntities().getCurrentRoomObject().getPlayer().getHeight();
		if (newXpos < minRoomWidth || newYpos < minRoomHeight || newXpos > maxRoomWidth || newYpos > maxRoomHeight) {
			if (this instanceof Projectile) {
				kill();
			}
			return;
		}
		super.setXpos(super.getXpos() + currentXvelocity);
		super.setYpos(super.getYpos() + currentYvelocity);
		super.updatePosition();
	}
}
