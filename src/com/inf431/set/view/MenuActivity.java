package com.inf431.set.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.inf431.set.R;


public class MenuActivity extends Activity implements OnClickListener{
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.screen_menu);
	    
	    Button buttonSinglePlayer = (Button) findViewById(R.id.button_single_player);
	    Button buttonMultiplayer = (Button) findViewById(R.id.button_multiplayer);
	    Button buttonHowToPlay = (Button) findViewById(R.id.button_how_to_play);
	    
	    buttonSinglePlayer.setOnClickListener(this);
	    buttonMultiplayer.setOnClickListener(this);
	    buttonHowToPlay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	        case R.id.button_single_player:
        		Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
				myIntent.putExtra("isMultiplayer", false);
				startActivity(myIntent);
	            break;
	        case R.id.button_multiplayer:
	            startActivity(new Intent(MenuActivity.this, MultiplayerSettingsActivity.class));
	            break;
	        case R.id.button_how_to_play:
	            startActivity(new Intent(MenuActivity.this, HowToPlayActivity.class));
	            break;
		}
	}

}
