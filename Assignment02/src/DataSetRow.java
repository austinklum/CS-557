
public class DataSetRow 
{
	private double[] attributes;
	private double target;
	private int fold;
	
	public DataSetRow(double[] attributes, double target, int fold)
	{
		this.setAttributes(attributes);
		this.setTarget(target);
		this.setFold(fold);
	}
	
	public DataSetRow(double[] attributes, double target)
	{
		this(attributes, target, -1);
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

	public int getFold() {
		return fold;
	}

	public void setFold(int fold) {
		this.fold = fold;
	}

	
}
