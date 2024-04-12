public class Driver {
	public static void main(String[] args) {
        int[] distribution = new int[26];
        for (int j = 0; j < 5000; j++) {
            Hanabi game = new Hanabi(false, new Player(), new Player());
            int score = game.play();
            distribution[score]++;
        }
        for (int i = 0; i < distribution.length; i++) {
            System.out.print(i + ":\t");
            for (int j = 0; j < distribution[i]; j+=10) {
                System.out.print("|");
            }
            System.out.print("\n");
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
