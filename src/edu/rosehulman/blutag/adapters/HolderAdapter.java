package edu.rosehulman.blutag.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class HolderAdapter<T> extends ArrayAdapter<T> {
	private Factory<T> holderFactory;
	private int resource;

	public HolderAdapter(Context context, int resource, Factory<T> holderFactory) {
		super(context, resource);
		this.holderFactory = holderFactory;
		this.resource = resource;
	}

	public HolderAdapter(Context context, int resource, int textViewResourceId,
			List<T> objects, Factory<T> holderFactory) {
		super(context, resource, textViewResourceId, objects);
		this.holderFactory = holderFactory;
		this.resource = resource;
	}

	public HolderAdapter(Context context, int resource, int textViewResourceId,
			T[] objects, Factory<T> holderFactory) {
		super(context, resource, textViewResourceId, objects);
		this.holderFactory = holderFactory;
		this.resource = resource;
	}

	public HolderAdapter(Context context, int resource, int textViewResourceId,
			Factory<T> holderFactory) {
		super(context, resource, textViewResourceId);
		this.holderFactory = holderFactory;
		this.resource = resource;
	}

	public HolderAdapter(Context context, int resource, List<T> objects,
			Factory<T> holderFactory) {
		super(context, resource, objects);
		this.holderFactory = holderFactory;
		this.resource = resource;
	}

	public HolderAdapter(Context context, int resource, T[] objects,
			Factory<T> holderFactory) {
		super(context, resource, objects);
		this.holderFactory = holderFactory;
		this.resource = resource;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder<T> holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);

			holder = holderFactory.create(convertView);

			convertView.setTag(holder);
		} else {
			holder = (Holder<T>) convertView.getTag();
		}

		holder.render(getItem(position));

		return convertView;
	}

	public static abstract class Holder<T> {
		public abstract void render(T item);
	}

	public static interface Factory<T> {
		public Holder<T> create(View view);
	}
}
