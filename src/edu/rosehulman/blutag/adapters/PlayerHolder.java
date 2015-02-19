package edu.rosehulman.blutag.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import edu.rosehulman.blutag.R;
import edu.rosehulman.blutag.service.data.Player;
import edu.rosehulman.blutag.service.rest.BluTagClient;
import edu.rosehulman.blutag.views.CircularImageView;

public class PlayerHolder extends HolderAdapter.Holder<Player> {
	private TextView name;
	private CircularImageView image;
	private Context context;

	public PlayerHolder(View convertView) {
		context = convertView.getContext();
		
		name = (TextView) convertView.findViewById(R.id.item_player_name);
		image = (CircularImageView) convertView.findViewById(R.id.item_player_image);
		image.setDefaultImageResId(R.drawable.ic_action_add_person);
	}

	@Override
	public void render(Player item) {
		name.setText(item.getName());
		image.setImageUrl(item.image, BluTagClient.getInstance(context).getImageLoader());
	}

	public static class Factory implements HolderAdapter.Factory<Player> {
		@Override
		public PlayerHolder create(View view) {
			return new PlayerHolder(view);
		}
	}
}
