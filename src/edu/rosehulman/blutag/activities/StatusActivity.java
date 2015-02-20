package edu.rosehulman.blutag.activities;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.service.BluTagService.GameListener;
import edu.rosehulman.blutag.service.data.Game;
import edu.rosehulman.blutag.service.data.Player;
import edu.rosehulman.blutag.service.data.Tag;

public class StatusActivity extends ServiceActivity implements GameListener {

	private Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);

		game = this.service.getGame();

		if (game != null) {
			this.service.registerGameListener(this);
			
			update();
		}
	}

	private void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		service.unregisterGameListener(this);

		super.onServiceDisconnected(name);
	}

	@Override
	public void onPlayerJoin(Player player) {
	}

	@Override
	public void onPlayerLeave(Player player) {
	}

	@Override
	public void onTag(Tag tag) {
		update();
	}

	@Override
	public void onGameDeleted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBluetoothDisabled() {
		// TODO Auto-generated method stub
		
	}
}
