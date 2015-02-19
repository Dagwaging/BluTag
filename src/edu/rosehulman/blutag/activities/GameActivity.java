package edu.rosehulman.blutag.activities;

import java.util.Iterator;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.adapters.HolderAdapter;
import edu.rosehulman.blutag.adapters.PlayerHolder;
import edu.rosehulman.blutag.service.BluTagService;
import edu.rosehulman.blutag.service.BluTagService.GameListener;
import edu.rosehulman.blutag.service.data.Game;
import edu.rosehulman.blutag.service.data.Player;
import edu.rosehulman.blutag.service.data.Tag;

public class GameActivity extends ServiceActivity implements OnClickListener,
		Listener<Player>, ErrorListener, GameListener {
	private static final String TAG = "edu.rosehulman.blutag.activities.GameActivity";

	public static final String EXTRA_GAME = "game";

	private static final int REQUEST_DISCOVERABLE_JOIN = 9000;

	private static final int REQUEST_DISCOVERABLE_CREATE = 9001;

	public static final String EXTRA_CREATED = "created";

	private HolderAdapter<Player> players;
	private AlertDialog leaveDialog;
	private MenuItem joinGame;
	private MenuItem startGame;

	private Game game;

	private boolean joined = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		players = new HolderAdapter<Player>(this, R.layout.item_player,
				new PlayerHolder.Factory());

		ListView playersListView = (ListView) findViewById(android.R.id.list);
		playersListView.setAdapter(players);

		leaveDialog = new AlertDialog.Builder(this)
				.setMessage(R.string.dialog_game_leave_message)
				.setPositiveButton(R.string.dialog_game_leave_button, this)
				.setNegativeButton(android.R.string.cancel, null).create();

		getActionBar().setDisplayHomeAsUpEnabled(true);

		game = getIntent().getParcelableExtra(EXTRA_GAME);
		setTitle(game.name);
	}

	@Override
	protected void onResume() {
		super.onResume();

		players.clear();

		players.addAll(game.getActivePlayers());
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		service.unregisterGameListener(this);

		super.onServiceDisconnected(name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game, menu);
		joinGame = menu.findItem(R.id.menu_game_join);
		startGame = menu.findItem(R.id.menu_game_start);

		joinGame.setVisible(!joined);
		startGame.setVisible(getIntent().hasExtra(EXTRA_CREATED));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.menu_game_join) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			startActivityForResult(discoverableIntent,
					REQUEST_DISCOVERABLE_JOIN);

			return true;
		} else if (id == R.id.menu_game_start) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			startActivityForResult(discoverableIntent,
					REQUEST_DISCOVERABLE_CREATE);

			return true;
		} else if (id == android.R.id.home) {
			finish();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_DISCOVERABLE_JOIN) {
			if (resultCode != RESULT_CANCELED) {
				Log.d(TAG, "Joining game " + game);

				BluTagService.joinGame(this, this, this, null, game);
			}
		} else if (requestCode == REQUEST_DISCOVERABLE_CREATE) {
			if (resultCode != RESULT_CANCELED) {
				Log.d(TAG, "Starting game " + game);

				service.startGame(new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						// TODO Auto-generated method stub

						startGame.setVisible(false);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				}, null);
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == leaveDialog) {
			service.leaveGame(new Listener<Void>() {
				@Override
				public void onResponse(Void response) {
					GameActivity.super.finish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub

				}
			}, null);
		}
	}

	@Override
	public void finish() {
		if (joined && service != null) {
			leaveDialog.show();
		} else {
			super.finish();
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		throw new RuntimeException(error);
	}

	@Override
	public void onResponse(Player response) {
		Log.d(TAG, "Joined game " + game);

		joined = true;
		joinGame.setVisible(false);

		this.service.registerGameListener(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);

		Game currentGame = this.service.getGame();

		if (currentGame != null) {
			if (currentGame.equals(game)) {
				joined = true;

				this.service.registerGameListener(this);
			} else {
				Log.d(TAG, "Game " + game._id
						+ " does not match current game, finishing...");

				super.finish();
			}
		}
	}

	@Override
	public void onPlayerJoin(Player player) {
		game.players.add(player);
		players.add(player);

		Log.d(TAG, player + " joined");
	}

	@Override
	public void onPlayerLeave(Player player) {
		Iterator<Player> iterator = game.players.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(player))
				iterator.remove();
		}
		for (int i = 0; i < players.getCount(); i++) {
			if (players.getItem(i).equals(player)) {
				players.remove(players.getItem(i));
				i--;
			}
		}

		Log.d(TAG, player + " left");
	}

	@Override
	public void onTag(Tag tag) {
		// TODO Auto-generated method stub

	}
}
