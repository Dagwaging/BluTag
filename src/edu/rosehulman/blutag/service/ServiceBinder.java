package edu.rosehulman.blutag.service;

import android.app.Service;
import android.os.Binder;

public class ServiceBinder<T extends Service> extends Binder {
	private T service;

	public ServiceBinder(T service) {
		this.service = service;
	}

	public T getService() {
		return service;
	}
}
