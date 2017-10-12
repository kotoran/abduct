import java.util.ArrayList;
import com.jogamp.opengl.*;

public class Sprite {
	public ArrayList<Subimage> subimage = new ArrayList<Subimage>();
	int width, height, xorig = 0, yorig = 0;
	
	public void addAnimationFrame(int spriteID, int width, int height)
	{
		Subimage newFrame = new Subimage(spriteID , width, height);
		subimage.add(newFrame);
		this.width = width;
		this.height = height;
	}
	
	public int getTex(int subImg)
	{
		return subimage.get(subImg).tex;
	}

	public int getWidth(int subImg)
	{
		return subimage.get(subImg).width;
	}
	
	public int getHeight(int subImg)
	{
		return subimage.get(subImg).height;
	}

	public int getXorig()
	{
		return xorig;
	}
	
	public int getYorig()
	{
		return yorig;
	}
	
	public void setXorig(int newXorig)
	{
		xorig = newXorig;
	}
	
	public void setYorig(int newYorig)
	{
		yorig = newYorig;
	}
}

