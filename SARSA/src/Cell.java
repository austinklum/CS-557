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
		this.type = stringToType(cell);
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
	
}
