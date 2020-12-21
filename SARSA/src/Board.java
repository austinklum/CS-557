import java.awt.Point;

public class Board 
{
	Cell[][] board;
	Point agentPos;
	
	public Board(Cell[][] board)
	{
		this.board = board;
	}
	
	public Cell getCell(int x, int y)
	{
		return board[x][y];
	}
}
