package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class MultiplayerServer {
	private boolean startGame;
	private int serverPort;
	private int[] score;
	private String serverIP;

	private LinkedBlockingQueue<String> messageQueue;
	private ArrayList<Socket> players;
	private ServerMessageProcessor processor;
	private ServerSocket serverSocket;

	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	public MultiplayerServer() {
		serverIP = "127.0.0.1";
		serverPort = 6000;
	}
		
	public void gameOver(){
		int id = 0;
		for (int i = 1; i < score.length; i++) {
			if(score[i] > score[id]){
				id = i;
			}
		}
		
		String message = "MESSAGE_END:Game over! Player " + id + " won with " + score[id] + " points!";
		processor.notifyAllPlayers(message, players);
	}
	
	public int getNumberOfPlayers() {
		return players.size();
	}

	public void notifyAllPlayersOfSet(int id, String set){
		String message = "MESSAGE:Player " + id + " found a set!";
		processor.notifyAllPlayers(message, players);
		
		message = "SET:" + id + ":" + set;
		processor.notifyAllPlayers(message, players);
	}
	
	public void run(){
		if (serverIP == null){
			System.out.println("Couldn't detect internet connection.");
			return;
		}
		
		while(true){
			try {
				System.out.println("Listening on IP: " + serverIP);

				startGame = false;
				score = null;
				int id = 0;
				players = new ArrayList<Socket>();
				messageQueue = new LinkedBlockingQueue<String>();
				processor = new ServerMessageProcessor(messageQueue, this);
				processor.start();
				serverSocket = new ServerSocket(serverPort);

				while (!startGame){
					Socket client = serverSocket.accept();
					players.add(client);
					processor.processMessages(client, id);
					processor.notifyPlayer(id, "ADD_SELF:" + id, players);
					processor.notifyAllPlayers("ADD_PLAYER:" + id, players);
					id++;
				}
			} catch (Exception e) {
				System.out.println("Error");
				e.printStackTrace();
			}

			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void score(int id, int score){
		if(this.score == null)
			this.score = new int[players.size()];
		this.score[id] = score;
	}
	
	public void startGame(){
		startGame = true;
		int[] cardsIndexes = getCardsIndexes();
		String s = "";
		for (int i = 0; i < cardsIndexes.length; i++) {
			s += ":" + cardsIndexes[i];
		}processor.notifyAllPlayers("CARDS" + s, players);
	}

	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	private int[] getCardsIndexes(){
		int[] integers = new int[81];
		for (int i = 0; i < integers.length; i++)
			integers[i] = i;
		int n = integers.length;
		Random gen = new Random();
		while (n > 0) {
			int k = gen.nextInt(n--);
			int temp = integers[n];
			integers[n] = integers[k];
			integers[k] = temp;
		}
		
		return integers;
	}
		
	/* ---------------------------------------------------------- */
	/* ---------------------------------------------------------- */
	public static void main(String[] args) {
		MultiplayerServer server = new MultiplayerServer();
		server.run();
	}
}
