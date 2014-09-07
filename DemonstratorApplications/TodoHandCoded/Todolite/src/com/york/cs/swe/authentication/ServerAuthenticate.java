package com.york.cs.swe.authentication;




/**
 * Authenticator interface that should be implemented by the class that provides the authentication services for the app
 * @author sebas
 *
 */
public interface ServerAuthenticate {
    public User userSignUp(final String name, final String email, final String pass, String authType) throws AuthenticationExceptionServer;

    public User userSignIn(final String email, final String pass, String authType) throws AuthenticationExceptionServer;
}
