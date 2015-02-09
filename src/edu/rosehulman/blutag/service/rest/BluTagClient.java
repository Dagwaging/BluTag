package edu.rosehulman.blutag.service.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.rosehulman.blutag.service.data.Game;

public class BluTagClient {
	public interface GetGamesCallback {
		
	}

	private static final String API_URL = "http://private-4e59b2-blutag.apiary-mock.com";
	private static final String GAMES_URL = "/games";
	private static final String GAME_URL = "/games/%s";
	private static final String TAGS_URL = "/tags";
	private static final String PLAYERS_URL = "/players";
	
	private URL apiUrl;
	
	private Gson gson;
	
	private RequestQueue requestQueue;
	
	private static BluTagClient instance;
	
	private BluTagClient(Context context) {
		gson = new GsonBuilder().create();
		
		requestQueue = Volley.newRequestQueue(context.getApplicationContext());

		try {
			apiUrl = new URL(API_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Game> getGames(GetGamesCallback listener, String... players) {
		
		String response = "";
		
		
		
		return null;
		
	}
	
	public static synchronized BluTagClient getInstance(Context context) {
		if(instance == null) {
			instance = new BluTagClient(context);
		}
		return instance;
	}
}
