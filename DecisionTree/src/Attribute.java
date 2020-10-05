import java.util.List;

public class Attribute {
	private String name;
	private List<String> possibleValues;
	private String value;
	
	public Attribute(String name, List<String> possibleValues, String value)
	{
		this.name = name;
		this.possibleValues = possibleValues;
		this.value = value;
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
}
