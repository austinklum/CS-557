import java.util.LinkedList;
import java.util.List;

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

//	public Layer getInputLayer(Layer firstHiddenLayer)
//	{
//		Layer layer = new Layer(new LinkedList<Neuron>());
//		layer.setupEmptyLayer(this.getAttributes().length);
//		
//		for(double attr : this.getAttributes())
//		{
//			Neuron neuron = new Neuron(attr, new LinkedList<WeightEdge>(),  new LinkedList<WeightEdge>());
//			List<WeightEdge> out = new LinkedList<>();
//	
//			for(Neuron hiddenNeuron : hiddenLayers[0].getNeurons())
//			{
//				WeightEdge edge = new WeightEdge(neuron, hiddenNeuron, getRand());
//				out.add(edge);
//			}
//			neuron.setOutEdges(out);
//			layer.getNeurons().add(neuron);
//		}
//		return layer;
//	}

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
