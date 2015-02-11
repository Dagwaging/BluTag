package edu.rosehulman.blutag.service.rest;

import java.io.IOException;
import java.util.Date;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DateTypeAdapter extends TypeAdapter<Date> {

	@Override
	public Date read(JsonReader reader) throws IOException {
		return new Date(reader.nextLong());
	}

	@Override
	public void write(JsonWriter writer, Date date) throws IOException {
		writer.value(date.getTime());
	}

}
