package com.inf431.set.model_game;

import java.util.Random;

import android.os.Handler;

import com.inf431.set.model.CardStatus;
import com.inf431.set.model.Comb;
import com.inf431.set.view.Card;
import com.inf431.set.view.GameActivity;

public class SinglePlayerGame extends Game{
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	public SinglePlayerGame(GameActivity activity) {
		super(activity);
		shuffle();
		getInitialCards();
	}

	public void giveHint() {
		unselectAllCards();

		boolean cardsAvailable;
		Comb comb = new Comb(isComplete() ? 15 : 12, 3);
		for (int[] c : comb.getCombinations()) {
			final int[] cards = new int[3];
			cardsAvailable = true;
			for (int i = 0; i < c.length; i++) {
				if (currentCards[c[i]].getCard() != null) // at the end, some cards are null
					cards[i] = currentCards[c[i]].getCard().getValue();
				else cardsAvailable = false;
			}
			
			if (cardsAvailable && isSet(cards[0], cards[1], cards[2])) {
				for (int j = 0; j < cards.length; j++) {
					final int index = c[j];
					Handler handler = new Handler();
					handler.postDelayed(new Runnable(){
						public void run(){
							currentCards[index].getCard().setStatus(CardStatus.SELECTED);
							activity.updateCardPanel(currentCards[index]);
							set.add((Integer) index);
						}
					}, timePause*(j+1)/3);

					handler.postDelayed(new Runnable(){
						public void run(){
							currentCards[index].getCard().setStatus(CardStatus.VALID_SELECTION);
							activity.updateCardPanel(currentCards[index]);
						}
					}, timePause*3/2);

					handler.postDelayed(new Runnable(){
						public void run(){
							replaceCards();
							unselectAllCards();
							
							statusPanel.setPreviousCards(cards);
							activity.updateStatusPanel();
						}
					}, timePause*2);
					
				}
				
				return;
			}
		}
	}

	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	// Checks if indexes of selected cards correspond to a set and
	// acts accordingly, updating game screen
	@Override
	protected synchronized void checkForSet() {
		final int[] setCards = new int[3];
		for (int i = 0; i < setCards.length; i++) {
			setCards[i] = currentCards[set.get(i)].getCard().getValue();
		}

		if (isSet(setCards[0], setCards[1], setCards[2])) {
			setSelectedCardStatus(CardStatus.VALID_SELECTION);
			for (int i = 0; i < setCards.length; i++) {
				activity.updateCardPanel(currentCards[set.get(i)]);
			}
			
			sleep(timePause);
			
			replaceCards();
			score++;
			statusPanel.setPreviousCards(setCards);
			statusPanel.updateScore(score);
			activity.updateStatusPanel();
			unselectAllCards();
		} else {
			setSelectedCardStatus(CardStatus.INVALID_SELECTION);
			sleep(timePause);
			setSelectedCardStatus(CardStatus.UNSELECTED);
			score--;
			statusPanel.updateScore(score);
			activity.updateStatusPanel();
		}
	}

	protected void endGame(){
		activity.displayMessage("Game over! Final score " + score + " points");
	}
	
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	private void shuffle() {
		int[] integers = new int[81];
		for (int i = 0; i < integers.length; i++)
			integers[i] = i;
		int n = integers.length;
		Random gen = new Random();
		while (n > 0) {
			int k = gen.nextInt(n--);
			int temp = integers[n];
			integers[n] = integers[k];
			integers[k] = temp;
		}

		for (int i = 0; i < nCards; i++)
			deck.push((new Card(integers[i])));
	}
}
