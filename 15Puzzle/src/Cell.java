
public class Cell {
	
	private String ID;
	private int row;
	private int col;
	
	public Cell(int row, int col, String ID){
		this.row = row;
		this.col = col;
		this.ID = ID;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	public String getID(){
		return ID;
	}
	
	public void setID(String text){
		this.ID = text;
	}

}
