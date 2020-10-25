import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProgramFlow {
	// Required
	private String fileName;
	// Optional
	private int kFolds;
	private int smallestPolynomialDegree;
	private int largestPolynomialDegree;
	private double learningRate;
	private int epochLimit;
	private int batchSize;
	private int verbosityLevel;
	
	private LinkedList<DataSetRow> data;
	private int numberOfAttributes;
	
	public ProgramFlow(String[] args) {
		setDefaults();
		processCommandLineArgs(args);
		createDataSet();
		run();
	}

	private void setDefaults()
	{
		this.kFolds = 5;
		this.smallestPolynomialDegree = 1;
		this.largestPolynomialDegree = smallestPolynomialDegree;
		this.learningRate = .005;
		this.epochLimit = 10000;
		this.batchSize = 0;
		this.verbosityLevel = 1;
		
		this.data = new LinkedList<>();
		this.numberOfAttributes = 0;
	}

	private void processCommandLineArgs(String[] args) 
	{
		for (int i = 0; i < args.length; i++) {
			switch (args[i])
			{
				case "-f":
					this.fileName = args[++i];
					break;
				case "-k":
					this.kFolds = Integer.parseInt(args[++i]);
					break;
				case "-d":
					this.smallestPolynomialDegree = Integer.parseInt(args[++i]);
					break;
				case "-D":
					this.largestPolynomialDegree = Integer.parseInt(args[++i]);
					break;
				case "-a":
					this.learningRate = Double.parseDouble(args[++i]);
					break;
				case "-e":
					this.epochLimit = Integer.parseInt(args[++i]);
					break;
				case "-m":
					this.batchSize = Integer.parseInt(args[++i]);
					break;
				case "-v":
					this.verbosityLevel = Integer.parseInt(args[++i]);
					break;
				default:
					printInvalidArgUsage(args[i]);
					System.exit(-1);
					break;
			}
		}
		setLargestPolynomialDegreeDefault();
	}

	private void printInvalidArgUsage(String arg) {
		System.out.println(arg + " is not a supported command-line argument.");
		System.out.println("Usage: -f fileName <FILENAME> "
				+ "[-k kFolds <INTEGER>{5}] "
				+ "[-d smallestPolynomialDegree <INTEGER>{1}] "
				+ "[-D largestPolynomialDegree <INTEGER>{smallestPolynomialDegree}]"
				+ "[-a learningRate <DOUBLE>{.005}]"
				+ "[-e epochLimit <INTEGER>{10000}]"
				+ "[-m batchSize <INTEGER>{0}]"
				+ "[-v verbosityLevel <INTEGER>]{1}");
	}

	private void setLargestPolynomialDegreeDefault()
	{
		if (this.largestPolynomialDegree == 1)
		{
			this.largestPolynomialDegree = this.smallestPolynomialDegree;
		}
	}
		
	private void createDataSet()
	{
		Matcher matcher = Pattern.compile("(?:-[A-z])?([0-9]+)(?:-[A-z])([0-9]+)").matcher(fileName);
		matcher.find();
		this.numberOfAttributes = Integer.parseInt(matcher.group(1));
		int trueFunctionDegree = Integer.parseInt(matcher.group(2));
		tryToReadFile();
		splitDataSetIntoKFolds();
	}

	private void tryToReadFile()
	{
			Scanner scan;
			try {
				scan = new Scanner(new File(fileName));
				readFile(scan);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
	}
	
	private void readFile(Scanner scan)
	{
		while(scan.hasNext())
		{
			double[] attributes = new double[this.numberOfAttributes];
			for (int i = 0; i < this.numberOfAttributes; i++)
			{
				attributes[i] = scan.nextDouble();
			}
			data.add(new DataSetRow(attributes, scan.nextDouble()));
		}
	}
	
	private void splitDataSetIntoKFolds()
	{
		int rowCount = 0;
		int foldNumber = 0;
		for(DataSetRow row : data)
		{
			if(rowCount % kFolds == 0)
			{
				foldNumber++;
			}
			row.setFold(foldNumber);
			rowCount++;
		}
	}
	
	private void run()
	{
		int foldNumber = 0;
		for(int d = this.smallestPolynomialDegree; d <= this.largestPolynomialDegree; d++)
		{
			for (int k = 0; k < kFolds; k++) 
			{
				List<DataSetRow> dataNotInFold = data
						.stream()
						.filter(row -> row.getFold() != foldNumber)
						.collect(Collectors.toList());
				miniBatchGradientDescent(d, dataNotInFold);
			}
		}
	}
	
	private void miniBatchGradientDescent(int degree, List<DataSetRow> dataNotInFold)
	{
		int weightsLength = 1 + (degree * this.numberOfAttributes);
		int[] weights = new int[weightsLength];
		int tIterations = 0;
		int epochCount = 0;
		double cost = 0;
		double costChange = 0;
		
		while(!stopConditionsMet(epochCount, cost, costChange))
		{
			List<List<DataSetRow>> batches = createMiniBatches(dataNotInFold);
			for(List<DataSetRow> batch : batches)
			{
				for(int k = 0; k < weightsLength; k++)
				{
					
				}
			}
		}
	}

	private List<List<DataSetRow>> createMiniBatches(List<DataSetRow> notInFold) {
		List<List<DataSetRow>> batches = new LinkedList<>();
		Collections.shuffle(notInFold);
		int lastSub = 0;
		for(int i = 0; i < notInFold.size(); i++) 
		{
			int subSize = i * batchSize;
			if (i * batchSize > notInFold.size())
			{
				subSize = notInFold.size();
			}
			batches.add(notInFold.subList(lastSub, subSize));
			lastSub = subSize;
		}
		return batches;
	}
	
	private boolean stopConditionsMet(int epochCount, double cost, double costChange)
	{
		boolean stopConditionsMet = false;
		if (epochCount == epochLimit)
		{
			stopConditionsMet = true;
		}
		else if (cost < Math.pow(10, -10))
		{
			stopConditionsMet = true;
		}
		else if (costChange < Math.pow(10, -10))
		{
			stopConditionsMet = true;
		}
		return stopConditionsMet;
	}
}
