/***
 * @author Austin Klum
 */
import java.util.List;

public class Attribute {
	private int position;
	private String name;
	private List<String> possibleValues;
	
	public Attribute(int position, String name, List<String> possibleValues)
	{
		this.name = name;
		this.position = position;
		this.possibleValues = possibleValues;
	}
	public List<String> getPossibleValues()
	{
		return possibleValues;
	}
	public int getPosition()
	{
		return position;
	}
	public String getName()
	{
		return name;
	}
}
