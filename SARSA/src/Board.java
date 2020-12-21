
public class Board 
{
	Cell[][] board;
	
	public Board(Cell[][] board)
	{
		this.board = board;
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
