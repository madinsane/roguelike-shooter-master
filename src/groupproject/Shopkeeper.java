package groupproject;

/**
 * Describes a shopkeeper
 * Extension of boss
 */
public class Shopkeeper extends Boss {
    private boolean angry;

    public Shopkeeper(Manager manager, float xpos, float ypos, float scale, String name, int difficulty) {
        super(manager, xpos, ypos, scale, name, difficulty);
        angry = false;
        super.changeWeapon("shopkeeper_spread1");
        super.setAngle(Math.PI);
        super.setCurrentAttackType("_spread");
    }

    /**
     * Makes shopkeeper angry
     */
    public void aggro() {
        angry = true;
    }

    /**
     * Is shopkeeper angry
     * @return is shopkeeper angry
     */
    public boolean isAngry() {
        return angry;
    }

    /**
     * Handles ai for shopkeeper
     */
    public void ai() {
        if (angry) {
            if (super.getCurrentAttackType().equals("_spread") && super.getRangedAmmo() == 0) {
                super.setAngle(Math.PI);
            } else {
                Player player = super.getManager().getEntities().getCurrentRoomObject().getPlayer();
                int playXPos = (int)super.getManager().getEntities().getCurrentRoomObject().getPlayer().getXpos();
                int playYPos = (int)super.getManager().getEntities().getCurrentRoomObject().getPlayer().getYpos();
                super.setAngle(Math.atan2(playXPos - super.getXpos(),super.getYpos() - playYPos) - Math.PI/2);
            }
            if (super.getRangedAmmo() == 0) {
                if (super.getCurrentAttackType().equals("_spread")) {
                    super.changeWeapon("shopkeeper" + super.getCurrentAttackType() + super.getCurrentAttack());
                    if (super.getCurrentAttack() == 1) {
                        super.setCurrentAttack(super.getCurrentAttack() + 1);
                    } else {
                        super.setCurrentAttack(super.getCurrentAttack() - 1);
                    }
                } else {
                    super.changeWeapon("shopkeeper" + super.getCurrentAttackType());
                }
                if (super.getAttackCounter() >= 0 && super.getAttackCounter() < 10) {
                    super.setCurrentAttackType("_spread");
                } else if (super.getAttackCounter() >= 10 && super.getAttackCounter() < 20) {
                    super.setCurrentAttackType("_machine");
                } else {
                    super.setAttackCounter(-1);
                }
                super.setAttackCounter(super.getAttackCounter() + 1);
            }
            super.ranged();
            String instruction = "_attack";
            if (super.getDeadCounter() >= 0) {
                instruction = "_death";
            }
            super.tryAddAnimation(super.getName() + instruction);
            super.animations();
            super.updatePosition();
            super.clearTemp();
        } else {
            super.tryAddAnimation(super.getName() + "_idle");
            super.animations();
            super.updatePosition();
            super.clearTemp();
        }
    }

    /**
     * Handles shopkeeper collision
     * @param collidedEntity other entity that is being collided with
     */
    @Override
    public void collide(Entity collidedEntity) {
        if (angry) {
            super.collide(collidedEntity);
        }
    }
}
