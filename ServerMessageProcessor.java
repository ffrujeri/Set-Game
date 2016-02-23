package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerMessageProcessor extends Thread {
	private LinkedBlockingQueue<String> messageQueue;
	private MultiplayerServer server;

	public ServerMessageProcessor(LinkedBlockingQueue<String> messageQueue, MultiplayerServer server) {
		this.messageQueue = messageQueue;
		this.server = server;
	}

	// messages identifiers player -> server: START_GAME, END_GAME, SET
	// messages identifiers server -> player: ADD_SELF, ADD_PLAYER, MESSAGE, MESSAGE_END, SET, CARDS
	public void run() {
		System.out.println("ServerMessageProcessor running...");
		try {
			boolean playing = false;
			while (!playing){
				String line = messageQueue.take();
				System.out.println("ServerMessageProcessor\t processing line = " + line);
				String s[] = line.split(":");
				if (s[1].equals("START_GAME") && !playing){
					playing = true;
					server.startGame();
				}
			}
			
			int counter = 0;
			while(playing){
				String line = messageQueue.take();
				System.out.println("ServerMessageProcessor\t, processing line = " + line);
				String s[] = line.split(":");
				int id = Integer.parseInt(s[0]);
				switch(s[1]){
					case "END_GAME":
							int score = Integer.parseInt(s[2]);
							server.score(id, score);
							counter++;
							if(counter == server.getNumberOfPlayers()){
								playing = false;
								server.gameOver();
							}
						break;
					case "SET":
						messageQueue.clear();
						server.notifyAllPlayersOfSet(id, s[2]+":"+s[3]+":"+s[4]);
						break;
				}
			}
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void notifyAllPlayers(String message, ArrayList<Socket> players){
		System.out.println("[ALL]\t" + message);
		for(Socket p : players){
			PrintWriter out;
			try {
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(p.getOutputStream())), true);
				out.println(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void notifyPlayer(int id, String message, ArrayList<Socket> players){
		System.out.println("[" + id + "]\t" + message);
		Socket p = players.get(id);
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(p.getOutputStream())), true);
			out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processMessages(Socket client, int id){
		new Thread(new Runnable(){
			public void run(){
				try {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(client.getInputStream()));
					while(true){
						String line = null;
						while ((line = in.readLine()) != null) {
							messageQueue.add(id + ":" + line);
						}
						
						Thread.sleep(50);
					}
				} catch (Exception e) {
					System.out.println("Error");
					e.printStackTrace();
				}
			}
		}).start();
	}
}