import java.awt.Point;
import java.awt.geom.Point2D;

public class Board 
{
	Cell[][] board;
	Point agentStart;
	
	public Board(Cell[][] board)
	{
		this.board = board;
		setAgentStart();
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
}
