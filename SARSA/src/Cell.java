import java.awt.Point;
import java.util.HashMap;

public class Cell 
{
	public enum CellType { START, GOAL, EMPTY, MINE, CLIFF };
	
	private CellType type;
	private Point cellLocation;
	
	private HashMap<Action, Double> actionQ;

	public Cell(String cell, int x, int y)
	{
		this.type(stringToType(cell));
		this.actionQ = new HashMap<>();
		this.cellLocation = new Point(x,y);
		actionQ.put(Action.UP, 0.0);
		actionQ.put(Action.DOWN, 0.0);
		actionQ.put(Action.LEFT, 0.0);
		actionQ.put(Action.RIGHT, 0.0);
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
	
	public HashMap<Action, Double> actionQ()
	{
		return actionQ;
	}
	
	public int x()
	{
		return cellLocation.x;
	}
	
	public int y()
	{
		return cellLocation.y;
	}
}
