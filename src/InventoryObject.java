
public class InventoryObject {
public Sprite sprite_index;
public int quantity = 1;
public String name = "";
public int cost = 100;

public void setSprite(Sprite spr)
{
	sprite_index = spr;
}

public InventoryObject()
{
}

public InventoryObject(String name, Sprite spr)
{
	this.name = name;
	sprite_index = spr;
}

public Sprite getSprite()
{
	return sprite_index;
}
}
