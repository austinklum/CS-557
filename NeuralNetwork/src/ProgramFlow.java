/**
 * @author Austin Klum
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramFlow {
	// Required
	private String fileName;
	// Optional
	private Layer inputLayer;
	private Layer[] hiddenLayers;
	private Layer outputLayer;
	
	private boolean isLogLoss;
	private double learningRate;
	private int epochLimit;
	private int batchSize;
	private double regularization;
	private double epsilonRange;
	private int verbosityLevel;
	
	private int attributeLength;
	
	private LinkedList<DataSetRow> data;
	private List<DataSetRow> trainSet;
	private List<DataSetRow> testSet;
	
	private ActivationFunction activationFunction;
	
	public ProgramFlow(String[] args) {
		setDefaults();
		processCommandLineArgs(args);
		createDataSets();
		run();
	}

	private void setDefaults()
	{
		this.inputLayer = new Layer(new LinkedList<Neuron>());
		this.hiddenLayers = new Layer[0];
		this.outputLayer = new Layer(new LinkedList<Neuron>());
		
		this.isLogLoss = false;
		this.learningRate = .01;
		this.epochLimit = 1000;
		this.batchSize = 1;
		this.regularization = 0.0;
		this.epsilonRange = .01;
		this.verbosityLevel = 1;
		
		this.data = new LinkedList<>();
		this.activationFunction = new SigmoidActivation();
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
					this.hiddenLayers = new Layer[size];
					
					for (int j = 0; j < size; j++)
					{
						this.hiddenLayers[j] = new Layer();
						this.hiddenLayers[j].size = Integer.parseInt(args[++i]);
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
		print(1, "* Reading "  + fileName + "...\n");
		int pos = 0;
		while(scan.hasNext())
		{
			String line = scan.nextLine();
			if(line.startsWith("#") || line.isEmpty())
			{
				continue;
			}
			String[] lineArr = line.split("\\) \\(");
			String[] attrString = lineArr[0].replace("(", "").split(" ");
			String[] targetString = lineArr[1].replace(")", "").split(" ");
			
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
		print(1, "* Scaling features...\n");
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
		print(1, "* Doing train/test split...\n");
		Collections.shuffle(data);
		int count = (int) (data.size() * .8);
		trainSet = data.subList(0, count);
		testSet = data.subList(count, data.size());
	}
	
	private void run()
	{
		//setup NN
		setUpHiddenLayers();
	
		print(1, "* Beginning evaluation...\n");
		int tIterations = 0;
		int epochs = 0;
		double absoluteError = 1;
		print(2, "   * Training with training data...\n");
		print(3, "     * Layer Sizes:\n");
		printLayers();
		print(3, "     * Beginning mini-batch gradient descent\n");
		print(3, "       (batchSize=" + batchSize + ", epochLimit=" + epochLimit +", learningRate=" + learningRate + ", lambda=" + regularization + "\n");
		if (epochs % 1000 == 0)
		{
			printEpochStats(epochs, tIterations);
		}
		long startTime = System.currentTimeMillis();
		while(!stopConditionsMet(epochs, absoluteError))
		{
			List<List<DataSetRow>> batches = createMiniBatches(trainSet);
			for(List<DataSetRow> batch : batches)
			{
				resetNeurons();
				for (DataSetRow row : batch)
				{
					//System.out.println("Row " + i++ + " of " + batch.size());
					backpropUpdate(row);
				}
				updateWeights(batch.size());
				updateOutputLayerWeights(batch.size());
				tIterations++;
			}
			epochs++;
		}
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		print(3, "     * Done with fitting!\n");
		print(3, "Training took " + time + "ms, " + epochs + " epochs, " + tIterations  + " iterations (" + (double) time/tIterations + " / iteration)\n" );
		printEpochStats(epochs, tIterations);
		double trainAccuracy = evaluateAccuracy(trainSet);
		print(2, "   * Evaluating accuracy on the test data...");
		double testAccuracy = evaluateAccuracy(testSet);
		print(1, "* Results:\n");
		System.out.printf("   TrainAcc: %1.6f\n", trainAccuracy);
		System.out.printf("   TestAcc:  %1.6f\n", testAccuracy);
	}

	private double evaluateAccuracy(List<DataSetRow> rows)
	{
		double result = 0;
		double correct = 1;
		for (DataSetRow row : rows)
		{
			forwardProp(row);
			if( row.getTarget() == outputLayer.getPositionOfClassification())
			{
				correct++;
			}
		}
		result = correct / (rows.size() + 1);
		return result;
	}
	
	private void setUpHiddenLayers()
	{
		for(Layer layer : hiddenLayers)
		{
			layer.setupEmptyLayer();
		}
		for (int i = 0; i < hiddenLayers.length - 1; i++) 
		{
			hiddenLayers[i].connectToLayer(hiddenLayers[i+1], randomWeightScalar());
		}
	}

	private void resetNeurons()
	{
		resetNeuronsInLayer(inputLayer);
		inputLayer.setNeurons(new ArrayList<Neuron>());
		for(Layer layer : hiddenLayers)
		{
			resetNeuronsInLayer(layer);
		}
		resetNeuronsInLayer(outputLayer);
		outputLayer.setNeurons(new ArrayList<Neuron>());
	}
	
	private void resetNeuronsInLayer(Layer layer)
	{
		for(Neuron neuron : layer.getNeurons())
		{
			neuron.setDeltas(new ArrayList<>());
			neuron.setOutputs(new ArrayList<>());
			neuron.setDelta(0);
			neuron.setInput(0);
			neuron.setOutput(0);
		}
	}

	private void backpropUpdate(DataSetRow row)
	{
		forwardProp(row);
		backProp(row);
	}

	private void backProp(DataSetRow row) {
		int i = 0;
		for(Neuron neuron : outputLayer.getNeurons())
		{
			double actualOutput = (i == row.getTarget()) ? 1 : 0;
			neuron.updateOutputLayerDelta(actualOutput);
			i++;
		}
		
		for (int l = hiddenLayers.length - 1; l > 0; l--)
		{
			for (Neuron neuron : hiddenLayers[l].getNeurons())
			{
				neuron.updateDelta();
			}
		}
	}

	private void forwardProp(DataSetRow row) {
		setupInputOutputLayers(row);
		
		for (Layer layer : hiddenLayers)
		{
			for (Neuron neuron : layer.getNeurons())
			{
				neuron.updateInput();
				neuron.activate();
			}
		}
		
		for (Neuron neuron : outputLayer.getNeurons())
		{
			neuron.updateInput();
			neuron.activate();
		}
	}

	private void updateWeights(int batchSize)
	{
		for (int l = 0; l < hiddenLayers.length; l++)
		{
			for (Neuron neuron : hiddenLayers[l].getNeurons())
			{
				List<Double> deltas = neuron.getDeltas();
			
				for(WeightEdge edge : neuron.getOutEdges())
				{
					List<Double> ais = edge.getEnd().getOutputs();
					double sum = 0;
					for (int n = 0; n < batchSize; n++)
					{
						sum += deltas.get(n) * ais.get(n);
					}
					double avgSum = sum / batchSize;
					double jw = avgSum + 2 * this.regularization * edge.getWeight();
					double newWeight = edge.getWeight() - this.learningRate * jw;
					edge.setWeight(newWeight);
				}
			}
		}
	}
	
	private void updateOutputLayerWeights(int batchSize)
	{
		for (Neuron neuron : outputLayer.getNeurons())
		{
			List<Double> deltas = neuron.getDeltas();
		
			for(WeightEdge edge : neuron.getOutEdges())
			{
				List<Double> ais = edge.getEnd().getOutputs();
				double sum = 0;
				for (int n = 0; n < batchSize; n++)
				{
					sum += deltas.get(n) * ais.get(n);
				}
				double avgSum = sum / batchSize;
				double jw = avgSum + 2 * this.regularization * edge.getWeight();
				double newWeight = edge.getWeight() - this.learningRate * jw;
				edge.setWeight(newWeight);
			}
		}
	}
	
	
	private double randomWeightScalar()
	{
		return (epsilonRange * -1)	+ (epsilonRange - (epsilonRange * -1));
	}
	
	private void setupInputOutputLayers(DataSetRow row) 
	{
		outputLayer.setupEmptyLayer(row.getTargetLength());
		inputLayer.setupEmptyLayer(row.getAttributes().length);
		
		Layer firstLayer;
		if (hiddenLayers.length > 0)
		{
			firstLayer = hiddenLayers[0]; 
			hiddenLayers[hiddenLayers.length - 1].connectToLayer(outputLayer, randomWeightScalar());
		}
		else
		{
			firstLayer = outputLayer;
		}
		
		inputLayer.connectToLayer(firstLayer, randomWeightScalar());
		
		for(int i = 0; i < inputLayer.size; i++)
		{
			inputLayer.getNeuronAt(i).setOutput(row.getAttributeAt(i));
		}
		
	}

	private double getRand()
	{
		Random rand = new Random();
		return (epsilonRange * -1)	+ (epsilonRange - (epsilonRange * -1)) * rand.nextDouble();
	}

	private List<List<DataSetRow>> createMiniBatches(List<DataSetRow> fullSet) {
		List<List<DataSetRow>> batches = new LinkedList<>();
		Collections.shuffle(fullSet);
		if (batchSize == 0)
		{
			batches.add(fullSet);
			return batches;
		}
		
		int lastSub = 0;
		int numBatches = (int)Math.ceil((double)fullSet.size() / batchSize);
		for(int i = 0; i < numBatches; i++) 
		{
			int sublistEndIndex = getSublistEndIndex(fullSet.size(), lastSub);
			
			batches.add(fullSet.subList(lastSub, sublistEndIndex));
			lastSub = sublistEndIndex;
		}
	
		return batches;
	}

	private int getSublistEndIndex(int fullSetSize, int lastSub) 
	{
		int sublistEndIndex = lastSub + batchSize;
		if (sublistEndIndex > fullSetSize)
		{
			sublistEndIndex = fullSetSize;
		}
		return sublistEndIndex;
	}
	
	private boolean stopConditionsMet(int epochCount, double absoluteError)
	{
		boolean stopConditionsMet = false;
		if (epochCount == epochLimit)
		{
			print(3, "     GD Stop condition: EpochLimit reached\n");
			stopConditionsMet = true;
		}
		else if (absoluteError < .01)
		{
			print(3, "     GD Stop condition: Absolute Error ~= 0\n");
			stopConditionsMet = true;
		}
		return stopConditionsMet;
	}

	private void print(int verbosity, String message)
	{
		if (verbosity <= verbosityLevel)
		{
			System.out.print(message);
		}
	}

	private void printLayers() 
	{
		if (verbosityLevel < 3)
		{
			return;
		}
		int i = 1;
		for(Layer layer : hiddenLayers)
		{
			System.out.println("     * Layer: " + i++ + "\t" + layer.size);
		}
	}
	
	
	private void printEpochStats(int epochCount, int tIterations)
	{
		if (verbosityLevel < 4)
		{
			return;
		}
		System.out.printf("        Epoch %6d (iter %6d):\n", epochCount, tIterations);
		
	}
	
}
