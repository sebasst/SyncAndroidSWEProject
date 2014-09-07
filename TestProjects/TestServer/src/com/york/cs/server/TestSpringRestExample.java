package com.york.cs.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class TestSpringRestExample {

	public static final String SERVER_URI = "http://localhost:8080/SyncService";
	public static final String SERVER_URI_user = "http://localhost:8080/SyncService/User";

	public static final String SERVER_URI_SYNC_SESSION = "http://localhost:4985/grocery-sync/_session";
	public static final String SERVER_URI_SYNC_CREATE = "http://localhost:4985/grocery-sync/_user";
	public static final String SERVER_URI_ADMIN_API = "http://localhost:4985";
	public static final String SERVER_URI_COUCHBASE = "http://localhost:8091";

	public static void main(String args[]) {

		
		//createDB("grocery-sync2");
		
		deleteDB("Users");
		createDB("Users");
		System.out.println("*****");

	}

	private static void getCookieService() {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setId("sebas5");
		user.setName("1sebas8123");

		user.setEmail("sebas5");

		user.setPassword("a1a2a3");

		ResponseEntity<SessionSync> response = restTemplate.postForEntity(
				SERVER_URI_user, user, SessionSync.class);
		HttpStatus status = response.getStatusCode();
		SessionSync session = response.getBody();

		// SessionSync session = restTemplate.postForObject(SERVER_URI_user,
		// user,SessionSync.class);

		System.out.println(session.toString());

		System.out.println(status.getReasonPhrase() + "-" + status.value());

	}

	private static void getCookie() {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setName("sebas81");

		SessionSync session;
		try {
			session = restTemplate.postForObject(SERVER_URI_SYNC_SESSION, user,
					SessionSync.class);
			System.out.println(session.toString());
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode());

			user.setEmail("sebas81");

			user.setPassword("a1a2a3");

			List<String> channels = new ArrayList<String>();

			channels.add("public");
			channels.add(user.getName());

			user.setAdmin_channels(channels);

			restTemplate.put(SERVER_URI_SYNC_CREATE + "/" + user.getName(),
					user);

			session = restTemplate.postForObject(SERVER_URI_SYNC_SESSION, user,
					SessionSync.class);

		}

		System.out.println(session.toString());

	}

	public static void createDB(String dbName) {
		String confiString = "{\"" + dbName + "\":{\"server\":\""
				+ SERVER_URI_COUCHBASE + "\",\"bucket\":\"" + dbName + "\"}}";
		
		RestTemplate restTemplate = new RestTemplate();
		
		restTemplate.put(SERVER_URI_ADMIN_API+"/"+dbName, confiString);
		
		

	}

	/**
	 * Delete the database in the server
	 * 
	 * @param dbName
	 */
	private static void deleteDB(String dbName) {
		String url = SERVER_URI_ADMIN_API + "/" + dbName + "/";
		System.out.println("url: " + url);

		RestTemplate restTemplate = new RestTemplate();

		try {
			restTemplate.delete(url);
		} catch (RestClientException e1) {
			System.out.println("Db was not deleted in sync gateway");
			// e1.printStackTrace();
		}

		String urlcouchdb = "http://@localhost:8091/pools/default/buckets/"
				+ dbName;
		// flus database should be enabled
		/*
		 * String urlServerCouch =
		 * "http://localhost:8091/pools/default/buckets/grocery-sync/controller/doFlush"
		 * ;
		 * //"http://admin:a1a2a3@localhost:8091/pools/default/buckets/"+dbName+
		 * "/controller/doFlush"; try{ restTemplate.delete(urlServerCouch);
		 * }catch(RestClientException e){ e.printStackTrace(); }
		 */
		// RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response;

		HttpHeaders httpHeaders = createHeaders("user", "pass");

		response = restTemplate.exchange(urlcouchdb, HttpMethod.DELETE,
				new HttpEntity<Object>(httpHeaders), String.class);

	}

	private static HttpHeaders createHeaders(final String username,
			final String password) {
		HttpHeaders headers = new HttpHeaders() {
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset
						.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
		headers.add("Content-Type", "application/xml");
		headers.add("Accept", "application/xml");

		return headers;
	}

	private static void testCreateEmployee() {
		RestTemplate restTemplate = new RestTemplate();
		Employee emp = new Employee();
		emp.setId(1);
		emp.setName("Pankaj Kumar");
		Employee response = restTemplate.postForObject(SERVER_URI
				+ EmpRestURIConstants.CREATE_EMP, emp, Employee.class);
		printEmpData(response);
	}

	private static void testGetEmployee() {
		RestTemplate restTemplate = new RestTemplate();
		Employee emp = restTemplate.getForObject(SERVER_URI + "/rest/emp/1",
				Employee.class);
		printEmpData(emp);
	}

	private static void testGetDummyEmployee() {
		RestTemplate restTemplate = new RestTemplate();
		Employee emp = restTemplate.getForObject(SERVER_URI
				+ EmpRestURIConstants.DUMMY_EMP, Employee.class);
		printEmpData(emp);
	}

	public static void printEmpData(Employee emp) {
		System.out.println("ID=" + emp.getId() + ",Name=" + emp.getName()
				+ ",CreatedDate=" + emp.getCreatedDate());
	}
}