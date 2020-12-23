import java.awt.Point;

public class Board 
{
	Cell[][] board;
	Point agentStart;
	
	public Board(Cell[][] board)
	{
		this.board = board;
		setAgentStart();
	}
	
	public boolean inBounds(int x, int y)
	{
		if (x < 0 || y < 0) return false;
		if (x >= width() || y >= height()) return false;
		
		return true;
	}
	
	public void printBoard()
	{
		for (int x = 0; x < board.length; x++)
		{
			for (int y = 0; y < board[0].length; y++) 
			{
				System.out.print(getCell(x,y).type() + " ");
			}
			System.out.println();
		}
	}
	
	
	public Cell getCell(int x, int y)
	{
		return board[x][y];
	}
	
	public int width()
	{
		return board.length;
	}
	
	public int height()
	{
		return board[0].length;
	}
	
	private void setAgentStart()
	{
		for (int x = 0; x < board.length; x++) 
		{
			for (int y = 0; y < board[0].length; y++)
			{
				if (getCell(x,y).type() == Cell.CellType.START)
				{
					agentStart = new Point(x,y);
					return;
				}
			}
		}
	}
	
	public Cell getAgentStart()
	{
		return board[agentStart.x][agentStart.y];
	}
	
	public int getMaxIterations()
	{
		return this.width() * this.height();
	}
	
	public void resetBoard()
	{
		for (int x = 0; x < board.length; x++) 
		{
			for (int y = 0; y < board[0].length; y++)
			{
				Cell cell = this.getCell(x, y);
				cell.actionQ().put(Action.UP, 0.0);
				cell.actionQ().put(Action.DOWN, 0.0);
				cell.actionQ().put(Action.LEFT, 0.0);
				cell.actionQ().put(Action.RIGHT, 0.0);
			}
		}
	}
		

	public void printBoardActions(boolean useUnicode)
	{
		for (int x = 0; x < board.length; x++) 
		{
			for (int y = 0; y < board[0].length; y++)
			{
				Cell cell = this.getCell(x, y);
				if (cell.type() == Cell.CellType.EMPTY)
				{
					Action action = cell.getBestActionSet();
					System.out.print(action.getRepresentation(useUnicode));
				}
				else
				{
					System.out.print(cell.typeChar());
				}
			}
			System.out.println();
		}
	}
	
	public void printBoardQ()
	{
		   for (int xx = 0; xx < board.length; xx++)
		   {
	            System.out.print("-");
	            
	            for (int y = 0; y < board[0].length; y++)
	            {
	                System.out.print("------------");
	            }
	            
	            System.out.println();

	            System.out.print("|");
	            for (int y = 0; y < board[0].length; y++) 
	            {
	                System.out.printf("  %6.1f   |", getCell(xx,y).actionQ(Action.UP));
	            }
	            System.out.println();

	            System.out.print("|");
	            for (int y = 0; y < board[0].length; y++) 
	            {
	                System.out.printf("%6.1f     |", getCell(xx,y).actionQ(Action.LEFT));
	            }
	            System.out.println();

	            System.out.print("|");
	            for (int y = 0; y < board[0].length; y++)
	            {
	                System.out.printf("     %6.1f|", getCell(xx,y).actionQ(Action.RIGHT));
	            }
	            System.out.println();

	            System.out.print("|");
	            for (int y = 0; y < board[0].length; y++) 
	            {
	                System.out.printf("  %6.1f   |", getCell(xx,y).actionQ(Action.DOWN));
	            }
	            System.out.println();
	        }
	        System.out.print("-");
	        for (int y = 0; y < board[0].length; y++) 
	        {
	            System.out.print("------------");
	        }
	        System.out.println();
	}
	
}
