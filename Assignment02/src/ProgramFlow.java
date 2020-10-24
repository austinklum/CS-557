
public class ProgramFlow 
{
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
	
	
	public ProgramFlow(String[] args)
	{
		setDefaults();
		processCommandLineArgs(args);
	}
	
	private void setDefaults()
	{
		
	}
	
	private void processCommandLineArgs(String[] args)
	{
		
	}
	
	private void createDataSet()
	{
		
	}
	
}
