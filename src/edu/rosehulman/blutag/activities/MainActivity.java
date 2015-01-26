package edu.rosehulman.blutag.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.adapters.GameHolder;
import edu.rosehulman.blutag.adapters.HolderAdapter;
import edu.rosehulman.blutag.data.Game;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {
	private HolderAdapter<Game> games;
	private AlertDialog createDialog;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		games = new HolderAdapter<Game>(this, R.layout.item_game,
				new GameHolder.Factory());

		ListView gamesListView = (ListView) findViewById(android.R.id.list);
		gamesListView.setAdapter(games);
		gamesListView.setOnItemClickListener(this);

		View view = getLayoutInflater().inflate(R.layout.dialog_game_create,
				null, false);

		createDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_game_create_title).setView(view)
				.setPositiveButton(R.string.dialog_game_create_button, this)
				.setNegativeButton(android.R.string.cancel, null).create();

		createDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.menu_game_create) {
			createDialog.show();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == createDialog) {
			Intent intent = new Intent(this, GameActivity.class);
			startActivity(intent);

			// TODO Create a game, add to the adapter, and submit to the REST
			// API;
			// then go to the game activity

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO View a game by going to the game activity
	}
}
