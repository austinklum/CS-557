import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class SARSA
{
	
	private String fileName;
	private double learningRate;
	private int learningRateDecay;
	private double futureDiscount;
	private double epsilon;
	private int epsilonDecay;
	private int episodes;
	private double successProbability;
	private boolean useQLearning;
	private boolean useUnicodeCharacters;
	private int verbosityLevel;
	
	private Board board;
	private HashMap<Cell, Action> Q;
	
	public SARSA(String[] args)
	{
		setDefaults();
		processCommandLineArgs(args);
		readFile();
		run();
	}
	
	private void setDefaults()
	{
		this.learningRate = .9;
		this.learningRateDecay = 1000;
		this.epsilon = .9;
		this.epsilonDecay = 200;
		this.futureDiscount = .9;
		this.episodes = 10000;
		this.successProbability = .8;
		this.useQLearning = false;
		this.useUnicodeCharacters = false;
		this.verbosityLevel = 1;
		this.Q = new HashMap<>();
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
	
	private void readFile()
	{
		Scanner scan;
		try
		{
			scan = new Scanner(new File(fileName));
			readFile(scan);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void readFile(Scanner scan)
	{
		print(1, "* Reading "  + fileName + "...\n");
		ArrayList<Cell[]> board = new ArrayList<>();
		while(scan.hasNext())
		{
			String line = scan.nextLine();
			if(line.startsWith("#") || line.isEmpty())
			{
				continue;
			}
			String[] splitLine = line.split("");
			Cell[] row = new Cell[splitLine.length];
			for (int i = 0; i < row.length; i++)
			{
				row[i] = new Cell(splitLine[i]);
			}
			board.add(row);
		}
		this.board = new Board(board.toArray(new Cell[board.size()][board.get(0).length]));
	}
	
	public void run() 
	{
		for (int i = 0; i < episodes; i++) 
		{
			decay(i);
			Cell state = board.getAgentStart();
			Action action = getAlmostGreedyAction(state);
			int iteration = 0;
			while (iteration < board.getMaxIterations())
			{
				// Cell nextState = execute(action);
				// double reward = reward(state, action, nextState);
				// Action nextAction = getGreedyAction(nextState);
				// double newQ = calculateNewQ(state, action, nextState, nextAction)
				// state = nextState;
				// action = nextAction;
			}
		}
	}

	private void decay(int episodeCount) 
	{
		if (episodeCount % learningRateDecay == 0)
		{
			updateLearningRate(episodeCount);
		}
		
		if (episodeCount % epsilonDecay == 0)
		{
			updateEpsilon(episodeCount);
		}
	}
	
	private void updateLearningRate(int episodeCount)
	{
		double denominator = 1 + Math.floor(episodeCount / learningRateDecay);
		learningRate = 0.9 / denominator;
	}
	
	private void updateEpsilon(int episodeCount)
	{
		double denominator = 1 + Math.floor(episodeCount / epsilonDecay);
		epsilon = 0.9 / denominator;
	}
	
	private Action getAlmostGreedyAction(Cell state)
	{
		if (Math.random() < epsilon)
		{
			Random random = new Random();
			int size = state.actionQ().size();
			return state.actionQ().keySet().toArray(new Action[size])[random.nextInt(size)];
		}
		
		Set<Map.Entry<Action, Double>> entrySet = state.actionQ().entrySet();
		return  entrySet.stream()
	        	.max(Comparator.comparingDouble(Map.Entry::getValue))
	        	.get()
	        	.getKey();
	}
	
	public void printBoard()
	{
		board.printBoard();
	}
	
	private void print(int verbosity, String message)
	{
		if (verbosity <= verbosityLevel)
		{
			System.out.print(message);
		}
	}


}
