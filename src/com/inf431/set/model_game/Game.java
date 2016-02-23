package com.inf431.set.model_game;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import com.inf431.set.model.CardStatus;
import com.inf431.set.model.Comb;
import com.inf431.set.view.Card;
import com.inf431.set.view.CardPanel;
import com.inf431.set.view.GameActivity;
import com.inf431.set.view.StatusPanel;

public abstract class Game {
	protected final int nCards = 81, timePause = 500;
	protected int counter, score;
	protected ArrayList<Integer> set;
	protected boolean gameOver;
	protected GameActivity activity;
	protected CardPanel[] currentCards;
	protected Deque<Card> deck;
	protected StatusPanel statusPanel;
	
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	public Game(GameActivity activity){
		this.activity = activity;
		gameOver = false;
		score = 0;
		counter = 0;
		set = new ArrayList<Integer>();
		deck = new LinkedList<Card>();
		statusPanel = new StatusPanel(activity);
	}
	
	public Game(){
		score = 0;
		counter = 0;
		set = new ArrayList<Integer>();
		deck = new LinkedList<Card>();
	}

	public int getCounter() {
		return counter;
	}

	public CardPanel[] getCurrentCards() {
		return currentCards;
	}

	public StatusPanel getStatusPanel() {
		return statusPanel;
	}
	
	public boolean isComplete() {
		if (currentCards[12].getCard() == null && 
				currentCards[13].getCard() == null &&
				currentCards[14].getCard() == null)
			return false;
		return true;
	}
	
	public synchronized void selected(int cardIndex){
		CardPanel selectedCardPanel = currentCards[cardIndex];
		Card selectedCard = currentCards[cardIndex].getCard();
		if (selectedCard == null)
			return;
		
		if (selectedCard.getStatus() == CardStatus.UNSELECTED) {
			set.add(cardIndex);
			counter++;
			selectedCard.setStatus(CardStatus.SELECTED);
			activity.updateCardPanel(selectedCardPanel);
			if (counter == 3) {
				counter = 0;
				checkForSet();
				set = new ArrayList<Integer>();
			}
		} else {
			selectedCard.setStatus(CardStatus.UNSELECTED);
			activity.updateCardPanel(selectedCardPanel);
			set.remove((Integer) cardIndex);
			counter--;
		}
	}
	
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	protected boolean containsSet() {
		Comb comb = new Comb(isComplete() ? 15 : 12, 3);

		boolean cardsAvailable;
		for (int[] c : comb.getCombinations()) {
			int[] cards = new int[3];
			cardsAvailable = true;
			for (int i = 0; i < c.length; i++) {
				if (currentCards[c[i]].getCard() != null) // at the end, some cards are null
					cards[i] = currentCards[c[i]].getCard().getValue();
				else cardsAvailable = false;
			}
			
			if (cardsAvailable && isSet(cards[0], cards[1], cards[2]))
				return true;
		}
		return false;
	}
	
	protected void getInitialCards() {
		currentCards = new CardPanel[15];
		for (int i = 0; i < 12; i++)
			currentCards[i] = new CardPanel(activity, null, 0, deck.pop());
		for (int i = 12; i < 15; i++)
			currentCards[i] = new CardPanel(activity, null, 0, null);

		while (!containsSet())
			refreshCards();
	}

	protected boolean isSet(int a, int b, int c) {
		for (int i = 0; i < 4; ++i) {
			if ((a % 3 + b % 3 + c % 3) % 3 != 0) {
				return false;
			}
			a /= 3;
			b /= 3;
			c /= 3;
		}
		return true;
	}
	
	protected void refreshCards() {
		for (int i = 12; i < 15 && !deck.isEmpty(); i++) {
			if (currentCards[i].getCard() != null)
				deck.addLast(currentCards[i].getCard());
			currentCards[i].setCard(deck.pop());
			activity.updateCardPanel(currentCards[i]);
		}
	}
	
	protected void replaceCards(){
		for (int i = 0; i < set.size(); i++) {
			if (!deck.isEmpty())
				currentCards[set.get(i)].setCard(deck.pop());
			else
				currentCards[set.get(i)].setCard(null);
			activity.updateCardPanel(currentCards[set.get(i)]);
		}

		for (int j = 0; j <= deck.size() && !containsSet(); j++) {
			if (j == deck.size()){
				endGame();
				return;
			}
			refreshCards();
		}

		if (deck.isEmpty() && !containsSet())
			endGame();
	}
	
	protected void setSelectedCardStatus(CardStatus status) {
		for (int i = 0; i < set.size(); i++) {
			currentCards[set.get(i)].getCard().setStatus(status);
			activity.updateCardPanel(currentCards[set.get(i)]);
		}
	}

	protected void sleep(int timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void unselectAllCards(){
		for (int i = 0; i < currentCards.length; i++) {
			if (currentCards[i] != null)
				if (currentCards[i].getCard() != null){
					currentCards[i].getCard().setStatus(CardStatus.UNSELECTED);
					activity.updateCardPanel(currentCards[i]);
				}
		}
		
		counter = 0;
		set = new ArrayList<Integer>();
	}
	
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	abstract protected void checkForSet();
	abstract protected void endGame();
}
