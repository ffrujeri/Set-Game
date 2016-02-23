package com.inf431.set.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.view.View;

public class StatusPanel extends View{
	private int score, time;
	private Card previousCards[];
	private Paint borderPaint, smallBorderPaint, textPaint;

	public StatusPanel(Context context) {
		super(context);
		setBackgroundColor(0xFFFFFFFF);

		score = 0;
		time = 0;
		
		initializePaints();
        startTime();
	}
	
	@Override
	public void onDraw(Canvas c) {
		int width = getWidth(), height = getHeight();
		borderPaint.setStrokeWidth(width/20);
		textPaint.setStrokeWidth(width/50);
		textPaint.setTextSize(height/8);

		drawBorder(c, width, height);
		drawScore(c, width, height);
		drawTime(c, width, height);

		if (previousCards != null){
			int cardWidth = width*9/32, cardHeight = height*9/32,
				dx = (width - 3*cardWidth)/4;
			drawCard(c, dx, height*3/5, 0, cardWidth, cardHeight);
			drawCard(c, dx+cardWidth, 0, 1, cardWidth, cardHeight);
			drawCard(c, dx+cardWidth, 0, 2, cardWidth, cardHeight);
		}
	}
	
	public void setPreviousCards(int[] cardsIndexes){
		if(cardsIndexes.length == 3){
			previousCards = new Card[3];
			for (int i = 0; i < cardsIndexes.length; i++) {
				previousCards[i] = new Card(cardsIndexes[i]);
			}
		}
	}
	
	public void updateScore(int score){
		this.score = score;
	}

	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	private void drawBorder(Canvas c, int width, int height){
		c.drawRect(0, 0, width, height, borderPaint);
	}

	private void drawCard(Canvas c, int dx, int dy, int index, int cardWidth, int cardHeight){
		c.translate(dx, dy);
		c.drawRect(0, 0, cardWidth, cardHeight, smallBorderPaint);
		previousCards[index].draw(c, cardWidth, cardHeight);		
	}
	
	private void drawScore(Canvas c, int width, int height){
		int w = width/(6 + (score/10 > 0 ? 2 : 0) + (score/100 > 0 ? 1 : 0));
		c.drawText("score: " + score, w, 2*height/5, textPaint);
	}
	
	private void drawTime(Canvas c, int width, int height){
		c.drawText(String.format("%02d:%02d", time/60, time%60), 9*width/32, height/5, textPaint);
	}

	private void initializePaints(){
		borderPaint = new Paint();
		borderPaint.setColor(Color.BLACK);
		borderPaint.setStyle(Style.STROKE);
		
		smallBorderPaint = new Paint();
		smallBorderPaint.setColor(Color.BLACK);
		smallBorderPaint.setStyle(Style.STROKE);

		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setStyle(Style.FILL);
	}
	
	private void startTime(){
		final Handler handler = new Handler(); 
        Runnable runable = new Runnable() {
            @Override 
            public void run() { 
                try{
                	time++;
                	invalidate();
                	handler.postDelayed(this, 1000);
                }
                catch (Exception e) {}
            }
        };
        
        handler.postDelayed(runable, 1000);
	}
	
}
