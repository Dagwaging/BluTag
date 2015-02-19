package edu.rosehulman.blutag.activities;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import edu.rosehulman.blutag.service.BluTagService;
import edu.rosehulman.blutag.service.ServiceBinder;

public class ServiceActivity extends Activity implements ServiceConnection {
	private static final String TAG = "edu.rosehulman.blutag.activities.ServiceActivity";

	protected BluTagService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent serviceIntent = new Intent(this, BluTagService.class);
		bindService(serviceIntent, this, Service.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unbindService(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		ServiceBinder<BluTagService> binder = (ServiceBinder<BluTagService>) service;
		
		this.service = binder.getService();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		this.service = null;
	}
}
