
public class Weapon extends InventoryObject {
	double damage = 1, knockback = 0, reloadTime = 500, 
			projectilesPerShot = 1, angleRange = 10;
	
	int ammo = 100, maxAmmo = 100, bursts = 1, burstTime = 50;
	
	boolean automatic = false;
	
	Sprite projectile_sprite;
	
	

	/**
	 * Creates a new weapon
	 * 
	 * @param damage
	 *            How much damage each projectile deals per hit
	 * @param knockback
	 *            Velocity that projectile will add to target upon impact
	 * @param reloadTime
	 *            Time it takes between bullets
	 * @param projectilesPerShot
	 *            Number of projectiles shot outwards in a spread fashion
	 * @param ammo
	 *            How many projectiles are left in the weapon
	 * @param maxAmmo
	 *            How many projectiles the weapon can hold
	 * @param angleRange
	 *            Random angle offset range when firing projectiles, smaller
	 *            number = more accurate weapon
	 * @param bursts
	 *            Number of successive bursts to fire
	 */
	public Weapon(double damage, double knockback, double reloadTime, double projectilesPerShot, int ammo,
			int maxAmmo, double angleRange, int bursts) {
		this.damage = damage;
		this.knockback = knockback;
		this.reloadTime = reloadTime;
		this.projectilesPerShot = projectilesPerShot;
		this.ammo = ammo;
		this.maxAmmo = maxAmmo;
		this.angleRange = angleRange;
	}
	
	public Weapon(String name) {
		this.name = name;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public void setknockback(double knockback) {
		this.knockback = knockback;
	}

	public void setReloadTime(double reloadTime) {
		this.reloadTime = reloadTime;
	}
	public void setBurstTime(int burstTime) {
		this.burstTime = burstTime;
	}

	public void setProjectilesPerShot(double projectilesPerShot) {
		this.projectilesPerShot = projectilesPerShot;
	}

	public void setAngleRange(double angleRange) {
		this.angleRange = angleRange;
	}

	public void setBursts(int bursts) {
		this.bursts = bursts;
	}

}
