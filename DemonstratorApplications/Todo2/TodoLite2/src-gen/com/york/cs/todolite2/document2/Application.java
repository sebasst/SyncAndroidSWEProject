package com.york.cs.todolite2.document2;

import java.io.IOException;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Status;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;


// protected region custom-imports on begin
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
// protected region custom-imports end


public class Application  extends com.york.cs.couchbaseapi.Application {


	
	// protected region custom-methods on begin
	private static final String PREF_CURRENT_LIST_ID = "CurrentListId";

	public void setCurrentListId(String id) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (id != null) {

			sp.edit().putString(PREF_CURRENT_LIST_ID, id).apply();
		} else {

			sp.edit().remove(PREF_CURRENT_LIST_ID).apply();

		}
	}

	public String getCurrentListId() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		return sp.getString(PREF_CURRENT_LIST_ID, null);
	}

	// protected region custom-methods end
	
	
	private TodoDB todoDB;
	
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
		

		mContext = getApplicationContext();

		/************************************/
		/*	set here your app configuration	*/ 
		/*	url of the tomcat server where syncserver will be deployed	*/
		SERVER_URL = "http://10.0.3.2:8080/SyncService";
		/*	url of the tomcat server where syncGateway will be deployed	*/
		GATEWAY_URL = "http://10.0.3.2:4984";
		ROOT_PACKAGE = "com.york.cs.todolite2.document2";
		
		/* local database name, */
		DATABASE_NAME = "todos";
	
		/*clearing cache after each sync forces 
		 * force system to query all objects again after sync process
		 * in order to get fresh data, nevertheless user can
		 * see unxepeced changes of the object while he or she is
		 * working in the object. If cache is no cleaned after syn the user
		 * can still work with the object and the system will resolve the conflict*/
		CLEAR_CHACHE_AFTER_SYNC=true;
		
		authenticationType = AuthenticationType.CUSTOM_COOKIE;

		/*	if you want to change ACCOUNT_TYPE or CONTENT_AUTHORITY do not 
		*	forget to change configuration in xml file configuration and manifest file
		*/ 
		ACCOUNT_TYPE = ROOT_PACKAGE ;
		CONTENT_AUTHORITY = ROOT_PACKAGE ;

		SERVER_URI_USER = SERVER_URL + "/User";
		KEY_TOKEN_TYPE = "AccountAuth.TOKEN_TYPE";
		SYNC_URL = GATEWAY_URL + "/" + DATABASE_NAME + " ";

		initDatabase();

		android.util.Log.d(TAG, SERVER_URL);

		super.onCreate();
		
		// protected region call custom-methods on begin
		// protected region call custom-methods end

	}
	
	public TodoDB getTodoDB() {

		return todoDB;
	}

	public String getAccountType() {
		return ACCOUNT_TYPE;
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
	protected void initDatabase() {
		try {
			// log for database, in production environment should be disabled
			// Useful for development purposes
			Manager.enableLogging(Log.TAG, Log.VERBOSE);
			Manager.enableLogging(Log.TAG_SYNC, Log.DEBUG);
			Manager.enableLogging(Log.TAG_QUERY, Log.DEBUG);
			Manager.enableLogging(Log.TAG_VIEW, Log.DEBUG);
			Manager.enableLogging(Log.TAG_DATABASE, Log.DEBUG);

			// initialize manager with defaul options
			/*
			 * By default a Manager will open a Database with read/write access.
			 * If you want to ensure that data can not be modified you can
			 * restrict Database access to read only by passing a ManagerOptions
			 * object to the Manager constructor or initilaizer.
			 * 
			 * ManagerOptions customOptions = new ManagerOptions();
			 * customOptions.setReadOnly(true); manager = new Manager(new
			 * AndroidContext(mContext), customOptions);
			 */

			manager = new Manager(new AndroidContext(getApplicationContext()),
					Manager.DEFAULT_OPTIONS);

		} catch (IOException e) {

			Log.e(TAG, "Cannot create Manager object", e);
			return;
		}

		try {

			database = manager.getDatabase(DATABASE_NAME);

			if (database == null) {
				throw new CouchbaseLiteException("not initiated",
						Status.DB_ERROR);
			} else {

				todoDB = new TodoDB(database);

			}

		} catch (CouchbaseLiteException e) {

			e.printStackTrace();
			return;
		}

	}

	public static Context getContext() {
		return mContext;
	}
	

}