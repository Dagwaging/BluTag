package edu.rosehulman.blutag.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import edu.rosehulman.blutag.service.data.Player;
import edu.rosehulman.blutag.service.data.Tag;
import edu.rosehulman.blutag.service.rest.BluTagClient;

/**
 * @author dagwaging
 * 
 */
public class PushReceiver extends BroadcastReceiver {
	private static final String TAG = PushReceiver.class.getName();

	public static final String ACTION_TAG = "tag";

	public static final String ACTION_JOIN = "join";

	public static final String ACTION_LEAVE = "leave";

	public static final String ACTION_DELETE = "delete";

	public static final String EXTRA_DATA = "data";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "Received intent " + intent.getAction());

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

		if (!extras.isEmpty()) {
			if (gcm.getMessageType(intent).equals(
					GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
				LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
						.getInstance(context);

				Intent pushIntent = null;
				Gson gson = BluTagClient.getInstance(context).getGson();

				if (extras.containsKey("tag")) {
					String data = extras.getString("tag");
					Tag tag = gson.fromJson(data, Tag.class);

					pushIntent = new Intent(ACTION_TAG);
					pushIntent.putExtra(EXTRA_DATA, tag);
				}

				if (extras.containsKey("left")) {
					String data = extras.getString("left");
					Player player = gson.fromJson(data, Player.class);
					
					pushIntent = new Intent(ACTION_LEAVE);
					pushIntent.putExtra(EXTRA_DATA, player);
				}

				if (extras.containsKey("joined")) {
					String data = extras.getString("joined");
					Player player = gson.fromJson(data, Player.class);
					
					pushIntent = new Intent(ACTION_JOIN);
					pushIntent.putExtra(EXTRA_DATA, player);
				}
				
				if(extras.containsKey("deleted")) {
					pushIntent = new Intent(ACTION_DELETE);
				}

				if (pushIntent != null) {
					localBroadcastManager.sendBroadcast(pushIntent);
				}
			}
		}
	}
}
