package groupproject;

import java.util.Random;

/**
 * Describes a Boss
 * Extension of Enemy
 */
public class Boss extends Enemy {
	private int currentAttack;
	private String currentAttackType;
	private int attackCounter;
	private int healthMax;
	private int dashSpeed;
	private boolean isDashing = false;
	private int dashCounter;
	private boolean shotgunMode;
	private boolean charge;
	private boolean phaseTwo;
	private int chargeCounter;

	public Boss(Manager manager, float xpos, float ypos, float scale, String name, int difficulty) {
		super(manager, xpos, ypos, scale, name, difficulty, false);
		scaleBoss();
		healthMax = super.getHealth();
		if (name.equals("plantBoss")) {
			changeWeapon("plant_spread1");
			super.setAngle(Math.PI);
			currentAttackType = "_spread";
		} else if (name.equals("shotgunBoss")){
			changeWeapon("shotgunBoss_shotgun");
			currentAttackType = "_shotgun";
			dashSpeed = 3;
			isDashing = false;
		} else if (name.equals("mentorBoss")){
			changeWeapon("mentor_sword");
			currentAttackType = "_sword";
			shotgunMode = false;
			charge = false;
			super.setRangedWidth(40);
            super.setRangedHeight(32);
		}
		super.setResistance(0);

		currentAttack = 1;
		attackCounter = 0;

	}

	/**
	 * Gets max health of boss
	 * @return max health
	 */
	public int getHealthMax() {
		return healthMax;
	}

	/**
	 * Sets max health of boss
	 * @param healthMax new max health
	 */
	public void setHealthMax(int healthMax) {
		this.healthMax = healthMax;
	}

	/**
	 * Scales the health and damage of the boss by the multipliers in enemies.csv
	 */
	public void scaleBoss() {
		String[] enemyData = super.getManager().getData().getRowData("enemies", super.getName());
		if (enemyData == null) {
			enemyData = super.getManager().getData().getRowData("enemies", "skinman");
		}
		super.setHealth((int)(super.getHealth() * Float.parseFloat(enemyData[16])));
		super.setDam((int)(super.getDam() * Float.parseFloat(enemyData[17])));
	}

	/**
	 * Gets the current attack number
	 * @return current attack number
	 */
	public int getCurrentAttack() {
		return currentAttack;
	}

	/**
	 * Gets the current type of attack
	 * @return current type of attack
	 */
	public String getCurrentAttackType() {
		return currentAttackType;
	}

	/**
	 * Gets the counter used for determining which attack the boss should use
	 * @return attack counter
	 */
	public int getAttackCounter() {
		return attackCounter;
	}

	/**
	 * Sets the current attack number
	 * @param currentAttack new attack number
	 */
	public void setCurrentAttack(int currentAttack) {
		this.currentAttack = currentAttack;
	}

	/**
	 * Sets the current attack type
	 * @param currentAttackType new attack type
	 */
	public void setCurrentAttackType(String currentAttackType) {
		this.currentAttackType = currentAttackType;
	}

	/**
	 * Sets the counter used for determining which attack the boss should use
	 * @param attackCounter attack counter
	 */
	public void setAttackCounter(int attackCounter) {
		this.attackCounter = attackCounter;
	}

	@Override
	public void ai() {
		//Plant boss ai
		if (super.getName().equals("plantBoss")) {
			//Locks the angle for spread attack
			if (currentAttackType.equals("_spread") && super.getRangedAmmo() == 0) {
				super.setAngle(Math.PI);
			} else {
				//Otherwise tracks player
				Player player = super.getManager().getEntities().getCurrentRoomObject().getPlayer();
				int playXPos = (int)super.getManager().getEntities().getCurrentRoomObject().getPlayer().getXpos();
				int playYPos = (int)super.getManager().getEntities().getCurrentRoomObject().getPlayer().getYpos();
				super.setAngle(Math.atan2(playXPos - super.getXpos() - (super.getWidth() / 6), super.getYpos() - (super.getHeight() / 1.3f) * super.getManager().getEntities().getScale() - playYPos) - Math.PI/2);
			}
			if (super.getRangedAmmo() == 0) {
				if (currentAttackType.equals("_spread")) {
					changeWeapon("plant" + currentAttackType + currentAttack);
					if (currentAttack == 1) {
						currentAttack++;
					} else {
						currentAttack--;
					}
				} else {
					changeWeapon("plant" + currentAttackType);
				}
				if (attackCounter >= 0 && attackCounter < 10) {
					currentAttackType = "_spread";
				} else if (attackCounter >= 10 && attackCounter < 15) {
					currentAttackType = "_machine";
				} else if (attackCounter >= 15 && attackCounter < 18) {
					currentAttackType = "_sniper";
				} else if (attackCounter >= 18 && attackCounter < 19) {
					currentAttackType = "_acid";
				} else {
					attackCounter = -1;
				}
				attackCounter++;
			}
			ranged();
			//Handles animations for plantBoss
			String instruction = "_idle";
			if (super.getDeadCounter() >= 0) {
				instruction = "_death";
			}
			super.tryAddAnimation(super.getName() + instruction);
			super.animations();
			super.updatePosition();
			super.clearTemp();
		}
		//Shotgun boss ai
		else if (super.getName().equals("shotgunBoss")){
			int playXPos = (int) super.getManager().getEntities().getCurrentRoomObject().getPlayer().getXpos();
			int playYPos = (int) super.getManager().getEntities().getCurrentRoomObject().getPlayer().getYpos();
			super.setAngle(Math.atan2(playXPos - super.getXpos() - (super.getWidth() / 6), super.getYpos() - (super.getHeight() / 1.3f) * super.getManager().getEntities().getScale() - playYPos) - Math.PI / 2);
			if (super.getRangedAmmo() == 0) {
				if (attackCounter % 3 == 0) {
					dash();
				}
				changeWeapon("shotgunBoss" + currentAttackType);
				if (attackCounter >= 0 && attackCounter < 5) {
					currentAttackType = "_shotgun";
				} else if (attackCounter >= 5 && attackCounter < 10) {
					currentAttackType = "_pistol";
				} else {
					attackCounter = -1;
				}
				attackCounter++;
			}
			if (isDashing) {
				if (dashCounter > Constants.PLAYER_DASH_DURATION_FRAMES*2) {
					endDash();
				} else {
					dashCounter++;
				}
			} else {
				ranged();
			}
			//Handles animations for shotgun boss
			super.setDirection(2);
			String instruction = "_idle";
			if (super.getDeadCounter() >= 0) {
				instruction = "_death";
			} else if (isDashing) {
				instruction = "_dash";
			} else if (super.getRangedFireCounter() > 0) {
				instruction = currentAttackType;
			}
			super.tryAddAnimation(super.getName() + super.getDirection() + instruction);
			super.animations();
			super.updatePosition();
			super.clearTemp();
		//Mentor boss ai
		} else if (super.getName().equals("mentorBoss")){
			if (super.getHealth() < healthMax / 2){
				phaseTwo = true;
				shotgunMode = false;
			}
			if (shotgunMode == false && charge == false) {
				int playXPos = (int) super.getManager().getEntities().getCurrentRoomObject().getPlayer().getXpos();
				int playYPos = (int) super.getManager().getEntities().getCurrentRoomObject().getPlayer().getYpos();
				double tempAngle = Math.atan2(playXPos - super.getXpos() - (super.getWidth() / 6), super.getYpos() - (super.getHeight() / 1.3f) * super.getManager().getEntities().getScale() - playYPos) - Math.PI / 2;
				super.setAngle(tempAngle);
				if (super.getRangedAmmo() == 0) {
					changeWeapon("mentor" + currentAttackType);
					if (!phaseTwo) {
						if (attackCounter == 7) {
							attackCounter = -1;
						} else if (attackCounter == 3) {
							shotgunMode = true;
							currentAttackType = "_shotgun";
						} else {
							currentAttackType = "_sword";
						}
					} else {
						if (attackCounter == -1) {
							currentAttackType = "_phase";
						} else if (attackCounter == 9) {
							attackCounter = -1;
						} else if (attackCounter == 3) {
							charge = true;
							chargeCounter = 20;
						} else if (attackCounter == 7) {
							currentAttackType = "_groundSlam";
						} else {
							currentAttackType = "_knives";
						}

					}
					attackCounter++;
				}
				double distance = Math.abs(Math.hypot(super.getXpos() - super.getRoom().getPlayer().getXpos(), super.getYpos() - super.getRoom().getPlayer().getYpos()));
				if (distance <= super.getRange() || super.getRange() == -1) {
					ranged();
					super.setXvelocity(0);
					super.setYvelocity(0);
				} else {
					super.setXvelocity(super.getMoveSpeed() * (float) Math.cos(super.getAngle()));
					super.setYvelocity(super.getMoveSpeed() * (float) Math.sin(super.getAngle()));
				}
				if (shotgunMode == true){
					changeWeapon("mentor" + currentAttackType);
				}
			//Shotgun random spread attack
			} else if (shotgunMode == true) {
				if (super.getRangedAmmo() == 1) {
					shotgunMode = false;
					currentAttackType = "_sword";
				}
				int roomWidth = getManager().getEntities().getRoomWidth();
				int roomHeight = getManager().getEntities().getRoomHeight();
				super.setAngle(Math.atan2(roomWidth / 2 - super.getXpos(), super.getYpos() - roomHeight / 4 - 50) - Math.PI / 2);
				if (super.getXpos() - roomWidth / 2 > 10 || super.getXpos() - roomWidth / 2 < -10 || super.getYpos() - roomWidth / 4 - 50 > 10 || super.getXpos() - roomWidth / 4 - 50 < -10) {
					super.setXvelocity(super.getMoveSpeed() * (float) Math.cos(super.getAngle()));
					super.setYvelocity(super.getMoveSpeed() * (float) Math.sin(super.getAngle()));
				} else {
					Random r = new Random();
					int xResult = r.nextInt((roomWidth / 6) * 5 - (roomWidth / 6)) + (roomWidth / 6);
					super.setAngle(Math.atan2(xResult - super.getXpos(), super.getYpos() - 600) - Math.PI / 2);
					super.setXvelocity(0);
					super.setYvelocity(0);
					ranged();
				}
			} else {
				super.setRange(-1);
				super.setMoveSpeed(6);
				int playXPos = (int) super.getManager().getEntities().getCurrentRoomObject().getPlayer().getXpos();
				int playYPos = (int) super.getManager().getEntities().getCurrentRoomObject().getPlayer().getYpos();
				super.setAngle(Math.atan2(playXPos - super.getXpos() - (super.getWidth() / 6), super.getYpos() - (super.getHeight() / 1.3f) * super.getManager().getEntities().getScale() - playYPos) - Math.PI / 2);
				super.setXvelocity(super.getMoveSpeed() * (float) Math.cos(super.getAngle()));
				super.setYvelocity(super.getMoveSpeed() * (float) Math.sin(super.getAngle()));
				chargeCounter = chargeCounter - 1;
				if (chargeCounter == 0){
					super.setRange(300);
					super.setMoveSpeed(3);
				}

			}
			//Handles animations for mentorBoss
			super.setDirection(2);
			String instruction = "_idle";
			String phase = "";
			if (super.getDeadCounter() >= 0) {
				instruction = "_death";
			} else if (Math.abs(super.getXvelocity()) > 0 || Math.abs(super.getYvelocity()) > 0) {
				instruction = "_moving";
			} else if (super.getRangedFireCounter() > 0) {
				instruction = currentAttackType;
			}
			if (phaseTwo && !currentAttackType.equals("_phase")) {
				phase = "_phased";
			}
			//System.out.println(super.getName() + phase + instruction + super.getDirection());
			super.tryAddAnimation(super.getName() + phase + instruction + super.getDirection());
			super.animations();
			super.updatePosition();
			super.clearTemp();
		} else {
			super.ai();
		}
	}

	/**
	 * Locks boss normal movement and causes the boss to gain a burst of speed
	 */
	public void dash() {
		super.setXvelocity(3*dashSpeed * (float) Math.cos(super.getAngle()));
		super.setYvelocity(3*dashSpeed * (float) Math.sin(super.getAngle()));
		dashCounter = 0;
		isDashing = true;
	}

	/**
	 * Ends the current dash returning control
	 */
	public void endDash() {
		super.setXvelocity(0);
		super.setYvelocity(0);
		isDashing = false;
		dashCounter = 0;
	}

	@Override
	public void ranged() {
		if (super.getName().equals("plantBoss") || super.getName().equals("shotgunBoss") || super.getName().equals("shopkeeper") || super.getName().equals("mentorBoss")) {
			if (super.getRangedFireCounter() == 0) {
                //System.out.println(super.getRangedWidth());
				super.setRangedAmmo(super.getRangedAmmo() - 1);
				double angle = Math.toDegrees(super.getAngle());
				angle = angle - (super.getRangedSpread() * ((super.getRangedProjectiles() - 1) / 2));
				for (int i = 0; i < super.getRangedProjectiles(); i++) {
					addProjectile(Math.toRadians(angle));
					angle += super.getRangedSpread();
				}
				super.setRangedFireCounter(super.getRangedFireRate());
			}
		} else {
			super.ranged();
		}
	}

	@Override
	public void addProjectile(double angle) {
		super.getManager().getEntities().addEntity(new Projectile(super.getManager(), super.getXpos() - (super.getWidth() / 6), super.getYpos() - (super.getHeight() / 1.3f) * super.getManager().getEntities().getScale(),
				super.getRangedScale(), super.getRangedWidth(), super.getRangedHeight(),
				angle, getRangedDamage(), getRangedSpeed(), getRangedLifetime(), this, super.getRangedTexture(), super.isRangedIsAnimated(), super.isRangedPierce(), super.isRangedRotate(), super.getRangedSplash()));
	}

	@Override
	public void changeWeapon(String name) {
		String[] projectileData = super.getManager().getData().getRowData("boss", name);
		if (projectileData == null) {
			projectileData = super.getManager().getData().getRowData("boss", "plant_spread1");
		}
		super.setRangedDamage(Integer.parseInt(projectileData[1]));
		super.setRangedFireRate(Integer.parseInt(projectileData[2]));
		super.setRangedSpeed(Integer.parseInt(projectileData[3]));
		super.setRangedLifetime(Integer.parseInt(projectileData[4]));
		super.setRangedProjectiles(Integer.parseInt(projectileData[5]));
		super.setRangedAmmo(Integer.parseInt(projectileData[6]));
		super.setRangedSpread(Integer.parseInt(projectileData[7]));
		super.setRangedTexture(projectileData[8]);
		super.setRangedSplash(projectileData[9]);
		super.setRangedWidth(Integer.parseInt(projectileData[10]));
		super.setRangedHeight(Integer.parseInt(projectileData[11]));
		super.setRangedIsAnimated(Boolean.parseBoolean(projectileData[12]));
		super.setRangedRotate(Boolean.parseBoolean(projectileData[13]));
		super.setRangedScale(Float.parseFloat(projectileData[14]));
	}

	@Override
	public void kill() {
		hide();
		super.getManager().getEntities().dropItem(super.getXpos(), super.getYpos() - super.getHeight(), super.getName(), false);
		super.getManager().getEntities().setCheckDoors(true);
		super.getManager().getEntities().getCurrentRoomObject().openExit();
		super.getManager().getEntities().removeEntity(super.getId());
	}
}