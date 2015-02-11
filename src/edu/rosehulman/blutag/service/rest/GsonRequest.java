package edu.rosehulman.blutag.service.rest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GsonRequest<T> extends Request<T> {
	private Gson gson;
	private Type type;
	private final Map<String, String> headers;
	private final Listener<T> listener;
	private byte[] body = new byte[0];

	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param url
	 *            URL of the request to make
	 * @param clazz
	 *            Relevant class object, for Gson's reflection
	 * @param headers
	 *            Map of request headers
	 */
	public GsonRequest(Gson gson, int method, String url, Class<T> clazz,
			Map<String, String> headers, Listener<T> listener,
			ErrorListener errorListener) {
		this(gson, method, url, TypeToken.get(clazz).getType(), headers, listener, errorListener);
	}

	public GsonRequest(Gson gson, int method, String url, Type type,
			Map<String, String> headers, Listener<T> listener,
			ErrorListener errorListener) {
		super(method, url, errorListener);
		this.gson = gson;
		this.type = type;
		this.headers = headers;
		this.listener = listener;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}
	
	@Override
	public String getBodyContentType() {
		return "application/json";
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return body;
	}
	
	public void setBody(Object body) {
		this.body = gson.toJson(body).getBytes();
	}

	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));

			T data = gson.fromJson(json, type);

			return Response.success(data,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}