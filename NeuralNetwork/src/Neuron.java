import java.util.LinkedList;
import java.util.List;

public class Neuron 
{
	private double output;
	private double input;
	private double bias;
	private List<WeightEdge> inEdges;
	private List<WeightEdge> outEdges;
	
	public Neuron(List<WeightEdge> inEdges, List<WeightEdge> outEdges)
	{
		setInEdges(inEdges);
		setOutEdges(outEdges);
	}
	
	public Neuron()
	{
		this(new LinkedList<WeightEdge>(), new LinkedList<WeightEdge>());
	}
	
	public double getOutput() {
		return output;
	}
	public void setOutput(double output) {
		this.output = output;
	}
	public double getInput() {
		return input;
	}
	public void setInput(double input) {
		this.input = input;
	}
	public List<WeightEdge> getInEdges() {
		return inEdges;
	}
	public void setInEdges(List<WeightEdge> inEdges) {
		this.inEdges = inEdges;
	}
	public List<WeightEdge> getOutEdges() {
		return outEdges;
	}
	public void setOutEdges(List<WeightEdge> outEdges) {
		this.outEdges = outEdges;
	}
	
	
	
}
