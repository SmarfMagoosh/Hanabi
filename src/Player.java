import java.util.*;
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

    // remaining cards in the deck
    final HashMap<Card, Integer> remaining;

    // play the left most hinted card
    private int nextPlay;

    // only true on the first turn
    private boolean firstRun;

    // set of cards for which all copies are accounted for
    final private Set<Card> impossibleCards;

    /**
	 * This default constructor should be the only constructor you supply.
	 */
	public Player() {
        firstRun = false;
        remaining = new HashMap<>();
        impossibleCards = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 6; j++) {
                switch (j) {
                    case 1 -> remaining.put(new Card(i, j), 3);
                    case 5 -> remaining.put(new Card(i, j), 1);
                    default -> remaining.put(new Card(i, j), 2);
                }
            }
        }
        nextPlay = -1;
        knowledge = new ArrayList<>();
        partnerKnowledge = new ArrayList<>();
        // initialize all card knowledges to all options
        for (int i = 0; i < 5; i++) {
            knowledge.add(new CardKnowledge(getImpossibleCards()));
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
        remaining.put(draw, remaining.get(draw) - 1);

        if (remaining.get(draw) == 0) {
            impossibleCardFound(draw);
        }

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
        remaining.put(discard, remaining.get(discard) - 1);

        if (remaining.get(discard) == 0) {
            impossibleCardFound(discard);
        }

        knowledge.remove(disIndex);
        knowledge.add(0, new CardKnowledge(getImpossibleCards()));
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
        remaining.put(draw, remaining.get(draw) - 1);

        if (remaining.get(draw) == 0) {
            impossibleCardFound(draw);
        }

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
        remaining.put(play, remaining.get(play) - 1);

        if (remaining.get(play) == 0) {
            impossibleCardFound(play);
        }

        knowledge.remove(playIndex);
        knowledge.add(0, new CardKnowledge(getImpossibleCards()));
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
        boolean warn_hint = false;
        for (Integer i : indices) {
            if (knowledge.get(i).getKnownValue() == 1) {
                warn_hint = true;
            }
        }
        updateKnowledgeColorHint(color, indices, boardState, knowledge);
        if (!warn_hint) {
            nextPlay = indices.get(0);
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
        updateKnowledgeNumberHint(number, indices, boardState, knowledge);
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
        // update remaining cards on first turn
        if (firstRun) {
            for (int i = 0; i < 5; i++) {
                Card c = partnerHand.get(i);
                remaining.put(c, remaining.get(c) - 1);
            }
            firstRun = false;
        }

        // if partner knows they have a 1 but they don't know the color and it isn't playable, hint the color
        for (int i = 0; i < 5; i++) {
            CardKnowledge know = partnerKnowledge.get(i);
            if (
                    know.getKnownValue() == 1 && know.getKnownColor() == -1 &&
                            !boardState.isLegalPlay(partnerHand.get(i)) &&
                            boardState.numHints > 0
            ) {
                ArrayList<Integer> indices = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    if (partnerHand.get(j).color == partnerHand.get(i).color) {
                        indices.add(j);
                    }
                }
                updateKnowledgeColorHint(partnerHand.get(i).color, indices, boardState, partnerKnowledge);
                return "COLORHINT " + partnerHand.get(i).color;
            }
        }

        // play the left most card that was hinted last turn
        if (nextPlay >= 0) {
            int temp = nextPlay;
            nextPlay = -1;
            return "PLAY " + temp + " 0";
        }

        // if we know a card is playable based on remaining options, play it
        for (int i = 0; i < 5; i++) {
            if (knowledge.get(i).isDefinitelyPlayable(boardState)) {
                return "PLAY " + i + " 0";
            }
        }

        // if we know a card is discardable from remaining options, discard it
        for (int i = 0; i < 5; i++) {
            if (knowledge.get(i).isDiscardable(boardState)) {
                return "DISCARD " + i + " 0";
            }
        }

        // if out partner is holding a playable card, give them a hint such that it will be played by the play left rule
        for (int i = 0; i < 5; i++) {
            Card c = partnerHand.get(i);
            boolean first_number = true;
            boolean first_color = true;
            // determine if a number hint or color hint is viable
            for (int j = 0; j < i; j++) {
                if (partnerHand.get(j).value == c.value) {
                    first_number = false;
                }
                if (partnerHand.get(j).color == c.color) {
                    first_color = false;
                }
            }

            // give number hint
            if (boardState.isLegalPlay(c) && first_number && boardState.numHints > 0) {
                ArrayList<Integer> indices = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    if (partnerHand.get(j).value == c.value) {
                        indices.add(j);
                    }
                }
                updateKnowledgeNumberHint(c.value, indices, boardState, partnerKnowledge);
                return "NUMBERHINT " + c.value;
            }

            // give color hint
            if (boardState.isLegalPlay(c) && first_color && boardState.numHints > 0) {
                ArrayList<Integer> indices = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    if (partnerHand.get(j).color == c.color) {
                        indices.add(j);
                    }
                }
                updateKnowledgeColorHint(c.color, indices, boardState, partnerKnowledge);
                return "COLORHINT " + c.color;
            }
        }

        // if its a one and we don't know its color, play it
        for (int i = 0; i < 5; i++) {
            CardKnowledge know = knowledge.get(i);
            if (know.getKnownValue() == 1 && know.getKnownColor() == -1) {
                return "PLAY " + i + " 0";
            } else if (know.getKnownValue() == 1) {
                return "DISCARD " + i + " 0";
            }
        }

        for (int i = 0; i < 5; i++) {
            if (knowledge.get(i).probablyPlayable(remaining, boardState) >= 0.5 && boardState.numFuses > 1) {
                return "PLAY " + i + " 0";
            }
        }

        // discard the chopping block
        return "DISCARD " + getMyChopBlock() + " 0";
	}

    // EXTRA METHODS
    public int getMyChopBlock() {
        for (int i = 4; i >= 0; i--) {
            if (!knowledge.get(i).beenHinted) { return i; }
        }
        return -1;
    }

    public void updateKnowledgeNumberHint(int number, ArrayList<Integer> indices, Board boardState, ArrayList<CardKnowledge> knowledge) {
        for (int i = 0; i < 5; i++) {
            if (indices.contains(i)) {
                knowledge.get(i).beenHinted = true;
                knowledge.get(i).knowValue(number);
            } else {
                knowledge.get(i).eliminateValue(number);
            }
        }
    }

    public void updateKnowledgeColorHint(int color, ArrayList<Integer> indices, Board boardState, ArrayList<CardKnowledge> knowledge) {
        for (int i = 0; i < 5; i++) {
            if (indices.contains(i)) {
                knowledge.get(i).knowColor(color);
            } else {
                knowledge.get(i).eliminateColor(color);
            }
        }
    }

    public void impossibleCardFound(Card card) {
        impossibleCards.add(card);
        for (int i = 0; i < 5; i++) {
            knowledge.get(i).eliminateCard(card);
        }
    }

    public Set<Card> getImpossibleCards() {
        return impossibleCards.size() > 0 ? impossibleCards : null;
    }
}
