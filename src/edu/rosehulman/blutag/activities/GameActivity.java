package edu.rosehulman.blutag.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.adapters.HolderAdapter;
import edu.rosehulman.blutag.adapters.PlayerHolder;
import edu.rosehulman.blutag.service.data.Player;

public class GameActivity extends Activity implements OnClickListener {
	private HolderAdapter<Player> players;
	private AlertDialog leaveDialog;
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		players = new HolderAdapter<Player>(this, R.layout.item_player,
				new PlayerHolder.Factory());
		
		ListView playersListView = (ListView) findViewById(android.R.id.list);
		playersListView.setAdapter(players);

		leaveDialog = new AlertDialog.Builder(this)
				.setMessage(R.string.dialog_game_leave_message)
				.setPositiveButton(R.string.dialog_game_leave_button, this)
				.setNegativeButton(android.R.string.cancel, null).create();

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game, menu);
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.menu_game_join) {
			// TODO Implement game joining
			menu.clear();
			
			return true;
		} else if (id == android.R.id.home) {
			finish();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == leaveDialog) {
			super.finish();
		}
	}

	@Override
	public void finish() {
		leaveDialog.show();
	}
}
