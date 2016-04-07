import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Iterator;

public class GameEngine extends Observable{
	
	Controller controller;
	ArrayList<Cell> board = new ArrayList<Cell>();
	Cell emptyBox;
	int boardSize;
	int nrOfClicks = 0;
	float time = 0;
	
	public GameEngine(int boardSize){
		this.boardSize = boardSize;
		
		for(int i = 0; i < boardSize*boardSize; i++){
			if(i != boardSize*boardSize-1)
				board.add(new Cell(i/boardSize, i%boardSize, Integer.toString(i+1)));
			else
				board.add(new Cell(i/boardSize, i%boardSize, ""));
		}
		emptyBox = board.get((boardSize*boardSize)-1);
	}
	
	
	public boolean click(String cellID){
		int prevNrOfClicks = nrOfClicks;
		Cell clicked = null;
		
		for(Cell cell : board){
			if(cell.getID().equals(cellID)){
				clicked = cell;
			}
		}
		
		if(clicked.getRow() == emptyBox.getRow()){
            if(emptyBox.getCol() < clicked.getCol()){
            	
            	for(int i = emptyBox.getRow()*boardSize + emptyBox.getCol() + 1; i <= clicked.getRow()*boardSize + clicked.getCol(); i++){
            		emptyBox.setID(board.get(i).getID());
            		board.get(i).setID("");
                    emptyBox = board.get(i);

                    nrOfClicks++;
            	}
            }else{
            	
            	for(int i = emptyBox.getRow()*boardSize + emptyBox.getCol() - 1; i >= clicked.getRow()*boardSize + clicked.getCol(); i--){
            		emptyBox.setID(board.get(i).getID());
            		board.get(i).setID("");
                    emptyBox = board.get(i);

                    nrOfClicks++;
            	}
            }
            
        }else if(clicked.getCol() == emptyBox.getCol()){
        	
          if(clicked.getRow() < emptyBox.getRow()){
        	  
        	  for(int i = (emptyBox.getRow()-1)*boardSize + emptyBox.getCol(); i >= clicked.getRow()*boardSize + clicked.getCol(); i -= boardSize){
        		  emptyBox.setID(board.get(i).getID());
        		  board.get(i).setID("");
                  emptyBox = board.get(i);
                  
                  nrOfClicks++;
        	  }
          }else{
        	  
        	  for(int i = (emptyBox.getRow()+1)*boardSize + emptyBox.getCol(); i <= clicked.getRow()*boardSize + clicked.getCol(); i += boardSize){
        		  emptyBox.setID(board.get(i).getID());
        		  board.get(i).setID("");
                  emptyBox = board.get(i);
                  
                  nrOfClicks++;
        	  }        	  
          }
        }
		setChanged(); 
		notifyObservers(board.clone()); 
		
		setChanged(); 
		notifyObservers(nrOfClicks); 
		
		if(prevNrOfClicks == nrOfClicks)
			return false;
		else
			return true;
	}
	
	public boolean won(){
		
		for(int i = 0; i < boardSize*boardSize-1; i++){
			if(! board.get(i).getID().equals(Integer.toString((i+1)))){				
				return false;
			}
		}
		return true;
	}
	
	public String getCellIdFromKey(int key){
		if(key == KeyEvent.VK_UP){
			if(emptyBox.getRow()-1 >= 0){
				return board.get((emptyBox.getRow()-1)*boardSize + emptyBox.getCol()).getID();
			}
		}else if(key == KeyEvent.VK_DOWN){
			if(emptyBox.getRow()+1 < boardSize){
				return board.get((emptyBox.getRow()+1)*boardSize + emptyBox.getCol()).getID();
			}	
		}else if(key == KeyEvent.VK_RIGHT){
			if(emptyBox.getCol()+1 < boardSize){
				return board.get(emptyBox.getRow()*boardSize + emptyBox.getCol() + 1).getID();
			}	
		}else if(key == KeyEvent.VK_LEFT){
			if(emptyBox.getCol()-1 >= 0){
				return board.get(emptyBox.getRow()*boardSize + emptyBox.getCol() - 1).getID();
			}
		}
		return null;
	}
	
	public void reset(){
		for(int i = 0; i < boardSize*boardSize; i++){
			if(i != (boardSize*boardSize)-1)
				board.get(i).setID(Integer.toString(i+1));
			else
				board.get(i).setID("");	
		}
		emptyBox = board.get((boardSize*boardSize)-1);
		
		setChanged(); 
		notifyObservers(board.clone());
		
		nrOfClicks = 0;
		setChanged(); 
		notifyObservers(-1);
	}
	
	public void scramble(){
		
		Scrambler scram = new Scrambler(boardSize);
		Iterator<Integer> it = scram.iterator();
		for(int i = 0; i < boardSize*boardSize && it.hasNext(); i++){
			board.get(i).setID(Integer.toString((Integer)it.next()));
			if(board.get(i).getID().equals("0"))
			{
				board.get(i).setID("");
				emptyBox = board.get(i);
			}
		}
		
		setChanged(); 
		notifyObservers(board.clone());
		
		nrOfClicks = 0;
		setChanged(); 
		notifyObservers(0);
		
		if(won())
			scramble();
	}
}
