package com.york.cs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.omg.CORBA.RepositoryIdHelper;
import org.springframework.http.HttpStatus;

public class ValidateCredentialsSyncG {

	public static void main(String[] args) {

		String reponse = null;
		String url = "http://localhost:4984/todos";
		String urlserver = "http://localhost:4985/todos";
		String urlSBad = "http://andy01:a1a2a3@localhost:4984/todos";

		// boolean result = validateCredentials(url, "andy01", "a1a2a4");

		boolean result;
		try {
			result = validateCredentials("http://localhost:4984/todos", "test8Email", "test5Passwordxx");
			 System.out.println("result: " + result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// method2(urlSGood);

		/*
		 * RestTemplate restTemplate = new RestTemplate();
		 * ResponseEntity<String> result=restTemplate.exchange(urlSGood,
		 * HttpMethod.GET, null, String.class);
		 * 
		 * HttpStatus statusCode = null;
		 * 
		 * statusCode= result.getStatusCode();
		 * 
		 * System.out.println("statusCode: "+statusCode);
		 */

	}

	private static void method2(String some_url) throws IOException {

		URL url = new URL(some_url);
		URLConnection connection = url.openConnection();

		connection.connect();

		// Cast to a HttpURLConnection
		if (connection instanceof HttpURLConnection) {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			int code = httpConnection.getResponseCode();

			System.out.println("code: " + code);
		} else {
			System.err.println("error - not a http request!");
		}

	}

	public static boolean userExists(String serverURL, String accountId) throws Exception {
		URL url;
//_user/andy02
		try {
			url = new URL(serverURL+"/_user/"+accountId);

			HttpURLConnection connection;

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpStatus.OK.value()) {
				System.out.println("user exists");
				return true;
			} else if (responseCode == HttpStatus.NOT_FOUND.value()) {
				System.out.println("user not found");
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

	private static boolean validateCredentials(String serverURL,
			String accountId, String password) {

		URL url;
		try {
			url = new URL(serverURL);

			String authString = accountId + ":" + password;

			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			HttpURLConnection connection;

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

				return false;
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		} catch (ProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	private static void method4() {
		try {
			String webPage = "http://localhost:4984/todos";
			String name = "andy01";
			String password = "a1a2a3";

			String authString = name + ":" + password;
			System.out.println("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			System.out.println("Base64 encoded auth string: " + authStringEnc);

			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic "
					+ authStringEnc);
			InputStream is = urlConnection.getInputStream();

			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();

			System.out.println("*** BEGIN ***");
			System.out.println(result);
			System.out.println("*** END ***");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void method5() {
		try {
			String webPage = "http://localhost:4984/todos";
			String name = "andy01";
			String password = "a1a2a3";

			String authString = name + ":" + password;
			System.out.println("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			System.out.println("Base64 encoded auth string: " + authStringEnc);

			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic "
					+ authStringEnc);
			InputStream is = urlConnection.getInputStream();

			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();

			System.out.println("*** BEGIN ***");
			System.out.println(result);
			System.out.println("*** END ***");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
