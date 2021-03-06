/***
 * @author Austin Klum
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Tree {

	private ArrayList<Tree> children;
	private Attribute attribute;
	private Tree parent;
	private boolean isPoison;
	private int attributeIndexForValue;
	private List<Example> examples;
	private HashMap<Integer, Tree> childByAttributeIndex;

	public Tree(List<Example> examples, Attribute attribute, int attributeIndexForValue) {
		this.examples = examples;
		this.attribute = attribute;
		this.attributeIndexForValue = attributeIndexForValue;
		childByAttributeIndex = new HashMap<>();
		children = new ArrayList<Tree>();
		this.isPoison = getMajority(examples);
	}

	public Tree(List<Example> examples, Attribute attributeToSplitOn) {
		this.examples = examples;
		this.attribute = attributeToSplitOn;
		this.attributeIndexForValue = -1;
		childByAttributeIndex = new HashMap<>();
		this.children = new ArrayList<Tree>();
		this.isPoison = getMajority(examples);
	}

	public Tree(boolean isPoison) {
		this.isPoison = isPoison;
		this.children = new ArrayList<Tree>();
	}

	public boolean isPoison() {
		return isPoison;
	}

	public void setPoison(boolean isPoison)
	{
		this.isPoison = isPoison;
	}
	
	public Tree getParent() {
		return this.parent;
	}

	public ArrayList<Tree> getChildren() {
		return this.children;
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}

	public int getNumberOfChildren() {
		return getChildren().size();
	}

	public void addChild(Tree child) {
		child.parent = this;
		children.add(child);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public List<Example> getExamples() {
		return examples;
	}
	
	public int getAttributeIndexForValue()
	{
		return attributeIndexForValue;
	}

	public void setAttributeIndexforValue(int attributeIndexForValue)
	{
		this.attributeIndexForValue = attributeIndexForValue;
	}
	
	public HashMap<Integer, Tree> getChildByAttributeIndex()
	{
		return this.childByAttributeIndex;
	}
	
	private boolean getMajority(List<Example> example) {
		long poisonCount = examples.stream().filter(ex -> ex.isPoison()).count();
		long edibleCount = examples.stream().filter(ex -> !ex.isPoison()).count();
		long difference = poisonCount - edibleCount; // negative means poison count is larger

		if (difference != 0) {
			return difference < 0;
		}

		Random rand = new Random();
		return rand.nextBoolean();
	}

	public Tree splitOnAttribute(Attribute attributeToSplitOn)
	{
		int index = 0;

		Tree tree = new Tree(examples, attribute, attributeIndexForValue);
		List<String> possibleValues = attributeToSplitOn.getPossibleValues();
		for (String possibleValue : possibleValues)
		{
			List<Example> filteredExamples = examples
					.stream()
					.filter(ex -> ex.getAttributeAt(attributeToSplitOn.getPosition()).equals(possibleValue))
					.collect(Collectors.toList());
			if(filteredExamples.size() > 0) 
			{
				Tree child = (new Tree(filteredExamples, attributeToSplitOn, index));
				tree.addChild(child);
				tree.childByAttributeIndex.put(index, child);
			}
			index++;
		}
		return tree;
	}

	private double getEntropy() 
	{
		long p = examples.stream().filter(ex -> ex.isPoison()).count();
		long n = examples.stream().filter(ex -> !ex.isPoison()).count();
		double ppn = (double) p / (p + n);
		double npn = (double) n / (p + n);

		double entropy = -ppn * (ppn != 0 ? log2(ppn) : 0) - npn * (npn != 0 ? log2(npn) : 0);

		return entropy;
	}

	public static double log2(double num)
	{
		return (double) (Math.log(num) / Math.log(2));
	}

	private double getRemainder(Attribute attributeToSplitOn)
	{
		double remainder = 0;
		Tree attributeSplitTree = splitOnAttribute(attributeToSplitOn);
		for (Tree treeValue : attributeSplitTree.getChildren()) 
		{
			double childEntropy = treeValue.getEntropy();
			double scalar = (double) treeValue.getExamples().size() / this.getExamples().size();
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
