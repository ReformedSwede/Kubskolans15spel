import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;

public class Highscore implements Serializable {

	private NumberPair<Integer, Float>[] scores = (NumberPair<Integer, Float>[]) new NumberPair<?, ?>[6];
	private static final long serialVersionUID = -657507324023299050L;

	public Highscore(int boardSize, int moves, float time) {
		for(NumberPair<Integer, Float> score : scores) {
			score = null;
		}
		scores[boardSize] = new NumberPair<Integer, Float>(moves, time);
	}

	public static String checkScore(int boardSize, int moves, float time) {
		time = time == 0 ? Float.MAX_VALUE : time;
		boolean movesRecord = false;
		boolean timeRecord = false;
		boolean error = false;
		Highscore hs;
		ObjectInputStream reader = null;
		ObjectOutputStream writer = null;

		try {
			// Check if folder exists
			File folder = new File("15pzl_hghscr");
			if(!folder.exists())
				folder.mkdir();

			// If file exists, check for new high score. Otherwise, create new
			// high score
			if((new File(folder.getName() + "\\score.15pzl")).exists()) {
				reader = new ObjectInputStream(new FileInputStream(folder.getName() + "\\score.15pzl"));
				hs = (Highscore) reader.readObject();

				if(hs.scores[boardSize] == null) { // if no previous entry for
													// this board size
					hs.scores[boardSize] = new NumberPair<Integer, Float>(moves, time);
				} else {

					// check for high score
					if(moves < hs.scores[boardSize].number1) {
						movesRecord = true;
						hs.scores[boardSize].number1 = moves;
					}
					if(time < hs.scores[boardSize].number2) {
						timeRecord = true;
						hs.scores[boardSize].number2 = time;
					}
				}
				reader.close();
			} else { // create new, almost empty, high score
				hs = new Highscore(boardSize, moves, time);
			}

			// Write high score to file
			(new File(folder.getName() + "\\score.15pzl")).createNewFile();
			writer = new ObjectOutputStream(new FileOutputStream(folder.getName() + "\\score.15pzl"));
			writer.writeObject(hs);

			writer.close();
		} catch(IOException | ClassNotFoundException e) {
			error = true;
		}

		// Set up string to return
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("Antal drag: " + moves + (movesRecord ? " (Nytt rekord!)" : ""));
		if(time == Float.MAX_VALUE)
			toReturn.append("\nTid: -");
		else
			toReturn.append("\nTid: " + (new DecimalFormat("0.00").format(time)) + (timeRecord ? " (Nytt rekord!)" : ""));
		if(error)
			toReturn.append("\n(Kunde inte jämföra med highscore)");

		return toReturn.toString();
	}

	public static void reset() {
		File folder = new File("15pzl_hghscr");
		File file = new File(folder.getName() + "\\score.15pzl");
		if(!folder.exists())
			folder.mkdir();

		if(file.exists())
			file.delete();
	}

	public static String getHighscore() {
		Highscore hs = null;
		ObjectInputStream reader = null;

		try {
			File folder = new File("15pzl_hghscr");
			if(!folder.exists())
				folder.mkdir();

			if((new File(folder.getName() + "\\score.15pzl")).exists()) {
				reader = new ObjectInputStream(new FileInputStream(folder.getName() + "\\score.15pzl"));
				hs = (Highscore) reader.readObject();
				reader.close();

				// setup string to return
				StringBuilder toReturn = new StringBuilder("Highscore:\n");

				for(int i = 0; i < 6; i++) {
					if(hs.scores[i] == null)
						toReturn.append("\n" + (i + 2) + "x" + (i + 2) + ": -");
					else
						toReturn.append("\n" + (i + 2) + "x" + (i + 2) + ": Antal drag: " + hs.scores[i].number1 + ", Tid "
								+ (hs.scores[i].number2 == Float.MAX_VALUE ? "-" : (new DecimalFormat("0.00").format(hs.scores[i].number2))));
				}

				return toReturn.toString();
			}
		} catch(IOException | ClassNotFoundException e) {
		}

		return "Ingen highscore att visa.";

	}
}
