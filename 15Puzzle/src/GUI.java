import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

public class GUI implements Observer, Comparator<Dimension> {

	JFrame theFrame = new JFrame("Kubskolans 15-spel");
	JPanel sidebar = new JPanel();
	JPanel gamePanel = new JPanel();
	JLabel labelMoves = new JLabel("Antal drag:");
	JLabel labelTimer = new JLabel("Tid:");
	ArrayList<Square> board = new ArrayList<Square>();
	int boardSize;
	boolean wasMaximized = false;

	public GUI(int boardSize, GameEngine engine) {
		engine.addObserver(this);
		this.boardSize = boardSize;

		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.getContentPane().setLayout(new BorderLayout());
		theFrame.addWindowStateListener(new WindowStateListener() {
			   public void windowStateChanged(WindowEvent arg0) {
				   GUI.this.windowStateChanged(arg0);
			   }
			});

		makeSidebar();
		makeGamePanel();
		makeMenubar();

		theFrame.pack();
		theFrame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (theFrame.getWidth() / 2), Toolkit.getDefaultToolkit()
				.getScreenSize().height / 2 - (theFrame.getHeight() / 2));
		changeLook();
		theFrame.setVisible(true);
	}

	private void makeMenubar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(new JMenu("Meny"));

		JMenuItem score = new JMenuItem("Visa highscore");
		JMenuItem reset = new JMenuItem("Radera highscore");
		JMenuItem color = new JMenuItem("Byt färg");
		JMenuItem help = new JMenuItem("Hjälp");
		score.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0));
		reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
		color.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));
		help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));

		menuBar.getMenu(0).add(score);
		menuBar.getMenu(0).add(reset);
		menuBar.getMenu(0).add(color);
		menuBar.getMenu(0).add(help);

		theFrame.setJMenuBar(menuBar);
	}

	private void makeSidebar() {
		sidebar.setLayout(new GridBagLayout());
		sidebar.setBorder(new CompoundBorder(new EmptyBorder(4, 10, 4, 20), new CompoundBorder(new EtchedBorder(), new EmptyBorder(0, 5, 0, 5))));

		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 0.9;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 0;
		sidebar.add(new JButton("Börja om"), gc);

		gc.gridy = 1;
		sidebar.add(new JButton("Blanda"), gc);

		gc.gridy = 2;
		sidebar.add(new JButton("Timer"), gc);

		gc.gridy = 3;
		sidebar.add(new JButton("+"), gc);

		gc.gridy = 4;
		sidebar.add(new JButton("- "), gc);

		gc.gridy = 5;
		sidebar.add(labelMoves, gc);

		gc.gridy = 6;
		sidebar.add(labelTimer, gc);

		gc.gridy = 7;
		gc.weighty = 2;
		sidebar.add(new JPanel(), gc);

		for(Component button : sidebar.getComponents()) {
			if(button instanceof JButton) {
				button.setFont(new Font("Arial", Font.PLAIN, 20));
			}
		}

		theFrame.getContentPane().add(sidebar, BorderLayout.WEST);
	}

	private void makeGamePanel() {
		gamePanel.setLayout(new GridLayout(boardSize, boardSize));

		for(int i = 0; i < boardSize * boardSize; i++) {
			if(i != boardSize * boardSize - 1)
				board.add(new Square(i / boardSize, i % boardSize, Integer.toString(i + 1), boardSize, gamePanel));
			else
				board.add(new Square(i / boardSize, i % boardSize, "", boardSize, gamePanel));
		}

		theFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
	}

	public void addActionListener(ActionListener act, KeyListener key) {
		theFrame.addKeyListener(key);
		for(Component button : sidebar.getComponents()) {
			if(button instanceof JButton) {
				((JButton) button).addActionListener(act);
				((JButton) button).addKeyListener(key);
			}
		}
		for(Square button : board) {
			button.addActionListener(act);
			button.addKeyListener(key);
		}
		for(Component menu : theFrame.getJMenuBar().getComponents()) {
			for(Component item : ((JMenu) menu).getMenuComponents()) {
				if(item instanceof JMenuItem) {
					((JMenuItem) item).addActionListener(act);
				}
			}
		}
	}
	
	public void windowStateChanged(WindowEvent e){
	  if (wasMaximized && (e.getNewState() & Frame.NORMAL) == Frame.NORMAL){
	      wasMaximized = false;
	      theFrame.pack();
	   }else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH){
		  wasMaximized = true;
	   }
	}

	@Override
	public void update(Observable arg0, Object obj) {
		if(obj instanceof ArrayList<?>) {
			ArrayList<Cell> list = (ArrayList<Cell>) obj;

			for(int i = 0; i < boardSize * boardSize; i++) {
				board.get(i).setID((list).get(i).getID());
				if((list).get(i).getID().equals("")) {
					board.get(i).toggle(false);
				} else
					board.get(i).toggle(true);
			}
		} else if(obj instanceof Integer) {
			if((int) obj == -1)
				labelMoves.setText("Antal drag:");
			else
				labelMoves.setText("Antal drag: " + obj.toString());
		}
	}

	public void congratulate(int boardSize, int clicks, float time) {
		JOptionPane.showMessageDialog(theFrame, Highscore.checkScore(boardSize, clicks, time), "Grattis, du klarade det!", JOptionPane.PLAIN_MESSAGE);
	}

	public void exit() {
		theFrame.dispose();
	}

	public void changeLook() {
		for(Square tile : board) {
			if(!tile.getID().equals(""))
				tile.changeLook();
		}
	}

	public synchronized void displayTime(String text) {
		labelTimer.setText(text);
	}

	@Override
	public int compare(Dimension d1, Dimension d2) {

		if(d1.getHeight() * d1.getWidth() > d2.getHeight() * d2.getWidth())
			return 1;
		else if(d1.getHeight() * d1.getWidth() == d2.getHeight() * d2.getWidth())
			return 0;
		else
			return -1;
	}
}
