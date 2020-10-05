import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class DecisionTree 
{
	private int i = 10;
	private int l = 100;
	private int t = 20;
	private boolean shouldPrint = false;
	
	private List<Example> examples;
	private HashMap<Integer, List<String>> features;
	private HashMap<Integer, String> featureName;
	private Tree root;
	
	public DecisionTree(int i, int l, int t, boolean shouldPrint)
	{
		this.i = i;
		this.l = l;
		this.t = t;
		this.shouldPrint = shouldPrint;
		initAttributes();
		
	}
	
	public DecisionTree(String[] args)
	{
		processArgs(args);
		initAttributes();
	}

	private void processArgs(String[] args)
	{
		for (int j = 0; j < args.length; j++)
		{
			switch (args[j])
			{
				case "-i":
					this.i = Integer.parseInt(args[j+1]);
					break;
				case "-l":
					this.l = Integer.parseInt(args[j+1]);
					break;
				case "-t":
					this.t = Integer.parseInt(args[j+1]);
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
	
	private void initAttributes()
	{
		try {
			tryToInitAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void tryToInitAttributes() throws IOException
	{
		int pos = 0;
		features = new HashMap<Integer, List<String>>();
		Scanner scan = new Scanner(new File("properties.txt"));
		String line[];
		while(scan.hasNext())
		{
			line = scan.nextLine().split(": ");
			
			String name = line[0];
			featureName.put(pos, name);

			List<String> possibleValues = Arrays.asList(line[1].split(" "));
			features.put(pos, possibleValues);
			
			pos++;
		}
		scan.close();
	}
	
	public void processFile(File file)
	{
		try {
			tryToProcessFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void tryToProcessFile(File file) throws IOException
	{
		Scanner scan = new Scanner(file);
		String line; 
		while (scan.hasNext())
		{
			int pos = 0;
			line = scan.nextLine();
			String[] lineSplit = line.split(" ");
			List<Attribute> lineAttributes = new LinkedList<>();
			for(String featureValue : lineSplit)
			{
				Attribute attr = new Attribute(pos++, featureValue);
				lineAttributes.add(attr);
			}
			Example example = new Example(lineAttributes, Boolean.getBoolean(lineSplit[lineSplit.length-1]));
			examples.add(example);
		}
		scan.close();
	}
	
	private boolean isPosion(String[] features)
	{
		return features[features.length - 1].equals("p");
	}
	
	public void run()
	{
		for (int k = 0; k*i < l; k++)
		{
			
		}
	}
	
	private Tree learnDecisionTree(List<String[]> examples, List<String[]> attributes, List<String[]>parentExamples)
	{
		if (examples.isEmpty())
		{
			return pluralityValue(parentExamples);
		}
		else if (allExamplesHaveSameClassification(examples))
		{
			return null;
		}
		else if (attributes.isEmpty())
		{
			return pluralityValue(examples);
		}
		else
		{
			int predictAttr = findAttributeWithHighestImportance(examples);
			Tree tree = new Tree(predictAttr);
			for(Value v in predictAttr)
			{
				 List<String[]> exs = getExamplesByValue(examples, predictAttr, v);
				 List<Integer> filteredAttr = attributes.remove(predictAttr);
				 Tree subTree = learnDecisionTree(exs, filteredAttr, examples);
				 tree.addChild(subTree);
			}
			return tree;
		}
		return null;
	}
	
	private Tree pluralityValue(List<String[]> parentExamples)
	{
		Tree node = new Tree(-1);
		return null; 
	}
	
	private boolean allExamplesHaveSameClassification(List<String[]> examples)
	{
		return false;
	}
	
	private int findAttributeWithHighestImportance(List<String[]> examples)
	{
		return -1;
	}
}
