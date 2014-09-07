package com.york.cs.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.york.cs.server.SessionSync;
//import com.york.cs.server.User;
import com.york.cs.server.model.User;
import com.york.cs.server.services.AuthenticatorServiceImpl;
import com.york.cs.server.services.ValidateCredentialSyncG;

public class AuthenticationTest {

	public static final String SERVER_URI = "http://localhost:8080/SyncService";
	public static final String SERVER_USER = "http://localhost:8080/SyncService/User";
	
	static List<String> listCreatedUsers;

	@BeforeClass
	public static void createTestUser() {
		listCreatedUsers = new ArrayList<String>();

	}



	@Test
	public void Authenticationtests() {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setId("test1Name4433");
		user.setName("test1Name4433");

		user.setEmail("test1Name4433");

		user.setPassword("test1Password");

		ResponseEntity<SessionSync> response = restTemplate.postForEntity(
				SERVER_USER, user, SessionSync.class);
		HttpStatus statusR = response.getStatusCode();
		SessionSync session = response.getBody();

		// VALIDATE THAT SESSION WAS CREATED

		assertEquals(HttpStatus.OK, statusR);
		assertNotNull(session);
		assertNotNull(session.getSession_id());

		// VALIDATE THAT User exist from SyncGateway directly

		SessionSync session2 = null;

		User tempUser = new User();
		tempUser.setName(user.getId());

		session2 = restTemplate.postForObject(
				AuthenticatorServiceImpl.SERVER_URI_SYNC_SESSION, tempUser,
				SessionSync.class);
		assertNotNull(session2);
		assertNotNull(session2.getSession_id());

		try {
			assertTrue(ValidateCredentialSyncG.validateCredentials(

			user.getId(), user.getPassword()));
		} catch (Exception e) {

			e.printStackTrace();
			fail("Exception during validation");

		}

		// validata that can login same user

		User userT2 = new User();
		userT2.setId(user.getId());
		userT2.setPassword(user.getPassword());
		userT2.setEmail(user.getEmail());

		ResponseEntity<SessionSync> response2 = restTemplate.postForEntity(
				SERVER_USER, userT2, SessionSync.class);
		HttpStatus statusR2 = response2.getStatusCode();
		SessionSync session3 = response2.getBody();

		assertEquals(HttpStatus.OK, statusR2);
		assertNotNull(session3);
		assertNotNull(session3.getSession_id());
		
		
		
		//try to create a new user with same email and different password should fail
		
		
		user.setPassword("Changed password");
		
		ResponseEntity<SessionSync> response3=null;
		try {
			response3 = restTemplate.postForEntity(
					SERVER_USER, user, SessionSync.class);
			System.out.println("xxxxy"+response3.getStatusCode().name());
		} catch (HttpServerErrorException e) {
			
			
			
		} catch (HttpClientErrorException e) {
			System.out.println("xxxx"+e.getStatusCode());
			assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
				
		}
		// not good info for request
		
		user.setPassword(null);
		try {
			response3 = restTemplate.postForEntity(
					SERVER_USER, user, SessionSync.class);
			System.out.println("xxxxy"+response3.getStatusCode().name());
		} catch (HttpServerErrorException e) {
			
			
			
		} catch (HttpClientErrorException e) {
			System.out.println("xxxx"+e.getStatusCode());
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
				
		}
		
		
		
		//assertNotNull(session4.getSession_id());
		
		
		
		deleteUser(user.getEmail());
		
		
		

	}

	@Test
	public void testValidateUserCredentials() {

		User user = new User();
		user.setId("test8Email");
		user.setName("test8Email");

		user.setEmail("test8Email");

		user.setPassword("test5Password");
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.put(AuthenticatorServiceImpl.SERVER_URI_SYNC_USER + "/"
				+ user.getEmail(), user);

		try {
			assertTrue(ValidateCredentialSyncG.userExists(user.getEmail()));

			assertTrue(ValidateCredentialSyncG.validateCredentials(user.getEmail(),
					user.getPassword()));
			
			assertFalse(ValidateCredentialSyncG.validateCredentials(user.getEmail(),
					user.getPassword()+"xddddddddddddddddddd"));

			assertFalse(ValidateCredentialSyncG.validateCredentials(user.getEmail()+"x",
					user.getPassword()));

			
			deleteUser(user.getEmail());

			assertFalse(ValidateCredentialSyncG.userExists(user.getEmail()));
		} catch (Exception e) {

			e.printStackTrace();
			fail();
		}

	}

	private void deleteUser(String email) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete(AuthenticatorServiceImpl.SERVER_URI_SYNC_USER + "/"
				+ email);
	}

	private static void getCookie() {
		RestTemplate restTemplate = new RestTemplate();

		User user = new User();
		user.setName("sebas81");

		SessionSync session;
		try {
			session = restTemplate.postForObject(
					AuthenticatorServiceImpl.SERVER_URI_SYNC_SESSION, user,
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
		}
		/*
		 * restTemplate.put(SERVER_URI_SYNC_CREATE + "/" + user.getName(),
		 * user);
		 * 
		 * session = restTemplate.postForObject(SERVER_URI_SYNC_SESSION, user,
		 * SessionSync.class);
		 * 
		 * }
		 * 
		 * System.out.println(session.toString());
		 */
	}

	public static void createDB(String dbName) {
		/*
		 * String confiString = "{\"" + dbName + "\":{\"server\":\"" +
		 * SERVER_URI_COUCHBASE + "\",\"bucket\":\"" + dbName + "\"}}";
		 * 
		 * RestTemplate restTemplate = new RestTemplate();
		 * 
		 * restTemplate.put(SERVER_URI_ADMIN_API+"/"+dbName, confiString);
		 */

	}

	/**
	 * Delete the database in the server
	 * 
	 * @param dbName
	 */
	private static void deleteDB(String dbName) {
		String url = AuthenticatorServiceImpl.SERVER_URI_SYNC_ADMIN + "/"
				+ dbName + "/";
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

}
