import java.util.ArrayList;

public class BucketGrid {
	
	public static int width, height;
	
	ArrayList<ArrayList<ArrayList<GameObject>>> bucketColumn = new ArrayList<ArrayList<ArrayList<GameObject>>>();
	
	public BucketGrid(int width , int height)
	{
		this.width = width;
		this.height = height;
		
		for(int i = 0 ; i < width ; i++)
		{
			ArrayList<ArrayList<GameObject>> bucketRow = new ArrayList<ArrayList<GameObject>>();
			for(int j = 0 ; j < height ; j++)
			{
				ArrayList<GameObject> objectsList = new ArrayList<GameObject>();
				bucketRow.add(objectsList);
			}
			bucketColumn.add(bucketRow);
		}
	}

	public void add(int x, int y, GameObject obj)
	{
		bucketColumn.get(x).get(y).add(obj);
	}

	public void remove(int x, int y, int i)
	{
		bucketColumn.get(x).get(y).remove(i);
	}

	public GameObject get(int x, int y, int i)
	{
		return bucketColumn.get(x).get(y).get(i);
	}

	public ArrayList<GameObject> getList(int x, int y)
	{
		return bucketColumn.get(x).get(y);
	}
	
	public int bucketSize(int x, int y)
	{
		return bucketColumn.get(x).get(y).size();
	}
	
	public void clear()
	{
		for(int i = 0 ; i < width ; i++)
		{
			ArrayList<ArrayList<GameObject>> bucketRow = new ArrayList<ArrayList<GameObject>>();
			for(int j = 0 ; j < width ; j++)
			{
				bucketRow.get(j).clear();
			}
		}
	}
}
