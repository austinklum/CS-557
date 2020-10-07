import java.io.File;

public class Driver {
	public static void main(String args[])
	{
		DecisionTree tree = new DecisionTree(10, 10, 10, true);
		tree.processFile(new File("mushroom_data.txt"));
		tree.run();
	}
}
