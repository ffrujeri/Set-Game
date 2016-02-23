package com.inf431.set.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inf431.set.R;
import com.inf431.set.model.PlayerMessageProcessor;
import com.inf431.set.model_game.MultiplayerGame;

public class MultiplayerSettingsActivity extends Activity implements OnClickListener{
	private Handler myHandler;
	private static MultiplayerGame game;
	private static PlayerMessageProcessor serverComm;
	private TextView textMultiplayerList;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.screen_multiplayer_settings);
        
	    myHandler = new Handler();
	    game = new MultiplayerGame();
	    serverComm = new PlayerMessageProcessor(this, game);
	    
        Button buttonConnect = (Button) findViewById(R.id.button_multiplayer_connect);
	    Button buttonStartGame = (Button) findViewById(R.id.button_multiplayer_start_game);
	    textMultiplayerList = (TextView) findViewById(R.id.text_multiplayer_list);
	    textMultiplayerList.setText("Players in game:\n");
	    
	    buttonConnect.setOnClickListener(this);
	    buttonStartGame.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	        case R.id.button_multiplayer_connect:
        		serverComm.connect();
        		new Thread(serverComm).start();
	            break;
	        case R.id.button_multiplayer_start_game:
	        	if(serverComm.isConnected()){
	        		serverComm.sendStartGameMessage();
	        	}else
	        		displayMessage("Must be connected to play!");
	            break;
		}
	}
	
	public void addPlayerToList(final int id, final boolean self){
		myHandler.post(new Runnable() {
			public void run() {
				if (self){
					for (int i = 0; i <= id; i++) {
						textMultiplayerList.append("\n• player " + i + (id == i ? " (you)" : ""));
					}
				}else{
					textMultiplayerList.append("\n• player " + id);
				}
				
				textMultiplayerList.invalidate();
			}
		});
	}
	
	public void displayMessage(final String message){
		myHandler.post(new Runnable(){
			public void run(){
		    	Toast.makeText(MultiplayerSettingsActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static MultiplayerGame getGame(){
		return game;
	}

	public static PlayerMessageProcessor getServerComm(){
		return serverComm;
	}
	
	public void startGame(){
		Intent myIntent = new Intent(MultiplayerSettingsActivity.this, GameActivity.class);
		myIntent.putExtra("isMultiplayer", true);
		startActivity(myIntent);
	}
}
