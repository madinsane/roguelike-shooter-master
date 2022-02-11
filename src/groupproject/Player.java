package groupproject;

import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.graphics.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Extension of Moby
 * Describes the player character
 */
public class Player extends Moby {
	private int moveSpeed;
	private float dashSpeed;
	private boolean isDashing;
	private int dashCounter;
	private boolean isDashCooldownActive;
	private int dashCooldown;
	private boolean canDash;
	private boolean dashReleased;
	private float resistance = 1;
	private int timer;
	private int stunnedTimer;
	private int healthPackCounter;
	private int weaponCounter;
	private int weaponId;
	private String direction;
	private int goldCounter;
	private int healthPackMax;
	private int healthPackTimer;
	private boolean isAnimationLocked;
	private int healthMax;
	private float goldMultiplier;
	private int teleportInTimer;
	private int teleportOutTimer;
	private Teleporter teleporter;

	public Player(Manager manager, int moveSpeed, float dashSpeed, float xpos, float ypos, float scale, float width, float height) {
		super(manager, xpos, ypos, scale, width, height);
		this.moveSpeed = moveSpeed;
		this.dashSpeed = dashSpeed;
		weaponId = 0;
		isDashing = false;
		dashCounter = 0;
		dashCooldown = 0;
		isDashCooldownActive = false;
		canDash = true;
		dashReleased = false;
		weaponCounter = 0;
		healthPackMax = 6;
		healthPackCounter = healthPackMax;
		goldCounter = 0;
		healthPackTimer = 0;
		isAnimationLocked = false;
		healthMax = 100;
		goldMultiplier = 1;
		teleportInTimer = -1;
		teleportOutTimer = -1;
		super.changeWeapon("player_pistol");
		super.setTexture(super.getManager().getAssets().getTexture("player"));
		super.setSprite(0, false);
		super.loadEnemyData("player", 1);
		super.setXorigin(width/2);
		super.setYorigin(height);
		super.setResistance(0);
		loadPassives();
	}

	/**
	 * Loads passive data from passives stored in DataManager
	 */
	public void loadPassives() {
		ArrayList<String[]> passiveData = super.getManager().getData().getData("passives");
		healthMax += Integer.parseInt(passiveData.get(1)[1]) * Integer.parseInt(passiveData.get(1)[2]);
		super.setHealth(healthMax);
		super.setDamageMultiplier(super.getDamageMultiplier() + (Float.parseFloat(passiveData.get(2)[1]) * Float.parseFloat(passiveData.get(2)[2])));
		goldMultiplier += Float.parseFloat(passiveData.get(3)[1]) * Float.parseFloat(passiveData.get(3)[2]);
		//System.out.println(healthMax + " " + super.getDamageMultiplier() + " " + goldMultiplier);
	}

	/**
	 * Handles non keyboard inputs
	 * @param event event from window.pollEvents
	 */
	@Override
	public void handleInput(Event event) {
		switch (event.type) {
			case KEY_RELEASED:
				if (event.asKeyEvent().key == Constants.PLAYER_KEY_DASH) {
					dashReleased = true;
				}
		}
	}

	/**
	 * Gets current health pack count
	 * @return health pack count
	 */
	public int getHealthPackCounter() {
		return healthPackCounter;
	}

	/**
	 * Sets gold counter
	 * @param goldCounter new gold counter
	 */
	public void setGoldCounter(int goldCounter) {
		this.goldCounter = goldCounter;
	}

	/**
	 * Gets current gold
	 * @return gold
	 */
	public int getGoldCounter() {
		return goldCounter;
	}

	/**
	 * Gets max health
	 * @return max health
	 */
	public int getHealthMax() {
		return healthMax;
	}

	/**
	 * Kills the player
	 */
	@Override
	public void kill() {
		super.getManager().getEntities().playerDied(goldCounter);
	}

	/**
	 * Handles ai and keyboard inputs
	 */
	@Override
	public void ai() {
		if (super.getManager().getEntities().isCheckDoors()) {
			super.getManager().getEntities().getCurrentRoomObject().checkDoors();
			super.getManager().getEntities().setCheckDoors(false);
		}
		int horizDirectionCounter = 0;
		int vertDirectionCounter = 0;
		int tempAngle = 0;
		if (healthPackTimer > 0) {
			isAnimationLocked = true;
			healthPackTimer--;
		} else if (isAnimationLocked) {
			isAnimationLocked = false;
		}
		if (!(super.getDeadCounter() > 0 || teleportInTimer > 0 || teleportOutTimer > 0)) {
			// Movement otherwise
			if (Keyboard.isKeyPressed(Constants.PLAYER_KEY_UP) && super.getManager().getStates().isFocused() && !isAnimationLocked) {
				if (!isDashing) {
					super.setTempYvelocity(-moveSpeed);
					super.setYacceleration(-Constants.MOBY_NATURAL_ACCELERATION);
				}
				vertDirectionCounter++;
				tempAngle += 0;
			}
			if (Keyboard.isKeyPressed(Constants.PLAYER_KEY_DOWN) && super.getManager().getStates().isFocused() && !isAnimationLocked) {
				if (!isDashing) {
					super.setTempYvelocity(moveSpeed);
					super.setYacceleration(Constants.MOBY_NATURAL_ACCELERATION);
				}
				vertDirectionCounter++;
				tempAngle += 180;
			}
			if (Keyboard.isKeyPressed(Constants.PLAYER_KEY_RIGHT) && super.getManager().getStates().isFocused() && !isAnimationLocked) {
				if (!isDashing) {
					super.setTempXvelocity(moveSpeed);
					super.setXacceleration(Constants.MOBY_NATURAL_ACCELERATION);
				}
				horizDirectionCounter++;
				tempAngle += 90;
			}
			if (Keyboard.isKeyPressed(Constants.PLAYER_KEY_LEFT) && super.getManager().getStates().isFocused() && !isAnimationLocked) {
				if (!isDashing) {
					super.setTempXvelocity(-moveSpeed);
					super.setXacceleration(-Constants.MOBY_NATURAL_ACCELERATION);
				}
				horizDirectionCounter++;
				if (vertDirectionCounter == 1 && tempAngle == 0) {
					tempAngle -= 360;
				}
				tempAngle += 270;
			}
			//Drops weapon
			if (Keyboard.isKeyPressed(Constants.PLAYER_DROP_WEAPON_KEY) && super.getManager().getStates().isFocused() && !isAnimationLocked) {
				super.changeWeapon("player_pistol");
				super.getManager().getEntities().setAvailableWeapon(false);
			}
			findAngle(horizDirectionCounter, vertDirectionCounter, tempAngle);
			//Dashes
			if (Keyboard.isKeyPressed(Constants.PLAYER_KEY_DASH) && canDash && super.getManager().getStates().isFocused() && dashReleased && !isAnimationLocked) {
				dash();
				dashReleased = false;
			}
			//Attacks
			if (Mouse.isButtonPressed(Constants.PLAYER_BUTTON_RANGED) && super.getManager().getStates().isFocused() && !isAnimationLocked) {
				ranged();
				if (super.getManager().getEntities().getCurrentRoomObject().getId() == super.getManager().getEntities().getShopRoomId()) {
					super.getManager().getEntities().getCurrentRoomObject().aggro();
				}
			}
			//Counters
			if (isDashing) {
				if (dashCounter > Constants.PLAYER_DASH_DURATION_FRAMES) {
					endDash();
				} else {
					dashCounter++;
				}
			}
			if (isDashCooldownActive) {
				if (dashCooldown <= 0) {
					isDashCooldownActive = false;
					canDash = true;
				} else if (dashCounter < Constants.PLAYER_DASH_DURATION_FRAMES && dashCounter >= Constants.PLAYER_DASH_DURATION_FRAMES - Constants.PLAYER_CHAIN_DASH_FRAMES) {
					canDash = true;
				} else {
					dashCooldown--;
				}
			}
			//Heals
			if (Keyboard.isKeyPressed(Constants.PLAYER_KEY_HEAL) && super.getManager().getStates().isFocused() && !isDashing) {
				heal();
			}
		}
		//Teleports
		if (teleportOutTimer > 0) {
			teleportOutTimer--;
		} else if (teleportOutTimer == 0) {
			teleportOutTimer--;
			teleporter.leave(this);
		}
		if (teleportInTimer > 0) {
			teleportInTimer--;
		}
		chooseAnimation();
		super.updatePosition();
		super.clearTemp();
		super.animations();
		if (weaponCounter > 0 && !super.getManager().getEntities().getAvailableWeapon()) {
			weaponCounter--;
		} else {
			if (!super.getManager().getEntities().getAvailableWeapon()) {
				weaponId = super.getManager().getEntities().dropRandomWeapon(weaponId);
				weaponCounter = Constants.WEAPON_DROP_DURATION_FRAMES;
			}
		}
	}

	/**
	 * Set teleport in counter
	 * @param teleportInTimer teleport counter
	 */
	public void setTeleportInTimer(int teleportInTimer) {
		this.teleportInTimer = teleportInTimer;
	}

	/**
	 * Sets current direction
	 */
	public void setDirection() {
		if (getAngle() >= 0 && getAngle() < 90) {
			//Back
			direction = "_back";
		} else if (getAngle() >= 90 && getAngle() < 180) {
			//Right
			direction = "_right";
		} else if (getAngle() >= 180 && getAngle() < 270) {
			//Forward
			direction = "_forward";
		} else {
			//Left
			direction = "_left";
		}
	}

	/**
	 * Chooses player animation
	 */
	public void chooseAnimation() {
		setDirection();
		String instruction = "_idle";
		if (super.getDeadCounter() > 0) {
			instruction = "_death";
		} else if (isDashing) {
			instruction = "_dash";
		} else if (teleportOutTimer > 0) {
			instruction = "_teleport_out";
			direction = "";
		} else if (teleportInTimer > 0) {
			instruction = "_teleport_in";
			direction = "";
		} else if (healthPackTimer > 0) {
			instruction = "_heal";
		} else if (Math.abs(super.getTempXvelocity()) > 0 || Math.abs(super.getTempYvelocity()) > 0) {
			instruction = "_moving";
		} else if (super.getRangedFireCounter() >= super.getRangedFireRate() - 5) {
			instruction = "_gun";
		}
		super.tryAddAnimation("player" + direction + instruction);
	}

	/**
	 * Causes the player to dash
	 */
	public void dash() {
		super.setXvelocity(dashSpeed * (float) Math.sin(Math.toRadians(super.getAngle())));
		super.setYvelocity(-dashSpeed * (float) Math.cos(Math.toRadians(super.getAngle())));
      	super.setXacceleration(Constants.PLAYER_DASH_ACCELERATION * (float)Math.sin(Math.toRadians(super.getAngle())));
		super.setYacceleration(-Constants.PLAYER_DASH_ACCELERATION * (float)Math.cos(Math.toRadians(super.getAngle())));
		dashCounter = 0;
		isDashing = true;
		dashCooldown = Constants.PLAYER_DASH_COOLDOWN_FRAMES;
		canDash = false;
		isDashCooldownActive = true;
	}

	/**
	 * Finds the current angle the player is facing
	 * @param horizDirectionCounter counter for amount of horizontal directions pressed
	 * @param vertDirectionCounter counter for amount of vertical directions pressed
	 * @param tempAngle temporary angle to be used for computation
	 */
	public void findAngle(int horizDirectionCounter, int vertDirectionCounter, int tempAngle) {
		if ((horizDirectionCounter+vertDirectionCounter) > 0) {
			if (horizDirectionCounter > 1) {
				tempAngle -= 270;
				horizDirectionCounter--;
			}
			if (vertDirectionCounter > 1) {
				tempAngle -= 180;
				vertDirectionCounter--;
			}
			super.setAngle(tempAngle / (horizDirectionCounter+vertDirectionCounter));
		}
	}

	/**
	 * Forces the current dash to end
	 */
	public void endDash() {
		super.setXvelocity(0);
		super.setYvelocity(0);
		isDashing = false;
		dashCounter = 0;
		canDash = false;
	}

	/**
	 * Causes the player to do a ranged attack
	 */
	public void ranged() {
		if (super.getRangedFireCounter() == 0) {
			if (super.getRangedAmmo() == 0) {
				super.changeWeapon("player_pistol");
				super.getManager().getEntities().setAvailableWeapon(false);
			} else if (super.getRangedAmmo() > 0) {
				super.setRangedAmmo(super.getRangedAmmo() - 1);
			}
			Vector2i mousePos = Mouse.getPosition(super.getManager().getWindow());
			Pair<Float, Float> windowRatio = getWindowRatio();
			double angle = Math.atan2((mousePos.x / windowRatio.getR()) - (super.getXpos() - super.getWidth() / 2), (super.getYpos() - super.getHeight() / 2) - (mousePos.y / windowRatio.getL())) - Math.PI / 2;
			angle = angle - (super.getRangedSpread() * ((super.getRangedProjectiles()-1)/2));
			for (int i=0; i<super.getRangedProjectiles(); i++) {
				super.addProjectile(angle);
				angle += super.getRangedSpread();
			}
			super.setRangedFireCounter(super.getRangedFireRate());
		}
	}

	/**
	 * Gets window ratio
	 * @return window ratio (x, y)
	 */
	public Pair<Float, Float> getWindowRatio() {
		float roomWidth = super.getManager().getEntities().getCurrentRoomObject().getWidth();
		float roomHeight = super.getManager().getEntities().getCurrentRoomObject().getHeight();
		float windowWidth = super.getManager().getWindow().getSize().x;
		float windowHeight = super.getManager().getWindow().getSize().y;
		return new Pair<>(windowWidth / roomWidth, windowHeight / roomHeight);
	}

	/**
	 * Causes the player to heal
	 */
	public void heal() {
		if(healthPackCounter > 0 && super.getHealth() < healthMax){
			healthPackTimer = Constants.PLAYER_HEAL_FRAMES;
			healthPackCounter--;
			super.setHealth(healthMax);
			// System.out.println(healthPackCounter + " health packs remaining. Health: " + super.getHealth()); //testing if healing works
		}
	}

	/**
	 * Obtains item with name and adds value to gold if item is gold
	 * @param name name of item
	 * @param value value of gold
	 * @return if item was picked up
	 */
	public boolean obtainItem(String name, int value) {
		String[] itemData = super.getManager().getData().getRowData("items", name);
		goldCounter = goldCounter + (int)(value * goldMultiplier);
		if ((healthPackCounter + Integer.parseInt(itemData[2])) > healthPackMax) {
			return false;
		}
		healthPackCounter = healthPackCounter + Integer.parseInt(itemData[2]);
		healthMax += Integer.parseInt(itemData[3]);
		super.setHealth(Integer.parseInt(itemData[3]) + super.getHealth());
		super.setDamageMultiplier(super.getDamageMultiplier() + Float.parseFloat(itemData[4]));
		super.flashColour(6, Color.GREEN);
		return true;
	}

	/**
	 * Handles player collision
	 * @param collidedEntity other entity that is being collided with
	 */
	@Override
	public void collide(Entity collidedEntity) {
		//Tries to change room, grants invulnerability on changing room
		if (collidedEntity instanceof Door) {
			if (((Door) collidedEntity).getEnabled()) {
				super.flashColour(50, Color.WHITE);
			}
		}
		//Tries to teleport to next level
		if (collidedEntity instanceof Teleporter) {
			if (((Teleporter) collidedEntity).getEnabled() && teleportOutTimer == -1) {
				teleportOutTimer = Constants.TELEPORT_FRAMES;
				teleporter = (Teleporter) collidedEntity;
			}
		}
		//Checks if hit by moby that wasn't created by player
		if (collidedEntity instanceof Moby) {
			if (collidedEntity instanceof Projectile) {
				if (((Projectile) collidedEntity).getSourceEntity().equals(this)) {
					return;
				}
			}
			if (super.getFlashTimer() == 0) {
				super.damage(collidedEntity);
				//System.out.println(super.getHealth());
				super.knockback((Moby) collidedEntity);
				super.flashColour(121, Color.RED);
			}
		}
	}
}
