import java.util.IllegalFormatException;

public class Cell 
{
	public enum CellType { START, GOAL, EMPTY, MINE, CLIFF };
	
	private CellType type;
	
	private double up;
	private double down;
	private double left;
	private double right;

	public Cell(String cell)
	{
		this.type(stringToType(cell));
		up(0);
		down(0);
		left(0);
		right(0);
	}
	
	private CellType stringToType(String cell)
	{
		char cellValue = cell.toUpperCase().charAt(0);
		switch (cellValue)
		{
			case 'S':
				return CellType.START;
			case 'G':
				return CellType.GOAL;
			case '_':
				return CellType.EMPTY;
			case 'M':
				return CellType.MINE;
			case 'C':
				return CellType.CLIFF;
		}
		throw new IllegalArgumentException(cellValue + " isn't a valid Cell");
	}

	public CellType type()
	{
		return type;
	}

	public void type(CellType type)
	{
		this.type = type;
	}

	public double up() {
		return up;
	}

	public void up(double up) {
		this.up = up;
	}

	public double down() {
		return down;
	}

	public void down(double down) {
		this.down = down;
	}

	public double left() {
		return left;
	}

	public void left(double left) {
		this.left = left;
	}

	public double right() {
		return right;
	}

	public void right(double right) {
		this.right = right;
	}
	
}
