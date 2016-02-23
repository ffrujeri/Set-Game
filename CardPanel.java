package fr.polytechnique.inf431.androidsetgame;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class CardPanel extends View {
	Card card;
	
	public CardPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// ...
	}

	// redefine the drawing method
	@Override
	public void onDraw(Canvas c) {
		if (card != null)
			card.draw(c, getWidth(), getHeight());
	}
	
	// ...
}
