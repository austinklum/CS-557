import java.util.LinkedList;
import java.util.List;

public class Neuron 
{
	private double aj;
	private double inj;
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
	
	public double getAj() {
		return aj;
	}
	public void setAj(double aj) {
		this.aj = aj;
	}
	public double getInj() {
		return inj;
	}
	public void setInj(double inj) {
		this.inj = inj;
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
