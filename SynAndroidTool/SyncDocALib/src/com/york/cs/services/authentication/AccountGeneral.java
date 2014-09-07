package com.york.cs.services.authentication;

import com.york.cs.couchbaseapi.Application;


/**
 * Defines General values for authentication constants
 * @author sebas
 *
 */
public class AccountGeneral {

	/**
	 * Account type id
	 */
	public static final String ACCOUNT_TYPE = Application.ACCOUNT_TYPE;

	
	// arguments for authentication activity
	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

	public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
	//public static final String KEY_ERROR_MESSAGE = "errorMessage";
	//public static final String KEY_ERROR_CODE = "errorCode";
	public final static String PARAM_USER_PASS = "USER_PASS";
	

	public final static int REQ_SIGNUP = 1;
	
	
	/**
	 * Account name
	 */
	//public static final String ACCOUNT_NAME = "Todo2";

	/**
	 * User data fields
	 */
	 //public static final String KEY_USERDATA = "userdata";
	public static final String USERDATA_USER_OBJ_ID = "userObjectId";
	public static final String USERDATA_USER_OBJ_NAME = "userObjectName";
	public static final String USERDATA_USER_OBJ_EMAIL = "userObjectEmail";
	public static final String USERDATA_USER_OBJ_PASSWORD = "userObjectPassword";
	//public static final String KEY_PASSWORD = "password";
	
	/**
	 * Auth token types
	 */
	
	//public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
	//public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an account";

	public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
	public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an  account";

	public static final ServerAuthenticate sServerAuthenticate = new AppServer();
}
