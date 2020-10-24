
public class ProgramFlow {
	// Required
	private String file;
	// Optional
	private int kFolds;
	private int smallestPolynomialDegree;
	private int largestPolynomialDegree;
	private double learningRate;
	private int epochLimit;
	private int batchSize;
	private int verbosityLevel;

	public ProgramFlow(String[] args) {
		setDefaults();
		processCommandLineArgs(args);
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
	}

	private void processCommandLineArgs(String[] args) 
	{
		for (int i = 0; i < args.length; i++) {
			switch (args[i])
			{
				case "-f":
					this.file = args[++i];
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
		
	private void createDataSet() {

	}

}
