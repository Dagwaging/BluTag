package edu.rosehulman.blutag.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Comparable<Player>, Parcelable {
	public String address;
	
	public String firstName;
	
	public String lastName;
	
	public String image;
	
	public boolean left;
	
	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public int compareTo(Player another) {
		return firstName.compareTo(another.firstName);
	}

	public String getName() {
		return firstName;
	}

    protected Player(Parcel in) {
        address = in.readString();
        firstName = in.readString();
        lastName = in.readString();
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
        dest.writeString(firstName);
        dest.writeString(lastName);
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