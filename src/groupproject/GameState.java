package groupproject;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.window.event.Event;
import org.jsfml.system.Clock;
import org.jsfml.graphics.Text;

import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Main game state
 */
public class GameState extends State {
	private Manager manager;
	private int playerId; //holds the player entity's id
	//private int zombieId; //holds the player entity's id

	private int[][] roomIds;
	private int[][] roomDiscovery;
	int bossRoomId;
	
	int difficultyLevel;
	
	private Sprite mapRoomSprite;
	private Color roomColor;
	private Color roomUndiscoveredColor;
	private Color roomUnenteredColor;
	private boolean pauseGame;
	private Random random;
	private UI ui;
	private int shopRoomId;
	
	Clock mapOscClock;
	
	private String[] options;
	private Text menuOption;
	private int menuSelection;
	Clock opOscClock;
	private Pair<String, Integer>[] enemyList;
	
	public GameState(Manager manager) {
		this.manager = manager;
		random = new Random();
		mapOscClock = new Clock();
		
		mapRoomSprite = null;
		
		// pause menu
		opOscClock = new Clock();
		
		options = new String[] {"Continue", "Quit"};
		menuOption = new Text("Noop", manager.getAssets().getFont("MainMenuFont"));
		menuOption.setCharacterSize(16);
		menuOption.setColor(Color.BLACK);
		
		menuSelection = 0;
		difficultyLevel = 0;
		//System.out.println(manager.getWindow().getSize().x + " " + manager.getWindow().getSize().y);
	}

	/**
	 * Gets current difficulty level
	 * @return current difficulty level
	 */
	public int getDifficultyLevel()
	{
		return difficultyLevel;
	}

	/**
	 * Sets current difficulty level
	 * @param difficultyLevel new difficulty level
	 */
	public void setDifficultyLevel(int difficultyLevel)
	{
		this.difficultyLevel = difficultyLevel;
		manager.getEntities().setDifficultyLevel(difficultyLevel);
	}

	/**
	 * Increments difficulty level
	 */
	public void incDifficultyLevel()
	{
		setDifficultyLevel(getDifficultyLevel()+1);
	}

	/**
	 * Initializes the game, loading assets and data and generating levels
	 */
	public void init() {
		manager.getData().readFromFile("animations", Constants.DATA_ANIMATIONS_FILEPATH);
		manager.getAssets().loadTexture("player", Constants.PLAYER_TEXTURE);
		manager.getAssets().loadTexture("teleporter", Constants.TELEPORTER_FILEPATH);
		manager.getAssets().loadTexture("pistol_projectile", Constants.PLAYER_PISTOL_PROJECTILE_FILEPATH);
		manager.getAssets().loadTexture("arrow_projectile", Constants.PLAYER_ARROW_PROJECTILE_FILEPATH);
		manager.getAssets().loadTexture("flame_projectile", Constants.PLAYER_FLAME_PROJECTILE_FILEPATH);
		manager.getAssets().loadTexture("rocket_projectile", Constants.PLAYER_ROCKET_PROJECTILE_FILEPATH);
		manager.getAssets().loadTexture("slow_projectile", Constants.PLAYER_SLOW_PROJECTILE_FILEPATH);
		manager.getAssets().loadTexture("room", Constants.ROOM_TEXTURE);
		manager.getAssets().loadTexture("roomBoss", Constants.ROOM_BOSS_TEXTURE);
		manager.getAssets().loadTexture("roomCurrent", Constants.ROOM_CURRENT_TEXTURE);
		manager.getAssets().loadTexture("doorForward", Constants.DOOR_FORWARD_FILEPATH);
		manager.getAssets().loadTexture("doorSide", Constants.DOOR_SIDE_FILEPATH);
		manager.getData().readFromFile("projectiles", Constants.DATA_PROJECTILES_FILEPATH);
		manager.getData().readFromFile("enemies", Constants.DATA_ENEMIES_FILEPATH);
		manager.getData().readFromFile("boss", Constants.DATA_BOSS_FILEPATH);
		manager.getAssets().loadTexture("weapon", Constants.WEAPON_FILEPATH);
		manager.getAssets().loadTexture("skinman", Constants.SKINMAN_TEXTURE);
		manager.getAssets().loadTexture("plantBoss", Constants.PLANTBOSS_TEXTURE);
		manager.getAssets().loadTexture("shotgunBoss", Constants.SHOTGUNBOSS_TEXTURE);
		manager.getAssets().loadTexture("mentorBoss", Constants.MENTORBOSS_TEXTURE);
		manager.getAssets().loadTexture("bossHealthBar", Constants.BOSS_HEALTH_BAR);
		manager.getData().readFromFile("items", Constants.DATA_ITEMS_FILEPATH);
		manager.getData().readFromFile("passives", Constants.DATA_PASSIVES_FILEPATH);
		manager.getData().readFromFile("splash", Constants.DATA_SPLASH_FILEPATH);
		manager.getEntities().setItemList(manager.getData().getItems());
		manager.getAssets().loadTexture("gold", Constants.ITEM_GOLD_TEXTURE);
		manager.getAssets().loadTexture("pillRed", Constants.ITEM_PILL_RED_TEXTURE);
		manager.getAssets().loadTexture("pillGreen", Constants.ITEM_PILL_GREEN_TEXTURE);
		manager.getAssets().loadTexture("dog", Constants.DOG_TEXTURE);
		manager.getAssets().loadTexture("healthPack", Constants.HEALTHPACK_TEXTURE);
		manager.getAssets().loadTexture("uiGold", Constants.UI_GOLD_TEXTURE);
		manager.getAssets().loadTexture("uiHealthBar", Constants.UI_HEALTH_BAR_TEXTURE);
		manager.getAssets().loadTexture("uiHealthPacks", Constants.UI_HEALTH_PACKS_TEXTURE);
		manager.getAssets().loadTexture("uiWeaponIcon", Constants.UI_WEAPON_ICON_TEXTURE);
		manager.getAssets().loadTexture("uiWeaponAmmo", Constants.UI_WEAPON_AMMO_TEXTURE);
		manager.getAssets().loadFont("uiFont", Constants.UI_FONT);

		manager.getAssets().loadTexture("groundSpike", Constants.GROUND_SLAM_TEXTURE);
		manager.getAssets().loadTexture("invis_projectile", Constants.INVIS_PROJECTILE_TEXTURE);
		manager.getAssets().loadTexture("shopkeeper", Constants.SHOPKEEPER_TEXTURE);
		manager.getAssets().loadTexture("petal", Constants.PLANT_PETAL_PROJECTILE_FILEPATH);
		manager.getAssets().loadTexture("plant_acid", Constants.PLANT_ACID_PROJECTILE_FILEPATH);
		manager.getAssets().loadTexture("plantacid", Constants.PLANT_ACID_SPLASH_FILEPATH);
		manager.getAssets().loadTexture("throwingKnife", Constants.THROWING_KNIFE_TEXTURE);
		manager.getAssets().loadTexture("sword", Constants.THROWING_SWORD_TEXTURE);
		manager.getAssets().loadTexture("rocket", Constants.ROCKET_SPLASH_TEXTURE);
		manager.getAssets().loadTexture("acid", Constants.ACID_SPLASH_TEXTURE);
		manager.getAssets().loadTexture("acid_projectile", Constants.ACID_BALL_TEXTURE);
		manager.getAssets().loadTexture("shield", Constants.SHIELD_MAN_TEXTURE);
		manager.getAssets().loadTexture("turret_rf", Constants.TURRET_RF_FILEPATH);
		manager.getAssets().loadTexture("turret_rock", Constants.TURRET_ROCK_FILEPATH);
		enemyList = manager.getData().getEnemies(12, 4);
		manager.getEntities().setEnemyGold(manager.getData().getEnemies(18, 0));

		/*
			Gets the tileset data by counting through all possible combinations in binary
		 */
		StringBuilder tileName = new StringBuilder();
		for (int i=1; i<4; i++) {
			String binaryString = "";
			for (int j=0b1; j<0b10000; j++) {
				boolean up = true;
				boolean east = true;
				boolean down = true;
				boolean west = true;
				binaryString = String.format("%4s", Integer.toBinaryString(j)).replace(' ', '0');
				if (binaryString.charAt(0) == '0') {
					up = false;
				}
				if (binaryString.charAt(1) == '0') {
					east = false;
				}
				if (binaryString.charAt(2) == '0') {
					down = false;
				}
				if (binaryString.charAt(3) == '0') {
					west = false;
				}
				tileName.append("tileset");
				tileName.append(i);
				if (up) {
					tileName.append("-up");
				}
				if (east) {
					tileName.append("-east");
				}
				if (down) {
					tileName.append("-down");
				}
				if (west) {
					tileName.append("-west");
				}
				manager.getAssets().loadTexture(tileName.toString(), Constants.TILESET_BASE_FILEPATH + tileName.toString() + ".png");
				tileName.delete(0, tileName.length());
			}
		}
		manager.getEntities().initRoom();
		playerId = manager.getEntities().addEntity(new Player(manager, Constants.PLAYER_MOVE_SPEED, Constants.PLAYER_DASH_SPEED, 500, 500, 2, 16, 32));

		bossRoomId = 0;

		roomColor = new Color(255, 255, 255);
		roomUnenteredColor = new Color(255, 255, 255, 180);
		roomUndiscoveredColor = new Color(0, 0, 0, 0);

		pauseGame = false;
		generateForestDungeon(Constants.DUNGEON_WIDTH, Constants.DUNGEON_HEIGHT);
		ui = new UI(manager);
	}

	/**
	 * Spawns enemies in a given room
	 * Spawns enemies based on difficulty values, each enemy has a difficulty value
	 * A random enemy is chosen and if the difficulty is below the max, the enemy's difficulty is subtracted from the counter
	 * Spawns a champion if the random roll is below the champion chance
	 * @param room room to spawn enemies in
	 */
	public void spawnEnemies(Room room) {
		//System.out.println("Started room" + room);
		int maxDifficulty = getDifficultyLevel()*Constants.DIFFICULTY_SCALING + Constants.DIFFICULTY_BASE;
		int difficulty = maxDifficulty;
		while (difficulty > 0) {
			boolean isChampion = false;
			int chance = random.nextInt(100);
			if (chance < Constants.CHAMPION_CHANCE) {
				isChampion = true;
			}
			int randomEnemy = random.nextInt(enemyList.length);
			if (enemyList[randomEnemy].getR() <= difficulty || (difficulty < 2 && enemyList[randomEnemy].getR() < maxDifficulty)) {
				difficulty -= enemyList[randomEnemy].getR();
				room.addEntity(new Enemy(
						manager,
						random.nextInt(room.getWidth() / 2) + ((room.getWidth() / 2) - (room.getWidth() / 4)),
						random.nextInt(room.getHeight() / 2) + ((room.getHeight() / 2) - (room.getHeight() / 4)),
						manager.getEntities().getScale(),
						enemyList[randomEnemy].getL(),
						getDifficultyLevel(),
						isChampion
				));
			}
		}
		//System.out.println("Finished room" + room);
	}

	/**
	 * Spawns a boss in a given room
	 * On odd levels spawns a scaled variant of a normal enemy
	 * On multiples of 6 spawns the mentor boss
	 * On multiples of 4 spawns the shotgun boss
	 * On multiples of 2 spawns the plant boss
	 * @param room room to place boss in
	 */
	public void spawnBoss(Room room) {
		String enemyType;
		float scale;
		System.out.println(getDifficultyLevel() + 1);
		/*if (getDifficultyLevel() + 1 == 1) {
			enemyType = "mentorBoss";
			scale = manager.getEntities().getScale() * 1.5f;
		} else*/
    	if ((getDifficultyLevel() + 1) % 6 == 0) {
			enemyType = "mentorBoss";
			scale = manager.getEntities().getScale() * 1.5f;
		} else if ((getDifficultyLevel() + 1) % 4 == 0) {
			enemyType = "shotgunBoss";
			scale = manager.getEntities().getScale() * 1.5f;
		} else if ((getDifficultyLevel() + 1) % 2 == 0) {
			enemyType = "plantBoss";
			scale = manager.getEntities().getScale() * 1.5f;
		} else {
			int randomEnemy = random.nextInt(enemyList.length);
			enemyType = enemyList[randomEnemy].getL();
			scale = manager.getEntities().getScale() * 2;
		}
		room.addEntity(new Teleporter(manager, (room.getWidth() / 2) - 32*manager.getEntities().getScale(), (room.getHeight() / 2) - 16*manager.getEntities().getScale(), manager.getEntities().getScale()*1.5f, 32, 32));
		room.addEntity(new Boss(manager, (room.getWidth() / 2) - 32, (room.getHeight() / 2) - 16*scale, scale, enemyType, getDifficultyLevel()));
	}

	/**
	 * Spawns a shop with items and shopkeeper
	 * @param room room to place shop in
	 */
	public void spawnShop(Room room) {
		room.addEntity(new Item(manager, "healthPack", room, room.getWidth() / 4, 2 * room.getHeight() / 3, manager.getEntities().getScale(), 16, 16, 0, true));
		room.addEntity(new Item(manager, "pillRed", room, 2 * room.getWidth() / 4, 2 * room.getHeight() / 3, manager.getEntities().getScale(), 16, 16, 0, true));
		room.addEntity(new Item(manager, "pillGreen", room, 3 * room.getWidth() / 4, 2 * room.getHeight() / 3, manager.getEntities().getScale(), 16, 16, 0, true));
		float scale = manager.getEntities().getScale();
		room.addEntity(new Shopkeeper(manager, (room.getWidth() / 2) - 32, (room.getHeight() / 2) - 48*scale, scale, "shopkeeper", getDifficultyLevel()));
	}

	/**
	 * Sets if the room has been discovered
	 * @param x x value of room to be discovered
	 * @param y y value of room to be discovered
	 * @param full ends recursive calls if false
	 */
	public void setRoomDiscovery(int x, int y, boolean full)
	{
		if(x < 0 || x >= roomDiscovery.length || y < 0 || y >= roomDiscovery[0].length)
			return;
		
		if(roomIds[x][y] == -1)
			return;
		
		
		if(full)
		{
			setRoomDiscovery(x-1, y, false);
			setRoomDiscovery(x+1, y, false);
			setRoomDiscovery(x, y-1, false);
			setRoomDiscovery(x, y+1, false);
			
			roomDiscovery[x][y] = 2;
		}
		else if(roomDiscovery[x][y] == 0)
		{
			roomDiscovery[x][y] = 1;
		}
	}

	/**
	 * Sets if the room with given id has been discovered
	 * @param id id of room
	 * @param full false to end recursive calls
	 */
	public void setRoomDiscovery(int id, boolean full)
	{
		// discover room by id
		int x = -1;
		int y = -1;
		
		for(int xx = 0; xx < roomIds.length; xx++)
		{
			for(int yy = 0; yy < roomIds[xx].length; yy++)
			{
				if(roomIds[xx][yy] == id)
				{
					x = xx;
					y = yy;
				}
			}
		}
		
		setRoomDiscovery(x, y, full);
	}

	/**
	 * Generates a forest dungeon
	 * @param dungeonSizeX width of dungeon
	 * @param dungeonSizeY height of dungeon
	 */
	public void generateForestDungeon(int dungeonSizeX, int dungeonSizeY)
	{
		int numRooms = (dungeonSizeX+dungeonSizeY) / 2;
		numRooms += (difficultyLevel)*5;
		
		manager.getEntities().removeOtherRooms();
		
		roomIds = new int[dungeonSizeX][dungeonSizeY];
		roomDiscovery = new int[dungeonSizeX][dungeonSizeY];
		
		// Create the rooms
		for(int x = 0; x < dungeonSizeX; x++)
		{
			for(int y = 0; y < dungeonSizeY; y++)
			{
				roomIds[x][y] = -1;
				roomDiscovery[x][y] = 0;
			}
		}
		
		ArrayList<Room> quickRoomLookup = new ArrayList<>();
		ArrayList<Pair<Integer, Integer>> Candidates = new ArrayList<>();

		// center room must always exist
		int nRoom = manager.getEntities().addRoom();
		int centerX = dungeonSizeX / 2;
		int centerY = dungeonSizeY / 2;
		
		roomIds[centerX][centerY] = nRoom;
		quickRoomLookup.add(manager.getEntities().getRoomOrNull(nRoom));
		
		int centerRoom = nRoom;
		
		Random r = new Random();
		
		for(int i = 0; i < numRooms; i++)
		{
			if(quickRoomLookup.size() == 0)
				break;
			
			// Choose a room at random
			Room pivotRoom = quickRoomLookup.get(r.nextInt(quickRoomLookup.size()));
			
			// Find room position in grid
			int x = 0;
			int y = 0;
			
			boolean match = false;
			
			for(x = 0; x < dungeonSizeX; x++)
			{
				for(y = 0; y < dungeonSizeY; y++)
				{
					if(roomIds[x][y] == pivotRoom.getId())
					{
						match = true;
						
						break;
					}
				}
				
				if(match) break;
			}
			
			if(match)
			{
				// Find Candidates
				Candidates.clear();
				
				if(y > 0)
				{
					if(roomIds[x][y-1] == -1)
					{
						Candidates.add(new Pair<Integer, Integer>(x, y-1)); // north candidate
					}
				}
				if(x < dungeonSizeX - 2)
				{
					if(roomIds[x+1][y] == -1)
					{
						Candidates.add(new Pair<Integer, Integer>(x+1, y)); // east candidate
					}
				}
				if(y < dungeonSizeY - 2)
				{
					if(roomIds[x][y+1] == -1)
					{
						Candidates.add(new Pair<Integer, Integer>(x, y+1)); // south candidate
					}
				}
				if(x > 0)
				{
					if(roomIds[x-1][y] == -1)
					{
						Candidates.add(new Pair<Integer, Integer>(x-1, y)); // west candidate
					}
				}
				
				// check candidates
				for(int j = 0; j < Candidates.size(); j++)
				{
					int xx = Candidates.get(j).getL();
					int yy = Candidates.get(j).getR();
					
					int neighborCount = 0;
					
					if(yy > 0)
					{
						if(roomIds[xx][yy-1] != -1)
						{
							neighborCount++;
						}
					}
					if(xx < dungeonSizeX - 2)
					{
						if(roomIds[xx+1][yy] != -1)
						{
							neighborCount++;
						}
					}
					if(yy < dungeonSizeY - 2)
					{
						if(roomIds[xx][yy+1] != -1)
						{
							neighborCount++;
						}
					}
					if(xx > 0)
					{
						if(roomIds[xx-1][yy] != -1)
						{
							neighborCount++;
						}
					}
					
					if(neighborCount > 1)
					{
						Candidates.remove(j);
						j--;
					}
				}
				
				if(Candidates.size() > 0)
				{
					// Select one at random
					Pair<Integer, Integer> Candidate = Candidates.get(r.nextInt(Candidates.size()));
					
					// Spawn a room there
					nRoom = manager.getEntities().addRoom();
					roomIds[Candidate.getL()][Candidate.getR()] = nRoom;
					quickRoomLookup.add(manager.getEntities().getRoomOrNull(nRoom));
				}
				else
				{
					quickRoomLookup.remove(quickRoomLookup.indexOf(pivotRoom));
					
					i -= 1; // try again
				}
			}
		}
		
		// determine boss room
		bossRoomId = centerRoom;
		shopRoomId = centerRoom;
		
		int greatestDistX = 0;
		int greatestDistY = 0;
		
		for(int x = 0; x < dungeonSizeX; x++)
		{
			for(int y = 0; y < dungeonSizeY; y++)
			{
				int distX = Math.abs(centerX - x);
				int distY = Math.abs(centerY - y);
				
				if((distX > greatestDistX && distY > greatestDistY) && roomIds[x][y] != -1)
				{
					greatestDistX = distX;
					greatestDistY = distY;
					
					bossRoomId = roomIds[x][y];
				}
			}
		}
		
		// Put things in the rooms
		for(int x = 0; x < dungeonSizeX; x++)
		{
			for(int y = 0; y < dungeonSizeY; y++)
			{
				int roomId = roomIds[x][y];
				
				if(roomId == -1) continue;
				
				boolean roomUp = y > 0 && roomIds[x][y-1] != -1;
				boolean roomDown = y < dungeonSizeY-1 && roomIds[x][y+1] != -1;
				boolean roomWest = x > 0 && roomIds[x-1][y] != -1;
				boolean roomEast = x < dungeonSizeX-1 && roomIds[x+1][y] != -1;

				StringBuilder tileName = new StringBuilder();
				int tileType = ThreadLocalRandom.current().nextInt(1, 3+1);
				//int tileType = 1;
				tileName.append("tileset");
				tileName.append(tileType);
				if (roomUp)
					tileName.append("-up");
				if (roomEast)
					tileName.append("-east");
				if (roomDown) {
					tileName.append("-down");
				}
				if (roomWest) {
					tileName.append("-west");
				}
				//System.out.println(tileName.toString());
				Room room = manager.getEntities().getRoomOrNull(roomId);
				room.setTileset(manager.getAssets().getTexture(tileName.toString()));



				// enemies
				if(roomId != centerRoom && roomId != bossRoomId)
				{
					spawnEnemies(room);
				}
				int scaledBounds = (int)(Constants.ROOM_BOUNDS_PIXELS * manager.getEntities().getScale());
				if(bossRoomId == roomId) {
					spawnBoss(room);
				} else if (shopRoomId == roomId && getDifficultyLevel() > 0) {
					spawnShop(room);
				}
				// Up Door
				room.addEntity(new Door(
					manager, 
					(room.getWidth()/2 - scaledBounds*1.5f),
					0,
					manager.getEntities().getScale(),
					64,
					32,
					y == 0 ? -1 : roomIds[x][y-1],
					false,
					true,
					"doorForward"
				));
				// East Door
				room.addEntity(new Door(
					manager, 
					room.getWidth() - 29*manager.getEntities().getScale(),
					(room.getHeight()/2 - 27*manager.getEntities().getScale()),
					manager.getEntities().getScale(),
					4,
					64,
					x == dungeonSizeX-1 ? -1 : roomIds[x+1][y],
					true,
					false,
					"doorSide"
				));
				
				// Down Door
				room.addEntity(new Door(
					manager,
					(room.getWidth()/2 - scaledBounds*1.5f),
					room.getHeight() - scaledBounds,
					manager.getEntities().getScale(),
					64,
					32,
					y == dungeonSizeY-1 ? -1 : roomIds[x][y+1],
					false,
					true,
					"doorForward"
				));
				
				// West Door
				room.addEntity(new Door(
					manager,
					28*manager.getEntities().getScale(),
					(room.getHeight()/2 - 27*manager.getEntities().getScale()),
					manager.getEntities().getScale(),
					4,
					64,
					x == 0 ? -1 : roomIds[x-1][y],
					true,
					false,
					"doorSide"
				));
				room.checkDoors();
			}
		}
		
		// Move to center room
		manager.getEntities().setCurrentRoom(roomIds[dungeonSizeX / 2][dungeonSizeY / 2]);
		manager.getEntities().getCurrentRoomObject().addEntity(manager.getEntities().getEntityOrNull(playerId));
		manager.getEntities().getCurrentRoomObject().checkDoors();
		manager.getEntities().getCurrentRoomObject().getPlayer().setTeleportInTimer(Constants.TELEPORT_FRAMES);

		setRoomDiscovery(dungeonSizeX / 2, dungeonSizeY / 2, true);
	}

	/**
	 * Handles input for GameState
	 */
	public void handleInput() {
		for (Event event : manager.getWindow().pollEvents()) {
			switch (event.type) {
				case CLOSED:
					manager.getWindow().close();
					break;
				case LOST_FOCUS:
					manager.getStates().setFocused(false);
					break;
				case GAINED_FOCUS:
					manager.getStates().setFocused(true);
					break;
				case KEY_PRESSED:
					if(pauseGame)
					{
						if(event.asKeyEvent().key == Constants.PLAYER_KEY_UP)
						{
							menuSelection--;
							opOscClock.restart();
						}
						else if(event.asKeyEvent().key == Constants.PLAYER_KEY_DOWN)
						{
							menuSelection++;
							opOscClock.restart();
						}
						else if(event.asKeyEvent().key == Constants.PLAYER_KEY_DASH)
						{
							optionSelected();
						}
						
						menuSelection = Math.max(0, Math.min(menuSelection, options.length-1));
					}
					if(event.asKeyEvent().key == Constants.PLAYER_KEY_PAUSE)
					{
						pause();
					}
					break;
			}
			manager.getEntities().handleInput(event);
		}
	}

	/**
	 * Updates each entity and checks collision if the game is not paused each dt
	 * @param dt time of 1 frame
	 */
	public void update(float dt) {
		if(!pauseGame)
		{
			manager.getEntities().update();
			manager.getEntities().checkCollision();
		}
	}

	/**
	 * Draws each drawable each dt
	 * @param dt time of 1 frame
	 */
	public void draw(float dt) {
		manager.getWindow().clear(new Color(53, 195, 80));
		manager.getWindow().draw(manager.getEntities().getCurrentRoomObject().getTileset());
		manager.getEntities().draw();
		
		drawMap();
		ui.draw();

		// pause menu
		if(pauseGame)
		{
			drawPauseMenu();
		}
		
		manager.getWindow().display();
	}

	/**
	 * Draws the map
	 */
	public void drawMap() {
		// Draw map
		if(mapRoomSprite == null)
		{
			mapRoomSprite = new Sprite();
		}

		float mapScale = 2.f;

		int mapRoomWidth = manager.getAssets().getTexture("room").getSize().x;
		int mapRoomHeight = manager.getAssets().getTexture("room").getSize().y;

		for(int x = 0; x < roomIds.length; x++)
		{
			for(int y = 0; y < roomIds[x].length; y++)
			{
				//if(roomIds[x][y] == -1) continue;

				mapRoomSprite.setPosition(manager.getEntities().getRoomWidth() - 16 - (mapRoomWidth*mapScale*Constants.DUNGEON_WIDTH) + ((mapRoomWidth*mapScale) * (float)x), 16.f + ((mapRoomHeight*mapScale) * (float)y));
				mapRoomSprite.setScale(mapScale, mapScale);
				mapRoomSprite.setOrigin(mapRoomWidth / 2, mapRoomHeight / 2);

				boolean inRoom = manager.getEntities().getCurrentRoom() == roomIds[x][y];
				boolean exists = roomIds[x][y] != -1;
				boolean boss = bossRoomId == roomIds[x][y];
				int discovery = roomDiscovery[x][y];

				mapRoomSprite.setTexture(manager.getAssets().getTexture("room"));

				if(boss)
					mapRoomSprite.setTexture(manager.getAssets().getTexture("roomBoss"));

				if(discovery == 0)
				{
					mapRoomSprite.setColor(roomUndiscoveredColor);
				}
				else if(discovery == 1)
				{
					mapRoomSprite.setColor(roomUnenteredColor);
				}
				else if(discovery == 2)
				{
					mapRoomSprite.setColor(roomColor);
				}

				if(exists)
					manager.getWindow().draw(mapRoomSprite);

				if(inRoom)
				{
					float s = mapOscClock.getElapsedTime().asSeconds();
					float sint = (float)Math.sin(s * 4) / 3 + (1 / 3);

					mapRoomSprite.setTexture(manager.getAssets().getTexture("roomCurrent"));
					mapRoomSprite.setScale(mapScale+sint, mapScale+sint);
					manager.getWindow().draw(mapRoomSprite);
				}
			}
		}
	}

	/**
	 * Gets the 2d array of room ids
	 * @return room id
	 */
	public int[][] getRoomIds() {
		return roomIds;
	}

	/**
	 * Draws the pause menu
	 */
	public void drawPauseMenu()
	{
		menuOption.setColor(Color.BLACK);
		
		for(int i = 0; i < options.length; i++)
		{
			menuOption.setScale(1.f, 1.f);
			menuOption.setString(options[i]);
			
			if(menuSelection == i)
			{
				float sc = 1.f;
				
				sc += (float)Math.abs(Math.sin((opOscClock.getElapsedTime().asSeconds()+0.5f)*2.f) / 4.f);
				
				menuOption.setScale(sc, sc);
			}
			
			menuOption.setPosition(
				(float)Math.floor(manager.getWindow().getSize().x / 2) - (float)Math.floor(menuOption.getGlobalBounds().width / 2.f),
				(float)Math.floor(manager.getWindow().getSize().y / 2) + (float)(i * 26.f)
			);
			
			manager.getWindow().draw(menuOption);
		}
	}

	/**
	 * Sets the game to paused
	 */
	public void pause() {
		pauseGame = !pauseGame;
	}

	/**
	 * Completes the action of the currently selected menu option
	 */
	public void optionSelected()
	{
		if(menuSelection == 0)
		{
			pauseGame = false;
		}
		else
		{
			manager.getWindow().close();
		}
	}

	/**
	 * Resumes the game
	 */
	public void resume() {

	}
}
