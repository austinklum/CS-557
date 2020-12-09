import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Neuron 
{
	private double output;
	private double input;
	private double delta;
	private double bias;
	private List<WeightEdge> inEdges;
	private List<WeightEdge> outEdges;
	private List<Double> deltas;
	private List<Double> outputs;
	private ActivationFunction activationFunction = new SigmoidActivation();
	
	public Neuron(List<WeightEdge> inEdges, List<WeightEdge> outEdges)
	{
		setInEdges(inEdges);
		setOutEdges(outEdges);
		deltas = new ArrayList<>();
		outputs = new ArrayList<>();
	}
	
	public Neuron()
	{
		this(new LinkedList<WeightEdge>(), new LinkedList<WeightEdge>());
	}
	
	public void updateInput()
	{
		double inj = 0;
		for (WeightEdge edge : inEdges)
		{
			inj += edge.getWeight() * edge.getStart().getOutput();
		}
		input = inj;
	}
	
	public void activate()
	{
		output = activationFunction.Activate(input);
	}
	
	public void updateOutputLayerDelta(double actualOutput)
	{
		double activationPrime = activationFunction.ActivatePrime(input);
		double outputDifference = actualOutput - output;
		setDelta(activationPrime * (-2 * outputDifference));
	}
	
	public void updateDelta()
	{
		double activationPrime = activationFunction.ActivatePrime(input);
		double sum = 0;
		
		for (WeightEdge edge : outEdges)
		{
			sum += edge.getWeight() * edge.getEnd().delta;
		}
		double newDelta = activationPrime * sum;
		setDelta(newDelta);
	}
	
	public double getOutput() {
		return output;
	}
	public void setOutput(double output) {
		outputs.add(output);
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

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		deltas.add(delta);
		this.delta = delta;
	}
	
	public List<Double> getDeltas()
	{
		return deltas;
	}
	
	public void setDeltas(List<Double> deltas)
	{
		this.deltas = deltas;
	}
	
	public List<Double> getOutputs()
	{
		return outputs;
	}
	
	public void setOutputs(List<Double> outputs)
	{
		this.outputs = outputs;
	}
}
