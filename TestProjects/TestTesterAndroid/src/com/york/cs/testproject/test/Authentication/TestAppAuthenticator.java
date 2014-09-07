package com.york.cs.testproject.test.Authentication;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;

import com.york.cs.services.authentication.AccountGeneral;
import com.york.cs.services.authentication.AppServer;
import com.york.cs.services.authentication.AuthenticationExceptionServer;
import com.york.cs.services.authentication.ServerAuthenticate;
import com.york.cs.services.authentication.User;

public class TestAppAuthenticator extends TestCase {

	public static final ServerAuthenticate sServerAuthenticate = new AppServer();
	User user;

	@Override
	protected void setUp() throws Exception {
		user = new User();

		user.setId("usertestFromApp");
		user.setName("usertestFromApp Name");
		user.setEmail("usertestFromApp@email");
		user.setPassword("usertestFromApppassword");
	}

	
	public void testAuthenticationIncorrectInformation()  {

		try {
			sServerAuthenticate.userSignUp(null, null, user.getPassword(),
					AccountGeneral.ACCOUNT_TYPE);
		
		} catch (AuthenticationExceptionServer e) {
			
			assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatus());
			
		} 
	}
	
	public void testSignUpNewUser() throws Exception  {

		
			sServerAuthenticate.userSignUp(getName(), user.getEmail(), user.getPassword(),
					user.getDefaultAccountType());
		
		 
			User userTemp=sServerAuthenticate.userSignIn(user.getEmail(), user.getPassword(), user.getDefaultAccountType());
			
			assertNotNull(userTemp.getSessionToken());
		
			
		
	}
	

	
	public static final String SERVER_URI_SYNC_ADMIN = "http://localhost:4985";
	public static final String SERVER_URI_SYNC_NOADMIN = "http://localhost:4984";
	public static final String SYNC_DATABASE_NAME = "todos";

	// public static final String SERVER_URI_SYNC =
	// "http://localhost:4985/todos/_session";
	public static final String SERVER_URI_SYNC_SESSION = SERVER_URI_SYNC_ADMIN
			+ "/" + SYNC_DATABASE_NAME + "/_session";
	public static final String SERVER_URI_SYNC_USER = SERVER_URI_SYNC_ADMIN
			+ "/" + SYNC_DATABASE_NAME + "/_user";

	public static final String SERVER_URI_SYNC_NOADMIN_DB = SERVER_URI_SYNC_NOADMIN
			+ "/" + SYNC_DATABASE_NAME;
}
