package com.york.cs.server.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.york.cs.server.controllers.AuthenticationController;
import com.york.cs.server.exception.IncorrectInformation;
import com.york.cs.server.exception.NotAuthorizedException;
import com.york.cs.server.exception.UserConflict;
import com.york.cs.server.model.SessionSync;
import com.york.cs.server.model.User;

public class AuthenticatorServiceImpl implements AuthenticatorService {

	public AuthenticatorServiceImpl() {
		super();

	}

	private static final Logger logger = LoggerFactory
			.getLogger(AuthenticationController.class);
	// final CouchbaseClient client = ConnectionManager.getInstance();
	final Gson gson = new Gson();

	public static final String SERVER_URI_SYNC_ADMIN = "http://localhost:4985";
	public static final String SERVER_URI_SYNC_NOADMIN = "http://localhost:4984";
	public static final String SYNC_DATABASE_NAME = "todo1";

	// public static final String SERVER_URI_SYNC =
	// "http://localhost:4985/todos/_session";
	public static final String SERVER_URI_SYNC_SESSION = SERVER_URI_SYNC_ADMIN
			+ "/" + SYNC_DATABASE_NAME + "/_session";
	public static final String SERVER_URI_SYNC_USER = SERVER_URI_SYNC_ADMIN
			+ "/" + SYNC_DATABASE_NAME + "/_user";

	public static final String SERVER_URI_SYNC_NOADMIN_DB = SERVER_URI_SYNC_NOADMIN
			+ "/" + SYNC_DATABASE_NAME;

	// public static final String SERVER_URI_SYNC_VALIDATE_USER =
	// "http://localhost:4985/todos/_session";

	@Override
	public SessionSync getCookie(User user) throws UserConflict, NotAuthorizedException, IncorrectInformation,Exception{
		logger.info("getcookie");

		RestTemplate restTemplate = new RestTemplate();
		SessionSync session = null;
		boolean existUser = false;

		if (user.getEmail() == null || user.getPassword() == null) {
			logger.info("getcookie invalid request");
			if (user.getId() == null)
				logger.info("getcookie invalid request user null");
			if (user.getPassword() == null)
				logger.info("getcookie invalid request password null");
			throw new IncorrectInformation("Not enough information provided");
		} else {
			// validate if exits
			logger.info("getcookie validate if exits");

			existUser = ValidateCredentialSyncG.userExists(user.getEmail());

			if (existUser) {
				
				// try to login 
				logger.info("getcookie user exits: " + user.getEmail());

				if (ValidateCredentialSyncG.validateCredentials(
						user.getEmail(), user.getPassword())) {
					logger.info("getcookie user valid password");
					// validated
					HttpStatus status;

					logger.info("1-username:" + user.getEmail());

					logger.info("1-username:2" + user.getEmail());
					ResponseEntity<String> response = restTemplate
							.getForEntity(
									SERVER_URI_SYNC_USER + "/" + user.getEmail(),
									String.class);
					logger.info("1-username3:" + user.getId() + "+"
							+ user.getName());
					status = response.getStatusCode();

					if (status.value() == HttpStatus.OK.value()) {

						User tempUser = new User();
						tempUser.setName(user.getEmail());

						String payload = "{\"name\":\"" + user.getEmail() + "\"}";

						logger.info("3" + payload);
						session = restTemplate.postForObject(
								SERVER_URI_SYNC_SESSION, tempUser,
								SessionSync.class);
						logger.info("4");
						logger.info("getcookie user old sessionid"
								+ session.getSession_id());

					} else {
						logger.error("user conflict0");
						logger.error("user conflict0"+status.name());
						throw new UserConflict(
								"User Already exists. Credentials not match");
					}

				}else{
					logger.error("user conflict");
					throw new UserConflict(
							"User Already exists. Credentials not match");
				}

			} else {
				// create user

				logger.info("getcookie create user : ");
				if (user.getEmail() != null && user.getPassword() != null && user.getName()!=null) {

					User userTemp = new User();

					userTemp.setEmail(user.getEmail());
					userTemp.setName(user.getEmail());
					userTemp.setPassword(user.getPassword());

					List<String> channels = new ArrayList<String>();

					channels.add("public");
					channels.add(user.getId());

					user.setAdmin_channels(channels);

					user.setName(user.getEmail());
					logger.info("getcookie create user1 : ");
					restTemplate.put(
							SERVER_URI_SYNC_USER + "/" + userTemp.getEmail(),
							user);

					session = restTemplate.postForObject(
							SERVER_URI_SYNC_SESSION, user, SessionSync.class);

				}else {
					throw new IncorrectInformation("Not enough information provided");
				}

			}

		}

		return session;

	}
}
