import java.util.HashMap;
import java.util.List;

public class Example 
{
	private HashMap<Integer,String> attributes;
	private boolean isPoison;
	
	public Example(HashMap<Integer,String> attributes, boolean isPoison)
	{
		this.attributes = attributes;
		this.isPoison = isPoison;
	}
	
	public HashMap<Integer,String> getAttributes()
	{
		return attributes;
	}
	public String getAttributeAt(int index)
	{
		return attributes.get(index);
	}
	public boolean isPoison()
	{
		return isPoison;
	}
	
}
