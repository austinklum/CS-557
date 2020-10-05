import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DecisionTree 
{
	private int i = 10;
	private int l = 100;
	private int t = 20;
	private boolean shouldPrint = false;
	
	private List<Example> examples;
	private HashMap<Integer, Attribute> attributes;
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
		attributes = new HashMap<Integer, Attribute>();
		Scanner scan = new Scanner(new File("properties.txt"));
		String line[];
		while(scan.hasNext())
		{
			line = scan.nextLine().split(": ");
			String name = line[0];
			List<String> possibleValues = Arrays.asList(line[1].split(" "));
			
			Attribute attribute = new Attribute(pos, name, possibleValues);
			attributes.put(pos++, attribute);
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
			HashMap<Integer, String> lineAttributes = new HashMap<>();
			for(String featureValue : lineSplit)
			{
				lineAttributes.put(pos++, featureValue);
			}
			Example example = new Example(lineAttributes, "p".equalsIgnoreCase(lineSplit[lineSplit.length-1]));
			examples.add(example);
		}
		scan.close();
	}
	
	public void run()
	{
		for (int k = 0; k*i < l; k++)
		{
			learnDecisionTree(examples, attributes, examples);
		}
	}
	
	private Tree learnDecisionTree(List<Example> examples, HashMap<Integer, Attribute> attributes, List<Example> parentExamples)
	{
		if (examples.isEmpty())
		{
			return pluralityValue(parentExamples);
		}
		else if (allExamplesHaveSameClassification(examples))
		{
			return new Tree(examples.get(0).isPosion());
		}
		else if (attributes.isEmpty())
		{
			return pluralityValue(examples);
		}
		else
		{
			int predictAttr = findAttributeWithHighestImportance(examples);
			Tree tree = new Tree(attributes.get(predictAttr));
			for(String possibleValue : attributes.get(predictAttr).getPossibleValues())
			{
				 List<Example> filteredExamples = examples
						 .stream()
						 .filter(ex -> ex.getAttributeAt(predictAttr).equals(possibleValue))
						 .collect(Collectors.toList());
				 
				 HashMap<Integer, Attribute> filteredAttributes = new HashMap<>();
				 filteredAttributes.putAll(attributes);
				 filteredAttributes.remove(predictAttr);
				 
				 Tree subTree = learnDecisionTree(filteredExamples, filteredAttributes, examples);
				 tree.addChild(subTree);
			}
			return tree;
		}
	}
	
	private Tree pluralityValue(List<Example> examples)
	{
		long poisonCount = examples.stream().filter(ex -> ex.isPosion()).count();
		long difference = examples.size() - poisonCount; // negative means poison count is larger
		
		if (difference != 0)
		{
			return new Tree(difference < 0);
		}
		
		Random rand = new Random();
		return new Tree(rand.nextBoolean()); 
	}
	
	private long getPosionClassificationCount(List<Example> examples)
	{
		return examples.stream().filter(Example::isPosion).count();
	}
	
	private boolean allExamplesHaveSameClassification(List<Example> examples)
	{
		return getPosionClassificationCount(examples) == examples.size();
	}
	
	private double getEntropy(Tree tree, List<Example> examples)
	{
		long p = getPosionClassificationCount(examples);
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
	
	private int findAttributeWithHighestImportance(List<Example> examples)
	{
		for(Attribute attr : examples)
		{
			
		}
		return -1;
	}
}