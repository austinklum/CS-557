import java.util.List;

public class Attribute {
	private String name;
	private List<String> possibleValues;
	private String value;
	private int position;
	
	public Attribute(String name, List<String> possibleValues, String value, int position)
	{
		this.name = name;
		this.possibleValues = possibleValues;
		this.value = value;
		this.position = position;
	}
	public String getName()
	{
		return name;
	}
	public List<String> getPossibleValues()
	{
		return possibleValues;
	}
	public String getValue()
	{
		return value;
	}
	public int getPosition()
	{
		return position;
	}
}
