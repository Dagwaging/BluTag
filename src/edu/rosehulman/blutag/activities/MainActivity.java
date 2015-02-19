package edu.rosehulman.blutag.activities;

import java.util.List;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.adapters.GameHolder;
import edu.rosehulman.blutag.adapters.HolderAdapter;
import edu.rosehulman.blutag.loaders.GamesLoader;
import edu.rosehulman.blutag.service.BluTagService;
import edu.rosehulman.blutag.service.data.Game;
import edu.rosehulman.blutag.service.receivers.RegistrationService;

public class MainActivity extends ServiceActivity implements OnClickListener,
		OnItemClickListener, OnCancelListener, LoaderCallbacks<List<Game>> {
	private static final String TAG = "edu.rosehulman.blutag.activities.MainActivity";

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private static final int GOOGLE_PLUS_AUTHENTICATION_REQUEST = 9001;

	private static final int BLUETOOTH_DISCOVERABLE_REQUEST = 9002;

	private HolderAdapter<Game> games;
	private AlertDialog createDialog;
	private TextView createGameName;

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
		createGameName = (TextView) view.findViewById(R.id.game_name);

		createDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_game_create_title).setView(view)
				.setPositiveButton(R.string.dialog_game_create_button, this)
				.setNegativeButton(android.R.string.cancel, null).create();

		createDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	@Override
	protected void onStart() {
		super.onStart();

		getLoaderManager().initLoader(0, null, this);

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (resultCode != ConnectionResult.SUCCESS) {
			if (!GooglePlayServicesUtil.showErrorDialogFragment(resultCode,
					this, PLAY_SERVICES_RESOLUTION_REQUEST, this)) {
				finish();

				return;
			}
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (!prefs.contains(RegistrationService.PREF_REGID)) {
			Intent intent = new Intent(this, RegistrationService.class);

			startService(intent);
		}

		startActivityForResult(AccountPicker.newChooseAccountIntent(null, null,
				new String[] { "com.google" }, false, null, null, null, null),
				GOOGLE_PLUS_AUTHENTICATION_REQUEST);
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
		} else if (id == R.id.menu_refresh) {
			getLoaderManager().restartLoader(0, null, this);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == createDialog) {
			if (BluTagService.startDiscoverable(this,
					BLUETOOTH_DISCOVERABLE_REQUEST)) {
				BluTagService.createGame(this, new Listener<Game>() {
					@Override
					public void onResponse(Game response) {
						Intent intent = new Intent(MainActivity.this,
								GameActivity.class);
						intent.putExtra(GameActivity.EXTRA_GAME, response);
						intent.putExtra(GameActivity.EXTRA_CREATED, true);
						startActivity(intent);

						Log.d(TAG, "Game created: " + response._id);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						throw new RuntimeException(error);
					}
				}, TAG, createGameName.getText().toString());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Game game = games.getItem(position);

		Intent gameIntent = new Intent(this, GameActivity.class);
		gameIntent.putExtra(GameActivity.EXTRA_GAME, game);

		startActivity(gameIntent);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GOOGLE_PLUS_AUTHENTICATION_REQUEST) {
			if (resultCode == RESULT_OK) {
				final String account = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

				PreferenceManager.getDefaultSharedPreferences(this).edit()
						.putString(BluTagService.PREF_ACCOUNT, account).apply();

				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						BluTagService.authenticate(account, MainActivity.this,
								MainActivity.this,
								PLAY_SERVICES_RESOLUTION_REQUEST);

						return null;
					}
				}.execute();
			} else {
				finish();
			}
		} else if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST) {
			if (resultCode == RESULT_OK) {
				recreate();
			} else {
				finish();
			}
		} else if (requestCode == BLUETOOTH_DISCOVERABLE_REQUEST) {
			if (resultCode == RESULT_OK) {
				BluTagService.createGame(this, new Listener<Game>() {
					@Override
					public void onResponse(Game response) {
						games.add(response);

						Intent intent = new Intent(MainActivity.this,
								GameActivity.class);
						startActivity(intent);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						throw new RuntimeException(error);
					}
				}, TAG, createGameName.getText().toString());
			} else {
			}
		}
	}

	@Override
	public Loader<List<Game>> onCreateLoader(int id, Bundle args) {
		return new GamesLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<Game>> loader, List<Game> data) {
		Log.d(TAG, "Games: " + data);

		games.clear();
		games.addAll(data);
		games.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<List<Game>> loader) {
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);

		Game currentGame = this.service.getGame();

		if (currentGame != null) {
			Intent gameIntent = new Intent(this, GameActivity.class);
			gameIntent.putExtra(GameActivity.EXTRA_GAME, currentGame);

			startActivity(gameIntent);
		}
	}
}
