package com.inf431.set.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class CardPanel extends View {
	private Card card;

	public CardPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCard(null);
	}
	
	public CardPanel(Context context, AttributeSet attrs, int defStyleAttr, Card card) {
		super(context, attrs, defStyleAttr);
		setCard(card);
	}

	public Card getCard(){
		return card;
	}
	
	// redefine the drawing method
	@Override
	public void onDraw(Canvas c) {
		setBackgroundColor(card == null ? Color.BLACK : Color.WHITE);
		if (card != null)
			card.draw(c, getWidth(), getHeight());
	}
	
	public void setCard(Card card){
		this.card = card;
	}
}