import java.awt.Point;
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
		this.episodes = 100;
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
		int colCount = 0;
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
				row[i] = new Cell(splitLine[i], colCount, i);
			}
			board.add(row);
			colCount++;
		}
		this.board = new Board(board.toArray(new Cell[board.size()][board.get(0).length]));
	}
	
	public void run() 
	{
		print(1, "* Beginning " + episodes + " learning episodes with " + (this.useQLearning ? "Q-Learning" : "SARSA") + "...\n");
		int totalReward = 0;
		for (int i = 0; i < episodes; i++) 
		{
			decay(i);
			board.resetBoard();
			Cell state = board.getAgentStart();
			Action action = getAlmostGreedyAction(state);
			int iteration = 0;
			while (iteration < board.getMaxIterations())
			{
				Cell nextState = execute(state, action);
				Action nextAction = getAlmostGreedyAction(nextState);
				
				totalReward += reward(nextState);
			
				double newQ = calculateNewQ(state, action, nextState, nextAction);
				state.actionQ().put(action, newQ); 
				
				state = nextState;
				action = nextAction;
				iteration++;
			}
			if (episodes + 1 % 100 == 0)
			{
				double avgReward = testRun();
			}
		}
		
		double avgReward = testRun();
		print(1, "* Avg. Total Reward of Learned Policy: " + avgReward + "\n");
		print(1, "* Learned greedy policy: \n" );
		printBoardActions();
	}
	
	public double testRun() 
	{
		int totalReward = 0;
		for (int i = 0; i < 50; i++) 
		{
			Cell state = board.getAgentStart();
			Action action = getAlmostGreedyAction(state);
			int iteration = 0;
			while (iteration < board.getMaxIterations())
			{
				Cell nextState = execute(state, action);
				Action nextAction = getAlmostGreedyAction(nextState);
				
				totalReward += reward(nextState);
				
				state = nextState;
				action = nextAction;
				iteration++;
			}
		}
		return (double)totalReward / 50.0;
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
		
		return state.bestAction();
	}
	
	private Cell execute(Cell state, Action action)
	{
		double rand = Math.random();

		Point newPoint = getNewPoint(state.x(), state.y(), action);
		
		if (rand > successProbability)
		{
			newPoint = drift(newPoint, action);
		}
		
		if (!board.inBounds(newPoint.x, newPoint.y))
		{
			return state;
		}
		
		return board.getCell(newPoint.x, newPoint.y);
	}

	private Point drift(Point newPoint, Action action)
	{
		if (action == Action.UP || action == Action.DOWN)
		{
			if (Math.random() >= .5)
			{ // Drift Left
				return new Point(newPoint.x - 1, newPoint.y);
			}
			// Drift Right
			return new Point(newPoint.x - 1, newPoint.y);
			
		}
		
		if (Math.random() >= .5)
		{ // Drift Up
			return new Point(newPoint.x, newPoint.y - 1);
		}
		// Drift down
		return new Point(newPoint.x, newPoint.y + 1);
	}
	
	private Point getNewPoint(int x, int y, Action action)
	{
		switch (action)
		{
			case DOWN:
				return new Point(x, y + 1);
			case UP:
				return new Point(x, y - 1);
			case LEFT:
				return new Point(x - 1, y);
			case RIGHT:
				return new Point(x + 1, y);
			default:
				return new Point(x,y);
		}
	}
	
	private int reward(Cell nextState)
	{
		switch (nextState.type())
		{
			case GOAL:
				return 0;
			case MINE:
				return -100;
			case CLIFF:
				return -20;
			default:
				return -1;
		}
	}
	
	private double calculateNewQ(Cell state, Action action, Cell nextState, Action nextAction)
	{
		double Q = state.actionQ(action);
		int reward = reward(state);
		double learnFactor = getLearnFactor(nextState, nextAction);
		double discountedQ = (futureDiscount * learnFactor);
		double learnedPart = learningRate * (reward + discountedQ - Q );
		return Q + learnedPart;
	}
	
	private double getLearnFactor(Cell state, Action action)
	{
		if (this.useQLearning)
		{
			return state.actionQ(state.bestAction());
		}
		
		return state.actionQ(action);
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

	public void printBoardActions()
	{
		
	}

}
