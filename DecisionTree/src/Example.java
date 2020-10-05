import java.util.HashMap;
import java.util.List;

public class Example 
{
	private HashMap<Integer,String> attributes;
	private boolean isPosion;
	
	public Example(HashMap<Integer,String> attributes, boolean isPosion)
	{
		this.attributes = attributes;
		this.isPosion = isPosion;
	}
	
	public HashMap<Integer,String> getAttributes()
	{
		return attributes;
	}
	public String getAttributeAt(int index)
	{
		return attributes.get(index);
	}
	public boolean isPosion()
	{
		return isPosion;
	}
	
}
