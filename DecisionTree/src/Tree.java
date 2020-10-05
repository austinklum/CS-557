import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Tree {
	
	private List<Tree> children;
	private Attribute attribute;
	private Tree parent;
	private boolean isPosion;
	private int attributeIndex;
	private List<Example> examples;
	
	public Tree(List<Example> examples, Attribute attribute, int attributeIndex)
	{
		this.examples = new LinkedList<Example>();
		this.attribute = attribute;
		children = new ArrayList<Tree>();
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
	
	public Tree splitOnAttribute()
	{
		Tree tree = new Tree(examples, attribute, attributeIndex);
		List<String> possibleValues = attribute.getPossibleValues();
		for (String possibleValue : possibleValues)                                                                                                                                                                                                                                                                    
		{
			List<Example> filteredExamples = examples
					.stream()
					.filter(ex -> ex.getAttributeAt(attributeIndex).equals(possibleValue))
					.collect(Collectors.toList());
			this.children.add(new Tree(filteredExamples, attribute, attributeIndex));
		}
		return tree;
	}
	
}
