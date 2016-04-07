import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class Controller implements ActionListener, KeyListener {

	private GameEngine theGameEngine;
	private GUI theGUI;
	public static Color tileColor = new Color(0.2f, 0.8f, 0.8f);
	float time;
	boolean running = false;
	boolean readyToGo = false;
	boolean readyToCalc = false;
	Timer timer = new Timer();

	public Controller(GameEngine engine, GUI gui) {
		theGameEngine = engine;
		theGUI = gui;
		theGUI.addActionListener(this, this);
	}

	public void actionPerformed(ActionEvent arg0) {
		switch(arg0.getActionCommand()) {

			case "Börja om":
				theGameEngine.reset();
				time = 0;
				if(running)
					toggleTimer();// stops the timer
				theGUI.displayTime("Tid:");
				readyToCalc = false;
				break;

			case "Blanda":
				if(running)
					toggleTimer(); // stops the timer
				theGameEngine.scramble();
				readyToCalc = true;
				break;

			case "Timer":
				if(readyToGo == false) {
					if(!running) {
						theGUI.displayTime("Redo...");
						readyToGo = true;
					}
				} else {
					theGUI.displayTime("Tid:");
					readyToGo = false;
				}
				break;

			case "+":
				if(running) // Cancel any running timer
					toggleTimer();
				if(theGUI.boardSize > 6) { // Warn for size limit
					JOptionPane.showMessageDialog(theGUI.theFrame, "Det här är den största storleken.", "Error 0x76278567",
							JOptionPane.INFORMATION_MESSAGE, null);
					break;
				}
				// gather some data from current frame, applied to new frame
				Point guiPos = theGUI.theFrame.getLocation();
				Dimension prevSize = theGUI.theFrame.getSize();
				boolean maximized = theGUI.theFrame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH;

				// Dispose old frame, create new board
				theGUI.exit();
				theGameEngine = new GameEngine(theGUI.boardSize + 1);
				theGUI = new GUI(theGUI.boardSize + 1, theGameEngine);

				// Change appearance and add listener to the new frame
				theGUI.theFrame.pack();
				if(theGUI.compare(theGUI.theFrame.getSize(), prevSize) < 0)
					theGUI.theFrame.setSize(prevSize);
				if(maximized)
					theGUI.theFrame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
				theGUI.addActionListener(this, this);
				theGUI.theFrame.setLocation(guiPos);
				theGUI.changeLook();
				break;

			case "- ":
				if(running)
					toggleTimer();
				if(theGUI.boardSize < 3) {
					JOptionPane.showMessageDialog(theGUI.theFrame, "Det här är den minsta storleken.", "Error 0x76278567",
							JOptionPane.INFORMATION_MESSAGE, null);
					break;
				}
				guiPos = theGUI.theFrame.getLocation();
				prevSize = theGUI.theFrame.getSize();
				maximized = theGUI.theFrame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH;

				theGUI.exit();
				theGameEngine = new GameEngine(theGUI.boardSize - 1);
				theGUI = new GUI(theGUI.boardSize - 1, theGameEngine);
				if(theGUI.compare(theGUI.theFrame.getSize(), prevSize) < 0)
					theGUI.theFrame.setSize(prevSize);
				if(maximized)
					theGUI.theFrame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
				theGUI.addActionListener(this, this);
				theGUI.theFrame.setLocation(guiPos);
				theGUI.changeLook();
				break;

			case "Visa highscore":
				JOptionPane.showMessageDialog(null, Highscore.getHighscore(), "Highscore", JOptionPane.INFORMATION_MESSAGE);
				break;

			case "Radera highscore":
				int ok = JOptionPane.showConfirmDialog(null, "Vill du verkligen radera all data?", "Varning!", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if(ok == JOptionPane.OK_OPTION)
					Highscore.reset();
				break;

			case "Hjälp":
				JOptionPane.showMessageDialog(null,
						"Tangentbordskommandon:\nBörja om - O\nBlanda - B\nTimer - T\nVisa highscore - V\nRadera highscore - R\nByt färg - F\nVisa hjälp - H\n"
								+ "\nFör mer hjälp, se:\nwww.kubskolan.se/15pussel.html", "Hjälp", JOptionPane.INFORMATION_MESSAGE);
				break;

			case "Byt färg":
				Random rand = new Random();
				Color newColor;
				do {
					newColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
				} while(newColor.equals(tileColor));
				tileColor = newColor;
				theGUI.changeLook();
				break;

			default:
				if(theGameEngine.click(arg0.getActionCommand()) == true) {

					if(readyToGo) {
						readyToGo = false;
						toggleTimer(); // start the timer
					}
					if(theGameEngine.won()) {
						if(running)
							toggleTimer(); // stops the timer
						if(readyToCalc) {
							readyToCalc = false;
							theGUI.congratulate(theGameEngine.boardSize - 2, theGameEngine.nrOfClicks, time);
							time = 0;
						}
					}
				}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

		if(arg0.getKeyCode() == KeyEvent.VK_RIGHT || arg0.getKeyCode() == KeyEvent.VK_LEFT || arg0.getKeyCode() == KeyEvent.VK_UP
				|| arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			String CellID = theGameEngine.getCellIdFromKey(arg0.getKeyCode());
			if(CellID != null) {
				if(theGameEngine.click(CellID) == true) {
					if(readyToGo) {
						readyToGo = false;
						toggleTimer(); // start the timer
					}

					if(theGameEngine.won()) {
						if(running)
							toggleTimer(); // stops the timer
						if(readyToCalc) {
							readyToCalc = false;
							theGUI.congratulate(theGameEngine.boardSize - 2, theGameEngine.nrOfClicks, time);
							time = 0;
						}
					}
				}
			}
		} else if(arg0.getKeyCode() == KeyEvent.VK_B) { // Scramble
			if(running)
				toggleTimer(); // stops the timer
			theGameEngine.scramble();
			readyToCalc = true;
		} else if(arg0.getKeyCode() == KeyEvent.VK_T) { // Timer
			if(readyToGo == false) {
				if(!running) {
					theGUI.displayTime("Redo...");
					readyToGo = true;
				}
			} else {
				theGUI.displayTime("Tid:");
				readyToGo = false;
			}
		} else if(arg0.getKeyCode() == KeyEvent.VK_O) { // Reset
			theGameEngine.reset();
			time = 0;
			if(running)
				toggleTimer();// stops the timer
			theGUI.displayTime("Tid:");
			readyToCalc = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public void toggleTimer() {

		DecimalFormat df;

		if(running) {
			timer.cancel();
			theGameEngine.time = this.time;
			running = false;
			df = new DecimalFormat("#.##");
			theGUI.displayTime("Tid: " + df.format(time));

		} else {
			df = new DecimalFormat("0.0");
			time = 0;
			timer = new Timer();
			running = true;
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					time += 0.01;
					theGUI.displayTime("Tid: " + df.format(time));
				}

			}, 0, 10);
		}
	}
}
