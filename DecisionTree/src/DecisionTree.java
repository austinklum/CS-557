import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class DecisionTree {
	private int i;
	private int l;
	private int t;
	private boolean shouldPrint;
	
	public DecisionTree(int i, int l, int t, boolean shouldPrint)
	{
		this.i = i;
		this.l = l;
		this.t = t;
		this.shouldPrint = shouldPrint;
	}
	
	public DecisionTree(String[] args)
	{
		processArgs(args);
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
		
		
		scan.close();
	}
}
