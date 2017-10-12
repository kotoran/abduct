import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import sun.security.util.Length;

public class LevelMap {
	public static int width = 120, height = 120;
	static int[][] mapLayer = new int[width][height];

	public static int[][] loadLayer(File arrayToLoad)
	{
		System.out.println(arrayToLoad.getAbsoluteFile());
		try(Scanner in = new Scanner(arrayToLoad))
		{
			in.useDelimiter(",\\s*");
			
			while(in.hasNext())
			{
				for(int i = 0; i < width; i++)
				{
					for(int j = 0; j < height; j++)
					{
						if (in.hasNextInt())
						{
							mapLayer[i][j] = in.nextInt(); //Integer.parseInt(in.next());
						}
						else
						{
							mapLayer[i][j] = 0;
							in.next();
						}
					}
				}
	        }
		} catch (FileNotFoundException e)
		{
			System.out.println("Couln't open map file");
			e.printStackTrace();
		}
		return mapLayer;
	}
	
	public static void saveLayer(int[][] layer, File writeOutTo)
    {
        try(PrintWriter out = new PrintWriter(writeOutTo))
        {
            for(int i = 0; i < height; i++)
            {
                for(int j = 0; j < width; j++)
                {
                    out.print(layer[i][j] + ",");
                }
                out.println("");
            }
        } catch (FileNotFoundException e)
        {
            System.out.println("Can't find this file! "+writeOutTo.toString());
            e.printStackTrace();
        }
    }
	
	/*
	public static void loadObjects()
	{
		
	}
	
	public static void loadMap()
	{
		loadArray();
	}
	*/
}
