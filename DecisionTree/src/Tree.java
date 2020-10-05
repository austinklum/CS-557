import java.util.ArrayList;
import java.util.List;

public class Tree {
	
	private List<Tree> children;
	private Attribute attribute;
	private Tree parent;
	private boolean isPosion;
	
	public Tree(Attribute attribute)
	{
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
	
}
