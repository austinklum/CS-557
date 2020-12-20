import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SARSA
{

	private String fileName;
	private int learningRate;
	private int learningRateDecay;
	private int epsilonDecay;
	private int episodes;
	private double successProbability;
	private boolean useQLearning;
	private boolean useUnicodeCharacters;
	private int verbosityLevel;
	
	public SARSA(String[] args)
	{
		setDefaults();
		processCommandLineArgs(args);
		loadFile();
		//run();
	}
	
	private void setDefaults()
	{
		this.learningRateDecay = 1000;
		this.epsilonDecay = 200;
		this.episodes = 10000;
		this.successProbability = .8;
		this.useQLearning = false;
		this.useUnicodeCharacters = false;
		this.verbosityLevel = 1;
	}
	
	private void processCommandLineArgs(String[] args) 
	{
		for (int i = 0; i < args.length; i++) 
		{
			switch (args[i])
			{
				case "-f":
					this.fileName = args[++i];
					break;
				case "-a":
					this.learningRateDecay = Integer.parseInt(args[++i]);
					break;
				case "-e":
					this.epsilonDecay = Integer.parseInt(args[++i]);
					break;
				case "-n":
					this.episodes = Integer.parseInt(args[++i]);
					break;
				case "-p":
					this.successProbability = Double.parseDouble(args[++i]);
					break;
				case "-q":
					this.useQLearning = true;
					break;
				case "-u":
					this.useUnicodeCharacters = true;
					break;
				case "-v":
					this.verbosityLevel = Integer.parseInt(args[++i]);
					break;
				default:
					System.out.println("Invalid argument");
					System.exit(-1);
					break;
			}
		}
	}
	
	private void loadFile()
	{
		tryToReadFile();
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
	
	
	private void print(int verbosity, String message)
	{
		if (verbosity <= verbosityLevel)
		{
			System.out.print(message);
		}
	}
}
