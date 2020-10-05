
public class Attribute {
	private String value;
	private int position;
	
	public Attribute(int position, String value)
	{
		this.position = position;
		this.value = value;
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
