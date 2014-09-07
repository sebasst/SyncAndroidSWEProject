package com.york.cs.server.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;

public class ValidateCredentialSyncG {

	public static boolean validateCredentials(String email, String password)
			throws Exception {

		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(AuthenticatorServiceImpl.SERVER_URI_SYNC_NOADMIN_DB);

			String authString = email + ":" + password;

			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic "
					+ authStringEnc);
			int responseCode = connection.getResponseCode();

			if (responseCode == HttpStatus.OK.value()) {
				System.out.println("ok");
				return true;
			} else if (responseCode == HttpStatus.UNAUTHORIZED.value()) {
				return false;
			} else {
				throw new Exception("Not possible to validate User");

			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			throw new Exception("Not possible to validate User");
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw new Exception("Not possible to validate User");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Not possible to validate User");
		} finally {
			connection.disconnect();
		}

	}

	public static boolean userExists(String email) throws Exception {
		URL url;
		try {
			url = new URL(AuthenticatorServiceImpl.SERVER_URI_SYNC_USER + "/"
					+ email);

			HttpURLConnection connection;

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpStatus.OK.value()) {

				return true;
			} else if (responseCode == HttpStatus.NOT_FOUND.value()) {

				return false;
			} else {

				throw new Exception("Not possible to validate User");
			}

		} catch (MalformedURLException e) {
			throw new Exception("Not possible to validate User");
		} catch (IOException e) {
			throw new Exception("Not possible to validate User");
		}

	}

}
