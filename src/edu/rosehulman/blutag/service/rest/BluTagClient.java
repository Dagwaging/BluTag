package edu.rosehulman.blutag.service.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.rosehulman.blutag.service.data.Game;
import edu.rosehulman.blutag.service.data.Player;
import edu.rosehulman.blutag.service.data.Tag;

public class BluTagClient {
	private static final String API_URL = "http://137.112.137.151";
	private static final String GAMES_URL = "games";
	private static final String GAME_PLAYERS_PARAMETER = "players";
	private static final String TAGS_URL = "tags";
	private static final String PLAYERS_URL = "players";

	private Uri apiUrl;

	private Gson gson;

	private RequestQueue requestQueue;

	private static BluTagClient instance;

	private BluTagClient(Context context) {
		gson = new GsonBuilder().registerTypeAdapter(Date.class,
				new DateTypeAdapter()).create();

		requestQueue = Volley.newRequestQueue(context.getApplicationContext());

		apiUrl = Uri.parse(API_URL);
	}

	public void getGames(Listener<List<Game>> listener,
			ErrorListener errorListener, Object tag, String... players) {
		StringBuilder playerList = new StringBuilder();

		for (String player : players) {
			if (playerList.length() > 0) {
				playerList.append(',');
			}

			playerList.append(player);
		}

		Builder url = apiUrl.buildUpon().appendPath(GAMES_URL);

		if (playerList.length() > 0) {
			url.appendQueryParameter(GAME_PLAYERS_PARAMETER,
					playerList.toString());
		}

		GsonRequest<List<Game>> getGamesRequest = new GsonRequest<List<Game>>(
				gson, Method.GET, url.build().toString(),
				new TypeToken<List<Game>>() {
				}.getType(), null, listener, errorListener);

		getGamesRequest.setTag(tag);

		requestQueue.add(getGamesRequest);
	}

	public void getGame(Listener<Game> listener, ErrorListener errorListener,
			Object tag, String id) {
		Uri url = apiUrl.buildUpon().appendPath(GAMES_URL)
				.appendEncodedPath(id).build();

		GsonRequest<Game> getGameRequest = new GsonRequest<Game>(gson,
				Method.GET, url.toString(), Game.class, null, listener,
				errorListener);

		getGameRequest.setTag(tag);

		requestQueue.add(getGameRequest);
	}

	public void createGame(Listener<Game> listener,
			ErrorListener errorListener, Object tag, String name) {
		Uri url = apiUrl.buildUpon().appendPath(GAMES_URL).build();

		GsonRequest<Game> createGameRequest = new GsonRequest<Game>(gson,
				Method.POST, url.toString(), Game.class, null, listener,
				errorListener);

		Game body = new Game();
		body.name = name;

		createGameRequest.setBody(body);
		createGameRequest.setTag(tag);

		requestQueue.add(createGameRequest);
	}

	public void tag(Listener<Tag> listener, ErrorListener errorListener,
			Object tag, String gameId, String authToken) {
		Uri url = apiUrl.buildUpon().appendPath(GAMES_URL)
				.appendEncodedPath(gameId).appendPath(TAGS_URL).build();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", authToken);

		GsonRequest<Tag> tagRequest = new GsonRequest<Tag>(gson, Method.POST,
				url.toString(), Tag.class, headers, listener, errorListener);

		tagRequest.setTag(tag);

		requestQueue.add(tagRequest);
	}

	public void joinGame(Listener<Player> listener,
			ErrorListener errorListener, Object tag, String gameId,
			String authToken, String pushId, String player) {
		Uri url = apiUrl.buildUpon().appendPath(GAMES_URL)
				.appendEncodedPath(gameId).appendPath(PLAYERS_URL).build();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", authToken);

		GsonRequest<Player> joinGameRequest = new GsonRequest<Player>(gson,
				Method.POST, url.toString(), Player.class, headers, listener,
				errorListener);

		Player body = new Player();
		body.address = player;
		body.pushId = pushId;

		joinGameRequest.setBody(body);
		joinGameRequest.setTag(tag);

		requestQueue.add(joinGameRequest);
	}

	public void leaveGame(Listener<Void> listener, ErrorListener errorListener,
			Object tag, String gameId, String authToken) {
		Uri url = apiUrl.buildUpon().appendPath(GAMES_URL)
				.appendEncodedPath(gameId).appendPath(PLAYERS_URL).build();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", authToken);

		GsonRequest<Void> leaveGameRequest = new GsonRequest<Void>(gson,
				Method.DELETE, url.toString(), Void.class, headers, listener,
				errorListener);

		leaveGameRequest.setTag(tag);

		requestQueue.add(leaveGameRequest);
	}

	public void cancelAll(Object tag) {
		requestQueue.cancelAll(tag);
	}

	public static synchronized BluTagClient getInstance(Context context) {
		if (instance == null) {
			instance = new BluTagClient(context);
		}
		return instance;
	}
}
