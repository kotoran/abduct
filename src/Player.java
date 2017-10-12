import java.awt.Point;
import java.util.ArrayList;

import com.jogamp.newt.event.KeyEvent;


public class Player extends GameObject {
	public double aim_direction = 0;
	boolean input_jump_prev = false;

	int tile_width = JavaTemplate.tile_width;
	int tile_height = JavaTemplate.tile_height;
	int game_htiles = JavaTemplate.game_htiles;
	int game_vtiles = JavaTemplate.game_vtiles;
	int game_ztiles = JavaTemplate.game_ztiles;

	int bursts_left = 0;
	
	//a value of > than 1 spawns more than 1 bullet in a straight line
	int burst = 1;
	double burstTimerMS;
	//a value of > than 1 spawns more than 1 bullet in a spread
	int spread = 3;
	
	//bool beam = false;

	//Handles Cheats
	boolean invincibilityCheatActive = false;
	
	GameObject objects_grid[][][] = JavaTemplate.objects_grid;
	ArrayList<GameObject> projectiles = JavaTemplate.projectiles;
	ArrayList<GameObject> projectiles_shadows = JavaTemplate.projectiles_shadows;
	ArrayList<GameObject> gameObjects = JavaTemplate.gameObjects;
	
	//A standard blaster, simple low level weapon.
	public static Weapon standardBlaster = new Weapon("STANDARD BLASTER");

			/*
			1,   //Damage
			0,   //Knockback
			500, //Reload Time
			1,   //Projectiles Per Shot
			100, //Ammo
			100, //Max Ammo
			20   //Angle Range (Weapon Accuracy)
			);*/

	//The primary weapon the player is holding
	Weapon primaryWeapon = null;
	
	//The secondary weapon the player is holding
	Weapon secondaryWeapon = null;

	boolean kbState[] = JavaTemplate.kbState;
	boolean kbPrevState[] = JavaTemplate.kbPrevState;
	boolean kbStatePressed[] = JavaTemplate.kbStatePressed;

	boolean mbState[] = JavaTemplate.mbState;
	boolean mbPrevState[] = JavaTemplate.mbPrevState;
	boolean mbStatePressed[] = JavaTemplate.mbStatePressed;

	Point mouse = JavaTemplate.mouse;

	JavaTemplate.ControlMode controlMode  = JavaTemplate.controlMode;

	//Player movement speed scalar
	public static double move_speed = .1;
	/*
	private enum Weapons { 
		STANDARD(2.0f, 1.0f, 1, 1), SHOTGUN(2.0f, 1.0f, 1, 1), LASER(2.0f, 1.0f, 1, 1);
		private float speed, damageMultiplier;
		private int spread, burstMode;
		Weapons(float speed, float damageMultiplier, int spread, int burstMode)
		{
			this.speed = speed;
			this.damageMultiplier = damageMultiplier;
			this.spread = spread;
			this.burstMode = burstMode;
		}
	}
	 */

	public void controls(double deltaTimeMS, Camera cam)
	{
		if(invincibilityCheatActive) {health = maxhealth;}
		objects_grid = JavaTemplate.objects_grid;
		projectiles = JavaTemplate.projectiles;
		projectiles_shadows = JavaTemplate.projectiles_shadows;
		gameObjects = JavaTemplate.gameObjects;

		kbState = JavaTemplate.kbState;
		kbPrevState = JavaTemplate.kbPrevState;
		kbStatePressed = JavaTemplate.kbStatePressed;

		mbState = JavaTemplate.mbState;
		mbPrevState = JavaTemplate.mbPrevState;
		mbStatePressed = JavaTemplate.mbStatePressed;

		mouse = JavaTemplate.mouse;

		controlMode  = JavaTemplate.controlMode;

		//Movement Controls

		int player_grid_x = (int) ((x) / tile_width);
		int player_grid_y = (int) ((y) / tile_height);
		int player_grid_z = (int) ((z) / tile_height);
		if (player_grid_z < 0)
			player_grid_z = 0;
		if (player_grid_z > objects_grid.length-1)
			player_grid_z = objects_grid.length-1;

		/*
		//Player dies when they fall into water or lava
		if (background[player_grid_y][player_grid_x] == 0 && z == 0)
		{
			health -= 1 * deltaTimeMS;
		}
		 */

		ground_level = 0; //Math.floor(z/tile_height) * tile_height;
		for(int i = player_grid_z ; i >= 0 ; i--)
		{
			if (objects_grid[i][player_grid_y][player_grid_x] != null)
			{
				ground_level = (i+1) * tile_height + 1;
				break;
			}
		}
		//Platform collisions
		/*
    	if (AABBIntersect((int)x+xorig-8,(int)y+yorig-8,16,16,(int)platform.x+32,(int)platform.y+64+8,platform.w-16,platform.h-32-16))
    		{
			if (z > platform.z + 32)
				{
				if (z < platform.z + 64 && vz < 0)
    				{
                		z = platform.z + 33;
                		vz = platform.vz;
                	}
        		ground_level = platform.z + 33;
            	}
			else
				{
				if (z + h / 2 > platform.z)
					{
					vz = -Math.abs(vz);
					z = platform.z - h;
					}
				}
        	}
		 */

		int player_grid_pixel_x = (int)(x - player_grid_x * tile_width);
		int player_grid_pixel_y = (int)(y - player_grid_y * tile_height);

		//COLLISION DETECTION, wall collisions
		boolean collision_north = false;
		boolean collision_south = false;
		boolean collision_left = false;
		boolean collision_right = false;

		boolean collision_below = false;

		//if (z < 32)
		//{
		double scale = 1/4;
		if (Math.abs(tile_width / 2 - player_grid_pixel_x) < tile_width/2 + 2)
		{		
			if (player_grid_y > 0 && objects_grid[player_grid_z][player_grid_y-1][player_grid_x] != null && player_grid_pixel_y < tile_height - tile_height * scale 
					&& sameZas(objects_grid[player_grid_z][player_grid_y-1][player_grid_x]))
			{
				collision_north = true;
			}
			if (player_grid_y < game_vtiles - 10 && objects_grid[player_grid_z][player_grid_y+1][player_grid_x] != null && player_grid_pixel_y > tile_height * scale 
					&& sameZas(objects_grid[player_grid_z][player_grid_y+1][player_grid_x]))
			{
				collision_south = true;
			}
		}
		if (Math.abs(tile_height / 2 - player_grid_pixel_y) < tile_height/2 + 2)
		{
			if (player_grid_x > 0 && objects_grid[player_grid_z][player_grid_y][player_grid_x-1] != null && player_grid_pixel_x < tile_height - tile_width * scale 
					&& sameZas(objects_grid[player_grid_z][player_grid_y][player_grid_x-1]))
			{
				collision_left = true;
			}
			if (player_grid_x < game_htiles - 1 && objects_grid[player_grid_z][player_grid_y][player_grid_x+1] != null && player_grid_pixel_x > tile_width * scale  
					&& sameZas(objects_grid[player_grid_z][player_grid_y][player_grid_x+1]))
			{
				collision_right = true;
			}
		}
		//}


		if (Math.abs(player_grid_pixel_x - tile_width / 2) < tile_width/2 + 2 
				&& (Math.abs(player_grid_pixel_y - tile_height / 2) < tile_height/2 + 2)
				&& z >= 32
				&& objects_grid[player_grid_z][player_grid_y][player_grid_x] != null)
		{
			collision_below = true;
		}

		double input_x = 0;
		double input_y = 0;


		//Movement controls
		boolean moving = false;

		if (health > 0)
		{
			if (!collision_left)
				if (kbState[KeyEvent.VK_A] || kbState[KeyEvent.VK_LEFT]) {
					input_x = -1;
					//vx -= move_speed * deltaTimeMS;
					setMX(-1);
					moving = true;
				}
			if (!collision_right)
				if (kbState[KeyEvent.VK_D] || kbState[KeyEvent.VK_RIGHT]) {
					input_x = 1;
					//vx += move_speed * deltaTimeMS;
					setMX(1);
					moving = true;
				}
			if (!collision_north)
				if (kbState[KeyEvent.VK_W] || kbState[KeyEvent.VK_UP]) {
					input_y = -1;
					//vy -= move_speed * deltaTimeMS;
					moving = true;
				}
			if (!collision_south)
				if (kbState[KeyEvent.VK_S] || kbState[KeyEvent.VK_DOWN]) {
					input_y = 1;
					//vy += move_speed * deltaTimeMS;
					moving = true;
				}

			if (moving)
			{
				image_index += deltaTimeMS / 50;
				if (image_index >= sprite_index.subimage.size())
				{
					image_index = 0;
				}
			}

			//testDir+= .001 * deltaTimeMS;

			//Get input direction and magnitude
			double input_direction = Math.atan2(input_y, input_x);
			double input_magnitude = Math.min(Math.abs(input_x) + Math.abs(input_y),1); //Math.sqrt(input_x*input_x + input_y*input_y);

			//Convert to a vector of magnitude input_magnitude (ranging from 0 to 1)
			double input_x_normalized = Math.cos(input_direction) * input_magnitude;
			double input_y_normalized = Math.sin(input_direction) * input_magnitude;

			//Add normalized vector to velocity x and y components
			vx += input_x_normalized * move_speed * deltaTimeMS;
			vy += input_y_normalized * move_speed * deltaTimeMS;

			resolveWallCollisions(tile_width, tile_height, game_htiles, game_vtiles, game_ztiles, objects_grid);

			Shoot(deltaTimeMS);
			//burstFireUpdate();

			if (controlMode == JavaTemplate.ControlMode.ARROWKEYS_Z)
			{
				if (distance(0, 0, vx, vy) > .1 * deltaTimeMS && moving)
				{
					aim_direction = Math.atan2(vy, vx);
				}
			}

			//Save the last direction the player moved into a variable so we can know which direction to shoot
			if (controlMode == JavaTemplate.ControlMode.MOUSE_WASD)
			{
				aim_direction = Math.atan2(mouse.y-(y-z-cam.y), mouse.x-(x-cam.x));
			}

			if (kbState[KeyEvent.VK_SPACE] && move_speed == .1) {
				if (vz > 0)
				{
					vz += .03 * deltaTimeMS;
				}
				if (z >= ground_level && z <= ground_level+1 && !input_jump_prev)
				{
					vz = 16;
					input_jump_prev = true;
				}
				//else
				//System.out.println("Cant jump, z = "+z+" ground_level = "+ground_level);
			}
			else
			{
				input_jump_prev = false;
			}

			setFriction(sqr(velocity) / 500 + .02 );
		}
	}

	private void Shoot(double deltaTimeMS)
	{
		//Shooting Controls
		shoot_timer -= deltaTimeMS;
		
		if (shoot_timer <= 0)
		{
		if (bursts_left > 0)
			{
			//Fire projectile(s)
			/*
			if (primaryWeapon.projectilesPerShot % 2 != 0)
			{
				//If odd number of blasts, shoot a center shot
				fireProjectile(aim_direction);
			}*/
			//for(double i = -(primaryWeapon.projectilesPerShot-1)/2; i <= (primaryWeapon.projectilesPerShot); i++)
			
			double startDir = -(primaryWeapon.projectilesPerShot-1)/2 * 5;
			
			for(double i = 0; i < primaryWeapon.projectilesPerShot ; i++)
			{
				double fireDir = aim_direction + Math.toRadians(startDir + i * 5 
						+ (Math.random()-.5) * primaryWeapon.angleRange);
				fireProjectile(fireDir);
			}
			
			//Subtract one from remaining bursts
			bursts_left--;
			
			//If there are bursts left, set shoot timer to burst time
			if (bursts_left > 0)
				{
				shoot_timer = primaryWeapon.burstTime;
				}
			else //If there are no bursts left, set shoot timer to reload time
				{
				shoot_timer = primaryWeapon.reloadTime;
				}
			}
		}
		/*
		boolean okToShoot = false;
		if (primaryWeapon.automatic)
			okToShoot = true;
		else
			{
			if (!mbPrevState[1]) 
				okToShoot = true;
			}
			*/
			
		if ((kbState[KeyEvent.VK_Z] || (mbState[1] && !JavaTemplate.in_interface)) && bursts_left == 0) {
			
			if (mbState[1])
				controlMode = JavaTemplate.ControlMode.MOUSE_WASD;
			if (kbState[KeyEvent.VK_Z])
				controlMode = JavaTemplate.ControlMode.ARROWKEYS_Z;

			if (shoot_timer <= 0)
			{
				bursts_left = primaryWeapon.bursts;
				System.out.println(bursts_left);
			}
		}	
	}

	//Fires a projectile with an angle to offset by
	private void fireProjectile(double shootDirection)
	{
		GameObject newProjectile = new GameObject();
		projectiles.add(newProjectile);
		gameObjects.add(newProjectile); //need this line only because this happens after normal gameObjects projectile adding
		newProjectile.setSprite(JavaTemplate.spr_projectile);
		newProjectile.x = x; //+ spr_projectile.width / 2;
		newProjectile.y = y; //+ spr_projectile.height / 2;
		
		newProjectile.image_angle = shootDirection;
		newProjectile.z = z + h / 1.6;
		newProjectile.depth = -newProjectile.y-newProjectile.z;
		newProjectile.setVX(Math.cos(shootDirection) * 20 + vx);
		newProjectile.setVY(Math.sin(shootDirection) * 20 + vy);
		newProjectile.setFriction(0);
		newProjectile.health = primaryWeapon.damage;
		
		//ADD PROJECTILES SHADOWS
		GameObject newProjectileShadow = new GameObject();
		newProjectileShadow.setSprite(JavaTemplate.spr_projectile_shadow);
		projectiles_shadows.add(newProjectileShadow);
		gameObjects.add(newProjectileShadow);

		newProjectile.team = 0; //team 0 is player's friends
		//newProjectile.angle = aim_directionewProjectile	

		//JavaTemplate.soundPlayer.playClip(JavaTemplate.pewClip);
		JavaTemplate.playSound("weapons/snd_shotgun.wav");
	}

	private void burstFireUpdate()
	{
		if(burstTimerMS > 0)
		{
			fireProjectile(aim_direction);
			burstTimerMS -= 100; //divide total burst over seconds
		}
	}
}
