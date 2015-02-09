package edu.rosehulman.blutag.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import edu.rosehulman.blutag.service.data.Game;

public class BluTagService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static List<Game> getGames() {
		//TODO Implement
		return null;
	}
	
	public static Game createGame(String name) {
		// TODO Implement
		return null;
	}
	
	public static void joinGame(Game game) {
		// TODO Implement
	}
	
	public static void leaveGame() {
		// TODO Implement
	}
}
