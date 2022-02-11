package groupproject;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;

import java.awt.*;

/**
 * Describes the ui
 */
public class UI {
    private Manager manager;
    private Sprite healthPackSprite;
    private Sprite healthBarSprite;
    private Sprite goldSprite;
    private Sprite weaponIconSprite;
    private Sprite weaponAmmoSprite;
    private Text ammoText;
    private Text goldText;

    private Sprite bossHealthBar;

    /**
     * Loads all data needed for UI and sets positions
     * @param manager manager
     */
    public UI(Manager manager) {
        this.manager = manager;
        healthBarSprite = new Sprite();
        healthPackSprite = new Sprite();
        goldSprite = new Sprite();
        weaponIconSprite = new Sprite();
        weaponAmmoSprite = new Sprite();
        bossHealthBar = new Sprite();
        ammoText = new Text("0", manager.getAssets().getFont("uiFont"));
        goldText = new Text("0", manager.getAssets().getFont("uiFont"));

        healthPackSprite.setTexture(manager.getAssets().getTexture("uiHealthPacks"));
        healthBarSprite.setTexture(manager.getAssets().getTexture("uiHealthBar"));
        goldSprite.setTexture(manager.getAssets().getTexture("uiGold"));
        weaponIconSprite.setTexture(manager.getAssets().getTexture("uiWeaponIcon"));
        weaponAmmoSprite.setTexture(manager.getAssets().getTexture("uiWeaponAmmo"));
        bossHealthBar.setTexture(manager.getAssets().getTexture("bossHealthBar"));

        setSprite(0, 41, 16, goldSprite);
        setSprite(0, 31, 16, weaponAmmoSprite);
        setSprite(0, 15, 7, healthPackSprite);
        setSprite(0, 110, 7, healthBarSprite);
        setSprite(0, 16, 16, weaponIconSprite);
        setSprite(0,110,7, bossHealthBar);

        float scale = manager.getEntities().getScale();
        healthPackSprite.setPosition(16,16);
        healthBarSprite.setPosition(healthPackSprite.getPosition().x + (15 * scale), healthPackSprite.getPosition().y);
        weaponIconSprite.setPosition(healthPackSprite.getPosition().x, healthPackSprite.getPosition().y + (7 * scale));
        weaponAmmoSprite.setPosition(weaponIconSprite.getPosition().x + (16 * scale), weaponIconSprite.getPosition().y);
        goldSprite.setPosition(weaponAmmoSprite.getPosition().x + (31 * scale), weaponIconSprite.getPosition().y);
        bossHealthBar.setPosition(530, 704);
        ammoText.setPosition(weaponAmmoSprite.getPosition().x + (7 * scale), weaponAmmoSprite.getPosition().y + (4 * scale));
        goldText.setPosition(goldSprite.getPosition().x + (15 * scale), goldSprite.getPosition().y + (4 * scale));

        ammoText.setCharacterSize(24);
        goldText.setCharacterSize(24);
        ammoText.setColor(new Color(0, 0, 0));
        goldText.setColor(new Color(133, 79, 27));

        healthPackSprite.setScale(scale, scale);
        healthBarSprite.setScale(scale, scale);
        weaponIconSprite.setScale(scale, scale);
        weaponAmmoSprite.setScale(scale, scale);
        goldSprite.setScale(scale, scale);
        bossHealthBar.setScale(scale, scale);

    }

    /**
     * Draws all drawables each frame
     */
    public void draw() {
        int healthPackCount = manager.getEntities().getCurrentRoomObject().getPlayer().getHealthPackCounter();
        int gold = manager.getEntities().getCurrentRoomObject().getPlayer().getGoldCounter();
        int health = manager.getEntities().getCurrentRoomObject().getPlayer().getHealth();
        int healthMax = manager.getEntities().getCurrentRoomObject().getPlayer().getHealthMax();
        int healthPercent = (int)(((float)health / (float)healthMax) * 100);
        healthPercent -= 100;
        healthPercent = Math.abs(healthPercent);
        //System.out.println(health + " " + healthPercent);
        int ammo = manager.getEntities().getCurrentRoomObject().getPlayer().getRangedAmmo();
        setSprite(healthPackCount, 15, 7, healthPackSprite);
        setSprite(healthPercent, 110, 7, healthBarSprite);
        setSprite(manager.getEntities().getCurrentRoomObject().getPlayer().getWeaponId(), 16, 16, weaponIconSprite);
        if (ammo == -1) {
            ammoText.setString("");
        } else {
            ammoText.setString(Integer.toString(ammo));
        }

        Boss boss = manager.getEntities().getCurrentRoomObject().getBoss();
        if (boss != null) {
            if (boss instanceof Shopkeeper) {
                if (((Shopkeeper) boss).isAngry()) {
                    int bossHealthMax = boss.getHealthMax();
                    int bossHealth = boss.getHealth();
                    int bossHealthPercent = (int) (((float) bossHealth / (float) bossHealthMax) * 100);
                    bossHealthPercent -= 100;
                    bossHealthPercent = Math.abs(bossHealthPercent);
                    if (bossHealthPercent == 100) {
                        bossHealthPercent--;
                    }
                    setSprite(bossHealthPercent, 110, 7, bossHealthBar);
                    manager.getWindow().draw(bossHealthBar);
                }
            } else {
                int bossHealthMax = boss.getHealthMax();
                int bossHealth = boss.getHealth();
                int bossHealthPercent = (int) (((float) bossHealth / (float) bossHealthMax) * 100);
                bossHealthPercent -= 100;
                bossHealthPercent = Math.abs(bossHealthPercent);
                if (bossHealthPercent == 100) {
                    bossHealthPercent--;
                }
                setSprite(bossHealthPercent, 110, 7, bossHealthBar);
                manager.getWindow().draw(bossHealthBar);
            }
        }

        goldText.setString(Integer.toString(gold));
        manager.getWindow().draw(healthPackSprite);
        manager.getWindow().draw(healthBarSprite);
        manager.getWindow().draw(weaponAmmoSprite);
        manager.getWindow().draw(weaponIconSprite);
        manager.getWindow().draw(goldSprite);
        manager.getWindow().draw(ammoText);
        manager.getWindow().draw(goldText);
    }

    /**
     * Sets current sprite based on the spritesheet of width 8 sprites
     * @param pos position in spritesheet
     * @param width width of singular sprite
     * @param height height of singular sprite
     * @param sprite sprite to change
     */
    public void setSprite(int pos, int width, int height, Sprite sprite) {
        int left = (Math.floorMod(pos, 8) * width);
        int top = (Math.floorDiv(pos, 8) * height);
        sprite.setTextureRect(new IntRect(left, top, width, height));
    }
}
