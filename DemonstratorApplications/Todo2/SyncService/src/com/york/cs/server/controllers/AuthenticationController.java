package com.york.cs.server.controllers;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.york.cs.server.exception.IncorrectInformation;
import com.york.cs.server.exception.NotAuthorizedException;
import com.york.cs.server.exception.UserConflict;
import com.york.cs.server.model.SessionSync;
import com.york.cs.server.model.User;
import com.york.cs.server.services.AuthenticatorServiceImpl;

/**
 * Handles requests for the authentication service.
 */
@Controller
public class AuthenticationController {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthenticationController.class);

	/**
	 * @param user
	 *            for login only userid and password are needed
	 * @return session object with session id which is needed t authenticate
	 * @throws Exception 
	 */
	@RequestMapping(value = "/User", method = RequestMethod.POST)
	public @ResponseBody SessionSync getUserCookie(@RequestBody User user) throws Exception {
		logger.info("0" + "user request");
		user.print();

		AuthenticatorServiceImpl authenticatorServiceImpl = new AuthenticatorServiceImpl();

		SessionSync session;

			session = authenticatorServiceImpl.getCookie(user);
			if (session == null) {
				logger.info("session nullxxxx");
				//throw new Exception();
				throw new Exception();	
			}


		return session;

	}
	
	
	
	@ExceptionHandler(UserConflict.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody ServerError userAlreadyExists(Exception e, HttpServletRequest req) {
		logger.info("user already exists");
		//return new SessionSync();
		return new ServerError(HttpStatus.CONFLICT.value(), e.getMessage());
	}
	
	@ExceptionHandler(NotAuthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ServerError handleBadCredentials(Exception e, HttpServletRequest req) {
		logger.info("UNAUTHORIZED");
		//return new SessionSync();
		return new ServerError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
	}
	
	@ExceptionHandler(IncorrectInformation.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ServerError handleBadRequest(Exception e, HttpServletRequest req) {
		logger.info("BAD_REQUEST");
		//return new SessionSync();
		return new ServerError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ServerError handleInternalError(Exception e, HttpServletRequest req) {
		logger.info("INTERNAL_SERVER_ERROR");
		e.printStackTrace();
		logger.info("INTERNAL_SERVER_ERROR end");
		
		
		return new ServerError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	    
	}
	

	
	public static class ServerError implements Serializable {

		private static final long serialVersionUID = 1L;
		public int code;
		public String error;
		public ServerError(int code, String error) {
			super();
			this.code = code;
			this.error = error;
		}
		
		
	}
}