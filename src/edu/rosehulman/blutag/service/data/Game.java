package edu.rosehulman.blutag.service.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Comparable<Game>, Parcelable {
	public String _id;
	
	public String name;
	
	public int playerCount;
	
	public List<Player> players;

	public List<Tag> tags;
	
	public Game() {
	}
	
	@Override
	public String toString() {
		return _id;
	}

	@Override
	public int hashCode() {
		return _id.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null || !(other instanceof Game)) {
			return false;
		}
		
		return other.hashCode() == hashCode();
	}

	@Override
	public int compareTo(Game another) {
		return players.size() - another.players.size();
	}
	
	public Player getPlayer(String address) {
		for(Player player : players) {
			if(player.address.equals(address)) {
				return player;
			}
		}
		
		return null;
	}
	
	public List<Player> getActivePlayers() {
		ArrayList<Player> activePlayers = new ArrayList<Player>();
		
		for(Player player : players) {
			if(!player.left) {
				activePlayers.add(player);
			}
		}
		
		return activePlayers;
	}
	
	public Player getIt() {
		if(tags.size() == 0) {
			return null;
		}

		Tag it = tags.get(tags.size() - 1);
		
		return getPlayer(it.player);
	}

    protected Game(Parcel in) {
        _id = in.readString();
        name = in.readString();
        if (in.readByte() == 0x01) {
            players = new ArrayList<Player>();
            in.readList(players, Player.class.getClassLoader());
        } else {
            players = null;
        }
        if (in.readByte() == 0x01) {
            tags = new ArrayList<Tag>();
            in.readList(tags, Tag.class.getClassLoader());
        } else {
            tags = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        if (players == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(players);
        }
        if (tags == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tags);
        }
    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
}