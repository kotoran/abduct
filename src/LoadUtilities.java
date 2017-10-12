import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoadUtilities
{
	private String[] loadedInText;
	public String[] loadInText(File arrayToLoad)
	{
		try(Scanner in = new Scanner(arrayToLoad))
		{
			in.useDelimiter(",\\s*");
			int i = 0;
			while(in.hasNext())
			{
				loadedInText[i] = in.next();
				i++;
	        }
		} catch (FileNotFoundException e)
		{
			System.out.println("Couldn't open text file");
			e.printStackTrace();
		}
		return loadedInText;
	}
}
