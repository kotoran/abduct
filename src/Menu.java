import java.util.ArrayList;

public class Menu {
ArrayList<String> options = new ArrayList<String>();
String selectedOption = "";

public void addOption(String option)
	{
	options.add(option);
	}

public String draw(int x , int y, int width)
	{
	selectedOption = "";
	for(int i = 0 ; i < options.size() ; i++)
		{
		if (JavaTemplate.drawButtonTab(JavaTemplate.spr_hud_back, x, y + i * 48, width, 48, options.get(i), JavaTemplate.menuSelected, i))
			{
			System.out.println("Pressed");
			selectedOption = options.get(i);
			}
		}
	options.clear();
	
	return selectedOption;
	}
}
