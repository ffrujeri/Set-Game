package com.inf431.set.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.inf431.set.model_game.MultiplayerGame;
import com.inf431.set.view.GameActivity;
import com.inf431.set.view.MultiplayerSettingsActivity;

public class PlayerMessageProcessor implements Runnable{

	private boolean connected, gameOver;
	private int serverPort, id;
	private MultiplayerGame game;
	private MultiplayerSettingsActivity settingsActivity;
	private GameActivity gameActivity;
	private Socket server;
	private String serverIp;

	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	public PlayerMessageProcessor(MultiplayerSettingsActivity settingsActivity, MultiplayerGame game) {
		this.settingsActivity = settingsActivity;
		serverIp = "10.0.2.2";
		serverPort = 6000;
		connected = false;
		gameOver = false;
		this.game = game;
	}

	public void connect() {
		if (connected){
			settingsActivity.displayMessage("Already connected to server!");
			return;
		}
		
		new Thread(new Runnable(){
			public void run(){
				try {
					InetAddress serverAddr = InetAddress.getByName(serverIp);
					server = new Socket(serverAddr, serverPort);
					connected = true;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if(connected)
					settingsActivity.displayMessage("Succesfully connected!");
				else
					settingsActivity.displayMessage("Connection unsuccessful");
			}
		}).start();
	}

	public boolean isConnected(){
		return connected;
	}
	
	public void run() {
		if (!connected) {
			return;
		}

		Log.d("PlayerMessageProcessor", "Running...");

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					server.getInputStream()));
	
			while(!gameOver){
				String line = null;
				while ((line = in.readLine()) != null) {
					Log.d("PlayerMessageProcessor", "id = " + id + ", processing line = " + line);
					String[] s = line.split(":");
					switch(s[0]){
						case "ADD_SELF":
							id = Integer.parseInt(s[1]);
							settingsActivity.addPlayerToList(id, true);
							break;
						case "ADD_PLAYER":
							int playerId = Integer.parseInt(s[1]);
							if(id < playerId)
								settingsActivity.addPlayerToList(playerId, false);
							break;
						case "CARDS":
							if (game != null){
								int[] cardIndexes = new int[s.length-1];
								for (int i = 0; i < s.length-1; i++) {
									cardIndexes[i] = Integer.parseInt(s[i+1]);
								}game.setCards(cardIndexes);
							}
							settingsActivity.startGame();
							break;
						case "MESSAGE_END":
							if (gameActivity != null)
								gameActivity.displayMessage(s[1]);
							gameOver = true;
							server.close();
							connected = false;
							break;
						case "MESSAGE":
							if (gameActivity != null)
								gameActivity.displayMessage(s[1]);
							break;
						case "SET":
							int playerScoredId = Integer.parseInt(s[1]);
							int[] setIndexes = {Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4])};
							game.scoreSet((id == playerScoredId), setIndexes);
							break;
					}
				}

				Thread.sleep(50);
			}
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}
	}

	public void sendEndGameMessage(int score) {
		String message = "END_GAME:" + score;
		sendMessage(message);
	}

	public void sendFoundSetMessage(int[] set) {
		String message = "SET:" + set[0] + ":" + set[1] + ":" + set[2];
		sendMessage(message);
	}

	public void sendStartGameMessage() {
		String message = "START_GAME";
		sendMessage(message);
	}

	public void setGameActivity(GameActivity gameActivity){
		this.gameActivity = gameActivity;
	}
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	private void sendMessage(String message) {
		if(!connected)
			return;
		
		Log.d("PlayerMessageProcessor", "id = " + id + ", sending message = " + message);
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(server.getOutputStream())), true);
			out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
