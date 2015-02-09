package edu.rosehulman.blutag.service;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.rosehulman.blutag.service.data.Game;
import edu.rosehulman.blutag.service.receivers.RegistrationService;

public class BluTagService extends Service {
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(activity);

		if (resultCode == ConnectionResult.SUCCESS) {
			Intent intent = new Intent(RegistrationService.ACTION_REGISTRATION);

			activity.sendBroadcast(intent);
		} else {
			if (resultCode != ConnectionResult.SERVICE_INVALID
					&& GooglePlayServicesUtil
							.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			}
		}
	}

	public static void leaveGame() {
		// TODO Implement
	}
}
