import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
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

	public ProgramFlow(String[] args) {
		setDefaults();
		processCommandLineArgs(args);
		LinkedList<Fold> splitData = createDataSet();
		run(splitData);
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
		
	private LinkedList<Fold> createDataSet()
	{
		Matcher matcher = Pattern.compile("(?:-[A-z])?([0-9]+)(?:-[A-z])([0-9]+)").matcher(fileName);
		matcher.find();
		int attributeNumber = Integer.parseInt(matcher.group(1));
		int trueFunctionDegree = Integer.parseInt(matcher.group(2));
		tryToReadFile(attributeNumber);
		LinkedList<Fold> splitData = splitDataSetIntoKFolds();
		return splitData;
	}

	private void tryToReadFile(int attributeNumber)
	{
			Scanner scan;
			try {
				scan = new Scanner(new File(fileName));
				readFile(scan, attributeNumber);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
	}
	
	private void readFile(Scanner scan, int attributeNumber)
	{
		while(scan.hasNext())
		{
			double[] attributes = new double[attributeNumber];
			for (int i = 0; i < attributeNumber; i++)
			{
				attributes[i] = scan.nextDouble();
			}
			data.add(new DataSetRow(attributes,scan.nextDouble()));
		}
	}
	
	private LinkedList<Fold> splitDataSetIntoKFolds()
	{
		LinkedList<Fold> splitData = new LinkedList<>();
		LinkedList<DataSetRow> kFoldData = new LinkedList<>();
		int rowCount = 0;
		int foldNumber = 0;
		for(DataSetRow row : data)
		{
			if(rowCount % kFolds == 0)
			{
				splitData.add(new Fold(foldNumber++, kFoldData));
				kFoldData = new LinkedList<>();
			}
			kFoldData.add(row);
			rowCount++;
		}
		return splitData;
	}
	
	private void run(LinkedList<Fold> splitData)
	{
		for(int d = this.smallestPolynomialDegree; d <= this.largestPolynomialDegree; d++)
		{
			for(Fold fold : splitData)
			{
				List<Fold> dataNotInFold = splitData.stream()
						.filter(aFold -> aFold.getFoldNumber() != fold.getFoldNumber())
						.collect(Collectors.toList());
				miniBatchGradientDescent(dataNotInFold);
			}
		}
	}
	
	private void miniBatchGradientDescent(List<Fold> dataNotInFold)
	{
		LinkedList<Integer> weights = new LinkedList<>();
	}
}
