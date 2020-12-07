import java.util.List;

public class Neuron 
{
	private double value;
	private double bias;
	private List<Edge> inEdges;
	private List<Edge> outEdges;
	
	public Neuron(double value, List<Edge> inEdges, List<Edge> outEdges)
	{
		setValue(value);
		setInEdges(inEdges);
		setOutEdges(outEdges);
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public List<Edge> getInEdges() {
		return inEdges;
	}
	public void setInEdges(List<Edge> inEdges) {
		this.inEdges = inEdges;
	}
	public List<Edge> getOutEdges() {
		return outEdges;
	}
	public void setOutEdges(List<Edge> outEdges) {
		this.outEdges = outEdges;
	}
	
	
	
}
