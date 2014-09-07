package com.york.cs.server.services;

import com.york.cs.server.exception.IncorrectInformation;
import com.york.cs.server.exception.NotAuthorizedException;
import com.york.cs.server.exception.UserConflict;
import com.york.cs.server.model.SessionSync;
import com.york.cs.server.model.User;

public interface AuthenticatorService {
	

	 public SessionSync getCookie(User user) throws UserConflict, NotAuthorizedException, IncorrectInformation, Exception ;
}
