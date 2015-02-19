package edu.rosehulman.blutag.service.receivers;

/**
 * 
 */

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * An IntentService whose job is to register the app with Google Cloud messaging
 * and then send the registration ID to the app server.
 * 
 * @author dagwaging
 * 
 */
public class RegistrationService extends IntentService {
	private static final String EXTRA_REGISTRATION_ID = "registration_id";

	private static final String TAG = RegistrationService.class.getName();

	private static final String GCM_SENDERID = "812914528803";

	private static final String PREF_VERSION = "version";

	public static final String PREF_REGID = "regID";

	public static final String ACTION_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION";

	private static final String ACTION_REGISTRATION_SUCCESS = "edu.rosehulman.blutag.intent.REGISTRATION_SUCCESS";

	private static final String ACTION_REGISTRATION_FAILURE = "edu.rosehulman.blutag.intent.FAILURE";

	public RegistrationService() {
		super("RegistrationService");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Get the version of the app last registered
		Integer appVersion = sharedPreferences.getInt(PREF_VERSION, 0);

		Intent result = new Intent(ACTION_REGISTRATION_FAILURE);

		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);

			// If the app has been updated or freshly installed, we need to
			// register
			if (appVersion != packageInfo.versionCode) {
				String action = intent.getAction();

				String regID = null;

				if (action != null && action.equals(ACTION_REGISTRATION)) {
					// Get the registration id from the intent

					regID = intent.getStringExtra(EXTRA_REGISTRATION_ID);
				} else {
					// Make sure Play Services are available
					if (GooglePlayServicesUtil
							.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
						GoogleCloudMessaging gcm = GoogleCloudMessaging
								.getInstance(this);

						// Register with Google Cloud Messaging
						try {
							regID = gcm.register(GCM_SENDERID);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				if (regID != null) {
					Log.v(TAG, "Registered with GCM: " + regID);

					// Store the registered app version and registration ID
					sharedPreferences.edit()
							.putInt(PREF_VERSION, packageInfo.versionCode)
							.putString(PREF_REGID, regID).commit();

					result = new Intent(ACTION_REGISTRATION_SUCCESS);
				}
			}
		} catch (NameNotFoundException e) {
			// Can't really recover from this, but shouldn't ever happen

			e.printStackTrace();
		}

		LocalBroadcastManager.getInstance(this).sendBroadcast(result);
	}

}
