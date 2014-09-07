package com.york.cs.swe.authentication;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.york.cs.swe.todolite.Application;


/**
 * Class that provides service of sign up and login from the server app
 * 
 * @author sebas
 * 
 */
public class AppServer implements ServerAuthenticate {

	private static final String TAG = "AppServer";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.york.cs.services.authentication.ServerAuthenticate#userSignUp(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public User userSignUp(String name, String email, String pass,
			String authType) throws AuthenticationExceptionServer {

		Log.d(TAG, "userSignUp server1");

		String url = Application.SERVER_URI_USER;
		User user = new User();
		user.setId(email);
		user.setName(name);
		user.setEmail(email);
		user.setPassword(pass);
		Log.d(TAG, "userSignUp server2");
		user.print();

		Gson gson = new Gson();

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		Log.d(TAG, "userSignUp server3");
		for (Header header : getAppHeaders()) {
			httpPost.addHeader(header);
		}
		try {
			httpPost.addHeader("Content-Type", "application/json");

			Log.d(TAG, "userSignUp server4");
			String payload = gson.toJson(user);

			Log.d(TAG, "userSignUp server5");
			HttpEntity entity = new StringEntity(payload);

			httpPost.setEntity(entity);
			Log.d(TAG, "userSignUp server6");

			Log.d(TAG, "userSignUp server7");

			HttpResponse response;

			response = httpClient.execute(httpPost);

			Log.d(TAG, "SignUp sending");
			String responseString = EntityUtils.toString(response.getEntity());
			Log.d(TAG, "SignUp sent");
			if (response.getStatusLine().getStatusCode() != 200) {
				Log.d(TAG, "SignUp sent status "
						+ response.getStatusLine().getStatusCode());
				ServerError error = new Gson().fromJson(responseString,
						ServerError.class);
				Log.d(TAG, "Error accessing account Account");
				

				throw new AuthenticationExceptionServer(error.code, error.error);
			}

			Log.d(TAG, "SignUp received no error");
			SessionSync session = new Gson().fromJson(responseString,
					SessionSync.class);

			Log.d(TAG, "session id" + session.getSession_id());
			user.setSessionToken(session.getSession_id());

			return user;

		} catch (IOException e) {
			Log.d(TAG, "Error creating Account exception raised");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public User userSignIn(String email, String pass, String authType)
			throws AuthenticationExceptionServer {

		Log.d(TAG, "userSignIn");

		Log.d(TAG, "userSignin" + email + "" + pass);

		String url = Application.SERVER_URI_USER;
		User user = new User();
		user.setEmail(email);
		user.setPassword(pass);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		for (Header header : getAppHeaders()) {
			httpPost.addHeader(header);
		}
		httpPost.addHeader("Content-Type", "application/json");

		String payload = new Gson().toJson(user);

		HttpEntity entity;
		try {
			entity = new StringEntity(payload);

			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(httpPost);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() != 200) {
				ServerError error = new Gson().fromJson(responseString,
						ServerError.class);
				

				throw new AuthenticationExceptionServer(error.code, error.error);
			}

			SessionSync session = new Gson().fromJson(responseString,
					SessionSync.class);
			user.setSessionToken(session.getSession_id());

			return user;

		} catch (IOException e) {
			e.printStackTrace();
			throw new AuthenticationExceptionServer(0,
					"Authentication failed before connecting to server");
		}

		
	}

	public static class ServerError implements Serializable {

		private static final long serialVersionUID = 1L;
		public int code;
		public String error;
	}

	/**
	 * Return the basic headers to connect our app's details
	 * 
	 * @return
	 */
	public static List<Header> getAppHeaders() {
		List<Header> ret = new ArrayList<Header>();
		return ret;
	}
}
