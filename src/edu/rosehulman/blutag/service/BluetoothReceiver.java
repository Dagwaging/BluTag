package edu.rosehulman.blutag.service;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {
	private static final short TAG_RSSI_THRESHOLD = -55;
	
	private DeviceFoundListener listener;
	
	public BluetoothReceiver(DeviceFoundListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		final Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
				(short) 0);
		
		
		String name = device.getName();
		String address = device.getAddress();
		Boolean near = rssi > TAG_RSSI_THRESHOLD;
		
		if(listener != null)
			listener.onDeviceFound(name, address, near);
	}

	public interface DeviceFoundListener {
		public void onDeviceFound(String name, String address, Boolean near);
	}

	public void setListener(DeviceFoundListener listener) {
		this.listener = listener;
	}
}
