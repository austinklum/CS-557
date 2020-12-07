import java.util.List;

public class Layer 
{
	private List<Neuron> neurons;
	
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
}
