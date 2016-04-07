import javax.swing.JButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class Square extends Cell{

	private JButton button = new JButton();
	
	public Square(int row, int col, String ID, int boardSize, JPanel board){
		super(row, col, ID);
		if(! ID.equals("")){
			button.setText(ID);
			button.setBackground(Color.LIGHT_GRAY);
		}else{
			button.setBackground(Color.WHITE);
			button.setEnabled(false);
		}
		board.add(button);
		button.setFont(new Font("Arial", Font.PLAIN, 100));
		button.setFocusPainted(false);
	}
	
	public void addActionListener(ActionListener listener){
		button.addActionListener(listener);
	}
	
	public void addKeyListener(KeyListener listener){
		button.addKeyListener(listener);
	}
	
	@Override
	public void setID(String ID){
		super.setID(ID);
		button.setText(ID);
	}
	
	public void toggle(boolean value){
		button.setEnabled(value);
		if(value)
			button.setBackground(Controller.tileColor);
		else
			button.setBackground(Color.WHITE);
	}
	
	public void changeLook(){
		button.setBackground(Controller.tileColor);
	}
}
