package edu.rosehulman.blutag.service.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author dagwaging
 * 
 */
public class PushReceiver extends BroadcastReceiver {

	private static final String TAG = PushReceiver.class.getName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "Received intent " + intent.getAction());
		
		// TODO: Handle push messages here
	}

}
