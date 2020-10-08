import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DecisionTree {
	private int i = 10;
	private int l = 100;
	private int t = 20;
	private boolean shouldPrint = false;

	private List<Example> examples;
	private HashMap<Integer, Attribute> attributes;
	private Tree root;

	public DecisionTree(int i, int l, int t, boolean shouldPrint) {
		this.i = i;
		this.l = l;
		this.t = t;
		this.shouldPrint = shouldPrint;
		initAttributes();

	}

	public DecisionTree(String[] args) {
		processArgs(args);
		initAttributes();
	}

	private void processArgs(String[] args) {
		for (int j = 0; j < args.length; j++) {
			switch (args[j]) {
			case "-i":
				this.i = Integer.parseInt(args[j + 1]);
				break;
			case "-l":
				this.l = Integer.parseInt(args[j + 1]);
				break;
			case "-t":
				this.t = Integer.parseInt(args[j + 1]);
				break;
			case "-p":
				this.shouldPrint = true;
				break;
			default:
				System.out.println(args[j] + " is not a supported command-line argument.");
				System.out.println("Expected format is -i <VAL> -l <VAL> -t <VAL> -p");
				System.exit(-1);
				break;
			}
		}

	}

	private void initAttributes() {
		try {
			tryToInitAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void tryToInitAttributes() throws IOException {
		int pos = 0;
		examples = new LinkedList<Example>();
		attributes = new HashMap<Integer, Attribute>();
		Scanner scan = new Scanner(new File("properties.txt"));
		String line[];
		while (scan.hasNext()) {
			line = scan.nextLine().split(": ");
			String name = line[0];
			List<String> possibleValues = Arrays.asList(line[1].split(" "));

			Attribute attribute = new Attribute(pos, name, possibleValues);
			attributes.put(pos++, attribute);
		}
		scan.close();
	}

	public void processFile(File file) {
		try {
			tryToProcessFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void tryToProcessFile(File file) throws IOException {
		Scanner scan = new Scanner(file);
		String line;
		while (scan.hasNext()) {
			int pos = 0;
			line = scan.nextLine();
			String[] lineSplit = line.split(" ");
			HashMap<Integer, String> lineAttributes = new HashMap<>();
			for (String featureValue : lineSplit) {
				lineAttributes.put(pos++, featureValue);
			}
			Example example = new Example(lineAttributes, "p".equalsIgnoreCase(lineSplit[lineSplit.length - 1]));
			examples.add(example);
		}
		scan.close();
	}

	public void run() {
		// for (int k = 0; k*i < l; k++)
		// {

		root = learnDecisionTree(examples, attributes, examples);
		test(examples);
		// }
	}

	private void test(List<Example> examples)
	{
		Tree tree = root;
		int correct = 0;
		int wrong = 0;
		for(Example ex : examples)
		{
			if (predict(ex, tree) == ex.isPoison())
			{
				correct++;
			}
			else
			{
				wrong++; 
			}
		}
		System.out.println("Train Accuracy : " + (double) wrong / correct);
	}
	
	private boolean predict(Example ex, Tree tree)
	{
		if (tree.isLeaf())
		{
			return tree.isPoison();
		}
		
		ex.getAttributeAt(tree.getAttribute().getPosition());
		for (Tree child : tree.getChildren())
		{
			if (child.getParent() == tree)
			{
				predict(ex, child);
				break;
			}
		}
		
		return false;
	}
	
	public static void printTree(Tree tree)
	{
		printTree(tree, 0);
	}

	private static void printTree(Tree tree, int tabbingCount)
	{
		 if (tree.isLeaf())
		 {
			 print(tabbingCount + 1, "Leaf: Predict " + getPredictText(tree));
		 }
		 
		 if (tree.getNumberOfChildren() > 0)
		 {
			 print(tabbingCount, "Node: Split on feature " + (tree.getAttribute().getPosition() + 1) + " (" + tree.getAttribute().getName() + ")");
			 for(Tree child : tree.getChildren())
			 {
				 print(tabbingCount + 1, "Branch = " + getBranch(child));
				 printTree(child, tabbingCount + 2);
			 }
		 }
		 
		  
	}
	
	private static String getBranch(Tree tree)
	{ 
		int attributeIndexForValue = tree.getAttributeIndexForValue();
		if(tree.getAttribute() == null)
		{
			return getPredictText(tree);
		}
		
		if(attributeIndexForValue >= tree.getAttribute().getPossibleValues().size()) {
			attributeIndexForValue = tree.getAttribute().getPossibleValues().size() - 1;
		}
		return tree.getAttribute().getPossibleValues().get(attributeIndexForValue);
	}
	
	private static void print(int tabbingCount, String msg)
	{
		for(int i = 0; i < tabbingCount; i++)
		 {
			 System.out.print("  ");
		 }
		System.out.println(msg);
	}
	private static String getPredictText(Tree tree) {
		String str = "Poison";
		if (!tree.isPoison()) {
			str = "Edible";
		}
		return str;
	}

	private Tree learnDecisionTree(List<Example> examples, HashMap<Integer, Attribute> attributes,
			List<Example> parentExamples) {
		if (examples.isEmpty()) {
			return pluralityValue(parentExamples);
		} else if (allExamplesHaveSameClassification(examples)) {
			return new Tree(examples.get(0).isPoison());
		} else if (attributes.isEmpty()) {
			return pluralityValue(examples);
		} else {
			Attribute predictAttr = findAttributeWithHighestImportance(attributes, examples);
			Tree tree = new Tree(examples, predictAttr, -1).splitOnAttribute(predictAttr);
			int index = 0;
			
			for (String possibleValue : attributes.get(predictAttr.getPosition()).getPossibleValues()) {
				List<Example> filteredExamples = examples.stream()
						.filter(ex -> ex.getAttributeAt(predictAttr.getPosition()).equals(possibleValue))
						.collect(Collectors.toList());

				HashMap<Integer, Attribute> filteredAttributes = new HashMap<>();
				filteredAttributes.putAll(attributes);
				filteredAttributes.remove(predictAttr.getPosition());

				Tree subTree = learnDecisionTree(filteredExamples, filteredAttributes, examples);
				subTree.setAttributeIndexforValue(index++);
				tree.addChild(subTree);
				
				//System.out.println(tree.getAttributeIndexForValue());
			}
			//System.out.println("Split on " + index + " : " + predictAttr.getName());
			return tree;
		}
	}

	private Tree pluralityValue(List<Example> examples) {
		long poisonCount = examples.stream().filter(ex -> ex.isPoison()).count();
		long difference = examples.size() - poisonCount; // negative means poison count is larger

		if (difference != 0) {
			return new Tree(difference < 0);
		}

		Random rand = new Random();
		return new Tree(rand.nextBoolean());
	}

	private long getPoisonClassificationCount(List<Example> examples) {
		return examples.stream().filter(Example::isPoison).count();
	}

	private boolean allExamplesHaveSameClassification(List<Example> examples) {
		return getPoisonClassificationCount(examples) == examples.size();
	}

	private Attribute findAttributeWithHighestImportance(HashMap<Integer, Attribute> attributes,
			List<Example> examples) {
		double bestGain = 0;
		int bestAttributeIndex = attributes.get(attributes.keySet().iterator().next()).getPosition();
		int index = 0;
		Tree tree = new Tree(examples);
		for (Attribute attributeToSplitOn : attributes.values()) {
			double gain = tree.getGain(attributeToSplitOn);
			if (gain > bestGain) {
				bestGain = gain;
				bestAttributeIndex = index;
			}
			index++;
		}

		return attributes.get(bestAttributeIndex);
	}
}
