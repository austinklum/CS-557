import java.util.LinkedList;

public class Fold 
{
	private LinkedList<DataSetRow> data;
	private int foldNumber;
	
	public Fold(int foldNumber, LinkedList<DataSetRow> data)
	{
		this.setFoldNumber(foldNumber);
		this.setData(data);
	}

	public LinkedList<DataSetRow> getData() {
		return data;
	}

	public void setData(LinkedList<DataSetRow> data) {
		this.data = data;
	}

	public int getFoldNumber() {
		return foldNumber;
	}

	public void setFoldNumber(int foldNumber) {
		this.foldNumber = foldNumber;
	}
}
