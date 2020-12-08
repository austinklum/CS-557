
public class WeightEdge 
{
	private double weight;
	private Neuron start;
	private Neuron end;
	
	public WeightEdge(Neuron start, Neuron end, double weight)
	{
		setStart(start);
		setEnd(end);
		setWeight(weight);
	}
	
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public Neuron getStart() {
		return start;
	}
	public void setStart(Neuron start) {
		this.start = start;
	}
	public Neuron getEnd() {
		return end;
	}
	public void setEnd(Neuron end) {
		this.end = end;
	}
	
}
