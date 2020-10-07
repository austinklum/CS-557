import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Tree {
	
	private List<Tree> children;
	private Attribute attribute;
	private Tree parent;
	private boolean isPosion;
	private int attributeIndexForValue;
	private List<Example> examples;
	
	public Tree(List<Example> examples, Attribute attribute, int attributeIndexForValue)
	{
		this.examples = examples;
		this.attribute = attribute;
		this.attributeIndexForValue = attributeIndexForValue;
		children = new ArrayList<Tree>();
	}
	
	public Tree(List<Example> examples)
	{
		this.examples = examples;
		this.attributeIndexForValue = -1;
		this.children = new ArrayList<Tree>();
	}
	
	public Tree(boolean isPosion)
	{
		this.isPosion = isPosion;
	}
	
    public Tree getParent() {
        return this.parent;
    }

    public List<Tree> getChildren() {
        return this.children;
    }
    
    public boolean isLeaf()
    {
    	return children.size() == 0;
    }

    public int getNumberOfChildren() {
        return getChildren().size();
    }
	
	public void addChild(Tree child)
	{
		 child.parent = this;
		 children.add(child);
	}
	
	public Attribute getAttribute()
	{
		return attribute;
	}
	
	public List<Example> getExamples()
	{
		return examples;
	}
	
	public Tree splitOnAttribute(Attribute attributeToSplitOn)
	{
		int index = 0;
		
		Tree tree = new Tree(examples, attribute, attributeIndexForValue);
		List<String> possibleValues = attribute.getPossibleValues();
		for (String possibleValue : possibleValues)                                                                                                                                                                                                                                                     
		{
			List<Example> filteredExamples = examples
					.stream()
					.filter(ex -> ex.getAttributeAt(attributeToSplitOn.getPosition()).equals(possibleValue))
					.collect(Collectors.toList());
			this.children.add(new Tree(filteredExamples, attributeToSplitOn, index++));
		}
		return tree;
	}
	
	private double getEntropy()
	{
		long p = examples.stream().filter(ex -> ex.isPosion()).count();
		long n = examples.stream().filter(ex -> !ex.isPosion()).count();
		double ppn = p / (p+n);
		double npn = n / (p+n);
		
		double entropy = - ppn * log2(ppn) - npn * log2(npn); 
		
		return entropy;
	}
	
	public static double log2(double npn)
	{
		return (double) (Math.log(npn) / Math.log(2));
	}
	
	private double getRemainder(Attribute attributeToSplitOn)
	{
		double remainder = 0;
		Tree attributeSplitTree = splitOnAttribute(attributeToSplitOn);
		for (Tree treeValue : attributeSplitTree.getChildren())
		{
			double childEntropy = treeValue.getEntropy();
			double scalar = treeValue.getExamples().size() / this.getExamples().size();
			double remainPart = scalar * childEntropy;
			remainder += remainPart;
		}
		return remainder;
	}
	
	public double getGain(Attribute attributeToSplitOn)
	{
		double entropy = this.getEntropy();
		double remainder = this.getRemainder(attributeToSplitOn);
		double gain = entropy - remainder;
		return gain;
	}
}
