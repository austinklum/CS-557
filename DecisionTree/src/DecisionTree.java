/***
 * @author Austin Klum
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	
	public DecisionTree() {
		initAttributes();
	}

	private void processArgs(String[] args) {
		for (int j = 0; j < args.length; j++) {
			switch (args[j]) {
			case "-i":
				this.i = Integer.parseInt(args[++j]);
				break;
			case "-l":
				this.l = Integer.parseInt(args[++j]);
				break;
			case "-t":
				this.t = Integer.parseInt(args[++j]);
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
		List<Example> shuffleSet = examples.subList(0, examples.size());
		System.out.println("(Results averaged across " + t + " trials)");
		System.out.println("TrainSize\tTrainAcc\tTestAcc");
		Tree tree = null;
		 for (int k = 1; k*i <= l; k++)
		 {
			 double trainAcc = 0;
			 double testAcc = 0;
			 for (int j = 0; j < t; j++) 
			 {
				 //System.out.println("Trial " + j);
				Collections.shuffle(shuffleSet);
				List<Example> training = shuffleSet.subList(0, k*i);
				List<Example> test = shuffleSet.subList(k*i, shuffleSet.size());

				tree = learnDecisionTree(training, attributes, training);
				trainAcc += testAccuracy(tree, training);
				testAcc += testAccuracy(tree, test);
			 }
			 trainAcc /= t;
			 testAcc /= t;
			 System.out.printf("%4s     \t%1.6f\t%1.6f\n",k*i, trainAcc, testAcc);
		 }
		 if(shouldPrint)
		 {
			 printTree(tree);
		 }
	}

	private double testAccuracy(Tree tree, List<Example> examples)
	{
		int correct = 0;

		for(Example ex : examples)
		{
			if (predict(ex, tree) == ex.isPoison())
			{
				correct++;
			}
		}
		return (double) correct / examples.size();
	}
	
	private boolean predict(Example ex, Tree tree)
	{
		if (tree.isLeaf())
		{
			return tree.isPoison();
		}
		
		String value = ex.getAttributeAt(tree.getAttribute().getPosition());
		for (Tree child : tree.getChildren())
		{
			if (child.getAttribute() != null && value.equals(child.getAttribute().getPossibleValues().get(child.getAttributeIndexForValue())))
			{
				return predict(ex, child);
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
				 if (child.getAttribute() != null) 
				 {
					 print(tabbingCount + 1, "Branch = " + getBranch(child));
					 printTree(child, tabbingCount + 2);
				 }
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

	private Tree learnDecisionTree(List<Example> examples, HashMap<Integer, Attribute> attributes, List<Example> parentExamples) {
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
			
//			for (String possibleValue : attributes.get(predictAttr.getPosition()).getPossibleValues()) {
//				List<Example> filteredExamples = examples.stream()
//						.filter(ex -> ex.getAttributeAt(predictAttr.getPosition()).equals(possibleValue))
//						.collect(Collectors.toList());

				HashMap<Integer, Attribute> filteredAttributes = new HashMap<>();
				filteredAttributes.putAll(attributes);
				filteredAttributes.remove(predictAttr.getPosition());
//
//				if (filteredExamples.size() > 0)
//				{
				for (Tree child : tree.getChildren()) 
				{
					Tree subTree = learnDecisionTree(child.getExamples(), filteredAttributes, examples);

					if (subTree.getNumberOfChildren() > 0)
					{
						child.addChild(subTree);
						subTree.setAttributeIndexforValue(index++);
					}
					else
					{
						child.setPoison(subTree.isPoison());
					}
//					tree.addChild(subTree);
//					tree.getChildren().size();
//				}
				}
				//System.out.println(tree.getAttributeIndexForValue());
				
			//System.out.println("Split on " + index + " : " + predictAttr.getName());
			return tree;
		}
	}

	private Tree pluralityValue(List<Example> examples) {
		long poisonCount = examples.stream().filter(ex -> ex.isPoison()).count();
		long edibleCount = examples.stream().filter(ex -> !ex.isPoison()).count();
		long difference = poisonCount - edibleCount; // negative means poison count is larger

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
		return getPoisonClassificationCount(examples) == 0 || examples.stream()
																 .filter(ex -> !ex.isPoison())
																 .count() == 0;
	}

	private Attribute findAttributeWithHighestImportance(HashMap<Integer, Attribute> attributes,
			List<Example> examples) {
		double bestGain = 0;
		Attribute bestAttribute = attributes.get(attributes.keySet().iterator().next());
		
		for (Attribute attributeToSplitOn : attributes.values()) {
			Tree tree = new Tree(examples, attributeToSplitOn);
			double gain = tree.getGain(attributeToSplitOn);
			if (gain > bestGain) {
				bestGain = gain;
				bestAttribute = attributeToSplitOn;
			}
		}
		if (bestAttribute == null) 
		{
			System.out.println("Null attributes");
			System.out.println("A little change");
		}
		return bestAttribute;
	}
}
