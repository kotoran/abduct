import com.jogamp.nativewindow.WindowClosingProtocol;

import com.jogamp.opengl.*;
import com.sun.xml.internal.ws.util.StringUtils;

import jdk.internal.org.objectweb.asm.commons.RemappingSignatureAdapter;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//import de.hardcode.jxinput.JXInputManager;

public class JavaTemplate {
	//1//////////////////////////////////////////////////////////////////////////
	// Set this to true to make the game loop exit.
	private static boolean shouldExit;
	// The previous frame's keyboard state.
	// The current frame's keyboard state.
	public static boolean kbState[] = new boolean[256];
	public static boolean kbPrevState[] = new boolean[256];
	public static boolean kbStatePressed[] = new boolean[256];

	public static ArrayList<String> levels = new ArrayList<String>();

	public static int current_level = 0;

	public static GameObject key1 = new GameObject();
	public static GameObject key2 = new GameObject();
	public static GameObject key3 = new GameObject();

	public static boolean mbState[] = new boolean[30];
	public static boolean mbPrevState[] = new boolean[30];
	public static boolean mbStatePressed[] = new boolean[30];

	public static boolean in_interface = false;

	private static boolean soundEffectsEnabled = true;
	private static boolean musicEnabled = true;
	private static boolean fullScreen = false;

	private static int coins_collected = 0;

	private static double tabTimer = 0, clickTimer = 0, screenTimer = 0;
	private static int menuItems = 0;
	//Initialize control settings enums
	public enum ControlMode {
		MOUSE_WASD, ARROWKEYS_Z, ARROWKEYS_WASD, XBOX360_GAMEPAD , LOGITECH_GAMEPAD
	}
	public static ControlMode controlMode  = ControlMode.ARROWKEYS_Z;

	//Texture for the background
	private static int backgroundIDS[] = new int[28];

	// Size of the sprite.
	private static int[] spriteSize = new int[2];
	private static int[] backgroundSize = new int[2];
	private static int[] backgroundSize2 = new int[2];
	// Is jump button held down
	private static boolean input_jump_prev = false;

	//Game space dimensions
	public static int window_width = 1024;
	public static int window_height = 768;

	public static int game_htiles = 120;
	public static int game_vtiles = 120;
	public static int game_ztiles = 4;

	private static int bucket_width = 20;
	private static int bucket_height = 20;

	public static int tile_width = 32;
	public static int tile_height = 32;

	public static int game_width = game_htiles * 32;
	public static int game_height = game_vtiles * 32;

	//Gravitational acceleration scalar
	private static double gravity = .1;

	//INITIALIZE MAP
	private static LevelMap level1;

	private static int background[][] = new int[game_vtiles][game_htiles];
	private static int objects_to_create[][][] = new int[game_ztiles][game_vtiles][game_htiles];

	public static GameObject objects_grid[][][] = new GameObject[10][120][120];
	//private static GameObject objects_grid2[][][] = new GameObject[10][120][120];

	private static int bucket_grid_width = game_htiles / bucket_width;
	private static int bucket_grid_height = game_vtiles / bucket_height;

	static BucketGrid bobTheWallGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);
	static BucketGrid joeTheNpcGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);
	static BucketGrid collectableGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);
	static BucketGrid interactableGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);

	private static boolean gamePaused = false, storeOpen = false, startGame = false;
	public static int menuSelected = 2;

	//private enum pauseMenuTabs { PAUSEMENU_TAB_HOME, PAUSEMENU_TAB_OPTIONS, PAUSEMENU_TAB_CONTROLS};
	private static int PAUSEMENU_TAB_HOME = 0;
	private static int PAUSEMENU_TAB_OPTIONS = 1;
	private static int PAUSEMENU_TAB_CONTROLS = 2;

	private static int menuTab = PAUSEMENU_TAB_HOME;

	//LOAD FONT
	private static String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static Sprite spr_alphabet = new Sprite();
	private static Font font = new Font(spr_alphabet);

	//Mouse Coordinates
	public static Point mouse = new Point(0,0), mousePre = new Point(0,0);

	private static Sprite spr_enemy_healthback = new Sprite();
	private static Sprite spr_enemy_healthfront = new Sprite();

	//Initialize Effects ArrayList
	public static ArrayList<GameObject> effects = new ArrayList<GameObject>();

	//CREATE LIST OF GAME OBJECTS IN THE GAME
	public static ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

	//INITIALIZE PROJECTILE ARRAYLIST
	public static ArrayList<GameObject> projectiles = new ArrayList<GameObject>();
	public static ArrayList<GameObject> projectiles_shadows = new ArrayList<GameObject>();

	public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public static ArrayList<GameObject> enemies_healthbar_back = new ArrayList<GameObject>();
	public static ArrayList<GameObject> enemies_healthbar_front = new ArrayList<GameObject>();
	public static ArrayList<GameObject> enemies_shadows = new ArrayList<GameObject>();

	//INIT PLAYER
	public static Player player = new Player();
	public static GameObject player_upper_body = new GameObject();

	public static int lives = 3;
	public static boolean lifeSubtracted = false, winCondition = false;

	public static ArrayList<InventoryObject> inventory = new ArrayList<InventoryObject>();
	public static ArrayList<InventoryObject> store = new ArrayList<InventoryObject>();
	
	public static int inventorySelected = 0;
	
	public static GLWindow window;
	public static GL2 gl;

	public static Sprite spr_hud_back = new Sprite();

	public static Clip pewClip;

	public static Sprite spr_coin, spr_health_bonus, spr_ammo_bonus, spr_cactus, spr_cactus2, spr_tree, 
	spr_magikarp, spr_magikarp_shadow, spr_wall_shadow, spr_wallgrey1, spr_wall, spr_platform,
	spr_explosion, spr_cactus_explosion, spr_cactus_explosion_large, spr_starlord, spr_starlord_legs, 
	spr_starlord_shooting_right, spr_starlord_shooting_right_up, spr_starlord_shooting_up, spr_starlord_shooting_right_down, spr_starlord_shooting_down,
	spr_starlord_legs_shadow, spr_starlord_upper_shadow, spr_splash, spr_upgrade_shop_owner, spr_upgrade_shop, spr_projectile, spr_projectile_shadow, 
	spr_select_box, spr_octopus, spr_target, spr_teleporter,
	spr_standard_blaster, spr_triburst_blaster, spr_trispread_blaster, spr_assault_rifle, 
	spr_sniper, spr_shotgun, spr_rocketlauncher, spr_grenadeLauncher,
	spr_key1, spr_key2, spr_key3;

	//public static ArrayList<ClipPlayer> soundPlayerList = new ArrayList<ClipPlayer>();
	public static ClipPlayer soundPlayer = new ClipPlayer();

	/*
static Clip clickClip = null;
static Clip dwingClip = null;
static Clip coinClip = null;
static Clip cactusSplatClip = null;
static Clip enemyHitClip = null;
	 */

	public static void playSound(String fname) //Clip sound
	{
		ClipPlayer cp = soundPlayer;
		try{
			Clip newClip = cp.loadClip(fname);
			cp.playClip(newClip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void playMusic(String fname) //Clip sound
	{
		ClipPlayer cp = soundPlayer;
		try{
			Clip newClip = cp.loadClip(fname);
			newClip.loop(Clip.LOOP_CONTINUOUSLY);
			cp.playClip(newClip, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//LOAD LEVEL, GOTO LEVEL
	public static void gotoLevel(String levelName)
	{
		for(int x = 0; x < 120 ; x++)
			for(int y = 0; y < 120 ; y++)
				for(int z = 0; z < 10 ; z++)
					objects_grid[z][y][x] = null;
		//objects_grid = new GameObject[10][120][120];
		bobTheWallGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);
		joeTheNpcGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);
		collectableGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);
		interactableGrid = new BucketGrid(bucket_grid_width, bucket_grid_height);
		enemies.clear();
		projectiles.clear();
		projectiles_shadows.clear();
		gameObjects.clear();
		effects.clear();
		enemies_healthbar_back.clear();
		enemies_healthbar_front.clear();


		//LOAD LEVEL, LOAD MAP, LOAD LAYERS
		int c[][] = LevelMap.loadLayer(new File(".\\maps\\"+levelName+"\\bgd_layer00.csv"));
		for(int ix = 0 ; ix < game_htiles ; ix++)
		{
			for(int iy = 0 ; iy < game_vtiles ; iy++)
			{
				background[iy][ix] = c[iy][ix];
			}
		}
		c = LevelMap.loadLayer(new File(".\\maps\\"+levelName+"\\obj_layer00.csv"));
		for(int ix = 0 ; ix < game_htiles ; ix++)
		{
			for(int iy = 0 ; iy < game_vtiles ; iy++)
			{
				objects_to_create[0][iy][ix] = c[iy][ix];
			}
		}

		c = LevelMap.loadLayer(new File(".\\maps\\"+levelName+"\\obj_layer01.csv"));
		for(int ix = 0 ; ix < game_htiles ; ix++)
		{
			for(int iy = 0 ; iy < game_vtiles ; iy++)
			{
				objects_to_create[1][iy][ix] = c[iy][ix];
			}
		}

		c = LevelMap.loadLayer(new File(".\\maps\\"+levelName+"\\obj_layer02.csv"));
		for(int ix = 0 ; ix < game_htiles ; ix++)
		{
			for(int iy = 0 ; iy < game_vtiles ; iy++)
			{
				objects_to_create[2][iy][ix] = c[iy][ix];
			}
		}

		c = LevelMap.loadLayer(new File(".\\maps\\"+levelName+"\\obj_layer03.csv"));
		for(int ix = 0 ; ix < game_htiles ; ix++)
		{
			for(int iy = 0 ; iy < game_vtiles ; iy++)
			{
				objects_to_create[3][iy][ix] = c[iy][ix];
			}
		}

		//ADD OBJECTS, CREATE OBJECTS
		GameObject newGameObject;

		//int[][] objects_to_create_temp = new int[game_vtiles][game_htiles];
		int create_z = 0;

		//objects_to_create2[][] = new int[game_vtiles][game_htiles];
		int maxEnemies = 100;
		int enemiesCreated = 0;
		for(int iz = 0 ; iz < game_ztiles ; iz++)
			for(int ix = 0 ; ix < game_htiles ; ix++)
				for(int iy = 0 ; iy < game_vtiles ; iy++)
				{
					create_z = iz * 32;
					//CREATE CACTUS 1s
					if (objects_to_create[iz][iy][ix] == 1) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_cactus);
						newGameObject.x = ix * tile_width;
						newGameObject.y = iy * tile_height;
						newGameObject.z = create_z;
						newGameObject.depth = -(newGameObject.y+newGameObject.h+newGameObject.z);

						newGameObject.maxhealth = 1;
						newGameObject.health = 1;

						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						bobTheWallGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);

						objects_grid[iz][iy][ix] = newGameObject;
					} 
					//CREATE CACTUS 2s
					if (objects_to_create[iz][iy][ix] == 2) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_cactus2);
						newGameObject.x = ix * tile_width;
						newGameObject.y = iy * tile_height;
						newGameObject.z = create_z;
						newGameObject.sprite_index.yorig = 32;
						newGameObject.depth = -(newGameObject.y+newGameObject.h);

						newGameObject.maxhealth = 1;
						newGameObject.health = 1;

						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						bobTheWallGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);

						objects_grid[iz][iy][ix] = newGameObject;
					}

					//CREATE TREES
					if (objects_to_create[iz][iy][ix] == 6) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_tree);
						newGameObject.x = ix * tile_width;
						newGameObject.y = iy * tile_height;
						newGameObject.z = create_z;
						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig);

						newGameObject.maxhealth = 10;
						newGameObject.health = 10;

						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						bobTheWallGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);

						objects_grid[iz][iy][ix] = newGameObject;
					}

					//CREATE COINS
					if (objects_to_create[iz][iy][ix] == 16) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_coin);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;

						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						newGameObject.image_speed = .01;
						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						collectableGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);
					}

					//CREATE KEY1
					if (objects_to_create[iz][iy][ix] == 23) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_key1);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;

						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						collectableGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);
					}

					//CREATE KEY2
					if (objects_to_create[iz][iy][ix] == 24) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_key2);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;

						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						collectableGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);
					}

					//CREATE KEY3
					if (objects_to_create[iz][iy][ix] == 25) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_key3);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;

						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						collectableGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);
					}
					

					//SET PLAYER POSITION
					if (objects_to_create[iz][iy][ix] == 26) 
					{
						player.x = ix * tile_width;
						player.x = iy * tile_height;
						player.x = iz * tile_height;
					}

					//CREATE ENEMIES
					//if (false)
					if (enemiesCreated < maxEnemies)
						if (objects_to_create[iz][iy][ix] == 17) 
						{
							enemiesCreated++;
							Enemy newEnemy = new Enemy();
							newEnemy.setSprite(spr_octopus);
							newEnemy.x = ix * tile_width + tile_width/2;
							newEnemy.y = iy * tile_height + tile_height/2;
							newEnemy.z = create_z;
							newEnemy.depth = -(newEnemy.y + newEnemy.sprite_index.yorig + newEnemy.z);
							newEnemy.image_speed = .01;
							newEnemy.maxhealth = 5;
							newEnemy.health = 5;
							int gridX = (int) (newEnemy.x / tile_width) / bucket_width;
							int gridY = (int) (newEnemy.y / tile_height) / bucket_height;

							enemies.add(newEnemy);
							gameObjects.add(newEnemy);

							//CREATE NPC HEALTH BACKGROUND OBJECT
							GameObject npc_health_back = new GameObject();
							npc_health_back.setSprite(spr_enemy_healthback);

							//CREATE NPC HEALTH FOREGROUND OBJECT
							GameObject npc_health_front = new GameObject();
							npc_health_front.setSprite(spr_enemy_healthfront);

							//CREATE NPC SHADOW
							GameObject npc_shadow = new GameObject();
							npc_shadow.setSprite(spr_magikarp_shadow);

							enemies_healthbar_back.add(npc_health_back);
							enemies_healthbar_front.add(npc_health_front);
							enemies_shadows.add(npc_shadow);
						}

					//18 = health
					//CREATE HEALTH BONUSES
					if (objects_to_create[iz][iy][ix] == 18) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_health_bonus);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;
						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						newGameObject.image_speed = .01;
						
						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						collectableGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);
					}

					//19 = ammo
					if (objects_to_create[iz][iy][ix] == 19) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_ammo_bonus);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;
						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						newGameObject.image_speed = .01;
						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						collectableGrid.add(gridX, gridY, newGameObject);
						gameObjects.add(newGameObject);
					}


					//20 = weapons shop
					if (objects_to_create[iz][iy][ix] == 20) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_upgrade_shop);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;
						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						newGameObject.invincible = true;

						gameObjects.add(newGameObject);
						objects_grid[iz][iy][ix] = newGameObject;

						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						bobTheWallGrid.add(gridX, gridY, newGameObject);
					}

					//21 = weapons shop owner
					if (objects_to_create[iz][iy][ix] == 21) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_upgrade_shop_owner);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;
						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						newGameObject.invincible = true;

						gameObjects.add(newGameObject);
						objects_grid[iz][iy][ix] = newGameObject;

						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						interactableGrid.add(gridX, gridY, newGameObject);
					}

					//22 = portal, teleporter
					if (objects_to_create[iz][iy][ix] == 22) 
					{
						newGameObject = new GameObject();
						newGameObject.setSprite(spr_teleporter);
						newGameObject.x = ix * tile_width + tile_width/2;
						newGameObject.y = iy * tile_height + tile_height/2;
						newGameObject.z = create_z;
						newGameObject.depth = -(newGameObject.y + newGameObject.sprite_index.yorig + newGameObject.z);
						newGameObject.invincible = true;

						gameObjects.add(newGameObject);
						objects_grid[iz][iy][ix] = newGameObject;

						int gridX = (int) (newGameObject.x / tile_width) / bucket_width;
						int gridY = (int) (newGameObject.y / tile_height) / bucket_height;

						interactableGrid.add(gridX, gridY, newGameObject);
					}

					//CREATE WALLS
					if (objects_to_create[iz][iy][ix] == 3 || objects_to_create[iz][iy][ix] == 4 || objects_to_create[iz][iy][ix] == 5 
							|| (objects_to_create[iz][iy][ix] >= 7 && objects_to_create[iz][iy][ix] <= 15)) 
					{
						GameObject wall = new GameObject();

						wall.maxhealth = 10;
						wall.health = 10;

						if (objects_to_create[iz][iy][ix] >= 7 && objects_to_create[iz][iy][ix] <= 15)
						{
							wall.setSprite(spr_wallgrey1);
							wall.image_index = objects_to_create[iz][iy][ix] - 7;
						}
						else
						{
							wall.setSprite(spr_wall);
						}

						wall.x = ix * tile_width;
						wall.y = iy * tile_height;
						wall.z = create_z;
						//wall.z = 32 * (objects_to_create[iy][ix] - 3);

						wall.depth = -(wall.y+wall.h/2)-wall.z;
						wall.setShadow(spr_wall_shadow);

						int gridX = (int) (wall.x / tile_width) / bucket_width;
						int gridY = (int) (wall.y / tile_height) / bucket_height;

						bobTheWallGrid.add(gridX, gridY, wall);
						gameObjects.add(wall);

						if (iz > 0)
						{
							if (objects_grid[iz-1][iy][ix] != null)
							{
								wall.ground_level = iz * tile_height;
							}
						}
						else
						{
							wall.ground_level = 0;
						}

						objects_grid[iz][iy][ix] = wall;
					}
				}
		/*
			//Key 1
			int key1_x = (int) (Math.random() * game_htiles);
			int key1_y = (int) (Math.random() * game_vtiles);
			int key1_z;

			for(key1_z = game_ztiles - 2 ; key1_z > 0 ; key1_z--)
			{
				if (objects_grid[key1_z][key1_y][key1_x] != null)
				{
					key1_z++;
					break;
				}
			}
			key1.x = key1_x * tile_width;
			key1.y = key1_y * tile_height;
			key1.z = key1_z * tile_height;

			//Key 2
			key1_x = (int) (Math.random() * game_htiles);
			key1_y = (int) (Math.random() * game_vtiles);

			for(key1_z = game_ztiles - 2 ; key1_z > 0 ; key1_z--)
			{
				if (objects_grid[key1_z][key1_y][key1_x] != null)
				{
					key1_z++;
					break;
				}
			}
			key1.x = key1_x * tile_width;
			key1.y = key1_y * tile_height;
			key1.z = key1_z * tile_height;

			//Key 3
			key1_x = (int) (Math.random() * game_htiles);
			key1_y = (int) (Math.random() * game_vtiles);

			for(key1_z = game_ztiles - 2 ; key1_z > 0 ; key1_z--)
			{
				if (objects_grid[key1_z][key1_y][key1_x] != null)
				{
					key1_z++;
					break;
				}
			}
			key1.x = key1_x * tile_width;
			key1.y = key1_y * tile_height;
			key1.z = key1_z * tile_height;

			collectableGrid.add(key1_x, key1_y, key1);
			gameObjects.add(key1);
			collectableGrid.add(key1_x, key1_y, key2);
			gameObjects.add(key2);
			collectableGrid.add(key1_x, key1_y, key3);
			gameObjects.add(key3);
		 */

		key1.setSprite(spr_key1);
		key2.setSprite(spr_key2);
		key3.setSprite(spr_key3);

	}

	//MAIN/////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {

		pewClip = null;

		levels.add("LEVEL1");
		levels.add("LEVEL2");
		levels.add("LEVEL3");
		levels.add("LEVEL4");

		//store.add()

		//soundPlayer.playClip(clickClip);

		int fps = 60;
		double testDir = 0;

		Camera cam = new Camera(0, 0, window_width, window_height);

		GLProfile gl2Profile;

		try {
			// Make sure we have a recent version of OpenGL
			gl2Profile = GLProfile.get(GLProfile.GL2);
		}
		catch (GLException ex) {
			System.out.println("OpenGL max supported version is too low.");
			System.exit(1);
			return;
		}

		// Create the window and OpenGL context.
		window = GLWindow.create(new GLCapabilities(gl2Profile));
		window.setSize(window_width, window_height);
		window.setTitle("Abducted by Aliens");
		window.setVisible(true);
		window.setResizable(false);
		window.setAlwaysOnTop(true);
		
		window.setDefaultCloseOperation(
				WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);

		mousePre.x = mouse.x;
		mousePre.y = mouse.y;
		mouse = getMouseLocation(window);

		mouse.x *= cam.w;
		mouse.x /= window.getWidth();
		mouse.y *= cam.h;
		mouse.y /= window.getHeight();

		window.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.isAutoRepeat()) {
					return;
				}

				//kbStatePressed[keyEvent.getKeyCode()] = false;
				mbState[mouseEvent.getButton()] = true;
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (mouseEvent.isAutoRepeat()) {
					return;
				}
				mbState[mouseEvent.getButton()] = false;
				//kbStatePressed[keyEvent.getKeyCode()] = false;

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseWheelMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		window.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.isAutoRepeat()) {
					return;
				}

				//kbStatePressed[keyEvent.getKeyCode()] = false;

				kbState[keyEvent.getKeyCode()] = true;
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.isAutoRepeat()) {
					return;
				}
				kbState[keyEvent.getKeyCode()] = false;
				//kbStatePressed[keyEvent.getKeyCode()] = false;

			}
		});

		// Setup OpenGL state.
		window.getContext().makeCurrent();
		gl = window.getGL().getGL2();
		gl.glViewport(0, 0, window_width, window_height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glOrtho(0, window_width, window_height, 0, 0, 100);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		//LOAD FONT
		for(int i = 0 ; i < alphabet.length() ; i++ )
			//LOAD NUMBERS AND LETTERS
			spr_alphabet.addAnimationFrame(glTexImageTGAFile(gl, "text_font/"+alphabet.charAt(i)+".tga", spriteSize) , spriteSize[0], spriteSize[1]);

		//COLON
		spr_alphabet.addAnimationFrame(glTexImageTGAFile(gl, "text_font/colon.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		//DOT / PERIOD / DECIMAL
		spr_alphabet.addAnimationFrame(glTexImageTGAFile(gl, "text_font/dot.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		//LOAD AND INITIALIZE SPRITES
		spr_target = new Sprite();
		spr_target.addAnimationFrame(glTexImageTGAFile(gl, "spr_target.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_target.xorig = spr_target.width / 2;
		spr_target.yorig = spr_target.height / 2;

		spr_magikarp = new Sprite();
		spr_magikarp.addAnimationFrame(glTexImageTGAFile(gl, "mag1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_magikarp.addAnimationFrame(glTexImageTGAFile(gl, "mag2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_magikarp.addAnimationFrame(glTexImageTGAFile(gl, "mag3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_magikarp.addAnimationFrame(glTexImageTGAFile(gl, "mag4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_magikarp.xorig = spr_magikarp.width / 2;
		spr_magikarp.yorig = spr_magikarp.height * 3 / 4;

		//KEYS SPRITES
		spr_key1 = new Sprite();
		spr_key1.addAnimationFrame(glTexImageTGAFile(gl, "keys/key1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_key1.xorig = 16;
		spr_key1.yorig = 16;

		spr_key2 = new Sprite();
		spr_key2.addAnimationFrame(glTexImageTGAFile(gl, "keys/key2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_key2.xorig = 16;
		spr_key2.yorig = 16;

		spr_key3 = new Sprite();
		spr_key3.addAnimationFrame(glTexImageTGAFile(gl, "keys/key3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_key3.xorig = 16;
		spr_key3.yorig = 16;

		//WEAPONS SPRITES
		spr_standard_blaster = new Sprite();
		spr_standard_blaster.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_standard_blaster.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_standard_blaster.xorig = spr_standard_blaster.width/2;
		spr_standard_blaster.yorig = spr_standard_blaster.height/2;

		spr_triburst_blaster = new Sprite();
		spr_triburst_blaster.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_triburst_blaster.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_triburst_blaster.xorig = spr_triburst_blaster.width/2;
		spr_triburst_blaster.yorig = spr_triburst_blaster.height/2;

		spr_trispread_blaster = new Sprite();
		spr_trispread_blaster.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_trispread_blaster.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_trispread_blaster.xorig = spr_trispread_blaster.width/2;
		spr_trispread_blaster.yorig = spr_trispread_blaster.height/2;

		spr_assault_rifle = new Sprite();
		spr_assault_rifle.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_assault_rifle.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_assault_rifle.xorig = spr_assault_rifle.width/2;
		spr_assault_rifle.yorig = spr_assault_rifle.height/2;

		spr_sniper = new Sprite();
		spr_sniper.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_sniper.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_sniper.xorig = spr_sniper.width/2;
		spr_sniper.yorig = spr_sniper.height/2;

		spr_shotgun = new Sprite();
		spr_shotgun.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_shotgun.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_shotgun.xorig = spr_shotgun.width/2;
		spr_shotgun.yorig = spr_shotgun.height/2;

		spr_rocketlauncher = new Sprite();
		spr_rocketlauncher.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_rocketlauncher.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_rocketlauncher.xorig = spr_rocketlauncher.width/2;
		spr_rocketlauncher.yorig = spr_rocketlauncher.height/2;

		spr_grenadeLauncher = new Sprite();
		spr_grenadeLauncher.addAnimationFrame(glTexImageTGAFile(gl, "weapons/spr_grenade_launcher.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_grenadeLauncher.xorig = spr_grenadeLauncher.width/2;
		spr_grenadeLauncher.yorig = spr_grenadeLauncher.height/2;

		spr_octopus = new Sprite();
		spr_octopus.addAnimationFrame(glTexImageTGAFile(gl, "octopus_enemy/oct1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_octopus.addAnimationFrame(glTexImageTGAFile(gl, "octopus_enemy/oct2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_octopus.addAnimationFrame(glTexImageTGAFile(gl, "octopus_enemy/oct3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_octopus.addAnimationFrame(glTexImageTGAFile(gl, "octopus_enemy/oct4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_octopus.xorig = spr_octopus.width / 2;
		spr_octopus.yorig = spr_octopus.height * 3 / 4;

		spr_magikarp_shadow = new Sprite();
		spr_magikarp_shadow.addAnimationFrame(glTexImageTGAFile(gl, "shadow.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_magikarp_shadow.xorig = spr_magikarp_shadow.width/2;
		spr_magikarp_shadow.yorig = spr_magikarp_shadow.height/2;

		spr_upgrade_shop = new Sprite();
		spr_upgrade_shop.addAnimationFrame(glTexImageTGAFile(gl, "spr_weapons_shop.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_upgrade_shop.xorig = 24;
		spr_upgrade_shop.yorig = 30;

		spr_upgrade_shop_owner = new Sprite();
		spr_upgrade_shop_owner.addAnimationFrame(glTexImageTGAFile(gl, "spr_weapons_shop_owner.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_upgrade_shop_owner.xorig = 14;
		spr_upgrade_shop_owner.yorig = 42;

		spr_splash = new Sprite();
		spr_splash.addAnimationFrame(glTexImageTGAFile(gl, "spr_splash0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_splash.addAnimationFrame(glTexImageTGAFile(gl, "spr_splash1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_splash.addAnimationFrame(glTexImageTGAFile(gl, "spr_splash2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_splash.xorig = spr_splash.width/2;
		spr_splash.yorig = spr_splash.height/2;

		spr_starlord = new Sprite();
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.addAnimationFrame(glTexImageTGAFile(gl, "starlord/walking/starlord7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord.xorig = 24;
		spr_starlord.yorig = 47;

		spr_starlord_legs = new Sprite();
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs.xorig = 24;
		spr_starlord_legs.yorig = 47;

		spr_starlord_legs_shadow = new Sprite();
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.addAnimationFrame(glTexImageTGAFile(gl, "starlord/legs/starlord_legs7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_legs_shadow.xorig = 24;
		spr_starlord_legs_shadow.yorig = 47;

		spr_starlord_shooting_up = new Sprite();
		spr_starlord_shooting_up.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_aim_up.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_up.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_shoot_up.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_up.xorig = 24;
		spr_starlord_shooting_up.yorig = 47;

		spr_starlord_shooting_right_up = new Sprite();
		spr_starlord_shooting_right_up.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_aim_right_up.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_right_up.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_shoot_right_up.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_right_up.xorig = 24;
		spr_starlord_shooting_right_up.yorig = 47;

		spr_starlord_shooting_right = new Sprite();
		spr_starlord_shooting_right.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_aim_right.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_right.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_shoot_right.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_right.xorig = 24;
		spr_starlord_shooting_right.yorig = 47;

		spr_starlord_shooting_right_down = new Sprite();
		spr_starlord_shooting_right_down.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_aim_right_down.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_right_down.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_shoot_right_down.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_right_down.xorig = 24;
		spr_starlord_shooting_right_down.yorig = 47;

		spr_starlord_shooting_down = new Sprite();
		spr_starlord_shooting_down.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_aim_down.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_down.addAnimationFrame(glTexImageTGAFile(gl, "starlord/shooting/starlord_shoot_down.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_starlord_shooting_down.xorig = 24;
		spr_starlord_shooting_down.yorig = 47;

		spr_cactus = new Sprite();
		spr_cactus.addAnimationFrame(glTexImageTGAFile(gl, "cactus.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus.xorig = 0; //17;
		spr_cactus.yorig = 16;

		spr_cactus2 = new Sprite();
		spr_cactus2.addAnimationFrame(glTexImageTGAFile(gl, "cactus2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus2.xorig = 0; //14;
		spr_cactus2.yorig = 42;

		spr_tree = new Sprite();
		spr_tree.addAnimationFrame(glTexImageTGAFile(gl, "tree_large.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_tree.xorig = 16;
		spr_tree.yorig = 42;

		spr_wall = new Sprite();
		spr_wall.addAnimationFrame(glTexImageTGAFile(gl, "wall.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wall.yorig = 32;

		spr_teleporter = new Sprite();
		spr_teleporter.addAnimationFrame(glTexImageTGAFile(gl, "spr_teleporter.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_teleporter.xorig = 20;
		spr_teleporter.yorig = 44;

		spr_select_box = new Sprite();
		spr_select_box.addAnimationFrame(glTexImageTGAFile(gl, "select_box.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_select_box.xorig = spr_select_box.width/2;
		spr_select_box.yorig = spr_select_box.height/2;

		spr_wallgrey1 = new Sprite();
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_left_up.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_up.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_right_up.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_right.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_right_down.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_down.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_left_down.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.addAnimationFrame(glTexImageTGAFile(gl, "walls/wallgrey1_left.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_wallgrey1.yorig = 32;

		spr_wall_shadow = new Sprite();
		spr_wall_shadow.addAnimationFrame(glTexImageTGAFile(gl, "wall_shadow.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		spr_platform = new Sprite();
		spr_platform.addAnimationFrame(glTexImageTGAFile(gl, "spr_platform.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		Sprite spr_platform_shadow = new Sprite();
		spr_platform_shadow.addAnimationFrame(glTexImageTGAFile(gl, "spr_platform_shadow.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		spr_projectile = new Sprite();
		spr_projectile.addAnimationFrame(glTexImageTGAFile(gl, "redlaser.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_projectile.xorig = spr_projectile.width/2;
		spr_projectile.yorig = spr_projectile.height/2;
		System.out.println(spr_projectile.width);

		spr_projectile_shadow = new Sprite();
		spr_projectile_shadow.addAnimationFrame(glTexImageTGAFile(gl, "projectile_shadow.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_projectile_shadow.xorig = spr_projectile_shadow.width/2;
		spr_projectile_shadow.yorig = spr_projectile_shadow.height/2;

		spr_enemy_healthback = new Sprite();
		spr_enemy_healthback.addAnimationFrame(glTexImageTGAFile(gl, "enemy_healthbar_background.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		spr_enemy_healthfront = new Sprite();
		spr_enemy_healthfront.addAnimationFrame(glTexImageTGAFile(gl, "enemy_healthbar_foreground.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		Sprite spr_qmark = new Sprite();
		spr_qmark.addAnimationFrame(glTexImageTGAFile(gl, "qmark.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		Sprite spr_helpshootlabel = new Sprite();
		spr_helpshootlabel.addAnimationFrame(glTexImageTGAFile(gl, "press_z_to_shoot.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		spr_hud_back.addAnimationFrame(glTexImageTGAFile(gl, "hud_background.tga", spriteSize) , spriteSize[0], spriteSize[1]);

		spr_explosion = new Sprite();
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.addAnimationFrame(glTexImageTGAFile(gl, "explosion/exp8.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_explosion.xorig = 16; // spr_explosion.width/2;
		spr_explosion.yorig = 16; //spr_explosion.height/2;

		spr_cactus_explosion = new Sprite();
		spr_cactus_explosion.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion/cactsplosion0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion/cactsplosion1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion/cactsplosion2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion/cactsplosion3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion/cactsplosion4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion/cactsplosion5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion/cactsplosion6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion.xorig = 34;
		spr_cactus_explosion.yorig = 52;

		spr_cactus_explosion_large = new Sprite();
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.addAnimationFrame(glTexImageTGAFile(gl, "cactsplosion_large/cactsplosion_large7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_cactus_explosion_large.xorig = 30;
		spr_cactus_explosion_large.yorig = 48;

		spr_coin = new Sprite();
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin8.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin9.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin10.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin11.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin12.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin13.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin13_5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.addAnimationFrame(glTexImageTGAFile(gl, "gold_coin/coin14.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_coin.xorig = 16;
		spr_coin.yorig = 22;

		spr_health_bonus = new Sprite();
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus8.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus9.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus10.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus11.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.addAnimationFrame(glTexImageTGAFile(gl, "health_bonus/health_bonus12.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_health_bonus.xorig = 16;
		spr_health_bonus.yorig = 22;

		spr_ammo_bonus = new Sprite();
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus0.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus1.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus2.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus3.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus4.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus5.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus6.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus7.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus8.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus9.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus10.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus11.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus12.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.addAnimationFrame(glTexImageTGAFile(gl, "ammo_bonus/ammo_bonus13.tga", spriteSize) , spriteSize[0], spriteSize[1]);
		spr_ammo_bonus.xorig = 16;
		spr_ammo_bonus.yorig = 22;

		//Load Backgrounds
		backgroundIDS[0] = glTexImageTGAFile(gl, "water.tga", backgroundSize2);
		backgroundIDS[1] = glTexImageTGAFile(gl, "land.tga", backgroundSize);

		backgroundIDS[2] = glTexImageTGAFile(gl, "land_left_up.tga", backgroundSize);
		backgroundIDS[3] = glTexImageTGAFile(gl, "land_up.tga", backgroundSize);
		backgroundIDS[4] = glTexImageTGAFile(gl, "land_right_up.tga", backgroundSize);
		backgroundIDS[5] = glTexImageTGAFile(gl, "land_right.tga", backgroundSize);
		backgroundIDS[6] = glTexImageTGAFile(gl, "land_right_down.tga", backgroundSize);
		backgroundIDS[7] = glTexImageTGAFile(gl, "land_down.tga", backgroundSize);
		backgroundIDS[8] = glTexImageTGAFile(gl, "land_left_down.tga", backgroundSize);
		backgroundIDS[9] = glTexImageTGAFile(gl, "land_left.tga", backgroundSize);

		backgroundIDS[10] = glTexImageTGAFile(gl, "grass.tga", spriteSize);

		backgroundIDS[11] = glTexImageTGAFile(gl, "grass_left_up.tga", backgroundSize);
		backgroundIDS[12] = glTexImageTGAFile(gl, "grass_up.tga", backgroundSize);
		backgroundIDS[13] = glTexImageTGAFile(gl, "grass_right_up.tga", backgroundSize);
		backgroundIDS[14] = glTexImageTGAFile(gl, "grass_right.tga", backgroundSize);
		backgroundIDS[15] = glTexImageTGAFile(gl, "grass_right_down.tga", backgroundSize);
		backgroundIDS[16] = glTexImageTGAFile(gl, "grass_down.tga", backgroundSize);
		backgroundIDS[17] = glTexImageTGAFile(gl, "grass_left_down.tga", backgroundSize);
		backgroundIDS[18] = glTexImageTGAFile(gl, "grass_left.tga", backgroundSize);

		backgroundIDS[19] = glTexImageTGAFile(gl, "lava_sprites/lava.tga", backgroundSize);
		backgroundIDS[20] = glTexImageTGAFile(gl, "lava_sprites/lava_left_up.tga", backgroundSize);
		backgroundIDS[21] = glTexImageTGAFile(gl, "lava_sprites/lava_up.tga", backgroundSize);
		backgroundIDS[22] = glTexImageTGAFile(gl, "lava_sprites/lava_right_up.tga", backgroundSize);
		backgroundIDS[23] = glTexImageTGAFile(gl, "lava_sprites/lava_right.tga", backgroundSize);
		backgroundIDS[24] = glTexImageTGAFile(gl, "lava_sprites/lava_right_down.tga", backgroundSize);
		backgroundIDS[25] = glTexImageTGAFile(gl, "lava_sprites/lava_down.tga", backgroundSize);
		backgroundIDS[26] = glTexImageTGAFile(gl, "lava_sprites/lava_left_down.tga", backgroundSize);
		backgroundIDS[27] = glTexImageTGAFile(gl, "lava_sprites/lava_left.tga", backgroundSize);

		//WEAPONS

		Weapon standardBlaster = new Weapon("STANDARD BLASTER");
		standardBlaster.setSprite(JavaTemplate.spr_standard_blaster);
		standardBlaster.setAngleRange(10);
		standardBlaster.setReloadTime(150);
		standardBlaster.setDamage(4);

		Weapon triBurstBlaster = new Weapon("BURST BLASTER");
		triBurstBlaster.setSprite(spr_triburst_blaster);
		triBurstBlaster.setBursts(3);
		triBurstBlaster.setBurstTime(50);

		Weapon triSpreadBlaster = new Weapon("SPREAD BLASTER");
		triSpreadBlaster.setSprite(spr_trispread_blaster);
		triSpreadBlaster.setProjectilesPerShot(5);
		triSpreadBlaster.setReloadTime(400);
		triSpreadBlaster.setDamage(2);

		Weapon assaultRifle = new Weapon("ASSAULT RIFLE");
		assaultRifle.setSprite(spr_assault_rifle);
		assaultRifle.setReloadTime(50);
		assaultRifle.setAngleRange(20);

		Weapon sniperBlaster = new Weapon("SNIPER BLASTER");
		sniperBlaster.setSprite(spr_sniper);
		sniperBlaster.setReloadTime(600);
		sniperBlaster.setAngleRange(1);
		sniperBlaster.setDamage(10);

		Weapon shotgun = new Weapon("SHOTGUN");
		shotgun.setSprite(spr_shotgun);
		shotgun.setAngleRange(1);
		shotgun.setProjectilesPerShot(3);

		Weapon rocketLauncher = new Weapon("ROCKET LAUNCHER");
		rocketLauncher.setSprite(spr_rocketlauncher);
		rocketLauncher.setReloadTime(2000);
		rocketLauncher.setDamage(100);

		Weapon grenadeLauncher = new Weapon("GRENADE LAUNCHER");
		grenadeLauncher.setSprite(spr_grenadeLauncher);
		grenadeLauncher.setReloadTime(2000);
		grenadeLauncher.setDamage(50);


		player.primaryWeapon = standardBlaster;
		inventory.add(standardBlaster);
		//inventory.add(shotgun);
		//inventory.add(sniperBlaster);
		//inventory.add(triSpreadBlaster);
		//inventory.add(triBurstBlaster);
		//inventory.add(assaultRifle);
		/*
		InventoryObject obj_key1 = new InventoryObject();
		InventoryObject obj_key2 = new InventoryObject();
		InventoryObject obj_key3 = new InventoryObject();
		obj_key1.setSprite(spr_key1);
		obj_key2.setSprite(spr_key2);
		obj_key3.setSprite(spr_key3);
		obj_key1.name = "BLUE KEY";
		obj_key2.name = "RED KEY";
		obj_key3.name = "YELLOW KEY";
		inventory.add(obj_key1);
		inventory.add(obj_key2);
		inventory.add(obj_key3);*/
		
		/*
    	inventory.add(triBurstBlaster);
    	inventory.add(triSpreadBlaster);
    	inventory.add(assaultRifle);
    	inventory.add(sniperBlaster);
    	inventory.add(shotgun);
    	inventory.add(rocketLauncher);
    	inventory.add(grenadeLauncher);
		 */

		gotoLevel("LEVEL"+(current_level+1));

		//ADD GAME OBJECTS TO THE GAME

		//ADD PLAYER
		player.gravity_z = .1;
		player.setSprite(spr_starlord_legs);
		player.x = game_width/2;
		player.y = game_height/3;
		player.xstart = player.x;
		player.ystart = player.y;

		player_upper_body.setSprite(spr_starlord_shooting_right);
		player_upper_body.x = player.x;
		player_upper_body.y = player.y;
		player_upper_body.z = player.z;

		//ADD PLAYER SHADOW
		GameObject shadow = new GameObject();
		shadow.setSprite(spr_starlord_legs_shadow);

		spr_starlord_upper_shadow = player_upper_body.sprite_index;
		GameObject shadow_upper = new GameObject();
		shadow_upper.setSprite(spr_starlord_upper_shadow);

//		//CREATE HELP BUBBLE OBJECT
//		GameObject obj_help_shoot = new GameObject();
//		obj_help_shoot.setSprite(spr_qmark);
//
//		//CREATE HELP BUBBLE LABEL OBJECT
//		GameObject obj_help_shoot_label = new GameObject();
//		obj_help_shoot_label.setSprite(spr_helpshootlabel);
//
//		//SET HELP BUBBLE COORDINATES
//		obj_help_shoot.x = game_width / 2 ;
//		obj_help_shoot.y = game_height / 2 - 540;
//		obj_help_shoot.depth = -(obj_help_shoot.y + obj_help_shoot.h+16);
//
//		//SET HELP BUBBLE LABEL COORDINATES
//		obj_help_shoot_label.x = obj_help_shoot.x - obj_help_shoot_label.w/2 + obj_help_shoot.w / 2;
//		obj_help_shoot_label.y = obj_help_shoot.y;
//		obj_help_shoot_label.z = 48;
//		obj_help_shoot_label.visible = false;
//		obj_help_shoot_label.depth = -(obj_help_shoot_label.y + obj_help_shoot_label.h + obj_help_shoot_label.z);

		//CREATE FLOATING PLATFORM OBJECT
		GameObject platform = new GameObject();
		platform.setSprite(spr_platform);

		//CREATE FLOATING PLATFORM SHADOW OBJECT
		GameObject platform_shadow = new GameObject();
		platform_shadow.setSprite(spr_platform_shadow);

		//SET FLOATING PLATFORM OBJECT COORDINATES, DEPTH AND VELOCITY
		platform.x = game_width/3;
		platform.y = game_height/3;
		platform.z = 40;
		platform.depth = -platform.y - platform.z;
		platform_shadow.setX(32 * 11 + 10);
		platform_shadow.setY(32 * 11 + 10);
		platform.vz = 1;

		//INITIALIZE DELTA TIME VARIABLES
		long lastFrameNS, lastPhysicsFrameNS;
		long curFrameNS = System.nanoTime();
		long curPhysicsFrameNS = System.nanoTime();

		//Start music
		playMusic("Mega_Hyper_Ultrastorm.wav");



		//5//////////////////////////////////////////////////////////////////////////



		// The game loop
		while (!shouldExit) 
		{
			if (!window.hasFocus())
			{
				gamePaused = true;
			}
			//PAUSE THE GAME WHEN ESC KEY IS PRESSED
			if ((kbState[KeyEvent.VK_ESCAPE] && kbPrevState[KeyEvent.VK_ESCAPE] == false)
					|| (kbPrevState[KeyEvent.VK_P] == false && kbState[KeyEvent.VK_P])) 
			{
				gamePaused = !gamePaused;
				storeOpen = false;
				menuTab = PAUSEMENU_TAB_HOME;
				//shouldExit = true;
			}
			GameObject selectedInteractable = new GameObject();

			//The main menu
			int H = 64;

			int W = cam.w - 64;
			int wH = H * 5+32;

			int X = (int)32;
			int Y = (int)cam.h/2-wH/2-32;

			int xof = W / 4, yof = 32;

			if(!startGame)
			{
				//drawRectangle(X,Y + yof,X + W, Y + yof + H);
				drawRectangle(75,100,cam.w-75, (int)cam.h-100);
				drawRectangle(70,95,cam.w-70, (int)cam.h-95);
				
				String tstr = "YOU WERE ABDUCTED BY ALIENS#"
				+"BUT THEY MADE A FATAL MISTAKE#"
				+"THEY LEFT YOU WITH YOUR BLASTER PISTOL#"
				+"YOU USE IT TO BLAST OPEN AND ESCAPE THE CAGE#"
				+"BUT NOW THE ALIENS SEE YOU AS A THREAT#"
				+"NOW YOU MUST FIGHT YOUR WAY OUT AND#"
				+"FIND A WAY TO ESCAPE BACK TO EARTH##";
				//+"PRESS SPACE TO START";
				drawText(tstr, 110, cam.h / 2 - 256 ); //Y+yof+16
				//cam.w/2-stringWidth(tstr)/2
				//FIND THE THREE KEYS TO THE PORTAL TO ESCAPE BACK TO EARTH#
				
				if (drawButton(spr_hud_back, cam.w/2-128, cam.h-192, 256, 64, "START GAME"))
				{
					startGame = true;	
				}
				
				if(kbState[KeyEvent.VK_ENTER])
				{	
					startGame = true;	
				}
			}

			if (kbState[KeyEvent.VK_R]) {
				enemies.get(0).x = player.x-300;
				enemies.get(0).y = player.y+200;
			}

			//Copy current keyboard state to previous keyboard state array
			System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);

			//Copy current keyboard state to previous keyboard state array
			System.arraycopy(mbState, 0, mbPrevState, 0, mbState.length);


			lastFrameNS = curFrameNS;
			lastPhysicsFrameNS = curFrameNS;
			curFrameNS = System.nanoTime();
			curPhysicsFrameNS = System.nanoTime();

			//Update mouse coordinates
			mousePre.x = mouse.x;
			mousePre.y = mouse.y;
			mouse = getMouseLocation(window);

			double deltaTimeMS = (curFrameNS - lastFrameNS) / 1000000;
			double physicsDeltaMs = (curPhysicsFrameNS - lastPhysicsFrameNS) / 1000000, totalPhysicsDeltaMs = 0;

			//Switch to mouse mode if the player moves the mouse
			if (distance(mouse.x,mouse.y,mousePre.x,mousePre.y) > 2 * deltaTimeMS && !gamePaused)
			{
				controlMode = ControlMode.MOUSE_WASD;
			}

			//FPS CALCULATION
			int current_fps = (int) (1000 / deltaTimeMS);
			float smoothing = 0.99f; // larger=more smoothing
			fps = (int) ((fps * smoothing) + (current_fps * (1.0-smoothing)));

			// Actually, this runs the entire OS message pump.
			window.display();

			if (!window.isVisible()) {
				shouldExit = true;
				break;
			}

/*
			if (kbState[KeyEvent.VK_N] && !kbPrevState[KeyEvent.VK_N]) {
				gotoLevel("LEVEL" + current_level++);
			}
*/
			//Physics update
			/*
            do {
			lastPhysicsFrameNS = curPhysicsFrameNS;
			curPhysicsFrameNS = System.nanoTime();
			physicsDeltaMs = (curPhysicsFrameNS - lastPhysicsFrameNS) / 1000000;

	       	totalPhysicsDeltaMs += physicsDeltaMs;
			 */
			//CLEAR GAMEOBJECTS LIST AT THE BEGGINING OF EVERY FRAME
			gameObjects.clear();
			gameObjects.add(shadow);
			gameObjects.add(shadow_upper);
			gameObjects.add(player);
			gameObjects.add(player_upper_body);

			int player_grid_x = (int) player.x / tile_width;
			int player_grid_y = (int) player.y / tile_height;

			//Check if player is in lava
			if (background[player_grid_y][player_grid_x] >= 19 && background[player_grid_y][player_grid_x] <= 27 && player.z <= 0)
			{
				if (player.health > 0)
					player.health -= .01 * deltaTimeMS;
			}
			boolean player_is_in_water;
			//Check if player is in the water
			if ((background[player_grid_y][player_grid_x] == 0) && player.z <= 0)
			{
				player_is_in_water = true;
				if (player.vz < 0)
				{
					playSound("snd_splash.wav");
					//createEffect(Sprite effectSprite, double playSpeed, int x, int y, int z, double vx, double vy, double vz)
					for(int i = 0 ; i < 20 ; i++)
					{
						GameObject ne = createEffect(spr_splash,.001,(int)(player.x+Math.random()*20-10),(int)(player.y+Math.random()*20-10),(int)Math.max(player.z+20,20),
								(Math.random()-.5)*2,(Math.random()-.5)*2,Math.random()*sqr(Math.abs(player.vz)/10));
						ne.gravity_z = .01;
					}
				}
				//player.image_color = new Color(255,255,255,50);
				player.visible = false;
				player.move_speed = .03;
				shadow.visible = false;
				shadow_upper.visible = false;
			}
			else
			{
				player_is_in_water = false;
				player.visible = true;
				//player.image_color = new Color(255,255,255,255);
				player.move_speed = .1;
				shadow.visible = true;
				shadow_upper.visible = true;
			}

			//ADD ALL ENEMIES TO THE LIST OF GAMEOBJECTS
			for(int i = 0 ; i < enemies.size(); i++)
			{
				GameObject npc = enemies.get(i);
				gameObjects.add(npc);
				gameObjects.add(enemies_healthbar_back.get(i));
				gameObjects.add(enemies_healthbar_front.get(i));
				gameObjects.add(enemies_shadows.get(i));
			}

			//gameObjects.add(platform_shadow);
			//gameObjects.add(platform);

//			gameObjects.add(obj_help_shoot);
//			gameObjects.add(obj_help_shoot_label);

			//ADD ALL PROJECTILES TO GAMEOBJECTS LIST AND UPDATE THEIR DEPTHS
			for(int i=0 ; i < projectiles.size() ; i++)
			{
				if (projectiles.get(i).isEnabled)
				{
					projectiles.get(i).ground_level = 0;

					int grid_x = (int) projectiles.get(i).x / tile_width;
					int grid_y = (int) projectiles.get(i).y / tile_height;
					int grid_z = (int) projectiles.get(i).z / tile_height;

					//if the projectile goes outside the game space then delete it
					if (grid_x < 0 || grid_x >= game_htiles || grid_y < 0 || grid_y >= game_vtiles) // || grid_z < 0 || grid_z > game_ztiles
					{
						projectiles.get(i).isEnabled = false;
						projectiles_shadows.get(i).isEnabled = false;
					}
					else
						//if the projectile hits a wall delete it, projectile wall collisions, projectile collisions
						if (objects_grid[grid_z][grid_y][grid_x] != null)
						{
							//If same z as wall
							if (projectiles.get(i).z >= objects_grid[grid_z][grid_y][grid_x].z && projectiles.get(i).z < objects_grid[grid_z][grid_y][grid_x].z+32)
							{
								//CREATE EXPLOSION
								createEffect(spr_explosion, .01, (int)projectiles.get(i).x, (int)projectiles.get(i).y, (int)projectiles.get(i).z, 32, 32);

								//Get the object hit by the projectile
								GameObject hitObject = objects_grid[grid_z][grid_y][grid_x];

								//Create exploding effects if the hit object is a cactus
								if (hitObject.sprite_index == spr_cactus)
								{
									//soundPlayerList.get(currentSoundPlayer).playClip(cactusSplatClip);
									playSound("cactus_splat.wav");
									createEffect(spr_cactus_explosion, .01, (int)hitObject.x+16, (int)hitObject.y+16, (int)hitObject.z, 70, 72); //(int)projectiles.get(i).x+4, (int)projectiles.get(i).y-4

								}

								if (objects_grid[grid_z][grid_y][grid_x].sprite_index == spr_cactus2)
								{
									createEffect(spr_cactus_explosion_large, .01, (int)hitObject.x+16-1, (int)hitObject.y+16-24, (int)hitObject.z); //(int)projectiles.get(i).x+4, (int)projectiles.get(i).y-4
								}

								//Subtract health from hit object
								if (!hitObject.invincible)
									hitObject.health -= projectiles.get(i).health;;

									//Destroy the hit object if its health is 0
									if (hitObject.health <= 0)
									{
										hitObject.isEnabled = false;
										objects_grid[grid_z][grid_y][grid_x] = null;
									}

									projectiles.get(i).isEnabled = false;
									projectiles_shadows.get(i).isEnabled = false;
									projectiles.remove(i);
									projectiles_shadows.remove(i);
									continue;
							}
						}
						else
						{
							for(int j = grid_z-1 ; j >= 0 ; j--)
							{
								if (objects_grid[j][grid_y][grid_x] != null)
								{
									projectiles.get(i).ground_level = objects_grid[j][grid_y][grid_x].z + 32;
									break;
								}
							}
						}
				}
				//if the projectile still exists, add it to the list of gameobjects
				if (projectiles.get(i).isEnabled)
				{
					projectiles_shadows.get(i).x = projectiles.get(i).x;
					projectiles_shadows.get(i).y = projectiles.get(i).y;
					projectiles_shadows.get(i).z = projectiles.get(i).ground_level;
					projectiles.get(i).depth = -projectiles.get(i).y - projectiles.get(i).z;
					projectiles_shadows.get(i).depth = -projectiles_shadows.get(i).y - projectiles_shadows.get(i).z - 32;

					gameObjects.add(projectiles.get(i));
					gameObjects.add(projectiles_shadows.get(i));
				}
			}

			//ADD EXPLOSIONS
			for(int i=0 ; i < effects.size() ; i++)
			{
				if (effects.get(i).reachedAnimationEnd())
				{
					effects.get(i).isEnabled = false;
				}

				if (effects.get(i).isEnabled)
				{
					gameObjects.add(effects.get(i));
				}
			}

			//SAVE ARRAY WHEN G KEY IS PRESSED
			/*
			if (kbState[KeyEvent.VK_G] && kbPrevState[KeyEvent.VK_G] == false) {
				int[][] saveGrid = new int[120][120];
				for(int iy = 0 ; iy < saveGrid.length ; iy++)
					for(int ix = 0 ; ix < saveGrid[0].length ; ix++)
					{
						if (objects_grid[0][iy][ix] != null)
							saveGrid[iy][ix] = objects_grid[0][iy][ix].sprite_index.getTex(0);
						else
							saveGrid[iy][ix] = 0;
					}
				LevelMap.saveLayer(saveGrid, new File("test.txt"));
			}*/

			ArrayList<GameObject> bucketGameObjects = new ArrayList<GameObject>();

			int bucketX = ((int)player.x / tile_width) / bucket_width;
			int bucketY = ((int)player.y / tile_height) / bucket_height;

			//COINS BUCKETGRID
			for(int ix = -1 ; ix <= 1 ; ix++)
				for(int iy = -1 ; iy <= 1 ; iy++)
				{
					if (bucketX+ix >= 0 && bucketX+ix < bucket_grid_width)
						if (bucketY+iy >= 0 && bucketY+iy < bucket_grid_height)
						{
							bucketGameObjects = collectableGrid.getList(bucketX + ix, bucketY + iy);
							for(int i = 0 ; i < bucketGameObjects.size(); i++)
							{
								GameObject collectedItem = bucketGameObjects.get(i);
								if (distance(collectedItem.x,collectedItem.y,collectedItem.z,player.x,player.y + 8,player.z) < 50)
								{
									if (collectedItem.sprite_index == spr_coin)
									{
										createEffect(spr_coin, .01, (int)collectedItem.x, (int)collectedItem.y, (int)collectedItem.z, -10, 0, 10);
										collectedItem.isEnabled = false;
										bucketGameObjects.remove(i);
										coins_collected++;
										//soundPlayer.playClip(dwingClip);
										playSound("dwing.wav");
										i--;
									}
									if (collectedItem.sprite_index == spr_health_bonus) // && player.health < player.maxhealth
									{
										createEffect(spr_health_bonus, .01, (int)collectedItem.x, (int)collectedItem.y, (int)collectedItem.z, -10, 0, 10);
										collectedItem.isEnabled = false;
										bucketGameObjects.remove(i);
										//coins_collected++;
										//soundPlayer.playClip(dwingClip);
										playSound("dwing.wav");
										addToInventory(new InventoryObject("HEALTH",spr_health_bonus));
										/*player.health += 3;
		            		if (player.health > player.maxhealth)
		            		{
		            			player.health = player.maxhealth;
		            		}*/
										i--;
									}
									if (collectedItem.sprite_index == spr_key1) // && player.health < player.maxhealth
									{
										createEffect(spr_key1, .01, (int)collectedItem.x, (int)collectedItem.y, (int)collectedItem.z, -10, 0, 10);
										collectedItem.isEnabled = false;
										bucketGameObjects.remove(i);
										playSound("dwing.wav");
										addToInventory(new InventoryObject("BLUE KEY",spr_key1));
										i--;
									}
									if (collectedItem.sprite_index == spr_key2) // && player.health < player.maxhealth
									{
										createEffect(spr_key1, .01, (int)collectedItem.x, (int)collectedItem.y, (int)collectedItem.z, -10, 0, 10);
										collectedItem.isEnabled = false;
										bucketGameObjects.remove(i);
										playSound("dwing.wav");
										addToInventory(new InventoryObject("RED KEY",spr_key2));
										i--;
									}
									if (collectedItem.sprite_index == spr_key3) // && player.health < player.maxhealth
									{
										createEffect(spr_key1, .01, (int)collectedItem.x, (int)collectedItem.y, (int)collectedItem.z, -10, 0, 10);
										collectedItem.isEnabled = false;
										bucketGameObjects.remove(i);
										playSound("dwing.wav");
										addToInventory(new InventoryObject("YELLOW KEY",spr_key3));
										i--;
									}
									if (collectedItem.sprite_index == spr_ammo_bonus)
									{
										createEffect(spr_ammo_bonus, .01, (int)collectedItem.x, (int)collectedItem.y, (int)collectedItem.z, -10, 0, 10);
										collectedItem.isEnabled = false;
										bucketGameObjects.remove(i);
										addToInventory(new InventoryObject("AMMO",spr_ammo_bonus));
										//coins_collected++;
										//soundPlayer.playClip(dwingClip);
										playSound("dwing.wav");
										i--;
									}

								}
								if (collectedItem.isEnabled)
								{
									gameObjects.add(bucketGameObjects.get(i));
								}
								//System.out.println("Added coin");
							}
						}
				}

			//bucketGameObjects.clear();

			//INTERACTABLE BUCKETGRID
			for(int ix = -1 ; ix <= 1 ; ix++)
				for(int iy = -1 ; iy <= 1 ; iy++)
				{
					if (bucketX+ix >= 0 && bucketX+ix < bucket_grid_width)
						if (bucketY+iy >= 0 && bucketY+iy < bucket_grid_height)
						{
							bucketGameObjects = interactableGrid.getList(bucketX + ix, bucketY + iy);
							for(int i = 0 ; i < bucketGameObjects.size(); i++)
							{
								GameObject interactableItem = bucketGameObjects.get(i);
								//Select nearby interactable object
								if (distance(interactableItem.x,interactableItem.y,player.x,player.y) < 100)
									//if (Math.abs(interactableItem.x - player.x) < 200 && Math.abs(interactableItem.y - player.y) < 200)
								{
									selectedInteractable = interactableItem;
								}
								if (interactableItem.isEnabled)
								{
									gameObjects.add(bucketGameObjects.get(i));
								}
								//System.out.println("Added coin");
							}
						}
				}

			//bucketGameObjects.clear();


			if (!gamePaused)
			{
				//Show the help label when the player approaches the question mark icon
//				if (distance(obj_help_shoot.x + obj_help_shoot.w/2, obj_help_shoot_label.y + obj_help_shoot.h / 2, player.x + player.w/2, player.y + player.h / 2) < 128)
//				{
//					obj_help_shoot_label.visible = true;
//				}
//				else
//				{
//					obj_help_shoot_label.visible = false;
//				}

				//Check NPC Health
				for(int i = 0 ; i < enemies.size(); i++)
				{
					GameObject npc = enemies.get(i);
					if (npc.health <= 0)
					{
						/*
	            	if (distance(player.x,player.y,game_width/4,game_height/4) < distance(player.x,player.y,game_width*3/4,game_height*3/4))
	            	{
		            	npc.x = game_width*3/4;
		            	npc.y = game_height*3/5;
	            	}
	            	else
	            	{
	                	npc.x = game_width/4;
	                	npc.y = game_height/4;
	                }
	            	npc.health = npc.maxhealth;
						 */
						enemies.remove(i);
						i--;
					}
				}

				//Platform Controller
				if (platform.z > 48)
					platform.vz -= .001 * deltaTimeMS;

				if (platform.z < 48)
					platform.vz += .001 * deltaTimeMS;

				if (platform.z < 32)
					platform.z = 32;

				if (platform.z > 64)
					platform.z = 64;

				for(int i = 0 ; i < enemies.size(); i++)
				{
					//enemies.get(i).setBehaviour(Enemy.Behaviour.PATROL);
					if(startGame) { enemies.get(i).executeBehaviour(deltaTimeMS*.1); } 
				}

				//NPC movement controls
				/*
            for(int i = 0 ; i < enemies.size(); i++)
    		{ 
            boolean moving = false;

    		GameObject npc = enemies.get(i);
    		if (curFrameNS > 2000)
    		if (distance(npc.x, npc.y, player.x, player.y) > 220)
    		if (distance(npc.x, npc.y, player.x, player.y) < 620)
	    		{
    			double target_direction = Math.atan2(player.y - npc.y, player.x - npc.x);

    			//Convert to a vector
    			double input_x_normalized = Math.cos(target_direction);
    			double input_y_normalized = Math.sin(target_direction);

    			//Add normalized vector to velocity x and y components
    			npc.vx += input_x_normalized * move_speed * deltaTimeMS * .5;
    			npc.vy += input_y_normalized * move_speed * deltaTimeMS * .5;

            	moving = true;
    			if (player.x < npc.x)
    				npc.mx = 1;
    			else
    				npc.mx = -1;
	    		}

            if (moving)
            {
            	npc.image_index += deltaTimeMS / 100;
            	if (npc.image_index >= npc.sprite_index.subimage.size())
            	{
            		npc.image_index = 0;
            	}
            }
    		}
				 */

				//Movement Controls

				//RESOLVE NPC COLLISIONS WITH OTHER NPCS

				for(int i = 0  ; i < enemies.size() ; i++)
					for(int j = 0  ; j < enemies.size() ; j++)
					{
						if (i!=j)
						{
							GameObject npc1 = enemies.get(i);
							GameObject npc2 = enemies.get(j);
							//if (AABBIntersect(
							//		(int)npc1.x-npc1.w/2,(int)npc1.y-npc1.h/4,(int)npc1.x+npc1.w/2,(int)npc1.y+npc1.h/4,  
							//		(int)npc2.x-npc2.w/2,(int)npc2.y-npc2.h/4,(int)npc2.x+npc2.w/2,(int)npc2.y+npc2.h/4))
							if (distance(npc1.x,npc1.y,npc2.x,npc2.y) < 32)
							{
								if (npc1.x < npc2.x)
								{
									npc1.x-=deltaTimeMS * .1;
								}
								if (npc1.y < npc2.y)
								{
									npc1.y-=deltaTimeMS * .1;
								}
								if (npc1.x > npc2.x)
								{
									npc1.x+=deltaTimeMS * .1;
								}
								if (npc1.y > npc2.y)
								{
									npc1.y+=deltaTimeMS * .1;
								}
							}
						}
					}

				//RESOLVE NPCS COLLISIONS WITH WALLS
				for(int i = 0 ; i < enemies.size(); i++)
				{
					GameObject npc = enemies.get(i);
					npc.resolveWallCollisions(tile_width, tile_height, game_htiles, game_vtiles, game_ztiles, objects_grid);
					npc.shoot_timer -= deltaTimeMS;
				}

				//Player Shooting Controls
				if(startGame)
				{
					player.controls(deltaTimeMS, cam);
				}
				//BUCKET STUFF WAS HERE

				for (GameObject obj : gameObjects) 
				{
					// Update the object
					if (obj.isEnabled)
					{
						obj.update(deltaTimeMS, objects_grid);
					}
				}

				for(int i = 0 ; i < enemies.size(); i++)
				{
					GameObject npc = enemies.get(i);
					GameObject npc_health_back = enemies_healthbar_back.get(i);
					GameObject npc_health_front = enemies_healthbar_front.get(i);

					npc.setFriction(sqr(npc.velocity) / 500 + .005 );
					//player.update(deltaTimeMS);
					//npc.update(deltaTimeMS, objects_grid);

					npc_health_back.x = npc.x - npc_health_back.w / 2;
					npc_health_back.y = npc.y - npc.h/3;
					npc_health_back.z = npc.z+32;
					npc_health_front.x = npc.x - npc_health_back.w / 2;
					npc_health_front.y = npc.y - npc.h/3;
					npc_health_front.z = npc.z+32;

					npc_health_back.depth = -(npc_health_front.y+npc_health_front.z+npc_health_front.h);
					npc_health_front.depth = -(npc_health_front.y+npc_health_front.z+npc_health_front.h);

					npc_health_front.w = (int) ((double) npc.health / (double)npc.maxhealth * npc_health_back.w);

					/*
	    		//Add gravitational acceleration to z-velocity of NPC
	    		if (npc.z > 0)
	    			{
	    			npc.vz -= gravity * deltaTimeMS;
	    			}

	    		//If NPC z-position is below the floor level, set it to the floor level
	    		if (npc.z <= 0)
	    			{
	    			npc.z = 0;
	    			npc.vz = 0;
	    			}
					 */
				}

				/*
            //Add gravitational acceleration to z-velocity of PLAYER
    		if (player.z > player.ground_level)
    		{
    			player.vz -= gravity * deltaTimeMS;
    		}

    		//If PLAYER z-position is below the floor level, set it to the floor level
    		if (player.z <= player.ground_level)
    		{
    			for(int i = 0 ; i < enemies.size(); i++)
        		{ 
        		GameObject npc = enemies.get(i);

    			if (distance(npc.x, npc.y, player.x, player.y) < 200)
    			if (player.vz < -.8 && player.ground_level == 0)
    				{
    				//npc.vz = 20; //Make NPCs jump when you land near them
    				npc.z += 1;
    				}
        		}

    			player.z = player.ground_level;
    			player.vz = 0;
    		}
				 */

				//Constrain player inside the virtual game space
				if (player.x < player.w / 2)
				{
					player.x = player.w / 2;
					player.vx = 0;
				}
				if (player.y < player.h / 2)
				{
					player.y = player.h / 2;
					player.vy = 0;
				}
				if (player.x > game_width - player.w / 2)
				{
					player.x = game_width - player.w / 2;
					player.vx = 0;
				}
				if (player.y > game_height - player.h / 4)
				{
					player.y = game_height - player.h / 4;
					player.vy = 0;
				}

				//Check for projectile collisions
				for(int i = 0  ; i < gameObjects.size() ; i++)
				{
					if (gameObjects.get(i).sprite_index == spr_projectile)
					{
						//Check if projectile hit NPC, projectile collisions
						for(int j = 0 ; j < enemies.size(); j++)
						{ 
							GameObject npc = enemies.get(j);
							if (AABBIntersect((int)gameObjects.get(i).x, (int)gameObjects.get(i).y, gameObjects.get(i).w, gameObjects.get(i).h, (int)npc.x, (int)npc.y, npc.w, npc.h)
									&& gameObjects.get(i).isEnabled && Math.abs(npc.z + npc.h/2 - gameObjects.get(i).z) < npc.h/3)
							{
								if (gameObjects.get(i).team == 0)
								{
									gameObjects.get(i).isEnabled = false;
									npc.hit_timer = 200;

									//CREATE EXPLOSION
									createEffect(spr_explosion, .01, (int)gameObjects.get(i).x, (int)gameObjects.get(i).y, (int)gameObjects.get(i).z, 32, 32);

									//soundPlayer.playClip(enemyHitClip);
									playSound("hit_enemy.wav");
									npc.health -= gameObjects.get(i).health;
									npc.vx += gameObjects.get(i).vx / 10;
									npc.vy += gameObjects.get(i).vy / 10;
								}
							}
						}

						//Check if projectile hit PLAYER
						if (AABBIntersect((int)gameObjects.get(i).x, (int)gameObjects.get(i).y, gameObjects.get(i).w, gameObjects.get(i).h, (int)player.x, (int)player.y, player.w, player.h)
								&& gameObjects.get(i).isEnabled && Math.abs(player.z - gameObjects.get(i).z) < player.h/3)
						{
							if (gameObjects.get(i).team == 1)
							{
								gameObjects.get(i).isEnabled = false;

								createEffect(spr_explosion, .01, (int)gameObjects.get(i).x, (int)gameObjects.get(i).y, (int)gameObjects.get(i).z, 32, 32);

								player.health -= gameObjects.get(i).health;
								player.vx += gameObjects.get(i).vx / 2;
								player.vy += gameObjects.get(i).vy / 2;
							}
						}
					}
				}

				shadow.x = player.x + (player.z - player.ground_level)/5;// + 14;
				shadow.y = player.y + (player.z - player.ground_level)/5;// + 26;
				shadow.z = player.ground_level;
				shadow.mx = -player.mx;
				shadow.my = -1;
				shadow.image_angle = -120;
				shadow.image_index = player.image_index;
				shadow.image_color = new Color(255,255,255,50);

				shadow_upper.setSprite(player_upper_body.sprite_index);
				shadow_upper.x = player.x + (player.z - player.ground_level)/5;// + 14;
				shadow_upper.y = player.y + (player.z - player.ground_level)/5;// + 26;
				shadow_upper.z = player.ground_level;
				shadow_upper.mx = player_upper_body.mx;
				shadow_upper.my = -1;
				shadow_upper.image_angle = -120;
				shadow_upper.image_index = player_upper_body.image_index;
				shadow_upper.image_color = new Color(0,0,0,50);

				platform_shadow.x = platform.x + platform.z/5;
				platform_shadow.y = platform.y  + platform.h/2 + platform.z/5;

				//Set depths of all objects
				platform.depth = -(platform.y + platform.h) - platform.z + 32;
				platform_shadow.depth = -(platform.y + platform.h) - platform.z + 1 + 32;

				player.depth = -(player.y)-player.z-22;
				shadow.depth = -(player.ground_level+shadow.y+(shadow.y-player.y))-22;//+22;
				shadow_upper.depth = -(player.ground_level+shadow.y+(shadow.y-player.y))-44;

				player_upper_body.x = player.x;
				player_upper_body.y = player.y;
				player_upper_body.z = player.z;
				player_upper_body.depth = player.depth;

				if (player.shoot_timer > 50)
				{
					player_upper_body.image_index = 1;
				}
				else
				{
					player_upper_body.image_index = 0;
				}
				//System.out.println((int)(Math.toDegrees(player.aim_direction) / 30));
				switch((int)(Math.toDegrees(player.aim_direction) / 30))
				{
				//RIGHT
				case 0:
					player_upper_body.setSprite(spr_starlord_shooting_right);
					player_upper_body.setMX(1);
					break;
					//RIGHT UP
				case -1:
					player_upper_body.setSprite(spr_starlord_shooting_right_up);
					player_upper_body.setMX(1);
					break;
					//UP
				case -2:
				case -3:
					player_upper_body.setSprite(spr_starlord_shooting_up);
					break;
					//LEFT UP
				case -4:
					player_upper_body.setSprite(spr_starlord_shooting_right_up);
					player_upper_body.setMX(-1);
					break;
					//LEFT
				case -5:
				case 5:
					player_upper_body.setSprite(spr_starlord_shooting_right);
					player_upper_body.setMX(-1);
					break;
					//LEFT DOWN
				case 4:
					player_upper_body.setSprite(spr_starlord_shooting_right_down);
					player_upper_body.setMX(-1);
					break;
					//DOWN
				case 2: 
				case 3:
					player_upper_body.setSprite(spr_starlord_shooting_down);
					break;
					//RIGHT DOWN
				case 1:
					player_upper_body.setSprite(spr_starlord_shooting_right_down);
					player_upper_body.setMX(1);
					break;
				}


				for(int i = 0 ; i < enemies.size(); i++)
				{
					GameObject npc = enemies.get(i);
					GameObject eshadow = enemies_shadows.get(i);

					npc.depth =  -(npc.y)-npc.z-32;

					eshadow.x = npc.x + (npc.z - npc.ground_level)/5;
					eshadow.y = npc.y + (npc.z - npc.ground_level)/5;

					eshadow.z = npc.ground_level;
					eshadow.depth = -(npc.ground_level+eshadow.y+(eshadow.y-npc.y))-22;

					eshadow.mx = npc.mx;
				}

				cam.x = player.x - cam.w/2;
				cam.y = player.y - cam.h/2;
			}

			//End of physics update
			//} while (totalPhysicsDeltaMs < .001 );
			if (clickTimer > 0)
				clickTimer -= deltaTimeMS;

			if (gamePaused)
			{
				if (tabTimer > 0)
					tabTimer -= deltaTimeMS;

				if (kbState[KeyEvent.VK_UP] && (kbPrevState[KeyEvent.VK_UP] == false || tabTimer <= 0)) 
				{
					//soundPlayer.playClip(clickClip);
					playSound("click.wav");
					tabTimer = 150;
					controlMode = ControlMode.ARROWKEYS_Z;
					if (menuSelected > 0)
						menuSelected--;
					else
						menuSelected=menuItems-1;
				}
				if (kbState[KeyEvent.VK_DOWN] && (kbPrevState[KeyEvent.VK_DOWN] == false || tabTimer <= 0)) 
				{
					//soundPlayer.playClip(clickClip);
					playSound("click.wav");
					tabTimer = 150;
					controlMode = ControlMode.ARROWKEYS_Z;
					if (menuSelected < menuItems - 1)
						menuSelected++;
					else
						menuSelected = 0;
				}

				menuItems = 0;

				/*
                if (kbState[KeyEvent.VK_ENTER] == false && kbPrevState[KeyEvent.VK_ENTER] == true) {

                	if (menuTab == PAUSEMENU_TAB_HOME)
	                	{
	                	if (menuSelected == 0)
	                		menuTab = PAUSEMENU_TAB_OPTIONS;
	                	if (menuSelected == 1)
	                		menuTab = PAUSEMENU_TAB_CONTROLS;
	                	if (menuSelected == 2)
	                		gamePaused = false;
	                	if (menuSelected == 3)
	                		shouldExit = true;
	                	}
                	else
                	if (menuTab == PAUSEMENU_TAB_CONTROLS)
	                	{

                		if (menuSelected == 0)
	                		controlMode = ControlMode.MOUSE_WASD;
                		if (menuSelected == 1)
	                		controlMode = ControlMode.ARROWKEYS_WASD;
                		if (menuSelected == 2)
	                		controlMode = ControlMode.ARROWKEYS_Z;

	                	if (menuSelected == 3)
	                		menuTab = PAUSEMENU_TAB_HOME;
	                	}
                }
				 */
			}

			if (cam.x < 0)
				cam.x = 0;
			if (cam.y < 0)
				cam.y = 0;
			if (cam.x > game_width - cam.w)
				cam.x = game_width - cam.w;
			if (cam.y > game_height - cam.h)
				cam.y = game_height - cam.h;

			bucketGameObjects = new ArrayList<GameObject>();

			bucketX = ((int)player.x / tile_width) / bucket_width;
			bucketY = ((int)player.y / tile_height) / bucket_height;

			//WALLS BUCKETGRID
			for(int ix = -1 ; ix <= 1 ; ix++)
				for(int iy = -1 ; iy <= 1 ; iy++)
				{
					if (bucketX+ix >= 0 && bucketX+ix < bucket_grid_width)
						if (bucketY+iy >= 0 && bucketY+iy < bucket_grid_height)
						{
							bucketGameObjects = bobTheWallGrid.getList(bucketX + ix, bucketY + iy);
							for(int i = 0 ; i < bucketGameObjects.size(); i++)
								gameObjects.add(bucketGameObjects.get(i));

						}
				}


			Collections.sort(gameObjects , new DepthComparator());

			//CLEAR THE SCREEN
			gl.glClearColor(.87f, .9f, .93f, 1);
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

			int x1 = 0, y1 = 0, w = backgroundSize[0], h = backgroundSize[1], space = 16;

			int start_nx = (int) Math.floor(cam.x / tile_width);
			int start_ny = (int) Math.floor(cam.y / tile_height);
			int end_nx = Math.min((int) Math.ceil((cam.x + cam.w) / tile_width) , game_htiles);
			int end_ny = Math.min((int) Math.ceil((cam.y + cam.h) / tile_height) , game_vtiles);

			//DRAW THE BACKGROUND
			for (y1 = start_ny; y1 < end_ny; y1++)
				for (x1 = start_nx; x1 < end_nx; x1++)
					glDrawSprite(gl, backgroundIDS[background[y1][x1]],
							(int)(x1 * w - cam.x),(int)(y1 * h - cam.y), backgroundSize[0], backgroundSize[1]);

			//Draw the player's collision box (for debugging collisions)
			//drawSprite(gl, 0,player_grid_x * tile_width, player_grid_y * tile_height, backgroundSize[0], backgroundSize[1], 1, 1, cam);

			//Draw Objects, Draw GameObjects
			for (GameObject obj : gameObjects) {
				// Draw the sprite
				if (obj.isEnabled && obj.visible)
				{
					int shadow_x = (int) ((obj.z - obj.ground_level)/5);
					int shadow_y = (int) ((obj.z - obj.ground_level)/5 - obj.ground_level);

					if (obj.shadow_index != null)
						drawSprite(obj.shadow_index, 0 , obj.x + shadow_x + tile_width/2, obj.y + shadow_y + tile_height/2, obj.shadow_w , obj.shadow_h , obj.mx , obj.my, cam);

					//if (obj.image_angle == 0)
						//	drawSpriteExt(gl, obj.sprite_index.getTex((int)Math.floor(obj.image_index)) , obj.x - obj.xorig , obj.y - obj.z - obj.yorig , obj.w , obj.h , obj.mx , obj.my, cam);
					//else
					// - obj.xorig,  - obj.yorig
					drawSpriteExt(obj.sprite_index,(int)Math.floor(obj.image_index) , obj.x, obj.y-obj.z , obj.w , obj.h , obj.mx , obj.my,obj.image_angle,obj.image_color, cam);
				}
			}
/*
			for(Enemy npc : enemies)
			{
				if (npc.greetingText != "")
				{
					drawText(npc.greetingText,npc.x-cam.x-stringWidth(npc.greetingText)/2,npc.y-npc.z-cam.y-64);
				}
			}
*/
			if (selectedInteractable != null && !storeOpen)
			{
				//drawSprite(Sprite spr, int frame, double x, double y, int w, int h, int mx , int my , Camera cam)
				int yoff = 0;
				if (selectedInteractable.sprite_index == spr_teleporter)
				{
					//drawRectangle((int)(selectedInteractable.x-100-cam.x), (int)(selectedInteractable.y-100-cam.y),
					//		(int)(selectedInteractable.x+100-cam.x),(int)(selectedInteractable.y+100-cam.y));

					boolean hasKey1 = false, hasKey2 = false, hasKey3 = false;
					for(int i = 0 ; i < inventory.size(); i++)
					{
						if (inventory.get(i).sprite_index == spr_key1)
							hasKey1 = true;
						if (inventory.get(i).sprite_index == spr_key2)
							hasKey2 = true;
						if (inventory.get(i).sprite_index == spr_key3)
							hasKey3 = true;
					}
					//SHOW PROMPT TO ENTER PORTAL
					if (hasKey1 && hasKey2 && hasKey3)
					{
						String tstr = "PRESS E TO ENTER PORTAL";
						infobox((int)(selectedInteractable.x-cam.x),(int)(selectedInteractable.y-cam.y-128-32),tstr);
						if (kbState[KeyEvent.VK_E] && kbPrevState[KeyEvent.VK_E] == false)
						{
							if(current_level == levels.size() - 1)
							{
								winCondition = true;
							}
							else
							{
								for(int i = 0 ; i < inventory.size(); i++)
								{
									if (inventory.get(i).name.contains("KEY"))
									{
										inventory.remove(i);
										i--;
									}
								}
								gotoLevel(levels.get(current_level+1));
								lives += 3;
								current_level++;
							}
						}
					}
					else
					{
						String tstr = "COLLECT ALL 3 KEYS#TO ENTER PORTAL";
						infobox((int)(selectedInteractable.x-cam.x),(int)(selectedInteractable.y-cam.y-128-32),tstr);
					}

					yoff = -58;
				}

				//Draws winscreen
				if(winCondition)
				{
					drawRectangle(X,Y + yof,X + W, Y + yof + H);
					drawText("A WINRAR IS YOU : HIT SPACE TO PLAY AGAIN", X + xof - 64, Y+yof+16);
					if(kbState[KeyEvent.VK_SPACE])
					{	
						inventory.clear();
						player.primaryWeapon = standardBlaster;
						inventory.add(standardBlaster);
						coins_collected = 0;
						player.health = player.maxhealth;
						player.x = player.xstart;
						player.y = player.ystart;
						player.z = player.zstart;
						gotoLevel("LEVEL1");
						startGame = false;
						winCondition = false;
					}
				}
				if (selectedInteractable.sprite_index == spr_upgrade_shop_owner)
				{
					//drawRectangle((int)(selectedInteractable.x-100-cam.x), (int)(selectedInteractable.y-100-cam.y),
					//		(int)(selectedInteractable.x+100-cam.x),(int)(selectedInteractable.y+100-cam.y));

					//SHOW PROMPT TO OPEN THE STORE MENU
					String tstr = "PRESS E TO TALK";
					infobox((int)(selectedInteractable.x-cam.x),(int)(selectedInteractable.y-cam.y-128),tstr);
					if (kbState[KeyEvent.VK_E] && kbPrevState[KeyEvent.VK_E] == false)
					{
						//OPEN THE WEAPONS / UPGRADES STORE
						storeOpen = true;
						gamePaused = true;
					}
				}

				drawSprite(spr_select_box,0,(int)selectedInteractable.x,(int)selectedInteractable.y-24+yoff,(int)spr_select_box.width,(int)spr_select_box.height,1,1,cam);
			}

			//Draw Inventory
			//drawRectangle(cam.w/2-100, cam.h-64,cam.w/2+100, cam.h);

			in_interface = false;

			if (player.health > 0)
	         {
	            if(kbState[KeyEvent.VK_Q] && !kbPrevState[KeyEvent.VK_Q])
	            {
	               boolean found = false;
	               for(int i = 0; i < inventory.size() && !found; i++)
	               {
	                  Sprite spr1 = inventory.get(i).getSprite();
	                  if (spr1 == spr_health_bonus)
	                  {
	                     if (player.health < player.maxhealth)
	                     {
	                        player.health += 3;
	                        if (player.health > player.maxhealth)
	                        {
	                           player.health = player.maxhealth;
	                        }
	                        removeFromInventory(inventory.get(i));
	                        found = !found;
	                        playSound("snd_use_item.wav");
	                        break;
	                     }
	                  }
	               }
	            }

	            int hoverItem = -1;
	            for(int i = 0 ; i < inventory.size(); i++)
	            {
	               Sprite spr1 = inventory.get(i).getSprite();
	               //drawButton(Sprite buttonSpr, int x, int y, int width, int height, String buttonText)
	               
	               
	               if (drawButton(spr_hud_back,i*64,cam.h-64,64,64,""))
	               {
	                  if (spr1 == spr_health_bonus)
	                  {
	                     if (player.health < player.maxhealth)
	                     {
	                        player.health += 3;
	                        if (player.health > player.maxhealth)
	                        {
	                           player.health = player.maxhealth;
	                        }
	                        removeFromInventory(inventory.get(i));
	                        playSound("snd_use_item.wav");
	                        break;
	                     }
	                  }

	                  if (inventory.get(i).getClass().getName().equals("Weapon"))
	                  {
	                     //Swap current weapon for inventory weapon
	                     InventoryObject c = inventory.get(i);
	                     player.primaryWeapon = (Weapon) inventory.get(i);
	                     inventory.set(i, c);
	                  }
	               }
	               
	               if (inregion(mouse.x, mouse.y, i*64,cam.h-64,i*64+64,cam.h-64+64))
	               {
						hoverItem = i;
	               }
	               		//InventoryObject obj = inventory.get(i);
						if(spr1 != null)
						{
							drawSprite(spr1,0,32+i*64, cam.h-32, spr1.width, spr1.height);
						}
						if (inventory.get(i).quantity > 1)
						{
							drawText(""+inventory.get(i).quantity,32+i*64+16, cam.h-32);
						}
	            	}
	            
	            if (hoverItem > -1)
	            infobox((int)mouse.x,(int)mouse.y-64,""+inventory.get(hoverItem).name);
	         	}

			//System.out.println(spr_projectile.width);
			
			testDir += deltaTimeMS / 500;


			//DRAW AIM DIRECTION LINE
			glDrawSpriteExt(gl, spr_hud_back.getTex(0) , (int) (player.x - cam.x +Math.cos(player.aim_direction) * 48) , 
					(int) (player.y-cam.y + Math.sin(player.aim_direction) * 48 - player.z) , //  
					32 , 4, player.aim_direction, new Color(255,255,255,255));

			/*
			double nearestEnemyDirection = Math.atan2(key1.y-player.y,key1.x-player.x);
			glDrawSpriteExt(gl, spr_hud_back.getTex(0) , (int) (player.x - cam.x +Math.cos(nearestEnemyDirection) * 320) , 
					(int) (player.y-cam.y + Math.sin(nearestEnemyDirection) * 320 - player.z) , //  
					32 , 4, nearestEnemyDirection, new Color(255,255,255,255));
			*/
			
			//String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			//Sprite spr_alphabet = new Sprite();
			/*
            int pos = 0;
            for(int i = 0 ; i < alphabet.length() ; i++ )
            {
            	drawSprite(gl, spr_alphabet.getTex(i) , pos + cam.x, cam.y, spr_alphabet.getWidth(i), spr_alphabet.getHeight(i), 1, 1, cam);
            	pos += spr_alphabet.getWidth(i) + 3;
            }
			 */

			drawRectangle(0, 0,200, 80);
			drawProgressBar(gl, 0, 0, 200, 30, player.maxhealth, player.health);
			drawSprite(spr_coin, 0, 24, 64, 40, 40);
			drawText(""+coins_collected, 54, 44);// "COINS: "+
			drawSprite(spr_starlord_shooting_right, 0, 124, 80, 64, 64);
			drawText(""+lives, 164, 44);
			/*
            if (fps > 1000)
            	fps = 1000;
            drawText("FPS: "+fps, cam.x + 10, cam.y + 50+64, cam);*/

			if (player.health <= 0)
			{
				if(!lifeSubtracted)
				{
					lives--;
					lifeSubtracted = true;
				}
				drawRectangle((int)cam.w/6,(int)cam.h/4,(int)cam.w*5/6,(int)cam.h*3/4);
				String tstr = "YOU DIED";
				drawText(tstr,cam.w/2-stringWidth(tstr)/2,cam.h/2-100);
				Menu deathMenu = new Menu();

				if(lives < 1)
				{
					deathMenu.addOption("NEW GAME");
				}
				else
				{
					deathMenu.addOption("CONTINUE");
				}
				//deathMenu.addOption("QUIT");

				String result = deathMenu.draw(window_width/2 - 200, window_height/2 + 50, 400);

				if (!result.equals(""))
				{
					System.out.println("RESULT = "+result);
				}
				if (result.equals("NEW GAME"))
				{
					inventory.clear();
					//inventory.add(player.standardBlaster);
					//player.primaryWeapon = player.standardBlaster;

					player.primaryWeapon = standardBlaster;
					inventory.add(standardBlaster);

					gotoLevel(levels.get(0));
					//System.out.println("LEVEL RESTARTED");
					lifeSubtracted = false;
					coins_collected = 0;
					player.health = player.maxhealth;
					player.x = player.xstart;
					player.y = player.ystart;
					player.z = player.zstart;
					lives = 3;
				}
				if (result.equals("CONTINUE"))
				{
					//inventory.add(player.standardBlaster);
					//player.primaryWeapon = player.standardBlaster;
					//gotoLevel(levels.get(current_level));
					//System.out.println("LEVEL RESTARTED");
					//coins_collected = 0;
					lifeSubtracted = false;
					player.health = player.maxhealth;
					player.x = player.xstart;
					player.y = player.ystart;
					player.z = player.zstart;
				}

				if (result.equals("QUIT"))
				{
					shouldExit = true;
				}
			}

			if (gamePaused)
			{
				//DRAW THE STORE, DRAW STORE
				if (storeOpen)
				{
					drawRectangle(100,100,cam.w-100,cam.h-100);

					//drawButton(Sprite buttonSpr, int x, int y, int width, int height, String buttonText)
					if (drawButton(spr_hud_back, cam.w-100-48, 100, 48, 48,"X"))
					{
						storeOpen = false;
						gamePaused = false;
					}
					
					int dx = 120, dy = 120, button_width = 640, button_height = 48, spacing = 16;

					drawText("BOBS WEAPONS SHOP",dx,dy);
					dy += button_height + spacing;
					
					if (drawButton(spr_hud_back, dx, dy, button_width, 48,triBurstBlaster.name 
							+ " ... "+triBurstBlaster.cost+" COINS"))
					{
						if (coins_collected >= triBurstBlaster.cost)
						{
							coins_collected-=triBurstBlaster.cost;
							addToInventory(triBurstBlaster);
							playSound("snd_chaching_purchase.wav");
						}
					}
					dy += button_height + spacing;
					if (drawButton(spr_hud_back, dx, dy, button_width, 48,triSpreadBlaster.name 
							+ " ... "+triSpreadBlaster.cost+" COINS"))
					{
						if (coins_collected >= triSpreadBlaster.cost)
						{
							coins_collected-=triSpreadBlaster.cost;
							addToInventory(triSpreadBlaster);
							playSound("snd_chaching_purchase.wav");
						}
					}
					dy += button_height + spacing;
					if (drawButton(spr_hud_back, dx, dy, button_width, 48,sniperBlaster.name 
							+ " ... "+sniperBlaster.cost+" COINS"))
					{
						if (coins_collected >= sniperBlaster.cost)
						{
							coins_collected-=sniperBlaster.cost;
							addToInventory(sniperBlaster);
							playSound("snd_chaching_purchase.wav");
						}
					}
					dy += button_height + spacing;
					if (drawButton(spr_hud_back, dx, dy, button_width, 48,assaultRifle.name 
							+ " ... "+assaultRifle.cost+" COINS"))
					{
						if (coins_collected >= assaultRifle.cost)
						{
							coins_collected-=assaultRifle.cost;
							addToInventory(assaultRifle);
							playSound("snd_chaching_purchase.wav");
						}
					}
					dy += button_height + spacing;
					if (drawButton(spr_hud_back, dx, dy, button_width, 48,"HEALTH ... 10 COINS"))
					{
						if (coins_collected >= 10)
						{
							coins_collected-=10;
							addToInventory(new InventoryObject("HEALTH",spr_health_bonus));
							playSound("snd_chaching_purchase.wav");
						}
					}
					/*

            		dy += button_height + spacing;
            		if (drawButton(spr_hud_back, dx, dy, button_width, 48,rocketLauncher.name + " ... 100 COINS"))
            		{
            			if (coins_collected >= 100)
            			{
            			coins_collected-=100;
	            		addToInventory(rocketLauncher);
	            		playSound("snd_chaching_purchase.wav");
            			}
            		}
            		dy += button_height + spacing;
            		if (drawButton(spr_hud_back, dx, dy, button_width, 48,grenadeLauncher.name + " ... 100 COINS"))
            		{
            			if (coins_collected >= 100)
            			{
            			coins_collected-=100;
	            		addToInventory(grenadeLauncher);
	            		playSound("snd_chaching_purchase.wav");
            			}
            		}
            		dy += button_height + spacing;
            		if (drawButton(spr_hud_back, dx, dy, button_width, 48,"AMMO CLIP ... 10 COINS"))
            		{
            			if (coins_collected >= 10)
            			{
            			coins_collected-=10;
	            		addToInventory(new InventoryObject("Ammo",spr_ammo_bonus));
	            		playSound("snd_chaching_purchase.wav");
            			}
            		}
            		dy += button_height + spacing;
            		if (drawButton(spr_hud_back, dx, dy, button_width, 48,"SHIELD ... 100 COINS"))
            		{

            		}
            		dy += button_height + spacing;
            		if (drawButton(spr_hud_back, dx, dy, button_width, 48,"GRENADE ... 100 COINS"))
            		{

            		}
            		dy += button_height + spacing;
					 */

				}
				else
				{
					if (menuTab == PAUSEMENU_TAB_HOME)
					{
						int xpos, ypos;
						xpos = cam.w/2 - 160; 
						ypos = cam.h/2 -48*3;

						/*
            		drawSprite(gl, spr_hud_back.getTex(0), cam.x + cam.w/2-32 + 128, cam.y + cam.h/2-32, 320,320 ,1,1,cam);
						 */

						int bh = 48;

						drawRectangle(xpos,ypos, xpos+320, ypos+bh*7);

						glDrawSprite(gl, spr_hud_back.getTex(0), xpos,ypos, 320, bh);

						drawText("GAME PAUSED", xpos + 320 / 2 - stringWidth("GAME PAUSED")/2 ,ypos+bh/2-stringHeight("GAME PAUSED")/2);

						//DRAW SELECTOR
						//drawSprite(gl, spr_hud_back.getTex(0), xpos,ypos, 320, bh ,1,1,cam);
						//if (kbState[KeyEvent.VK_ENTER])
						//	drawSprite(gl, spr_hud_back.getTex(0), xpos,ypos, 320, bh ,1,1,cam);

						ypos += bh;
						if (drawButtonTab(spr_hud_back, xpos,ypos, 320, bh,"SAVE GAME",menuSelected,0))
						{
							//TODO: Implement Save Game
						}
						ypos += bh;
						if (drawButtonTab(spr_hud_back, xpos,ypos, 320, bh,"LOAD GAME",menuSelected,1))
						{
							//TODO: Implement Load Game
						}
						ypos += bh;
						if (drawButtonTab(spr_hud_back, xpos,ypos, 320, bh,"OPTIONS",menuSelected,2))
						{
							menuTab = PAUSEMENU_TAB_OPTIONS;
						}
						ypos += bh;
						if (drawButtonTab(spr_hud_back, xpos,ypos, 320, bh,"CONTROLS",menuSelected,3))
						{
							menuTab = PAUSEMENU_TAB_CONTROLS;
						}
						ypos += bh;
						if (drawButtonTab(spr_hud_back, xpos,ypos, 320, bh,"RESUME GAME",menuSelected,4))
						{
							gamePaused = false;
						}
						ypos += bh;
						if (drawButtonTab(spr_hud_back, xpos,ypos, 320, bh,"EXIT GAME",menuSelected,5))
						{
							shouldExit = true;
						}
						ypos += 32;

						//(int)cam.x + cam.w/2-32 + 128, (int)cam.y + cam.h/2-32
						/*

	                drawText("OPTIONS", cam.x + cam.w/2 + 128, cam.y + cam.h/2 + 32, cam);
	                drawText("CONTROLS", cam.x + cam.w/2 + 128, cam.y + cam.h/2 + 32 * 2, cam);
	                drawText("RESUME GAME", cam.x + cam.w/2 + 128, cam.y + cam.h/2 + 32 * 3, cam);
	                drawText("EXIT GAME", cam.x + cam.w/2 + 128, cam.y + cam.h/2 + 32 * 4, cam);
						 */
					}

					if (menuTab == PAUSEMENU_TAB_CONTROLS)
					{
						int buttonH = 64;

						int windowW = cam.w - 64;
						int windowH = buttonH * 5+32;

						int windowX = (int)32;
						int windowY = (int)cam.h/2-windowH/2-32;

						int xoff = 32, yoff = 32;

						//Window Background
						//drawSprite(gl, spr_hud_back.getTex(0), windowX, windowY, windowW,windowH ,1,1,cam);

						//Selection Rectangle
						//drawSprite(gl, spr_hud_back.getTex(0), windowX, windowY +32+ 48 + menuSelected * buttonH-8, windowW, buttonH ,1,1,cam);
						/*
	                if (kbState[KeyEvent.VK_ENTER])
	                {
	                	drawSprite(gl, spr_hud_back.getTex(0), windowX, windowY +32+ 48 + menuSelected * buttonH-8, windowW, buttonH ,1,1,cam);
	                }*/

						int itemSelected = 0;
						switch (controlMode)
						{
						case MOUSE_WASD:
							itemSelected = 0;
							break;
						case ARROWKEYS_WASD:
							itemSelected = 1;
							break;
						case ARROWKEYS_Z:
							itemSelected = 2;
							break;
						}

						drawRectangle(windowX,windowY + yoff,windowX + windowW, windowY + windowH);

						drawRectangle(windowX,windowY + yoff,windowX + windowW, windowY + yoff + buttonH);

						drawText("CHOOSE A CONTROL SCHEME", windowX+xoff, windowY+yoff+16);
						xoff += 32;
						yoff += buttonH;

						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"MOUSE TO SHOOT AND WASD TO MOVE",menuSelected,0))
						{
							controlMode = ControlMode.MOUSE_WASD;
						}
						yoff += buttonH;
						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"WASD TO AIM AND SHOOT ARROW KEYS TO MOVE",menuSelected,1))
						{
							controlMode = ControlMode.ARROWKEYS_WASD;
						}
						yoff += buttonH;
						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"Z TO SHOOT AND ARROW KEYS TO MOVE AND AIM",menuSelected,2))
						{
							controlMode = ControlMode.ARROWKEYS_Z;
						}
						yoff += buttonH;
						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"BACK",menuSelected,3))
						{
							menuTab = PAUSEMENU_TAB_HOME;
						}

						//Draw magikarp next to selected option
						glDrawSprite(gl, spr_magikarp.getTex(0), windowX+spr_magikarp.getWidth(0), windowY +64+ 48 + itemSelected * buttonH-8, -spr_magikarp.getWidth(0), spr_magikarp.getHeight(0));
					}

					if (menuTab == PAUSEMENU_TAB_OPTIONS)
					{
						int windowW = cam.w - 64;
						int windowH = 360;

						int windowX = (int)32;
						int windowY = (int)cam.h/2-windowH/2;

						int buttonH = 64;

						int xoff = 32, yoff = 32;

						//soundEffectsEnabled = true;
						//musicEnabled = true;
						//fullScreen = false;

						String tstr;

						//drawRectangle(windowX,windowY,windowX + windowW, windowY + windowH);

						//drawText("GAME OPTIONS", windowX+xoff, windowY+yoff+16);
						drawRectangle(windowX,windowY + yoff,windowX + windowW, windowY + windowH);

						drawRectangle(windowX,windowY + yoff,windowX + windowW, windowY + yoff + buttonH);

						drawText("GAME OPTIONS", windowX+xoff, windowY+yoff+16);
						xoff += 32;
						if (soundEffectsEnabled)
							tstr = "ON";
						else
							tstr = "OFF";
						yoff += buttonH;

						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"SOUND EFFECTS: "+tstr,menuSelected,0))
						{
							soundEffectsEnabled = !soundEffectsEnabled;
						}
						yoff += buttonH;

						if (musicEnabled)
							tstr = "ON";
						else
							tstr = "OFF";

						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"MUSIC: "+tstr,menuSelected,1))
						{
							musicEnabled = !musicEnabled;
						}
						yoff += buttonH;


						if (fullScreen)
							tstr = "ON";
						else
							tstr = "OFF";

						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"FULLSCREEN: "+tstr,menuSelected,2))
						{
							fullScreen = !fullScreen;
						}
						yoff += buttonH;
						if (drawButtonTab(spr_hud_back, windowX, windowY + yoff, windowW, buttonH,"BACK",menuSelected,3))
						{
							menuTab = PAUSEMENU_TAB_HOME;
						}
					}
				}
			}
			drawSpriteExt(spr_target,0 , (int)mouse.x+cam.x , (int)mouse.y+cam.y , spr_target.width,spr_target.height, 
					1 , 1, testDir ,new Color(255,255,255,255), cam);

		}

	}

	public static void drawText(String text, double x, double y)
	{
		int xpos = 0, ypos = 0;
		for(int i = 0 ; i < text.length() ; i++ )
		{
			if (text.charAt(i) == ' ') //Spaces
			{
				xpos += 10;
			}
			else
				if (text.charAt(i) == '#') //Newline Characters
				{
					xpos = 0;
					ypos += font.getCharHeight('Q') + 6;
				}
				else
				{
					glDrawSprite(gl, font.getCharTex(text.charAt(i)) , (int)x + xpos, (int)y + ypos, font.getCharWidth(text.charAt(i)), font.getCharHeight(text.charAt(i)));
					xpos += font.getCharWidth(text.charAt(i)) + 3;
				}

		}
	}

	public static int stringWidth(String text)
	{
		int xpos = 0; //, ypos = 0;
		int biggest = 0;
		for(int i = 0 ; i < text.length() ; i++ )
		{
			if (text.charAt(i) == ' ') //Spaces
			{
				xpos += 10;
			}
			else
				if (text.charAt(i) == '#') //Newline Characters
				{
					xpos = 0;
					//ypos += font.getCharHeight('Q') + 6;
				}
				else
				{
					xpos += font.getCharWidth(text.charAt(i)) + 3;
				}

			if (xpos > biggest)
				biggest = xpos;
		}

		return biggest;
	}

	public static int stringHeight(String text)
	{
		/*
		int xpos = 0, ypos = 0;
	    for(int i = 0 ; i < text.length() ; i++ )
	    {
	    	if (text.charAt(i) == ' ') //Spaces
	    	{
	    		xpos += 10;
	    	}
	    	else
			if (text.charAt(i) == '#') //Newline Characters
	    	{
				xpos = 0;
	    		ypos += font.getCharHeight('Q')+6;
	    	}
	    	else
	    	{
	    	xpos += font.getCharWidth(text.charAt(i)) + 3;
	    	}
	    }
		 */
		return font.getCharHeight('Q') * (stringCount(text, "#")+1) + 6 * stringCount(text, "#");
	}

	public static int stringCount(String str, String substr)
	{
		int count = 0;
		for(int i = 0 ; i < str.length() ; i++)
		{
			if (str.substring(i, i+substr.length()).equals(substr))
			{
				count++;
			}
		}
		return count;
	}

	public static void drawText(String text, double x, double y, Camera cam, GL2 gl)
	{
		int xpos = 0, ypos = 0;
		for(int i = 0 ; i < text.length() ; i++ )
		{
			if (text.charAt(i) == ' ') //Spaces
			{
				xpos += 10;
			}
			else
				if (text.charAt(i) == '#') //Newline Characters
				{
					xpos = 0;
					ypos += font.getCharHeight('Q') + 6;
				}
				else
				{//.getCharTex(text.charAt(i))
					drawSprite(font.fontSprite, i , x + xpos, y + ypos, font.getCharWidth(text.charAt(i)), font.getCharHeight(text.charAt(i)), 1, 1, cam);
					xpos += font.getCharWidth(text.charAt(i)) + 3;
				}

		}
	}

	// Load a file into an OpenGL texture and return that texture.
	public static int glTexImageTGAFile(GL2 gl, String filename, int[] out_size) {
		final int BPP = 4;

		DataInputStream file = null;
		try {
			// Open the file.
			file = new DataInputStream(new FileInputStream(filename));
		} catch (FileNotFoundException ex) {
			System.err.format("File: %s -- Could not open for reading.", filename);
			return 0;
		}

		try {
			// Skip first two bytes of data we don't need.
			file.skipBytes(2);

			// Read in the image type.  For our purposes the image type
			// should be either a 2 or a 3.
			int imageTypeCode = file.readByte();
			if (imageTypeCode != 2 && imageTypeCode != 3) {
				file.close();
				System.err.format("File: %s -- Unsupported TGA type: %d", filename, imageTypeCode);
				return 0;
			}

			// Skip 9 bytes of data we don't need.
			file.skipBytes(9);

			int imageWidth = Short.reverseBytes(file.readShort());
			int imageHeight = Short.reverseBytes(file.readShort());
			int bitCount = file.readByte();
			file.skipBytes(1);

			// Allocate space for the image data and read it in.
			byte[] bytes = new byte[imageWidth * imageHeight * BPP];

			// Read in data.
			if (bitCount == 32) {
				for (int it = 0; it < imageWidth * imageHeight; ++it) {
					bytes[it * BPP + 0] = file.readByte();
					bytes[it * BPP + 1] = file.readByte();
					bytes[it * BPP + 2] = file.readByte();
					bytes[it * BPP + 3] = file.readByte();
				}
			} else {
				for (int it = 0; it < imageWidth * imageHeight; ++it) {
					bytes[it * BPP + 0] = file.readByte();
					bytes[it * BPP + 1] = file.readByte();
					bytes[it * BPP + 2] = file.readByte();
					bytes[it * BPP + 3] = -1;
				}
			}

			file.close();

			// Load into OpenGL
			int[] texArray = new int[1];
			gl.glGenTextures(1, texArray, 0);
			int tex = texArray[0];
			gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
			gl.glTexImage2D(
					GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth, imageHeight, 0,
					GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes));
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

			out_size[0] = imageWidth;
			out_size[1] = imageHeight;
			return tex;
		}
		catch (IOException ex) {
			System.err.format("File: %s -- Unexpected end of file.", filename);
			return 0;
		}
	}

	public static void drawSprite(Sprite spr, int frame, double x, double y, int w, int h)
	{
		int tex = spr.getTex(frame);

		//Compute x and y offset based on mirroring

		glDrawSprite(gl, tex, (int) (x - spr.xorig), (int) (y - spr.yorig), w , h );
	}

	public static void drawSprite(Sprite spr, int frame, double x, double y, int w, int h, int mx , int my , Camera cam)
	{
		int tex = spr.getTex(frame);
		int xoff = 0, yoff = 0;

		//Compute x and y offset based on mirroring
		if (mx == -1)
			xoff = w;
		if (my == -1)
			yoff = h;

		glDrawSprite(gl, tex, (int) (x - cam.x + xoff - spr.xorig), (int) (y - cam.y + yoff - spr.yorig), w * mx, h * my);
	}

	public static void drawSpriteExt( Sprite spr, int frame, double x, double y, int w, int h, int mx , int my , double angle, Color col, Camera cam)
	{
		int tex = spr.getTex(frame);

		double tdir, tdis, cx1, cy1, ndx, ndy;
		cx1 = w/2 * mx;
		cy1 = h/2 * my;
		tdir = Math.atan2(cy1-spr.yorig * my , cx1-spr.xorig * mx) + angle;
		tdis = distance(cx1,cy1, spr.xorig * mx,spr.yorig * my);
		ndx = x + Math.cos(tdir) * tdis - cam.x;
		ndy = y + Math.sin(tdir) * tdis - cam.y;

		glDrawSpriteExt(gl, tex, (int)ndx, (int)ndy, w * mx, h * my, angle, col);
	}

	public static void glDrawSprite(GL2 gl, int tex, int x, int y, int w, int h) {

		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
		gl.glBegin(GL2.GL_QUADS);
		{	
			//Color col = new Color(255,255,255);
			gl.glColor4ub((byte)255,(byte)255,(byte)255,(byte)255);//(byte)-1, (byte)-1, (byte)-1, (byte)-.5);
			gl.glTexCoord2f(0, 1);
			gl.glVertex2i(x, y);
			gl.glTexCoord2f(1, 1);
			gl.glVertex2i(x + w, y);
			gl.glTexCoord2f(1, 0);
			gl.glVertex2i(x + w, y + h );
			gl.glTexCoord2f(0, 0);
			gl.glVertex2i(x, y + h);
		}
		gl.glEnd();
	}

	public static void glDrawSpriteExt(GL2 gl, int tex, int x, int y, int w, int h, double angle, Color col) {

		//int xoff = -(window_width/2 - x) * 32 / window_width;

		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
		gl.glBegin(GL2.GL_QUADS);
		{
			int centerX = x; //+ w/2;
			int centerY = y; //+ h/2;

			int x1, y1, x2, y2, x3, y3, x4, y4;

			double dir1, dir2, dir3, dir4, radius;
			/*
        	dir1 = Math.atan2(-h/2, -w/2) + angle;
        	dir2 = Math.atan2(-h/2, w/2) + angle;
        	dir3 = Math.atan2(h/2, w/2) + angle;
        	dir4 = Math.atan2(h/2, -w/2) + angle;
			 */
			dir3 = Math.atan2(h/2, w/2) + angle;
			dir4 = Math.atan2(h/2, -w/2) + angle;
			dir1 = Math.atan2(-h/2, -w/2) + angle;
			dir2 = Math.atan2(-h/2, w/2) + angle;

			radius = distance(0,0,w,h)/2;

			x1 = (int) (centerX + radius * Math.cos(dir1));
			y1 = (int) (centerY + radius * Math.sin(dir1));

			x2 = (int) (centerX + radius * Math.cos(dir2));
			y2 = (int) (centerY + radius * Math.sin(dir2));

			x3 = (int) (centerX + radius * Math.cos(dir3));
			y3 = (int) (centerY + radius * Math.sin(dir3));

			x4 = (int) (centerX + radius * Math.cos(dir4));
			y4 = (int) (centerY + radius * Math.sin(dir4));

			gl.glColor4ub((byte)col.getRed(), (byte)col.getGreen(), (byte)col.getBlue(), (byte)col.getAlpha());
			gl.glTexCoord2f(0, 1);
			gl.glVertex2i(x1, y1);
			gl.glTexCoord2f(1, 1);
			gl.glVertex2i(x2, y2);
			gl.glTexCoord2f(1, 0);
			gl.glVertex2i(x3, y3 );
			gl.glTexCoord2f(0, 0);
			gl.glVertex2i(x4, y4);
		}
		gl.glEnd();
	}

	//2D distance
	public static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(sqr(x2 - x1)+sqr(y2-y1));
	}

	//3D distance
	public static double distance(double x1, double y1,double z1, double x2, double y2,double z2)
	{
		return Math.sqrt(sqr(x2 - x1)+sqr(y2-y1)+sqr(z2-z1));
	}

	public static double sqr(double num)
	{
		return num * num;
	}

	public static boolean isEven(int num)
	{
		return (num % 2 == 0);
	}

	/**
	 * Collision Ray detects if there is a object colliding with the line between two grid coordinates
	 * @param x1 Grid Coordinate Point1 X
	 * @param y1 Grid Coordinate Point1 Y
	 * @param z1 Grid Coordinate Point1 Z
	 * @param x2 Grid Coordinate Point2 X
	 * @param y2 Grid Coordinate Point2 Y
	 * @param z2 Grid Coordinate Point2 Z
	 * @return Returns true if there is a collision between point1 and point2
	 */
	public static boolean collision_ray(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		double dist = distance(x1,y1,x2,y2);
		double direction = Math.atan2(y2-y1 , x2-x1);
		double xdirection = Math.atan2(z2-z1 , dist);
		double scanx = x1, scany = y1, scanz = z1;
		int steps = (int) (distance(x1,y1,x2,y2));
		int steps_taken = 0;
		while(steps_taken < steps)
		{
			scanx += Math.cos(direction);
			scany += Math.sin(direction);
			scanz += Math.sin(xdirection);
			int gridx1 = (int)(scanx );
			int gridy1 = (int)(scany );
			int gridz1 = (int)(scanz );
			//System.out.println("HERE "+gridx1+", "+gridy1+", "+gridz1);

			//If there is an object in the path, return true
			//if (gridx1 >= 0 && gridy1 >= 0 && gridz1 >= 0
			//		&& gridx1 < game_htiles && gridy1 < game_vtiles && gridz1 < game_ztiles)
			if (inregion3d(gridx1,gridy1,gridz1,0,0,0,game_htiles,game_vtiles,game_ztiles))
			{
				if (objects_grid[gridz1][gridy1][gridx1] != null)
				{
					//System.out.println(objects_grid[gridz1][gridy1][gridx1].getClass().getName());
					return true;
				}
			}

			steps_taken ++;
		}
		return false;
	}

	public static boolean AABBIntersect(int box_x1, int box_y1, int box_w1, int box_h1, int box_x2, int box_y2, int box_w2, int box_h2)
	{
		// box1 to the right
		if (box_x1 > box_x2 + box_w2) {
			return false;
		}
		// box1 to the left
		if (box_x1 + box_w1 < box_x2) {
			return false;
		}
		// box1 below
		if (box_y1 > box_y2 + box_h2) {
			return false;
		}
		// box1 above
		if (box_y1 + box_h1 < box_y2) {
			return false;
		}
		//Returns true if the boxes are intersecting
		return true;
	}

	public static Point getMouseLocation(GLWindow window){
		int x = MouseInfo.getPointerInfo().getLocation().x - window.getX();
		int y = MouseInfo.getPointerInfo().getLocation().y - window.getY();
		return new Point(x,y);
	}

	public static void drawProgressBar(GL2 gl, int x, int y, int width, int height, double maxVal, double curVal) //, Color backColor, Color frontColor
	{
		//public static void glDrawSprite(GL2 gl, int tex, int x, int y, int w, int h)
		glDrawSprite(gl, spr_enemy_healthback.getTex(0) , x, y, width, height);
		glDrawSprite(gl, spr_enemy_healthfront.getTex(0) , x, y, (int) ((curVal / maxVal) * width), height);
	}

	public static GameObject createEffect(Sprite effectSprite, double playSpeed, int x, int y, int z, int width, int height)
	{
		GameObject newEffect = new GameObject();
		newEffect.setSprite(effectSprite);
		newEffect.image_speed = playSpeed;
		newEffect.x = x;
		newEffect.y = y;
		newEffect.z = z;
		newEffect.depth = -(y+z);
		newEffect.w = width;
		newEffect.h = height;
		newEffect.animation_loops = false;
		effects.add(newEffect);

		return newEffect;
	}

	public static GameObject createEffect(Sprite effectSprite, double playSpeed, int x, int y, int z, double vx, double vy, double vz)
	{
		GameObject newEffect = new GameObject();
		newEffect.setSprite(effectSprite);
		newEffect.image_speed = playSpeed;
		newEffect.x = x;
		newEffect.y = y;
		newEffect.z = z;

		newEffect.vx = vx;
		newEffect.vy = vy;
		newEffect.vz = vz;

		newEffect.depth = -(y+z);
		newEffect.animation_loops = false;
		effects.add(newEffect);

		return newEffect;
	}

	public static GameObject createEffect(Sprite effectSprite, double playSpeed, int x, int y, int z)
	{
		GameObject newEffect = new GameObject();
		newEffect.setSprite(effectSprite);
		newEffect.image_speed = playSpeed;
		newEffect.x = x;
		newEffect.y = y;
		newEffect.z = z;
		newEffect.depth = -(y+z);
		newEffect.animation_loops = false;
		effects.add(newEffect);

		return newEffect;
	}

	public static boolean inRectangle(int x, int y, int x1, int y1, int x2, int y2)
	{
		int w = Math.abs(x1-x2);
		int h = Math.abs(y1-y2);
		int xc = (x1+x2)/2;
		int yc = (y1+y2)/2;
		if (Math.abs(x - xc)<w/2 && Math.abs(y - yc)<h/2)
		{
			return true;
		}
		return false;
	}

	public static boolean drawButton(Sprite buttonSpr, int x, int y, int width, int height, String buttonText)
	{
		boolean ret = false;
		glDrawSprite(gl, buttonSpr.getTex(0), (int)x, y, width, height);
		if (inRectangle(mouse.x,mouse.y,x,y,x+width,y+height))
		{
			in_interface = true;
			glDrawSprite(gl, buttonSpr.getTex(0), x, y, width, height);
			if (mbState[1])
				glDrawSprite(gl, buttonSpr.getTex(0), x, y, width, height);

			if (!mbState[1] && mbPrevState[1])
			{
				playSound("click.wav");
				ret = true;
			}
		}

		drawText(buttonText,x+width/2-stringWidth(buttonText)/2,y+height/2-stringHeight(buttonText)/2);

		return ret;
	}

	/*
	 * Draw a button that can be selected and tabbed between
	 * 
	 */
	public static boolean drawButtonTab(Sprite buttonSpr, int x, int y, int width, int height, String buttonText, int curTab, int tabID)
	{
		menuItems++;
		boolean ret = false, in = false;
		glDrawSprite(gl, buttonSpr.getTex(0), (int)x, y, width, height);
		if (controlMode == ControlMode.ARROWKEYS_Z && (curTab == tabID))
		{
			glDrawSprite(gl, buttonSpr.getTex(0), (int)x, y, width, height);
		}

		if (controlMode == ControlMode.ARROWKEYS_Z && (kbState[KeyEvent.VK_ENTER] && curTab == tabID))
		{
			glDrawSprite(gl, buttonSpr.getTex(0), (int)x, y, width, height);
		}

		if (inRectangle(mouse.x,mouse.y,x,y,x+width,y+height))
		{
			in_interface = true;
			glDrawSprite(gl, buttonSpr.getTex(0), x, y, width, height);
			if (mbState[1])
				glDrawSprite(gl, buttonSpr.getTex(0), x, y, width, height);
			in = true;
		}

		if (clickTimer <= 0 && ((!mbState[1] && mbPrevState[1] && in) 
				|| (kbState[KeyEvent.VK_ENTER] == false && kbPrevState[KeyEvent.VK_ENTER] == true && curTab == tabID)))
		{
			playSound("click.wav");
			ret = true;
			clickTimer = 150;
		}

		drawText(buttonText,x+width/2-stringWidth(buttonText)/2,y+height/2-stringHeight(buttonText)/2);

		return ret;
	}

	public static void drawRectangle(int x1, int y1, int x2, int y2)
	{
		glDrawSprite(gl, spr_hud_back.getTex(0),x1,y1,x2-x1,y2-y1);
	}

	public static void infobox(int x1, int y1, String text)
	{
		//DRAWS TEXT WITH A RETANGLE BEHIND IT
		int w = stringWidth(text);
		int h = stringHeight(text);
		
		if (x1 - w/2 < 20)
		{
			x1 = w/2 + 20;
		}
		drawRectangle(x1 - w/2 - 10, y1 - h/2 - 10, x1 + w/2 + 10, y1 + h/2 + 10);
		drawText(text,x1 - w/2,y1 - h/2);
	}

	public static boolean keyboard_check(int key)
	{
		return kbState[key];
	}
	public static boolean keyboard_check_pressed(int key)
	{
		return kbState[key] && !kbPrevState[key];
	}
	public static boolean keyboard_check_released(int key)
	{
		return !kbState[key] && kbPrevState[key];
	}

	public static void addToInventory(InventoryObject obj)
	{
		for(int i = 0 ; i < inventory.size() ; i++)
		{
			if (inventory.get(i).name.equals(obj.name))
			{
				inventory.get(i).quantity++;
				return;
			}
		}
		inventory.add(obj);
	}

	public static void removeFromInventory(InventoryObject obj)
	{
		for(int i = 0 ; i < inventory.size() ; i++)
		{
			if (inventory.get(i).name.equals(obj.name))
			{
				if (inventory.get(i).quantity > 1)
					inventory.get(i).quantity--;
				else
					inventory.remove(i);
				return;
			}
		}
		inventory.add(obj);
	}
	
	public static boolean inregion(int x, int y, int x1, int y1, int x2, int y2)
	{
		if (x > x1 && y > y1 && x < x2 && y < y2 )
		return true;
		return false;
	}
	
	public static boolean inregion3d(int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		if (x > x1 && y > y1 && z > z1 && x < x2 && y < y2 && z < z2 )
		return true;
		return false;
	}
	
	

	//TODO: void drawProgressBar(int x, int y, int width, int height, double maxVal, double curVal, Color backColor, Color frontColor) //ANDREW
	//TODO: void createEffect(Sprite effectSprite, double playSpeed, int x, int y, int width, int height)
	//TODO: add map loading from file functionality
	//TODO: add save game functionality
}
