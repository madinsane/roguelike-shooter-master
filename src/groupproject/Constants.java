package groupproject;

import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

/**
 * Private class. Should not be instantiated.
 * Stores constants to be used by any number of classes
 * Private constructor prevents class from being instantiated
 * Reference constants using either Constants.CONSTANT_NAME
 * or import static Constants.CONSTANT_NAME
 */
public class Constants {
	public static final String TITLE = "Title";
	public static final float DT = 1.0f / 60.0f;
	public static final float SPLASH_STATE_SHOW_TIME = 0.5f;
	
	public static final int COLLISION_ALPHA_LIMIT = 126;
	
	public static final String DATA_SEPARATOR = ",";
	public static final String DATA_ANIMATIONS_FILEPATH = "resources/data/animations.csv";
	public static final String DATA_PROJECTILES_FILEPATH = "resources/data/projectiles.csv";
	public static final String DATA_ENEMIES_FILEPATH = "resources/data/enemies.csv";
	public static final String DATA_BOSS_FILEPATH = "resources/data/boss.csv";
	public static final String DATA_ITEMS_FILEPATH = "resources/data/items.csv";
	public static final String DATA_PASSIVES_FILEPATH = "resources/data/passives.csv";
	public static final String DATA_SPLASH_FILEPATH = "resources/data/splash.csv";

	public static final String SPLASH_SCREEN_FILEPATH = "resources/assets/splash.png";
	public static final String DEFAULT_TEXTURE_FILEPATH = "resources/assets/default.png";
	public static final String GAME_SCREEN_BACKGROUND_FILEPATH = "resources/assets/splash.png";
	public static final String PLAYER_PISTOL_PROJECTILE_FILEPATH = "resources/assets/pistolshot.png";
	public static final String PLAYER_ARROW_PROJECTILE_FILEPATH = "resources/assets/arrow.png";
	public static final String PLAYER_FLAME_PROJECTILE_FILEPATH = "resources/assets/flamethrower.png";
	public static final String PLAYER_ROCKET_PROJECTILE_FILEPATH = "resources/assets/rocketidle.png";
	public static final String PLAYER_SLOW_PROJECTILE_FILEPATH = "resources/assets/slowMovingWeapon.png";
	public static final String PLANT_PETAL_PROJECTILE_FILEPATH = "resources/assets/petal.png";
	public static final String PLANT_ACID_PROJECTILE_FILEPATH = "resources/assets/acidBallPlantBoss.png";
	public static final String PLANT_ACID_SPLASH_FILEPATH = "resources/assets/acidAoePlantBoss.png";
	public static final String DOOR_FORWARD_FILEPATH = "resources/assets/Fence Forward.png";
	public static final String DOOR_SIDE_FILEPATH = "resources/assets/Fence Side.png";
	public static final String MAIN_MENU_SCREEN_FONT = "resources/assets/pixearg.ttf";
	public static final String UI_FONT = "resources/assets/pixearg.ttf";
	public static final String TILESET_BASE_FILEPATH = "resources/tileset/";
	public static final String HEALTHPACK_TEXTURE = "resources/assets/HealthPack.png";
	public static final String TELEPORTER_FILEPATH = "resources/assets/Teleporter.png";
	public static final String WEAPON_FILEPATH = "resources/assets/Barrel Sprite Sheet.png";
	public static final String TURRET_RF_FILEPATH = "resources/assets/Turret Rapid Fire Sheet.png";
	public static final String TURRET_ROCK_FILEPATH = "resources/assets/Turret Rocket Sheet.png";
	
    public static final String SKINMAN_TEXTURE = "resources/assets/skinman.png";
    public static final String SHOPKEEPER_TEXTURE = "resources/assets/shopkeeper.png";

	public static final String SHIELD_MAN_TEXTURE = "resources/assets/Shield Enemy Sheet.png";

	public static final String ROCKET_SPLASH_TEXTURE = "resources/assets/rocketSplash.png";
	public static final String ACID_SPLASH_TEXTURE = "resources/assets/acidAoe.png";
	public static final String ACID_BALL_TEXTURE = "resources/assets/acidBall.png";
	public static final String THROWING_KNIFE_TEXTURE = "resources/assets/ThrowingKnife.png";
	public static final String THROWING_SWORD_TEXTURE = "resources/assets/thrownSword.png";
	public static final String INVIS_PROJECTILE_TEXTURE = "resources/assets/invisshot.png";
	public static final String GROUND_SLAM_TEXTURE = "resources/assets/groundSpike.png";

	public static final String DOG_TEXTURE = "resources/assets/Dog Sheet.png";
	public static final String PLANTBOSS_TEXTURE = "resources/assets/plantBoss.png";
	public static final String MENTORBOSS_TEXTURE = "resources/assets/mentorBoss.png";
	public static final String SHOTGUNBOSS_TEXTURE = "resources/assets/Shotgun Sheet.png";
	public static final String BOSS_HEALTH_BAR = "resources/assets/bossHealthBar.png";
	
	public static final String PLAYER_TEXTURE = "resources/assets/Player Sheet.png";
	
	public static final String ROOM_TEXTURE = "resources/assets/room.png";
	public static final String ROOM_BOSS_TEXTURE = "resources/assets/roomBoss.png";
	public static final String ROOM_CURRENT_TEXTURE = "resources/assets/roomCurrent.png";

	public static final String ITEM_GOLD_TEXTURE = "resources/assets/gold.png";
	public static final String ITEM_PILL_RED_TEXTURE = "resources/assets/pillRed.png";
	public static final String ITEM_PILL_GREEN_TEXTURE = "resources/assets/pillGreen.png";

	public static final String UI_HEALTH_BAR_TEXTURE = "resources/assets/HealthBarSegment.png";
	public static final String UI_HEALTH_PACKS_TEXTURE = "resources/assets/HealthPackSegment.png";
	public static final String UI_WEAPON_ICON_TEXTURE = "resources/assets/WeaponIconSegment.png";
	public static final String UI_WEAPON_AMMO_TEXTURE = "resources/assets/WeaponAmmoSegment.png";
	public static final String UI_GOLD_TEXTURE = "resources/assets/GoldSegment.png";
	
	public static final Keyboard.Key PLAYER_KEY_UP = Keyboard.Key.W;
	public static final Keyboard.Key PLAYER_KEY_DOWN = Keyboard.Key.S;
	public static final Keyboard.Key PLAYER_KEY_RIGHT = Keyboard.Key.D;
	public static final Keyboard.Key PLAYER_KEY_LEFT = Keyboard.Key.A;
	public static final Keyboard.Key PLAYER_KEY_DASH = Keyboard.Key.SPACE;
	public static final Keyboard.Key PLAYER_KEY_HEAL = Keyboard.Key.Q;
	public static final Mouse.Button PLAYER_BUTTON_RANGED = Mouse.Button.LEFT;
	public static final Keyboard.Key PLAYER_DROP_WEAPON_KEY = Keyboard.Key.R;
	public static final Keyboard.Key PLAYER_KEY_PAUSE = Keyboard.Key.ESCAPE;

	public static final int WEAPON_DROP_DURATION_FRAMES = 75;
	public static final int ITEM_DROP_CHANCE = 10;
	public static final int CHAMPION_CHANCE = 10;
	public static final int CHAMPION_MULTIPLIER = 5;
	public static final float ITEM_GOLD_SCALING = 1.5f;
	public static final int PLAYER_HEAL_FRAMES = 45;
	public static final int TELEPORT_FRAMES = 35;
	
	public static final float MOBY_NATURAL_ACCELERATION = 0.25f;
	public static final int PLAYER_MOVE_SPEED = 4;
	
	public static final int PLAYER_DASH_DURATION_FRAMES = 15;
	public static final float PLAYER_DASH_SPEED = 15;
	public static final float PLAYER_DASH_ACCELERATION = 5f;
	
	public static final int PLAYER_CHAIN_DASH_FRAMES = 5;
	public static final int PLAYER_DASH_COOLDOWN_FRAMES = 30;

	public static final float ENEMY_FIRERATE_MULTIPLIER = 4f;

	public static final int ROOM_BOUNDS_PIXELS = 32;
	public static final int ROOM_ENEMY_LIMIT = 10;
	
	public static final int DUNGEON_WIDTH = 9;
	public static final int DUNGEON_HEIGHT = 9;

	public static final int DIFFICULTY_BASE = 6;
	public static final int DIFFICULTY_SCALING = 2;
	
	public static final int DOG_SPAWN_FRACTION = 3;
	
	private Constants() {
		//Prevents native class from calling the constructor
		throw new AssertionError();
	}
}
