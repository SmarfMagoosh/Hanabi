import java.util.stream.IntStream;

public class Driver {
	public static void main(String[] args) {
        // TODO: Change from ConsolePlayer to Player after you have an implementation
		boolean trials = false;
		boolean oneRun = true;
		if (oneRun) {
			Hanabi game = new Hanabi(true, new Player(), new Player());
			game.play();
		} else {
			if (!trials) {
				int out = -1;
				while (out != 0) {
					System.out.println("STARTING");
					Hanabi game = new Hanabi(true, new Player(), new Player());
					out = game.play();
				}

			} else {
				System.out.println(simulateGames(1000, false));
			}
		}


	}

	/**
	 * Used to evaluate your code
	 * @param numGames - number of games to run
	 * @param verbose - if true, prints each game's score
	 * @return average score
	 */
	public static double simulateGames(final int numGames, boolean verbose){
		int total = 0;
		for (int i = 0; i < numGames; i++) {
			Hanabi next = new Hanabi(false, new Player(), new Player());
			int score;
			try {
				score = next.play();
			} catch (Exception e) {
				e.printStackTrace();
				if (verbose) {
					System.out.println("Error; Score: 0");
				}
				return 0.0;
			}
			if (verbose) {
				System.out.println("Game " + i + " score: " + score);
			}
			total += score;
		}
		return total/(double)numGames;
	}
}
