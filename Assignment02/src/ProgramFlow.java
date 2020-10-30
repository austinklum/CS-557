import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
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
		int pos = 0;
		while(scan.hasNext())
		{
			double[] attributes = new double[this.numberOfAttributes];
			for (int i = 0; i < this.numberOfAttributes; i++)
			{
				attributes[i] = scan.nextDouble();
			}
			data.add(new DataSetRow(pos, attributes, scan.nextDouble()));
			pos++;
		}
	}
	
	private void splitDataSetIntoKFolds()
	{
		Collections.shuffle(data);
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
		LinkedList<Double> validationErrors = new LinkedList<Double>();
		LinkedList<Double> trainErrors = new LinkedList<Double>();
		for(int d = this.smallestPolynomialDegree; d <= this.largestPolynomialDegree; d++)
		{
			List<DataSetRow> augmentedData = augmentData(d);
			for (int k = 1; k <= kFolds; k++) 
			{
				List<DataSetRow> dataNotInFold = getDataNotInFold(augmentedData, k);
				List<DataSetRow> dataInFold = getDataInFold(augmentedData, k);
				
				double[] weights = miniBatchGradientDescent(d, dataNotInFold);

				double trainError = calculateValidationError(weights, dataNotInFold);
				double validationError = calculateValidationError(weights, dataInFold);
				trainErrors.add(trainError);
				validationErrors.add(validationError);
				
				System.out.print(trainErrors.stream().mapToDouble(Double::doubleValue).sum() + " ");
				System.out.println(validationErrors.stream().mapToDouble(Double::doubleValue).sum());
				Arrays.stream(weights).forEach(w -> System.out.print(" " + w));
				System.out.println("");
			}
		}
	}

	private double calculateValidationError(double[] weights, List<DataSetRow> dataInFold)
	{
		double error = 0; 
		for (DataSetRow row : dataInFold)
		{
			double predictionY = predictY(weights, row);
			double YDifference = row.getTarget() - predictionY;

			error += Math.pow(YDifference, 2);
		}
		double errorNormalized = error / dataInFold.size();
		return errorNormalized;
	}
	
	private List<DataSetRow> getDataNotInFold(List<DataSetRow> augmentedData, int foldNumber) {
		List<DataSetRow> dataNotInFold = augmentedData
				.stream()
				.filter(row -> row.getFold() != foldNumber)
				.collect(Collectors.toList());
		return dataNotInFold;
	}
	
	private List<DataSetRow> getDataInFold(List<DataSetRow> augmentedData, int foldNumber) {
		List<DataSetRow> dataInFold = augmentedData
				.stream()
				.filter(row -> row.getFold() == foldNumber)
				.collect(Collectors.toList());
		return dataInFold;
	}
	
	private double[] miniBatchGradientDescent(int degree, List<DataSetRow> dataNotInFold)
	{
		int weightsLength = 1 + (degree * this.numberOfAttributes);
		double[] weights = new double[weightsLength];
		int tIterations = 0;
		int epochCount = 0;
		double cost = 1;
		double costChange = 1;
		
		while(!stopConditionsMet(epochCount, cost, costChange))
		{
			List<List<DataSetRow>> batches = createMiniBatches(dataNotInFold, weightsLength);
			for(List<DataSetRow> batch : batches)
			{
				for(int k = 0; k < weightsLength; k++)
				{
					weights[k] = calculateNewWeight(weights, batch, k);
				}
				tIterations++;
			}
			epochCount++;
		}
		return weights;
	}

	private double calculateNewWeight(double[] weights, List<DataSetRow> batch, int k) 
	{
		double gradient = calculateGradient(batch, weights, k);
		double gradDesc = learningRate * gradient;
		double newWeight = weights[k] - gradDesc;
		return newWeight;
	}

	private List<List<DataSetRow>> createMiniBatches(List<DataSetRow> notInFold, int weightsLength) {
		List<List<DataSetRow>> batches = new LinkedList<>();
		Collections.shuffle(notInFold);
		
		if (batchSize == 0)
		{
			batches.add(notInFold);
			return batches;
		}
		
		int lastSub = 0;
		int numBatches = (int)Math.ceil((double)notInFold.size() / batchSize);
		for(int i = 0; i < numBatches; i++) 
		{
			int sublistEndIndex = getSublistEndIndex(notInFold.size(), lastSub);
			
			batches.add(notInFold.subList(lastSub, sublistEndIndex));
			lastSub = sublistEndIndex;
		}
	
		return batches;
	}

	private int getSublistEndIndex(int notInFoldSize, int lastSub) 
	{
		int sublistEndIndex = lastSub + batchSize;
		if (sublistEndIndex > notInFoldSize)
		{
			sublistEndIndex = notInFoldSize;
		}
		return sublistEndIndex;
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
	
	private double calculateGradient(List<DataSetRow> batch, double[] weights, int k)
	{
		double unnormalizedGradient = 0;
		for (DataSetRow row : batch)
		{
			double scalar = -2 * row.getAttributeAt(k);
			double cost = cost(weights, row);
			unnormalizedGradient += scalar * cost;
		}
		double normalizedGradient = unnormalizedGradient / batch.size();
		return normalizedGradient;
	}

	private double l2Loss(double[] weights, DataSetRow row)
	{
		return Math.pow(cost(weights, row), 2);
	}
	
	private double cost(double[] weights, DataSetRow row) {
		double predictionY = predictY(weights, row);
		double targetMinusSum = row.getTarget() - predictionY;
		return targetMinusSum;
	}

	private double predictY(double[] weights, DataSetRow row) {
		double predictedY = 0;
		for (int j = 0; j < weights.length; j++)
		{
			predictedY += weights[j] * row.getAttributeAt(j);
		}
		return predictedY;
	}
	
	private List<DataSetRow> augmentData(int degree)
	{
		int weightsLength = 1 + (degree * this.numberOfAttributes);
		List<DataSetRow> augmentedData = data.stream().collect(Collectors.toList());
		for (DataSetRow row : augmentedData)
		{
			double[] newAttributes = new double[weightsLength];
			newAttributes[0] = 1;
			
			int oldAttributesLength = row.getAttributes().length;
			int pow = 0;
			
			for (int i = 1; i < weightsLength; i++)
			{
				int modI = (i-1) % oldAttributesLength;
				if (modI == 0)
				{
					pow++;
				}
				double xSubi = row.getAttributeAt(modI);
				newAttributes[i] = Math.pow(xSubi, pow);
			}
			row.setAttributes(newAttributes);
		}
		return augmentedData;
	}
}
