package edu.rosehulman.blutag.service.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Comparable<Player>, Parcelable {
	public String address;
	
	public String pushId;
	
	public String givenName;
	
	public String familyName;
	
	public String image;
	
	public boolean left;
	
	public Player() {
	}
	
	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public int compareTo(Player another) {
		return givenName.compareTo(another.givenName);
	}

	public String getName() {
		return givenName;
	}

    protected Player(Parcel in) {
        address = in.readString();
        pushId = in.readString();
        givenName = in.readString();
        familyName = in.readString();
        image = in.readString();
        left = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(pushId);
        dest.writeString(givenName);
        dest.writeString(familyName);
        dest.writeString(image);
        dest.writeByte((byte) (left ? 0x01 : 0x00));
    }

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}