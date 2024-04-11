import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.IntStream;

/**
 * @author Evan Dreher, Caleb Frey
 *
 */
public class Player {
	// what I know about my cards
    final ArrayList<CardKnowledge> knowledge;

    // what I know my partner knows about their cards
    final ArrayList<CardKnowledge> partnerKnowledge;

    private int nextPlay;

    /**
	 * This default constructor should be the only constructor you supply.
	 */
	public Player() {
        nextPlay = -1;
        knowledge = new ArrayList<>();
        partnerKnowledge = new ArrayList<>();
        // initialize all card knowledges to all options
        for (int i = 0; i < 5; i++) {
            knowledge.add(new CardKnowledge());
            partnerKnowledge.add(new CardKnowledge());
        }
	}
	
	/**
	 * This method runs whenever your partner discards a card.
	 * @param startHand The hand your partner started with before discarding.
	 * @param discard The card he discarded.
	 * @param disIndex The index from which he discarded it.
	 * @param draw The card he drew to replace it; null, if the deck is empty.
	 * @param drawIndex The index to which he drew it.
	 * @param finalHand The hand your partner ended with after redrawing.
	 * @param boardState The state of the board after play.
	 */
	public void tellPartnerDiscard(
            Hand startHand,
            Card discard,
            int disIndex,
            Card draw,
            int drawIndex,
            Hand finalHand,
            Board boardState) {
        partnerKnowledge.remove(disIndex);
        partnerKnowledge.add(0, new CardKnowledge());
	}
	
	/**
	 * This method runs whenever you discard a card, to let you know what you discarded.
	 * @param discard The card you discarded.
	 * @param disIndex The index from which you discarded it.
	 * @param drawIndex The index to which you drew the new card (if drawSucceeded)
	 * @param drawSucceeded true if there was a card to draw; false if the deck was empty
	 * @param boardState The state of the board after play.
	 */
	public void tellYourDiscard(
            Card discard,
            int disIndex,
            int drawIndex,
            boolean drawSucceeded,
            Board boardState) {
        // TODO: update card knowledge
        knowledge.remove(disIndex);
        knowledge.add(0, new CardKnowledge());
	}
	
	/**
	 * This method runs whenever your partner played a card
	 * @param startHand The hand your partner started with before playing.
	 * @param play The card she played.
	 * @param playIndex The index from which she played it.
	 * @param draw The card she drew to replace it; null, if the deck was empty.
	 * @param drawIndex The index to which she drew the new card.
	 * @param finalHand The hand your partner ended with after playing.
	 * @param wasLegalPlay Whether the play was legal or not.
	 * @param boardState The state of the board after play.
	 */
	public void tellPartnerPlay(
            Hand startHand,
            Card play,
            int playIndex,
            Card draw,
            int drawIndex,
            Hand finalHand,
            boolean wasLegalPlay,
            Board boardState) {
        // TODO: update card knowledge
        partnerKnowledge.remove(playIndex);
        partnerKnowledge.add(0, new CardKnowledge());
    }

	
	/**
	 * This method runs whenever you play a card, to let you know what you played.
	 * @param play The card you played.
	 * @param playIndex The index from which you played it.
	 * @param drawIndex The index to which you drew the new card (if drawSucceeded)
	 * @param drawSucceeded  true if there was a card to draw; false if the deck was empty
	 * @param wasLegalPlay Whether the play was legal or not.
	 * @param boardState The state of the board after play.
	 */
	public void tellYourPlay(
            Card play,
            int playIndex,
            int drawIndex,
            boolean drawSucceeded,
            boolean wasLegalPlay,
            Board boardState) {
        // TODO: update card knowledge
        knowledge.remove(playIndex);
        knowledge.add(0, new CardKnowledge());

    }

	/**
	 * This method runs whenever your partner gives you a hint as to the color of your cards.
	 * @param color The color hinted, from Colors.java: RED, YELLOW, BLUE, GREEN, or WHITE.
	 * @param indices The indices (from 0-4) in your hand with that color.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellColorHint(
            int color,
            ArrayList<Integer> indices,
            Hand partnerHand,
            Board boardState) {
        nextPlay = indices.get(0);
        for (int i = 0; i < 5; i++) {
            if (indices.contains(i)) {
                knowledge.get(i).knowColor(color);
            } else {
                knowledge.get(i).eliminateColor(color);
            }
        }
	}
	
	/**
	 * This method runs whenever your partner gives you a hint as to the numbers on your cards.
	 * @param number The number hinted, from 1-5.
	 * @param indices The indices (from 0-4) in your hand with that number.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellNumberHint(
            int number,
            ArrayList<Integer> indices,
            Hand partnerHand,
            Board boardState) {
        nextPlay = indices.get(0);
        for (int i = 0; i < 5; i++) {
            if (indices.contains(i)) {
                knowledge.get(i).beenHinted = true;
                knowledge.get(i).knowValue(number);
            } else {
                knowledge.get(i).eliminateValue(number);
            }
        }
        if (indices.size() == 1 && number != 5) {
            for (Integer pile : boardState.tableau) {
                if (pile != number - 1) {
                    knowledge.get(indices.get(0)).eliminateColor(pile);
                }
            }
        }
	}
	
	/**
	 * This method runs when the game asks you for your next move.
	 * @param yourHandSize How many cards you have in hand.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The current state of the board.
	 * @return A string encoding your chosen action. Actions should have one of the following formats; in all cases,
	 *  "x" and "y" are integers.
	 * 	a) "PLAY x y", which instructs the game to play your card at index x and to draw a card back to index y. You
	 *     should supply an index y even if you know the deck to be empty. All indices should be in the range 0-4.
	 *     Illegal plays will consume a fuse; at 0 fuses, the game ends with a score of 0.
	 *  b) "DISCARD x y", which instructs the game to discard the card at index x and to draw a card back to index y.
	 *     You should supply an index y even if you know the deck to be empty. All indices should be in the range 0-4.
	 *     Discarding returns one hint if there are fewer than the maximum number available.
	 *  c) "NUMBERHINT x", where x is a value from 1-5. This command informs your partner which of his cards have a value
	 *     of the chosen number. An error will result if none of his cards have that value, or if no hints remain.
	 *     This command consumes a hint.
	 *  d) "COLORHINT x", where x is one of the RED, YELLOW, BLUE, GREEN, or WHITE constant values in Colors.java.
	 *     This command informs your partner which of his cards have the chosen color. An error will result if none of
	 *     his cards have that color, or if no hints remain. This command consumes a hint.
	 */
	public String ask(int yourHandSize, Hand partnerHand, Board boardState) {
        // play left discard right
        boolean spare_fuses = boardState.numFuses != 1;
        if (nextPlay >= 0) {
            int temp = nextPlay;
            nextPlay = -1;
            return "PLAY " + temp + " 0";
        }

        for (int i = 0; i < 5; i++) {
            Card c = partnerHand.get(i);
            boolean first_number = true;
            boolean first_color = true;
            for (int j = 0; j < i; j++) {
                if (partnerHand.get(j).value == c.value) {
                    first_number = false;
                }
                if (partnerHand.get(j).color == c.color) {
                    first_color = false;
                }
            }
            if (boardState.isLegalPlay(c) && first_number && boardState.numHints > 0) {
                return "NUMBERHINT " + c.value;
            }
            if (boardState.isLegalPlay(c) && first_color && boardState.numHints > 0) {
                return "COLORHINT " + c.color;
            }
        }

        for (int i = 0; i < 5; i++) {
            CardKnowledge know = knowledge.get(i);
            if (know.getKnownValue() == 1 && know.getKnownColor() == -1 || know.isDefinitelyPlayable(boardState)) {
                return "PLAY " + i + " 0";
            } else if (know.getKnownValue() == 1) {
                return "DISCARD " + i + " 0";
            }
        }
        return "DISCARD " + getYourChopBlock() + " 0";
	}

    // EXTRA METHODS
    public int getMyChopBlock() {
        for (int i = 4; i >= 0; i--) {
            if (!knowledge.get(i).beenHinted) { return i; }
        }
        return -1;
    }

    public int getYourChopBlock() {
        for (int i = 4; i >= 0; i--) {
            if (!partnerKnowledge.get(i).beenHinted) { return i; }
        }
        return -1;
    }
}
