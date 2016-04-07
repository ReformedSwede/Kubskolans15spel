
public class Main {

	public static void main(String[] args) {

		int boardSize = 4;
				
		GameEngine engine = new GameEngine(boardSize);
		GUI gui = new GUI(boardSize, engine);
		Controller controller = new Controller(engine, gui);
	}

}
