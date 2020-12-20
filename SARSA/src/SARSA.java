
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
		//createDataSets();
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
	
}
