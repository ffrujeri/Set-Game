package com.inf431.set.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.inf431.set.R;
import com.inf431.set.model.PlayerMessageProcessor;
import com.inf431.set.model_game.Game;
import com.inf431.set.model_game.MultiplayerGame;
import com.inf431.set.model_game.SinglePlayerGame;


public class GameActivity extends ActionBarActivity implements OnClickListener {

	private Handler myHandler;
	private Game game;
	private StatusPanel status;

	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myHandler = new Handler();
		
		if (getIntent().getExtras().getBoolean("isMultiplayer")){
			PlayerMessageProcessor serverComm = MultiplayerSettingsActivity.getServerComm();
			game = MultiplayerSettingsActivity.getGame();
			((MultiplayerGame) game).setGame(this, serverComm);
			serverComm.setGameActivity(this);
		}else
			game = new SinglePlayerGame(this);

		
		CardPanel[] currentCards = game.getCurrentCards();
		status = game.getStatusPanel();
		TableLayout table = getGameScreenTable(currentCards, status);

		setListeners(currentCards);
		setContentView(table);
	}

	@Override
	public void onClick(final View v) {
		new Thread(new Runnable() {
			public void run() {
				CardPanel selectedCardPanel = (CardPanel) v;
				CardPanel[] currentCards = game.getCurrentCards();

				int cardIndex, nCardsInGame = (game.isComplete() ? 15 : 12);
				for (cardIndex = 0; cardIndex < nCardsInGame
						&& currentCards[cardIndex] != selectedCardPanel; cardIndex++)
					;

				if (cardIndex < nCardsInGame)
					game.selected(cardIndex);
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_hint) {
			if (game.getClass() == SinglePlayerGame.class)
				((SinglePlayerGame) game).giveHint();
			else
				displayMessage("Hints unavailable on multiplayer!");
				// comment line above and uncomment line below to have hints
				// ((MultiplayerGame) game).giveHint();
			return true;
		}else if (id == R.id.action_how_to_play) {
            startActivity(new Intent(GameActivity.this, HowToPlayActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void displayMessage(final String message){
		myHandler.post(new Runnable(){
			public void run(){
		    	Toast.makeText(GameActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void updateCardPanel(final CardPanel cardPanel) {
		if (cardPanel == null)
			return;
		
		myHandler.post(new Runnable() {
			public void run() {
				cardPanel.invalidate();
			}
		});
	}
	
	public void updateStatusPanel() {
		myHandler.post(new Runnable() {
			public void run() {
				status.invalidate();
			}
		});
	}

	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	private TableLayout getGameScreenTable(CardPanel[] initialCards,
			StatusPanel statusPanel) {
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setStretchAllColumns(true);
		tableLayout.setBackgroundColor(getResources().getColor(R.color.black));
		tableLayout.setLayoutParams(new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.MATCH_PARENT));
		tableLayout.setWeightSum(4);

		TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
		TableRow.LayoutParams elemParams = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT);
		elemParams.setMargins(5, 5, 5, 5);

		for (int i = 0; i < 4; i++) {
			TableRow tableRow = new TableRow(this);
			tableRow.setGravity(Gravity.CENTER);
			tableRow.setLayoutParams(rowParams);

			for (int j = 0; j < 4; j++) {
				if (i != 3 || j != 3) {
					int index = 4 * i + j;
					initialCards[index].setLayoutParams(elemParams);
					tableRow.addView(initialCards[index]);
				} else {
					tableRow.addView(statusPanel);
				}
			}

			tableLayout.addView(tableRow);
		}

		return tableLayout;
	}

	private void setListeners(CardPanel[] cards) {
		for (int i = 0; i < cards.length; i++)
			cards[i].setOnClickListener(this);
	}
}
