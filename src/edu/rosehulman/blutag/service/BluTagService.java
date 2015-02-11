package edu.rosehulman.blutag.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import edu.rosehulman.blutag.service.data.Game;

public class BluTagService extends Service {
	protected static final String TAG = "edu.rosehulman.blutag.service.BluTagService";

	private String authToken = null;

	private String pushId = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static List<Game> getGames() {
		// TODO Implement
		return null;
	}

	public static Game createGame(String name) {
		// TODO Implement
		return null;
	}

	public static void joinGame(Activity activity, Game game) {
		// TODO Implement

	}

	public static void leaveGame() {
		// TODO Implement
	}

	public static String authenticate(final Activity activity,
			final int requestCode) {
		final GoogleApiClient client = new GoogleApiClient.Builder(activity)
				.useDefaultAccount().addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_PROFILE).build();

		ConnectionResult result = client.blockingConnect(10, TimeUnit.SECONDS);

		String authToken = null;

		if (result.isSuccess()) {
			final String accountName = Plus.AccountApi.getAccountName(client);

			final String scope = "oauth2:profile email";

			try {
				authToken = GoogleAuthUtil.getToken(activity, accountName,
						scope);
			} catch (UserRecoverableAuthException e) {
				activity.startActivityForResult(e.getIntent(), requestCode);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException e) {
				e.printStackTrace();
			}

			client.disconnect();
		} else {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(activity, requestCode);
				} catch (SendIntentException e) {
					e.printStackTrace();
				}
			} else {
				Log.e(TAG, result.toString());
				// TODO: Unable to login for some reason
			}
		}

		return authToken;
	}
}
