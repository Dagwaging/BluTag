package edu.rosehulman.blutag.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.service.data.Game;
import edu.rosehulman.blutag.service.data.Player;
import edu.rosehulman.blutag.service.data.Tag;
import edu.rosehulman.blutag.service.receivers.BluetoothReceiver;
import edu.rosehulman.blutag.service.receivers.BluetoothReceiver.BluetoothListener;
import edu.rosehulman.blutag.service.receivers.PushReceiver;
import edu.rosehulman.blutag.service.receivers.RegistrationService;
import edu.rosehulman.blutag.service.rest.BluTagClient;

public class BluTagService extends Service implements BluetoothListener,
		Listener<Tag>, ErrorListener {
	protected static final String TAG = "edu.rosehulman.blutag.service.BluTagService";

	public static final String PREF_ACCOUNT = "account";

	private static final String EXTRA_GAME = "game";

	private static String authToken = null;

	private static String pushId = null;

	private static Listener<List<Game>> gamesListener = null;

	private static BluetoothReceiver bluetoothReceiver = null;

	private Game currentGame = null;

	private ServiceBinder<BluTagService> binder;

	private BroadcastReceiver pushReceiver;

	private GameListener gameListener;

	@Override
	public void onCreate() {
		binder = new ServiceBinder<BluTagService>(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		currentGame = intent.getParcelableExtra(EXTRA_GAME);

		final Player it = currentGame.getIt();

		if (it != null) {
			BluTagClient.getInstance(BluTagService.this).getImage(
					new Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap response) {
							String title = getString(R.string.notification_tag,
									it.givenName);

							Notification tagNotification = new Notification.Builder(
									BluTagService.this)
									.setContentTitle(title)
									.setLargeIcon(response)
									.setSmallIcon(
											R.drawable.ic_stat_device_bluetooth_searching)
									.getNotification();

							BluTagService.this.startForeground(1,
									tagNotification);
						}
					}, null, null, it.image, 192, 192);
		} else {
			Notification tagNotification = new Notification.Builder(this)
					.setContentTitle(getString(R.string.notification_wait))
					.setSmallIcon(R.drawable.ic_stat_device_bluetooth_searching)
					.getNotification();

			BluTagService.this.startForeground(1, tagNotification);
		}

		bluetoothReceiver = new BluetoothReceiver(this);
		bluetoothReceiver.register(this);

		BluetoothAdapter.getDefaultAdapter().startDiscovery();

		pushReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, intent.getAction());

				if (PushReceiver.ACTION_JOIN.equals(intent.getAction())) {
					Player player = intent.getExtras().getParcelable(
							PushReceiver.EXTRA_DATA);

					for (Player _player : currentGame.players) {
						if (_player.equals(player)) {
							if(! _player.left) {
								return;
							}
							
							_player.left = false;
						}
					}

					currentGame.players.add(player);
					currentGame.playerCount++;

					if (BluTagService.this.gameListener != null)
						BluTagService.this.gameListener.onPlayerJoin(player);
				} else if (PushReceiver.ACTION_LEAVE.equals(intent.getAction())) {
					Player player = intent.getExtras().getParcelable(
							PushReceiver.EXTRA_DATA);

					for (Player _player : currentGame.players) {
						if (_player.address.equals(player.address)) {
							_player.left = true;

							if (BluTagService.this.gameListener != null)
								BluTagService.this.gameListener
										.onPlayerLeave(_player);
							break;
						}
					}

					currentGame.playerCount--;
				} else if (PushReceiver.ACTION_TAG.equals(intent.getAction())) {
					Tag tag = intent.getExtras().getParcelable(
							PushReceiver.EXTRA_DATA);

					currentGame.tags.add(tag);
					final Player it = currentGame.getIt();

					BluTagClient.getInstance(BluTagService.this).getImage(
							new Listener<Bitmap>() {
								@Override
								public void onResponse(Bitmap response) {
									String title = getString(
											R.string.notification_tag,
											it.givenName);

									Notification tagNotification = new Notification.Builder(
											BluTagService.this)
											.setContentTitle(title)
											.setLargeIcon(response)
											.setDefaults(
													Notification.DEFAULT_ALL)
											.setSmallIcon(
													R.drawable.ic_stat_device_bluetooth_searching)
											.getNotification();

									BluTagService.this.startForeground(1,
											tagNotification);
								}
							}, null, null, it.image, 192, 192);

					if (BluTagService.this.gameListener != null)
						BluTagService.this.gameListener.onTag(tag);
				} else if (PushReceiver.ACTION_DELETE
						.equals(intent.getAction())) {
					LocalBroadcastManager.getInstance(BluTagService.this)
							.unregisterReceiver(pushReceiver);

					currentGame = null;

					stopSelf();

					stopForeground(true);

					if (BluTagService.this.gameListener != null)
						BluTagService.this.gameListener.onGameDeleted();
				}
			}
		};

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PushReceiver.ACTION_JOIN);
		intentFilter.addAction(PushReceiver.ACTION_LEAVE);
		intentFilter.addAction(PushReceiver.ACTION_TAG);
		intentFilter.addAction(PushReceiver.ACTION_DELETE);

		LocalBroadcastManager.getInstance(this).registerReceiver(pushReceiver,
				intentFilter);

		Log.d(TAG, "Current game: " + currentGame._id);

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (bluetoothReceiver != null)
			bluetoothReceiver.unregister(this);

		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void registerGameListener(GameListener listener) {
		gameListener = listener;
	}

	public void unregisterGameListener(GameListener listener) {
		gameListener = null;
	}

	public static void registerGamesListener(final Context context,
			final Listener<List<Game>> listener,
			final ErrorListener errorListener) {
		gamesListener = listener;

		if (bluetoothReceiver != null) {
			bluetoothReceiver.unregister(context);
		}

		bluetoothReceiver = new BluetoothReceiver(new BluetoothListener() {
			Set<String> addresses = new HashSet<String>();

			@Override
			public void onDeviceFound(String name, String address, Boolean near) {
				if (addresses.add(address)) {
					Log.d(TAG, "Looking up game for " + address);

					BluTagClient.getInstance(context).getGames(gamesListener,
							errorListener, listener, address);
				}
			}

			@Override
			public void onStateChanged(int state) {
			}

			@Override
			public void onDiscoveryFinished() {
			}
		});

		bluetoothReceiver.register(context);

		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		BluetoothAdapter.getDefaultAdapter().startDiscovery();
	}

	public static void unregisterGamesListener(Context context,
			Listener<List<Game>> listener) {
		if (bluetoothReceiver != null)
			bluetoothReceiver.unregister(context);

		if (gamesListener != null)
			BluTagClient.getInstance(context).cancelAll(gamesListener);

		gamesListener = null;
		bluetoothReceiver = null;

		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	}

	public static void getGame(Context context, Listener<Game> listener,
			ErrorListener errorListener, Object tag, Game game) {
		BluTagClient.getInstance(context).getGame(listener, errorListener, tag,
				game._id);
	}

	public static void createGame(final Activity context,
			final Listener<Game> listener, final ErrorListener errorListener,
			final Object tag, String name) {
		final BluTagClient client = BluTagClient.getInstance(context);

		client.createGame(new Listener<Game>() {
			@Override
			public void onResponse(final Game gameResponse) {
				joinGame(context, new Listener<Player>() {
					@Override
					public void onResponse(Player playerResponse) {
						listener.onResponse(gameResponse);
					}
				}, errorListener, tag, gameResponse);
			}
		}, errorListener, tag, name);
	}

	public static void joinGame(final Activity context,
			final Listener<Player> listener, final ErrorListener errorListener,
			final Object tag, final Game game) {

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				authenticate(
						PreferenceManager.getDefaultSharedPreferences(context)
								.getString(PREF_ACCOUNT, null), context, null,
						0);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				String player = BluetoothAdapter.getDefaultAdapter()
						.getAddress();

				pushId = PreferenceManager.getDefaultSharedPreferences(context)
						.getString(RegistrationService.PREF_REGID, null);

				BluTagClient.getInstance(context).joinGame(
						new Listener<Player>() {
							@Override
							public void onResponse(Player response) {
								Intent service = new Intent(context,
										BluTagService.class);
								service.putExtra(EXTRA_GAME, game);
								context.startService(service);

								listener.onResponse(response);
							}
						}, errorListener, tag, game._id, authToken, pushId,
						player);
			}

		}.execute();
	}

	public void startGame(Listener<Void> listener, ErrorListener errorListener,
			Object tag) {
		BluTagClient.getInstance(this).startGame(listener, errorListener, tag,
				currentGame._id);
	}

	public void deleteGame(final Listener<Void> listener,
			final ErrorListener errorListener, final Object tag) {
		final Game game = currentGame;

		BluTagClient.getInstance(this).leaveGame(new Listener<Void>() {
			@Override
			public void onResponse(Void response) {
				BluTagClient.getInstance(BluTagService.this).deleteGame(
						listener, errorListener, tag, game._id, authToken);
			}
		}, errorListener, tag, game._id, authToken);
	}

	public void leaveGame(Listener<Void> listener, ErrorListener errorListener,
			Object tag) {
		BluTagClient.getInstance(this).leaveGame(listener, errorListener, tag,
				currentGame._id, authToken);

		LocalBroadcastManager.getInstance(this)
				.unregisterReceiver(pushReceiver);

		currentGame = null;

		stopSelf();

		stopForeground(true);
	}

	public Game getGame() {
		return currentGame;
	}

	/**
	 * Synchronously authenticate with Google+. May present a dialog to the user
	 * if not already authorized.
	 * 
	 * @param context
	 * @param activity
	 *            activity to return to after presenting authorization UI. If
	 *            null, no UI will be shown and authentication will fail if the
	 *            app is not authorized.
	 * @param requestCode
	 *            request code to pass into onActivityResult of the activity
	 * @return true if successfully authenticated
	 */
	public static boolean authenticate(String accountName,
			final Context context, final Activity activity,
			final int requestCode) {

		final String scope = "oauth2:profile email";

		try {
			authToken = GoogleAuthUtil.getToken(context, accountName, scope);
		} catch (UserRecoverableAuthException e) {
			if (activity != null) {
				activity.startActivityForResult(e.getIntent(), requestCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GoogleAuthException e) {
			e.printStackTrace();
		}

		return authToken != null;
	}

	public static boolean startDiscoverable(Activity activity, int requestCode) {
		if (BluetoothAdapter.getDefaultAdapter().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);

			activity.startActivityForResult(discoverableIntent, requestCode);

			return false;
		}

		return true;
	}

	/**
	 * Register the device with Google Cloud Messaging. Sends a broadcast
	 * indicating success or failure.
	 * 
	 * @param context
	 */
	public static void register(Context context) {
		Intent registerIntent = new Intent(context, RegistrationService.class);
		context.startService(registerIntent);
	}

	@Override
	public void onDeviceFound(String name, String address, Boolean near) {
		Log.d(TAG,
				"Saw "
						+ address
						+ (near ? " nearby, " : ", ")
						+ (currentGame != null && currentGame.getIt() != null ? currentGame
								.getIt().address : "nobody") + " is it");

		if (near && currentGame != null && currentGame.getIt() != null
				&& address.equals(currentGame.getIt().address)) {
			BluTagClient.getInstance(this).tag(this, this, null,
					currentGame._id, authToken);
		}
	}

	@Override
	public void onStateChanged(int state) {
		if (state == BluetoothAdapter.STATE_TURNING_OFF
				|| state == BluetoothAdapter.STATE_OFF) {
			if(currentGame != null) {
				leaveGame(new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						// TODO Auto-generated method stub
						
					}
				}, null, null);
				
				if(gameListener != null)
					gameListener.onBluetoothDisabled();
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(Tag response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDiscoveryFinished() {
		BluetoothAdapter.getDefaultAdapter().startDiscovery();
	}

	public interface GameListener {
		public void onPlayerJoin(Player player);

		public void onBluetoothDisabled();

		public void onGameDeleted();

		public void onPlayerLeave(Player player);

		public void onTag(Tag tag);
	}
}
