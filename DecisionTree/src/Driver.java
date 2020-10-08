/***
 * @author Austin Klum
 */
import java.io.File;

public class Driver {
	public static void main(String args[])
	{
		DecisionTree tree = new DecisionTree(args);
		tree.processFile(new File("mushroom_data.txt"));
		tree.run();
	}
}
