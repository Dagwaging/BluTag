package edu.rosehulman.blutag.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Loader;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import edu.rosehulman.blutag.service.BluTagService;
import edu.rosehulman.blutag.service.data.Game;

public class GamesLoader extends Loader<List<Game>> implements
		Listener<List<Game>>, ErrorListener {
	private static final String TAG = "edu.rosehulman.blutag.loaders.GamesLoader";

	private List<Game> data;

	public GamesLoader(Context context) {
		super(context);
	}

	@Override
	public void deliverResult(List<Game> data) {
		if (isReset()) {
			this.data = null;
			return;
		}

		if (isStarted()) {
			Log.d(TAG, "Loaded " + data);

			super.deliverResult(data);
		}
	}

	@Override
	protected void onStartLoading() {
		BluTagService.registerGamesListener(getContext(), this, this);

		Log.d(TAG, "Load started");

		if (takeContentChanged() || data == null) {
			forceLoad();
		}
	}

	@Override
	protected void onForceLoad() {
		if (data != null) {
			deliverResult(data);
		}
	}

	@Override
	protected void onReset() {
		onStopLoading();

		Log.d(TAG, "Load reset");

		data = null;

		BluTagService.unregisterGamesListener(getContext(), this);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(List<Game> response) {
		if (data == null) {
			data = response;
			onContentChanged();
		} else {
			if (!data.containsAll(response)) {
				data.addAll(response);
				data = new ArrayList<Game>(data);
				onContentChanged();
			}
		}
	}
}
