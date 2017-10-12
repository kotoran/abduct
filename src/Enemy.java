import java.util.Random;

public class Enemy extends GameObject {
	
	private double move_speed = .1;
	private double patrolDistance;
	private double maxPatrolDistance;
	private Behaviour currentBehaviour;
	private double axis;
	private double direction = Math.toDegrees((Math.random()) * 360);
	private boolean hasBeenShot = false;
	public String greetingText = "";
	private double timeSinceSeenPlayer = 0;
	
	private static double behaviourTimer, initialBehaviourTimer;

	Weapon standardBlaster = new Weapon("STANDARD BLASTER");
	
	public Weapon primaryWeapon = standardBlaster;
	
	double getTimeLeft()
	{
		return behaviourTimer;
	}
	
	public Weapon getWeapon()
	{
		return primaryWeapon;
	}

	void updateTimer(double delta_time)
	{
		behaviourTimer -= delta_time;
	}
	
	void resetTimer()
	{
		behaviourTimer = currentBehaviour.behaviourMaxTime;
	}
	
	public Enemy()
	{
		super();
		currentBehaviour = Behaviour.PATROL;
		patrolDistance = 0;
		maxPatrolDistance = 1024;
		gravity_z = .1;
		standardBlaster.setDamage(1);
	}
	
	public Enemy(Behaviour initialBehavior)
	{
		super();
		currentBehaviour = initialBehavior;
		gravity_z = .1;
		standardBlaster.setDamage(1);
	}
	
	public enum Behaviour {
	    CHARGE(100, .1f), RUN(200, .30f), PATROL(200, .50f), SHOOT(100, .85f), GREET(100, 1f);
		
		private float weight; // % of time this should happen
		private double behaviourMaxTime;
		Behaviour(double timer, float weight)
		{
			behaviourMaxTime = timer;
			this.weight = weight;
			//behaviourTimer = timer;
		}
	}
	
	public void setBehaviour(Behaviour newBehaviour)
	{
		currentBehaviour = newBehaviour;
		behaviourTimer = newBehaviour.behaviourMaxTime;
	}
	
	public Behaviour rollBehaviour()
	{

		//greetingText = "";
		Random r = new Random();
		float rolled = r.nextFloat();
		if (rolled < Behaviour.CHARGE.weight )
			return Behaviour.CHARGE;
		else
			if (rolled < Behaviour.RUN.weight)
				return Behaviour.RUN;
			else
				if (rolled < Behaviour.PATROL.weight)
					return Behaviour.PATROL;
				else
					return Behaviour.SHOOT;

	}
	
	public void updateAI(double delta_time)
	{
		if (getTimeLeft()<= 0)
		{
			setBehaviour(rollBehaviour());

		}
		else
		{
			updateTimer(delta_time);
		}

	}
	
	public void executeBehaviour(double delta_time) //, ArrayList<GameObject> projecileList
	{	
		if (timeSinceSeenPlayer > 0)
			timeSinceSeenPlayer -= delta_time;
		/*
		switch (currentBehaviour)
		{
		case SHOOT:
			greetingText = "SHOOTING";
			break;
		case PATROL:
			if (health < maxhealth)
			{
				currentBehaviour = Behaviour.SHOOT;
			greetingText = "GREET";
			}
			greetingText = "PATROL";
			break;
		case CHARGE:
			greetingText = "CHARGE";
			break;
		case RUN:
			greetingText = "RUN";
			break;
		case GREET:
			if (health < maxhealth)
			{
				currentBehaviour = Behaviour.SHOOT;
			greetingText = "GREET";
			}
			break;
		}	*/
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x > JavaTemplate.game_width - w)
			x = JavaTemplate.game_width - w;
		if (y > JavaTemplate.game_height - h)
			y = JavaTemplate.game_height - h;
		
		behaviourTimer-=delta_time;
		
		GameObject player = JavaTemplate.player;
		
		int grid_x = (int)(x / JavaTemplate.tile_width);
		int grid_y = (int)(y / JavaTemplate.tile_height);
		int grid_z = (int)(z / JavaTemplate.tile_height);
		
		int grid_forward_x = (int)((x + vx * 320) / JavaTemplate.tile_width);
		int grid_forward_y = (int)((y + vy * 320) / JavaTemplate.tile_height);
		int grid_forward_z = (int)((z + vz * 320) / JavaTemplate.tile_height);
		
		int player_grid_x = (int)(player.x / JavaTemplate.tile_width);
		int player_grid_y = (int)(player.y / JavaTemplate.tile_height);
		int player_grid_z = (int)(player.z / JavaTemplate.tile_height);
		
		boolean seesPlayer;
		
		//System.out.println("HERE");
		
		seesPlayer = !JavaTemplate.collision_ray( grid_x, grid_y, grid_z, player_grid_x, player_grid_y, player_grid_z);
		
		if (seesPlayer)
		{
		timeSinceSeenPlayer = 100;
		greetingText = "I SEE YOU";
		}
		else
		{
		greetingText = "CANT SEE YOU";
		}
		
		if (behaviourTimer > 0)
		{
		switch(currentBehaviour)
			{
			case CHARGE:
				//System.out.println("CHARGE");
				move_towards(player,delta_time);
				
				if (distance(this.x, this.y, player.x, player.y) < 200  
						&& Math.abs(z - player.z) < this.h
						&& !seesPlayer)
				{
					respond_to_player();
				}
				//ATTACK
				
				break;
			case RUN:
				move_away(player,delta_time);
				
				//If can't run then attack
				if (JavaTemplate.collision_ray( grid_x, grid_y, grid_z, 
						grid_forward_x, grid_forward_y, grid_forward_z))
				{
					currentBehaviour = Behaviour.SHOOT;
				}
				break;
			case PATROL:
				if (seesPlayer) //&& distance(this.x, this.y, player.x, player.y) < 300  
						//&& Math.abs(z - player.z) < this.h)
				{
					respond_to_player();
				}
				
				move_direction(direction, delta_time);
				//vx = Math.cos(direction);
				//vy = Math.sin(direction); 
				
				if (priorHealth < health)
				{
					rollBehaviour();
				}
				break;
			case SHOOT:
				if (player.health >= 0)
				{
				if (timeSinceSeenPlayer > 0)
					{
					move_towards(player,delta_time);
					}
				else
					{
					currentBehaviour = Behaviour.PATROL;
					}
				if (seesPlayer) //distance(this.x, this.y, player.x, player.y) < 300  && Math.abs(z - player.z) < this.h)
					{
		    		//Shooting
					shoot(player);
					}
				}
				break;
			case GREET:
				
	    		move_towards(player,delta_time);
				break;	
			}
		}
		else
			{
			direction = Math.toRadians((Math.random()) * 360);
			if (distance(this.x, this.y, player.x, player.y) < 300  && Math.abs(z - player.z) < this.h)
			{
				respond_to_player();
			}
			else
				{
				//if (currentBehaviour == Behaviour.SHOOT)
				//	System.out.print("Lost sight of player. ");
				currentBehaviour = Behaviour.PATROL;
				//System.out.println("Set behaviour to patrol");
				}

			behaviourTimer = currentBehaviour.behaviourMaxTime;
			}
		
		if (vx > .1) mx = -1;
		if (vx<-.1) mx = 1;
		
		updateHealth();
	}
	
	private void updateHealth()
	{
		if(health != priorHealth)
		{
			priorHealth = health;
		}
	}
	
	public void respond_to_player()
	{
		Random r = new Random();
		float rolled = r.nextFloat();
		//if (health < maxhealth)
		//{
			if (rolled < Behaviour.RUN.weight * 2f)
			{
				currentBehaviour = Behaviour.RUN;
				//greetingText = "AHH";
			}
			else
			{
				currentBehaviour = Behaviour.SHOOT;
				//greetingText = "DIE";
			}
		//}
		/*else
		{
			currentBehaviour = Behaviour.GREET;
			//greetingText = "HELLO";
			//System.out.println("PLAYER SIGHTED!!!!!! Set behaviour to greet");
		}*/
	}

	public void move_direction(double direction, double delta_time)
	{
	boolean moving = false;
		
			
			//Convert to a vector
			double input_x_normalized = Math.cos(direction);
			double input_y_normalized = Math.sin(direction);

			//Add normalized vector to velocity x and y components
			this.vx += input_x_normalized * move_speed * delta_time * 1;
			this.vy += input_y_normalized * move_speed * delta_time * 1;
		
        if (moving)
            {
            	this.image_index += delta_time / 100;
            	if (this.image_index >= this.sprite_index.subimage.size())
            	{
            		this.image_index = 0;
            	}
            }
	}
	
	public void move_towards(GameObject target, double delta_time)
	{
	boolean moving = false;
		
	if (distance(this.x, this.y, target.x, target.y) > 120)
		if (distance(this.x, this.y, target.x, target.y) < 620)
    		{
			double target_direction = Math.atan2(target.y - this.y, target.x - this.x);

			//Convert to a vector
			double input_x_normalized = Math.cos(target_direction);
			double input_y_normalized = Math.sin(target_direction);

			//Add normalized vector to velocity x and y components
			this.vx += input_x_normalized * move_speed * delta_time * 5;
			this.vy += input_y_normalized * move_speed * delta_time * 5;

        	moving = true;
			if (target.x < this.x)
				this.mx = 1;
			else
				this.mx = -1;
    		}
		

	if (Math.random()>.95)
		if (distance(this.x, this.y, target.x, target.y) < 400)
		if (target.z > z + 16)
		{
			if (z == ground_level)
			{
				vz = 16;
			}
		}
	if (moving)
    {
	image_speed = .01;
		/*
    	if (this.image_index >= this.sprite_index.subimage.size())
    	{
    		this.image_index = 0;
    	}
		 */
    }
	else
	{
		image_index = 0;
		image_speed = 0;
	}
//        if (moving)
//            {
//            	this.image_index += delta_time / 100;
//            	if (this.image_index >= this.sprite_index.subimage.size())
//            	{
//            		this.image_index = 0;
//            	}
//            }
	}
	
	public void move_away(GameObject target, double delta_time)
	{
	boolean moving = false;
	
	if (distance(this.x, this.y, target.x, target.y) > 220)
		if (distance(this.x, this.y, target.x, target.y) < 620)
    		{
			double target_direction = Math.atan2(target.y - this.y, target.x - this.x);

			//Convert to a vector
			double input_x_normalized = Math.cos(target_direction);
			double input_y_normalized = Math.sin(target_direction);

			//Add normalized vector to velocity x and y components
			this.vx -= input_x_normalized * move_speed * delta_time * 5;
			this.vy -= input_y_normalized * move_speed * delta_time * 5;

        	moving = true;
			if (target.x < this.x)
				this.mx = 1;
			else
				this.mx = -1;
    		}
		
        if (moving)
            {
        	image_speed = .01;
        		/*
            	if (this.image_index >= this.sprite_index.subimage.size())
            	{
            		this.image_index = 0;
            	}
        		 */
            }
        else
        {
        	image_index = 0;
        	image_speed = 0;
        }
	}

	public void shoot(GameObject target)
	{
	//NPC Shooting Controls
	if (distance(this.x, this.y, target.x, target.y) < 300  && Math.abs(z - target.z) < this.h) 
	{
    	if (shoot_timer <= 0)
		{
    		GameObject newProjectile = new GameObject();
    		JavaTemplate.gameObjects.add(newProjectile);
    		newProjectile.setSprite(JavaTemplate.spr_projectile);
			newProjectile.x = x;
			newProjectile.y = y;
			newProjectile.z = z + h / 4;

			//ADD PROJECTILES SHADOWS
    		GameObject newProjectileShadow = new GameObject();
    		newProjectileShadow.setSprite(JavaTemplate.spr_projectile_shadow);
			JavaTemplate.projectiles_shadows.add(newProjectileShadow);
			JavaTemplate.gameObjects.add(newProjectileShadow);
			
			newProjectile.depth = -newProjectile.y-newProjectile.z;
			
			double target_direction = Math.atan2(target.y-y,target.x-x);
			newProjectile.image_angle = target_direction;
			
			newProjectile.setVX(Math.cos(target_direction) * 10 + vx);
			newProjectile.setVY(Math.sin(target_direction) * 10 + vy);
			newProjectile.setFriction(0);
			
			newProjectile.health = 1;
			
			//object[objects].angle = aim_direction;
			JavaTemplate.projectiles.add(newProjectile);
			
			newProjectile.team = 1; //team 1 is player's enemies

			shoot_timer = 200;
			
			//JavaTemplate.soundPlayer.playClip(JavaTemplate.pewClip);
			JavaTemplate.playSound("weapons/pew.wav");
		}
    }
	}
}
