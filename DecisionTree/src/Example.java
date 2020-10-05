import java.util.List;

public class Example 
{
	private List<Attribute> attributes;
	private boolean isPosion;
	
	public Example(List<Attribute> attributes, boolean isPosion)
	{
		this.attributes = attributes;
		this.isPosion = isPosion;
	}
	
	public List<Attribute> getAttributes()
	{
		return attributes;
	}
	public Attribute getAttributeAt(int index)
	{
		return attributes.get(index);
	}
	public boolean isPosion()
	{
		return isPosion;
	}
	
}
