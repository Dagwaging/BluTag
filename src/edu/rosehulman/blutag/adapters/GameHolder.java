package edu.rosehulman.blutag.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.data.Game;

public class GameHolder extends HolderAdapter.Holder<Game> {
	private TextView players;
	private TextView name;
	private ImageView status;

	public GameHolder(View convertView) {
		name = (TextView) convertView.findViewById(R.id.item_game_name);
		players = (TextView) convertView.findViewById(R.id.item_game_players);
		status = (ImageView) convertView.findViewById(R.id.item_game_status);
	}

	@Override
	public void render(Game item) {
		name.setText(item.name);
		players.setText(item.players.size());
		status.setImageResource(item.tags.isEmpty() ? R.drawable.ic_action_stop
				: R.drawable.ic_action_play);
	}
	
	public static class Factory implements HolderAdapter.Factory<Game> {
		@Override
		public GameHolder create(View view) {
			return new GameHolder(view);
		}
	}
}
