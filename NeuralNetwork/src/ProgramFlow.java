/**
 * @author Austin Klum
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramFlow {
	// Required
	private String fileName;
	// Optional
	private ArrayList<Integer> hiddenLayers;
	private boolean isLogLoss;
	private double learningRate;
	private int epochLimit;
	private int batchSize;
	private double regularization;
	private double epsilonRange;
	private int verbosityLevel;
	
	private LinkedList<DataSetRow> data;
	private List<DataSetRow> trainSet;
	private List<DataSetRow> testSet;
	
	public ProgramFlow(String[] args) {
		setDefaults();
		processCommandLineArgs(args);
		createDataSets();
		run();
	}

	private void setDefaults()
	{
		this.hiddenLayers = new ArrayList<>();
		this.isLogLoss = false;
		this.learningRate = .01;
		this.epochLimit = 1000;
		this.batchSize = 1;
		this.regularization = 0.0;
		this.epsilonRange = .01;
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
				case "-h":
					int size = Integer.parseInt(args[++i]);
					this.hiddenLayers = new ArrayList<Integer>(size);
					
					for (int j = 0; j < size; j++)
					{
						this.hiddenLayers.add(Integer.parseInt(args[++i]));
					}
					break;
				case "-l":
					this.isLogLoss = true;
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
				case "-r":
					this.regularization = Integer.parseInt(args[++i]);
					break;
				case "-w":
					this.epsilonRange = Integer.parseInt(args[++i]);
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
	}

	private void printInvalidArgUsage(String arg) {
		System.out.println(arg + " is not a supported command-line argument.");
		System.out.println("Usage: -f fileName <FILENAME> "
				+ "[-h <NH> <S1 <S2> .. hiddenLayers <INTEGERLIST>{0}] "
				+ "[-l IsLogLoss {false}] "
				+ "[-a learningRate <DOUBLE>{.01}] "
				+ "[-e epochLimit <INTEGER>{1000}] "
				+ "[-m batchSize <INTEGER>{1}] "
				+ "[-r regularization <DOUBLE>{0.0}] "
				+ "[-w epsilonRange <DOUBLE>{0.01}] "
				+ "[-v verbosityLevel <INTEGER>]{1}");
	}

	private void createDataSets()
	{
		tryToReadFile();
		featureScaling(data);
		splitDataIntoSets();
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
			String line = scan.nextLine();
			if(line.startsWith("#") || line.isEmpty())
			{
				continue;
			}
			String[] lineArr = scan.nextLine().split(") (");
			String[] attrString = lineArr[0].replaceAll("(", "").split(" ");
			String[] targetString = lineArr[1].replaceAll(")", "").split(" ");
			
			Double[] attr = Stream.of(attrString).map(Double::valueOf).collect(Collectors.toList()).toArray(new Double[attrString.length]);
			int targetPos = 0;
			while(!targetString[targetPos].equals("1"))
			{
				targetPos++;
			}
			
			data.add(new DataSetRow(pos, attr, targetPos, targetString.length));
			pos++;
		}
	}
	
	private void featureScaling(List<DataSetRow> data)
	{
		int numberOfFeatures = data.get(0).getAttributes().length;
		for (int i = 0; i < numberOfFeatures; i++)
		{
			double max = data.get(0).getAttributeAt(i);
			double min = data.get(0).getAttributeAt(i);
			// Get min and max
			for (DataSetRow row : data)
			{ 
				double value = row.getAttributeAt(i);
				if (value > max)
				{
					max = value;
				}
				if (value < min)
				{
					min = value;
				}
			}
			
			scaleFeatureValues(i, data, max, min);		
		}
	}
	
	private void scaleFeatureValues(int i, List<DataSetRow> data, double max, double min)
	{
		for (DataSetRow row : data)
		{
			if (max == min)
			{
				row.setAttributeAt(i, -1);
				continue;
			}
			double value = row.getAttributeAt(i);
			double scaledValue = -1 + 2 * ((value - min) / (max - min));
			row.setAttributeAt(i, scaledValue);
		}
	}
	
	private void splitDataIntoSets()
	{
		Collections.shuffle(data);
		int count = (int) (data.size() * .8);
		trainSet = data.subList(0, count);
		testSet = data.subList(count, data.size());
	}
	
	private void run()
	{
		
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
	
	private double[] miniBatchGradientDescent(int degree, List<DataSetRow> dataNotInFold)
	{
		int weightsLength = 1 + (degree * this.hiddenLayers.size());
		double[] weights = new double[weightsLength];
		int tIterations = 0;
		int epochCount = 0;
		double costChange = 0;
		double cost = 0;
		print(4, "       * Beginning mini-batch gradient descent\n");
		print(4, "         (alpha=" + learningRate + ", epochLimit=" + epochLimit + ", batchSize=" + batchSize + ")\n");
		long startTime = System.currentTimeMillis();
		while(!stopConditionsMet(epochCount, cost, costChange))
		{
			List<List<DataSetRow>> batches = createMiniBatches(dataNotInFold, weightsLength);
			double costBatch = costBatches(weights, batches);  
			costChange = Math.abs(cost - costBatch);
			//System.out.println("Cost: " + cost + " CostBatch: " + costBatch + " CostChange: " + costChange);
			cost = costBatch;
			if (epochCount % 1000 == 0)
			{
				printEpochStats(epochCount, tIterations, weights);
			}
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
		long endTime = System.currentTimeMillis();
		printEpochStats(epochCount, tIterations, weights);
		print(4, "      * Done with fitting!\n");
		long trainTime = endTime - startTime;
		double timePerIteration = (double) tIterations / trainTime;
		print(3, "     Training took " + trainTime + "ms, " + epochCount + " epochs, " + tIterations + " iterations (" + timePerIteration + "ms / iteration)\n");
		
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
			print(3, "     GD Stop condition: EpochLimit reached\n");
			stopConditionsMet = true;
		}
		else if (cost < Math.pow(10, -10))
		{
			print(3, "     GD Stop condition: Cost ~= 0\n");
			stopConditionsMet = true;
		}
		else if (costChange < Math.pow(10, -10))
		{
			//System.out.println("costChange: " + costChange);
			print(3, "      GD Stop condition: DeltaCost ~= 0\n");
			costChange = 1;
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
			//costBookKeeping(cost);
			unnormalizedGradient += scalar * cost;
		}
		double normalizedGradient = unnormalizedGradient / batch.size();
		return normalizedGradient;
	}

//	private void costBookKeeping(double cost) 
//	{
//		double costSquared = Math.pow(cost, 2);
//		if (isFirstIter) 
//		{
//			isFirstIter = false;
//			this.costChange = costSquared;
//			this.cost = costSquared;
//		}
//		else
//		{
//			this.costChange = this.cost - costSquared; // eww... This is shoe horned in
//			this.cost = costSquared; // these definitely shouldn't be globals..
//		}
//	}

	private double l2Loss(double[] weights, DataSetRow row)
	{
		return Math.pow(cost(weights, row), 2);
	}
	
	private double costBatch(double[] weights, List<DataSetRow> batch)
	{
		double sum = 0;
		for(DataSetRow row : batch)
		{
			sum += Math.pow(cost(weights, row), 2);
		}
		double sumAvg = sum / batch.size();
		return sumAvg;
	}
	
	private double costBatches(double[] weights, List<List<DataSetRow>> batches)
	{
		double sum = 0;
		int size = 0;
		for(List<DataSetRow> batch : batches) 
		{
			for(DataSetRow row : batch)
			{
				sum += Math.pow(cost(weights, row), 2);
			}
			size += batch.size();
		}
		double sumAvg = sum / size;
		return sumAvg;
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
	
	private void print(int verbosity, String message)
	{
		if (verbosity <= verbosityLevel)
		{
			System.out.print(message);
		}
	}
	
	private void printWhen(int verbosity, String message)
	{
		if (verbosity == verbosityLevel)
		{
			System.out.print(message);
		}
	}
	
	private void printError(String fold, double trainMSE, double validMSE)
	{
		if (verbosityLevel == 2)
		{
			System.out.printf("  %s\t\t%.6f\t%,6f\n", fold, trainMSE, validMSE);
		}
		if (verbosityLevel > 2)
		{
			System.out.printf("     CurFoldTrainErr:     %.6f\n", trainMSE);
			System.out.printf("     CurFoldValidErr:     %.6f\n\n", validMSE);
		}
	}
	
	private void printAvgError(int degree, double trainMSESum, double validMSESum)
	{
		double trainMSE = trainMSESum / 1;//kFolds;
		double validMSE = validMSESum / 1;//kFolds;
		String avgLabel = "Avg:";
		
		if (verbosityLevel == 1)
		{
			avgLabel = Integer.toString(degree);
		}
		if (verbosityLevel <= 2)
		{
			System.out.printf("%6s\t\t%.6f\t%,6f\n", avgLabel, trainMSE, validMSE);
		}
		if (verbosityLevel > 2)
		{
			System.out.println("   * Averaging across the folds");
			System.out.printf("     AvgFoldTrainError:   %.6f\n", trainMSE);
			System.out.printf("     AvgFoldValidError:   %.6f\n", validMSE);
		}
	}
	
	private void printDegreeHeader(int d)
	{
		printWhen(2, "----------------------------------\n");
		print(2, "* Testing degree " + d + "\n");
		printWhen(2, "\t\tTrainMSE\tValidMSE\n");
	}
	
	private void printModel(double[] weights)
	{
		if (verbosityLevel < 3)
		{
			return;
		}
		System.out.print("     Model: Y = ");
		System.out.printf("%.4f", weights[0]);
		for (int i = 1; i < weights.length; i++)
		{
			int attrCount = 1;
			while(attrCount <= this.hiddenLayers.size())
			{
				System.out.printf(" + %.4f X%d ^%d", weights[i], attrCount, i);
				attrCount++;
			}
		}
		System.out.println("");
	}
	
	private void printEpochStats(int epochCount, int tIterations, double[] weights)
	{
		if (verbosityLevel < 4)
		{
			return;
		}
		//System.out.printf("         Epoch %6d (iter %6d): Cost = %.9f;", epochCount, tIterations, cost);
		if (verbosityLevel == 5)
		{
			printModel(weights);
		}
		else
		{
			System.out.println("");
		}
	}
	
}
