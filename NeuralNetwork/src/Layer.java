import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Layer 
{
	private List<Neuron> neurons;
	public int size;
	
	
	public Layer(List<Neuron> neurons)
	{
		setNeurons(neurons);
	}
	
	public List<Neuron> getNeurons() {
		return neurons;
	}

	public void setNeurons(List<Neuron> neurons) {
		this.neurons = neurons;
	}
	
	public void setupEmptyLayer(int size)
	{
		this.size = size;
		setupEmptyLayer();
	}
	
	public void setupEmptyLayer()
	{
		for (int i = 0; i < size; i++) 
		{
			neurons.add(new Neuron());
		}
	}
	
	public Neuron getNeuronAt(int index)
	{
		return this.getNeurons().get(index);
	}
	
	public void connectToLayer(Layer otherLayer, double randomWeightScalar)
	{
		for (Neuron neuron : this.getNeurons())
		{
			connectNeuronToLayer(neuron, otherLayer, randomWeightScalar);
		}
	}
	
	public static void connectNeuronToLayer(Neuron neuron, Layer otherLayer, double randomWeightScalar)
	{
		Random rand = new Random();
		for (Neuron nextNeuron : otherLayer.getNeurons()) 
		{
			WeightEdge edge = new WeightEdge(neuron, nextNeuron, randomWeightScalar * rand.nextDouble());
			neuron.getOutEdges().add(edge);
			nextNeuron.getInEdges().add(edge);
		}
	}
}
