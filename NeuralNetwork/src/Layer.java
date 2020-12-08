import java.util.LinkedList;
import java.util.List;

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
	
	public void setupEmptyLayer()
	{
		for (int i = 0; i < size; i++) 
		{
			neurons.add(new Neuron());
		}
	}
}
