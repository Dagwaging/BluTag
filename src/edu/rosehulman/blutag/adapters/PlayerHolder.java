package edu.rosehulman.blutag.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.data.Player;

public class PlayerHolder extends HolderAdapter.Holder<Player> {
	private TextView name;
	private ImageView image;

	public PlayerHolder(View convertView) {
		name = (TextView) convertView.findViewById(R.id.item_player_name);
		image = (ImageView) convertView.findViewById(R.id.item_player_image);
	}

	@Override
	public void render(Player item) {
		name.setText(item.getName());
		// TODO: load player image using volley
	}

	public static class Factory implements HolderAdapter.Factory<Player> {
		@Override
		public PlayerHolder create(View view) {
			return new PlayerHolder(view);
		}
	}
}
