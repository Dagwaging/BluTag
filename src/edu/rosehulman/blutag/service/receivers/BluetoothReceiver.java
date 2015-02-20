package edu.rosehulman.blutag.service.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class BluetoothReceiver extends BroadcastReceiver {
	private static final String TAG = "edu.rosehulman.blutag.service.receivers.BluetoothReceiver";

	private static final short TAG_RSSI_THRESHOLD = -75;

	private BluetoothListener listener;

	public BluetoothReceiver(BluetoothListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

			if (listener != null)
				listener.onStateChanged(state);

		} else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
			final BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			final Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
					(short) 0);

			String name = device.getName();
			String address = device.getAddress();
			Boolean near = rssi > TAG_RSSI_THRESHOLD;

			Log.d(TAG, "Bluetooth device found: " + address + " with RSSI " + rssi);

			if (listener != null)
				listener.onDeviceFound(name, address, near);
		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent
				.getAction())) {
			if (listener != null)
				listener.onDiscoveryFinished();
		} else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) && intent.getBooleanExtra((ConnectivityManager.EXTRA_NO_CONNECTIVITY), false)) {
			int state = BluetoothAdapter.STATE_OFF;

			if (listener != null)
				listener.onStateChanged(state);
		}
	}

	public void register(Context context) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

			context.registerReceiver(this, filter);
	}

	public void unregister(Context context) {
		try {
			context.unregisterReceiver(this);
		} catch(IllegalArgumentException e) {
			
		}
	}

	public interface BluetoothListener {
		public void onDeviceFound(String name, String address, Boolean near);

		public void onStateChanged(int state);

		public void onDiscoveryFinished();
	}

	public void setListener(BluetoothListener listener) {
		this.listener = listener;
	}
}
