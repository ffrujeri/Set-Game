package com.inf431.set.view;

import com.inf431.set.model.CardStatus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class Card {
	private int number;
	private int color;
	private int filling;
	private int shape;
	private int value; // value = number + 3 * (color + 3 * (filling + 3 * shape))
	private CardStatus status;

	public Card(int value) {
		this.value = value;
		// compute the other attributes from value
		number = value%3;
		color = (value/3)%3;
		filling = (value/9)%3;
		shape = (value/27)%3;
		status = CardStatus.UNSELECTED;
	}

	// equality test on SetCards
	public boolean equals(Object o) {
		Card c = (Card) o;
		return (value == c.value);
	}

	public CardStatus getStatus(){
		return status;
	}

	public int getValue(){
		return value;
	}

	public void setStatus(CardStatus status){
		this.status = status;
	}
	
	// return the characteristics of the card
	int[] characteristics() {
		return new int[] {number, color, filling, shape};
	}

	// draw the card on the Canvas c
	// different copies of the same shape are drawn, according to this.number
	void draw(Canvas c, int width, int height) {
		// set the default paint style
		Paint p = new Paint();
		setColor(p);

		// computes the topmost point
		int startY = (36 - 16*number)*height/96;
		// draw as many shapes as this.number
		for (int i = 0; i <= number; i++) {
			RectF r = new RectF(width/8, startY + i * height/3, width*7/8, startY + i * height/3 + height/4);
			drawFilledShape(c, p, r);
		}
		
        /* Draw selection */
		drawSelection(c, p, width, height);
	}

	private void drawSelection(Canvas c, Paint p, int width, int height){
		int color = 0;
		switch(status){
			case UNSELECTED: return;
			case SELECTED: color = Color.BLUE; break;
			case VALID_SELECTION: color = Color.GREEN; break;
			case INVALID_SELECTION: color = Color.RED; break;
		}
		
        p.setColor(color);
        p.setAlpha(255);
        p.setStyle(Paint.Style.STROKE);
		RectF b = new RectF(0, 0, width, height);
        c.drawRect(b, p);
        p.setAlpha(60);
        p.setStyle(Paint.Style.FILL);
        c.drawRect(b, p);
	}

	// set the color of the Paint p according to this.color
	private void setColor(Paint p) {
		switch (color) {
		case 0: p.setColor(Color.RED); break;
		case 1: p.setColor(Color.BLUE); break;
		case 2: p.setColor(Color.GREEN); break;
		default: new Error("invalid color");
		}
	}

	// draw the desired shape (on the Canvas c, with the Paint p) according to this.shape
	private void drawShape(Canvas c, Paint p, RectF r) {
		switch (shape) {
		case 0: c.drawOval(r, p); break;
		case 1: c.drawRect(r, p); break;
		case 2: c.drawPath(diamond(r), p); break;
		default: new Error("invalid shape");
		}
	}

	// draw the desired shape (on the Canvas c, with the Paint p) with the correct filling according to this.filling
	private void drawFilledShape(Canvas c, Paint p, RectF r) {
		switch (filling) {
		case 0: p.setStyle(Paint.Style.STROKE); drawShape(c, p, r); break;
		case 1:
			// in case of intermediate filling, we draw concentric copies of the same shape
			p.setStyle(Paint.Style.STROKE);
			for (int i = 0; i < r.width()/2; i+=6) {
				drawShape(c, p, new RectF(r.left + i, r.top + i * r.height()/r.width(), r.right - i, r.bottom - i * r.height()/r.width()));
			}
			break;
		case 2: p.setStyle(Paint.Style.FILL); drawShape(c, p, r); break;
		default: new Error("invalid filling");
		}
	}
	
	// creates a diamond in the rectangle r
	private Path diamond(RectF r) {
		Path p = new Path();
		p.moveTo(r.left, r.centerY());
		p.lineTo(r.centerX(), r.top);
		p.lineTo(r.right, r.centerY());
		p.lineTo(r.centerX(), r.bottom);
		p.lineTo(r.left, r.centerY());
		return p;
	}
}
