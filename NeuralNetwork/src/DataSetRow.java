/**
 * @author Austin Klum
 */
public class DataSetRow 
{
	private Double[] attributes;
	private int target;
	private int position;
	private int targetLength;
	
	public DataSetRow(int position, Double[] attr, int targetPos, int targetLength)
	{
		this.setPosition(position);
		this.setAttributes(attr);
		this.setTarget(targetPos);
		this.setTargetLength(targetLength);
	}

	public Neuron getInputLayer()
	{
		Neuron neuron = new Neuron();
	}

	public Double[] getAttributes() 
	{
		return this.attributes;
	}

	public Double getAttributeAt(int index)
	{
		return this.attributes[index];
	}
	
	public void setAttributes(Double[] attributes) 
	{
		this.attributes = attributes;
	}
	
	public void setAttributeAt(int index, double value) 
	{
		this.attributes[index] = value;
	}

	public double getTarget()
	{
		return this.target;
	}

	public void setTarget(int target)
	{
		this.target = target;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) 
	{
		this.position = position;
	}


	public int getTargetLength() 
	{
		return targetLength;
	}


	public void setTargetLength(int targetLength) 
	{
		this.targetLength = targetLength;
	}

	
}
