
public class DataSetRow 
{
	private double[] attributes;
	private double target;
	
	public DataSetRow(double[] attributes, double target)
	{
		this.setAttributes(attributes);
		this.setTarget(target);
	}

	public double[] getAttributes() 
	{
		return this.attributes;
	}

	public double getAttributeAt(int index)
	{
		return this.attributes[index];
	}
	
	public void setAttributes(double[] attributes) 
	{
		this.attributes = attributes;
	}

	public double getTarget()
	{
		return this.target;
	}

	public void setTarget(double target)
	{
		this.target = target;
	}

	
}
