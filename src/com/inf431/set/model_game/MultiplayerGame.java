package com.inf431.set.model_game;

import com.inf431.set.model.CardStatus;
import com.inf431.set.model.Comb;
import com.inf431.set.model.PlayerMessageProcessor;
import com.inf431.set.view.Card;
import com.inf431.set.view.GameActivity;
import com.inf431.set.view.StatusPanel;

public class MultiplayerGame extends Game{
	private PlayerMessageProcessor comm;
	
	public MultiplayerGame() {
		super();
	}
	
	public void giveHint() {
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
				activity.displayMessage(c[0] + " * " + c[1] + " * " + c[2]);
				return;
			}
		}
	}

	public void setCards(int[] cardIndexes){
		for (int i = 0; i < nCards; i++)
			deck.push((new Card(cardIndexes[i])));
	}

	public void setGame(GameActivity activity, PlayerMessageProcessor comm){
		this.activity = activity;
		this.comm = comm;
		statusPanel = new StatusPanel(activity);
		getInitialCards();
	}

	public synchronized void scoreSet(boolean self, int[] setIndexes){
		unselectAllCards();

		final int[] setCards = new int[3];
		for (int i = 0; i < setCards.length; i++) {
			setCards[i] = currentCards[setIndexes[i]].getCard().getValue();
			set.add((Integer) setIndexes[i]);
		}
		
		setSelectedCardStatus(CardStatus.VALID_SELECTION);
		sleep(timePause);
		replaceCards();

		if(self){
			score++;
			statusPanel.updateScore(score);
		}
		
		statusPanel.setPreviousCards(setCards);
		activity.updateStatusPanel();
		unselectAllCards();
	}
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	@Override
	protected synchronized void checkForSet() {
		final int[] setCards = new int[3],
					setIndexes = {set.get(0), set.get(1), set.get(2)};
		for (int i = 0; i < setCards.length; i++) {
			setCards[i] = currentCards[set.get(i)].getCard().getValue();
		}

		if (isSet(setCards[0], setCards[1], setCards[2])) {
			comm.sendFoundSetMessage(setIndexes);
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
		comm.sendEndGameMessage(score);
	}
}
