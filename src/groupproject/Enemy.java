package groupproject;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2i;

/**
 * Extension of Moby
 * Describes the Enemy
 */
public class Enemy extends Moby {
    private Entity player;
    private float playXPos;
    private float playYPos;
    private int difficulty;
    private boolean isChampion;

    public Enemy(Manager manager, float xpos, float ypos, float scale, String name, int difficulty, boolean isChampion) {
        super(manager, xpos, ypos, scale, 0, 0);
        this.difficulty = difficulty;
        this.isChampion = isChampion;
        super.setName(name);
        if (isChampion) {
            difficulty *= Constants.CHAMPION_MULTIPLIER;
        }
        super.loadEnemyData(name, difficulty);
        super.setTexture(super.getManager().getAssets().getTexture(name));
        super.setSprite(0, false);
        super.setResistance(1);
        setXorigin(super.getWidth() / 2);
        setYorigin(super.getHeight());
        super.changeWeapon(super.getWeaponName());
        if (isChampion) {
            super.setBaseColor(Color.YELLOW);
        }
    }

    /**
     * Ai method for enemy, called every frame
     * Find player's position, moves to within range of player and then performs attack when in range
     */
    @Override
    public void ai() {
        if (getDeadCounter() >= 0) {
            super.setXvelocity(0);
            super.setYvelocity(0);
        } else {
            double distance = Math.abs(Math.hypot(super.getXpos() - super.getRoom().getPlayer().getXpos(), super.getYpos() - super.getRoom().getPlayer().getYpos()));
            double tempAngle = 0;

            //Finds Player location
            player = super.getRoom().getPlayer();
            playXPos = player.getXpos();
            playYPos = player.getYpos();

            //Angle between player and enemy
            tempAngle = Math.atan2(playXPos - (super.getXpos() - super.getWidth() / 2), (super.getYpos() - super.getHeight() / 2) - playYPos ) - Math.PI/2;
            float angle = (float) tempAngle;
            super.setAngle(angle);
            if (distance <= super.getRange() || super.getRange() == -1) {
                ranged();
                super.setXvelocity(0);
                super.setYvelocity(0);
            } else {
                super.setXvelocity(super.getMoveSpeed() * (float) Math.cos(angle));
                if (!super.getName().equals("shield")) {
                    super.setYvelocity(super.getMoveSpeed() * (float) Math.sin(angle));
                } else {
                    super.setYvelocity(0);
                }
            }
        }
        super.chooseAnimation(super.getDirections());
        super.animations();
        super.updatePosition();
        super.clearTemp();
    }

    /**
     * Performs a ranged attack if the counter is 0, ignores ammo and is reduced by enemy firerate multiplier
     */
    public void ranged() {
        if (super.getRangedFireCounter() == 0) {
            double angle = super.getAngle();
            angle = angle - (super.getRangedSpread() * ((super.getRangedProjectiles() - 1) / 2));
            for (int i = 0; i < super.getRangedProjectiles(); i++) {
                super.addProjectile(angle);
                angle += super.getRangedSpread();
            }
            super.setRangedFireCounter((int)(super.getRangedFireRate() * Constants.ENEMY_FIRERATE_MULTIPLIER));
        }
    }

    /**
     * Kills the enemy dropping an item and checking if doors should open
     */
    @Override
    public void kill() {
        hide();
        super.getManager().getEntities().dropItem(super.getXpos(), super.getYpos() - super.getHeight(), super.getName(), isChampion);
        super.getManager().getEntities().setCheckDoors(true);
        super.getManager().getEntities().removeEntity(super.getId());
    }
}
