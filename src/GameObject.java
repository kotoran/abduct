import java.awt.Color;

import com.jogamp.opengl.*;

public class GameObject {
	public double x = 0, y = 0, z = 0, xstart = 0, ystart = 0, zstart = 0, xprevious = 0, yprevious = 0, zprevious = 0, 
			ground_level = 0, vx = 0, vy = 0, vz = 0, depth = 0, image_angle = 0, image_index = 0, friction = 0, 
			velocity = 0, shoot_timer = 0, hit_timer = 0,
			image_speed = 0, gravity_x = 0, gravity_y = 0, gravity_z = 0, 
			health = 10, maxhealth = 10, priorHealth = 10;
																			
	int w = 32, h = 32, shadow_w = 32, shadow_h = 32, spr, mx = 1, my = 1, team = 0; // Sprite width and height, sprite
												// index, mirror x and y
	boolean isEnabled = true, visible = true, reached_animation_end = false, animation_loops = true, invincible = false;

	public Color image_color = new Color(255, 255, 255, 255);

	public Sprite sprite_index, shadow_index;

	public boolean reachedAnimationEnd() {
		return reached_animation_end;
	}

	// Set the x-position of the game object
	public void setX(double new_x) {
		x = new_x;
	}

	// Set the y-position of the game object
	public void setY(double new_y) {
		y = new_y;
	}

	// Set the z-position of the game object
	public void setZ(double new_z) {
		z = new_z;
	}

	// Set the x-component of the velocity of the game object
	public void setVX(double new_vx) {
		vx = new_vx;
	}

	// Set the y-component of the velocity of the game object
	public void setVY(double new_vy) {
		vy = new_vy;
	}

	// Set the z-component of the velocity of the game object
	public void setVZ(double new_vz) {
		vz = new_vz;
	}

	// Set the x-position of the game object
	public void setFriction(double new_friction) {
		friction = new_friction;
	}

	// Set x-mirroring
	public void setMX(int new_mx) {
		mx = new_mx;
	}

	// Set y-mirroring
	public void setMY(int new_my) {
		mx = new_my;
	}

	// Add motion to the object
	public void addMotion(double xc, double yc, double zc) {
		vx += xc;
		vy += yc;
		vz += zc;
	}

	// Set the sprite index of the game object
	public void setSprite(Sprite new_spr) {
		sprite_index = new_spr;
		w = new_spr.getWidth(0);
		h = new_spr.getHeight(0);
	}

	// Set shadow sprite for this object
	public void setShadow(Sprite new_spr) {
		shadow_index = new_spr;
		shadow_w = new_spr.getWidth(0);
		shadow_h = new_spr.getHeight(0);
	}

	// This function not currently being used
	public void draw(GL2 gl, Camera cam) {
		// public static void drawSprite(GL2 gl, int tex, int x, int y, int w,
		// int h, int mx , int my , Camera cam)
		if (visible)
			JavaTemplate.drawSprite(sprite_index,(int) image_index, x, y - z, w, h, mx, my, cam);
	}

	public void update(double delta_time, GameObject[][][] objects_grid) {
		// Add gravitational acceleration to z-velocity of GAMEOBJECT
		
		if (hit_timer > 0)
		{
			if (Math.floor(hit_timer / 2) % 2 == 0)
			{
				image_color = new Color(200, 15, 15, 255);
			}
			else
			{
				image_color = new Color(255, 255, 255, 255);
			}
			hit_timer -= delta_time;
		}
		else
		{
			image_color = new Color(255, 255, 255, 255);
		}
		if (gravity_z > 0) {
			ground_level = 0; // Math.floor(player.z/tile_height) * tile_height;

			int grid_x = (int) ((x) / JavaTemplate.tile_width);
			int grid_y = (int) ((y) / JavaTemplate.tile_height);
			int grid_z = (int) ((z) / JavaTemplate.tile_height);


			if (grid_x < 0)
				grid_x = 0;
			if (grid_x >= objects_grid[0][0].length)
				grid_x = objects_grid[0][0].length-1;
			
			if (grid_y < 0)
				grid_y = 0;
			if (grid_y >= objects_grid[0].length)
				grid_y = objects_grid[0].length-1;
			
			if (grid_z < 0)
				grid_z = 0;
			if (grid_z >= objects_grid.length)
				grid_z = objects_grid.length-1;
			
			for (int i = grid_z; i >= 0; i--) {
				if (objects_grid[i][grid_y][grid_x] != null) {
					ground_level = (i + 1) * JavaTemplate.tile_height + 1;
					break;
				}
			}

			if (z > ground_level) {
				vz -= gravity_z * delta_time;
			}

			// If GAMEOBJECT z-position is below the floor level, set it to the
			// floor level
			if (z < ground_level) {
				z = ground_level;
				vz = 0;
			}
		}

		// PLAY ANIMATION
		if (image_speed != 0)
			image_index += image_speed * delta_time;

		reached_animation_end = false;

		// LOOP ANIMATION
		if (image_index < 0)
			image_index += sprite_index.subimage.size() - 1;

		if (image_index >= sprite_index.subimage.size() - 1) {
			if (animation_loops)
				image_index -= sprite_index.subimage.size() - 1;
			else
				image_index = sprite_index.subimage.size() - 1;
			reached_animation_end = true;
		}
		xprevious = x;
		yprevious = y;
		zprevious = z;

		x += vx; // Add x component of velocity to x-position
		y += vy; // Add y component of velocity to y-position
		z += vz; // Add z component of velocity to z-position

		// Apply force of friction to player
		double direction = (Math.atan2(vy, vx));
		velocity = Math.sqrt(sqr(vx) + sqr(vy));
		double fx = Math.cos(direction) * friction;
		double fy = Math.sin(direction) * friction;

		if (velocity > friction) {
			vx -= fx * delta_time;
			vy -= fy * delta_time;
		} else {
			vx = 0;
			vy = 0;
		}

		velocity = Math.sqrt(sqr(vx) + sqr(vy));
		if (velocity < 0) {
			vx = 0;
			vy = 0;
		}
	}

	// Default constructor for game object
	public GameObject() {
		x = 0;
		y = 0;
		z = 0;
		vx = 0;
		vy = 0;
		vz = 0;
	}

	public int compareTo(GameObject otherGameObject) {
		return Double.compare(otherGameObject.depth, this.depth);
	}

	// 2D distance
	double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(sqr(x2 - x1) + sqr(y2 - y1));
	}

	// 3D distance
	double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt(sqr(x2 - x1) + sqr(y2 - y1) + sqr(z2 - z1));
	}

	double sqr(double num) {
		return num * num;
	}

	public void resolveWallCollisions(int tile_width, int tile_height, int game_htiles, int game_vtiles, int game_ztiles,
			GameObject objects_grid[][][]) {

		// COLLISION DETECTION VARIABLES
		int grid_x = (int) ((x) / tile_width);
		int grid_y = (int) ((y) / tile_height);
		int grid_z = (int) ((z) / tile_height);

		int grid_pixel_x = (int) x - grid_x * tile_width;
		int grid_pixel_y = (int) y - grid_y * tile_height;
		int grid_pixel_z = (int) z - grid_z * tile_height;

		if (grid_x < 0)
			grid_x = 0;
		if (grid_y < 0)
			grid_y = 0;
		if (grid_x > game_htiles - 1)
			grid_x = game_htiles - 1;
		if (grid_y > game_vtiles - 1)
			grid_y = game_vtiles - 1;
		/*
		if (grid_z < 0)
			grid_z = 0;
		if (grid_z > game_ztiles - 1)
			grid_z = game_ztiles - 1;
		*/
		// COLLISION DETECTION, wall collisions
		boolean collision_north = false;
		boolean collision_south = false;
		boolean collision_left = false;
		boolean collision_right = false;
		boolean collision_below = false;
		boolean collision_above = false;
		boolean collision_on = false;

		boolean collision_left_north = false;
		boolean collision_left_south = false;
		boolean collision_right_north = false;
		boolean collision_right_south = false;

		int border = 20;

		if (grid_pixel_z < game_ztiles) {
			// CHECK UP AND DOWN
			if (Math.abs(grid_pixel_x - tile_width / 2) < tile_width / 2 + border) {
				if (grid_y > 0 && objects_grid[grid_z][grid_y - 1][grid_x] != null && grid_pixel_y < tile_height / 2
						&& sameZas(objects_grid[grid_z][grid_y - 1][grid_x])) {
					collision_north = true;
				}
				if (grid_y < game_vtiles - 10 && objects_grid[grid_z][grid_y + 1][grid_x] != null
						&& grid_pixel_y > tile_height / 2 && sameZas(objects_grid[grid_z][grid_y + 1][grid_x])) {
					collision_south = true;
				}
			}
			// CHECK LEFT AND RIGHT
			if (Math.abs(grid_pixel_y - tile_height / 2) < tile_height / 2 + border) {
				if (grid_x > 0 && objects_grid[grid_z][grid_y][grid_x - 1] != null && grid_pixel_x < tile_width / 2
						&& sameZas(objects_grid[grid_z][grid_y][grid_x - 1])) {
					collision_left = true;
				}
				if (grid_x < game_htiles - 1 && objects_grid[grid_z][grid_y][grid_x + 1] != null
						&& grid_pixel_x > tile_width / 2 && sameZas(objects_grid[grid_z][grid_y][grid_x + 1])) {
					collision_right = true;
				}
			}
			// CHECK DIAGONALS
			if (grid_x > 0 && grid_y > 0 && objects_grid[grid_z][grid_y - 1][grid_x - 1] != null
					&& grid_pixel_x < tile_width / 2 && grid_pixel_y < tile_height / 2
					&& sameZas(objects_grid[grid_z][grid_y - 1][grid_x - 1])) {
				collision_left_north = true;
			}
			if (grid_x < game_htiles - 2 && grid_y > 0 && objects_grid[grid_z][grid_y - 1][grid_x + 1] != null
					&& grid_pixel_x > tile_width / 2 && grid_pixel_y < tile_height / 2
					&& sameZas(objects_grid[grid_z][grid_y - 1][grid_x + 1])) {
				collision_right_north = true;
			}
			if (grid_x < game_htiles - 2 && grid_y < game_vtiles - 2
					&& objects_grid[grid_z][grid_y + 1][grid_x + 1] != null && grid_pixel_x > tile_width / 2
					&& grid_pixel_y > tile_height / 2 && sameZas(objects_grid[grid_z][grid_y + 1][grid_x + 1])) {
				collision_right_south = true;
			}
			if (grid_x > 0 && grid_y < game_vtiles - 2 && objects_grid[grid_z][grid_y + 1][grid_x - 1] != null
					&& grid_pixel_x > tile_width / 2 && grid_pixel_y < tile_height / 2
					&& sameZas(objects_grid[grid_z][grid_y + 1][grid_x - 1])) {
				collision_left_south = true;
			}
		}

		if (Math.abs(grid_pixel_x - tile_width / 2) < tile_width / 2 + border
				&& (Math.abs(grid_pixel_y - tile_height / 2) < tile_height / 2 + border)) {
			if (grid_z > 0) {
				if (objects_grid[grid_z - 1][grid_y][grid_x] != null) {
					if (z >= objects_grid[grid_z - 1][grid_y][grid_x].z + 32) {
						collision_below = true;
					}
				}
			}
			if (grid_z < JavaTemplate.game_ztiles - 1) {
				if (objects_grid[grid_z + 1][grid_y][grid_x] != null) {
					if (z <= objects_grid[grid_z + 1][grid_y][grid_x].z) {
						collision_above = true;
					}
				}
			}
			if (grid_pixel_z < 12)
				if (objects_grid[grid_z][grid_y][grid_x] != null) {
					collision_on = true;
				}
		}

		// COLLISION (IR)RESOLUTION

		// COLLISION ON THE LEFT
		if (collision_left) {
			vx = 0;
			x += 1;
		}
		// COLLISION ON THE RIGHT
		if (collision_right) {
			vx = 0;
			x -= 1;
		}

		// COLLISION ON THE TOP
		if (collision_north) {
			vy = 0;
			y += 1;
		}
		// COLLISION ON THE BOTTOM
		if (collision_south) {
			vy = 0;
			y -= 1;
		}

		// COLLISION ON THE LEFT NORTH
		if (collision_left_north) {
			x += 1;
			y += 1;
		}
		// COLLISION ON THE RIGHT NORTH
		if (collision_right_north) {
			x -= 1;
			y += 1;
		}

		// COLLISION ON THE LEFT SOUTH
		if (collision_left_south) {
			x += 1;
			y -= 1;
		}
		// COLLISION ON THE RIGHT SOUTH
		if (collision_right_south) {
			x -= 1;
			y -= 1;
		}

		// COLLISION BELOW THE PLAYER
		if (collision_below) {
			ground_level = 32 + objects_grid[grid_z - 1][grid_y][grid_x].z;
		}

		if (collision_above) {
			if (z > grid_z * tile_height) {
				vz = 0;
				z = grid_z * tile_height;
				// health--;
			}
		}

		// COLLISION BELOW THE PLAYER
		if (collision_on) {
			/*
			 * if (grid_pixel_x > tile_width / 2) x += tile_width -
			 * grid_pixel_x; if (grid_pixel_x <= tile_width / 2) x -=
			 * grid_pixel_x;
			 */
			vx = -vx;
			vy = -vy;
			x += vx;
			y += vy;
		}
	}

	public int compare(Object o1, Object o2) {
		GameObject s1 = (GameObject) o1;
		GameObject s2 = (GameObject) o2;

		if (s1.depth == s2.depth)
			return 0;
		else if (s1.depth > s2.depth)
			return 1;
		else
			return -1;
	}

	public boolean sameZas(GameObject obj2) {
		return (z >= obj2.z && z < obj2.z + 32);
	}

}
