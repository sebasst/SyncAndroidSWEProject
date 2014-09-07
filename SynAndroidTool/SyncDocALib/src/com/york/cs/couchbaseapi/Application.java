package com.york.cs.couchbaseapi;



import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;


/**
 * 
 * Base class to maintain global application state. You can provide your own
 * implementation by specifying its name in your AndroidManifest.xml's
 * <application> tag, which will cause that class to be instantiated for you
 * when the process for your application/package is created.
 * 
 * 
 * @author sebas
 * 
 */
public abstract class Application  extends android.app.Application {

	public static final String TAG = "Application";

	public static  String SERVER_URL;// = "http://10.0.3.2:8080/SyncService";
	public static  String GATEWAY_URL;//  = "http://10.0.3.2:4985";
	public static  String ROOT_PACKAGE;//  = "com.york.cs";
	//public static  String APP_NAME;//  = "todolite2";
	// local database name
	public static  String DATABASE_NAME;// = "todos";
	// database bucket on server
	public static  String DATABASE_BUCKET;// = "todos";
	
	public static  boolean CLEAR_CHACHE_AFTER_SYNC=true;// = "todos";
	

	// database in app
	

	public static  String ACCOUNT_TYPE;// = ROOT_PACKAGE + "." + APP_NAME;// "com.york.cs.todolite2";
	public static  String CONTENT_AUTHORITY;// = ROOT_PACKAGE + "."	+ APP_NAME;;

	public static  String SERVER_URI_USER;// = SERVER_URL + "/User";
	public static  String KEY_TOKEN_TYPE;// = "AccountAuth.TOKEN_TYPE";
	public static  String SYNC_URL;// = GATEWAY_URL + "/" + DATABASE_BUCKET			+ " ";

	
	
	// Couchlite database manager
	protected Manager manager;

	// Couchlite database
	protected Database database;

	protected static Context mContext;

	public enum AuthenticationType {
		FACEBOOK, CUSTOM_COOKIE
	}

	protected AuthenticationType authenticationType = AuthenticationType.CUSTOM_COOKIE;

	/*
	public TodoDB getTodoDB() {

		return this.todoDB;
	}
*/
	public String getAccountType() {
		return ACCOUNT_TYPE;
	}


	
	@Override
	public void onCreate() {

		/*
		 * Called when the application is starting, before any activity,
		 * service, or receiver objects (excluding content providers) have been
		 * created. Implementations should be as quick as possible (for example
		 * using lazy initialization of state) since the time spent in this
		 * function directly impacts the performance of starting the first
		 * activity, service, or receiver in a process. If you override this
		 * method, be sure to call super.onCreate().
		 */
		super.onCreate();

		

		//initDatabase();

		android.util.Log.d(TAG, SERVER_URL);

		// FOR TESTING
		// setCurrentUserId(null);
		// setCurrentListId(null);

	}

	/**
	 * @return database instance of couchlite
	 * @throws CouchbaseLiteException
	 */
	public Database getDatabase() {

		if (database == null) {
			initDatabase();
			System.out.println("<><><><><><><><><><<><><><><><><><");
		}

		return this.database;
	}

	/**
	 * @return authenticationType
	 */
	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	/**
	 * Initiates database manager and database
	 */
	protected abstract void initDatabase();
	
	public static Context getContext() {
		return mContext;
	}

}
