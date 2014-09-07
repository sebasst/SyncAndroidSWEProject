

package com.york.cs.swe.todolite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.auth.BasicAuthenticator;
import com.couchbase.lite.auth.FacebookAuthorizer;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;

import org.apache.http.client.HttpResponseException;
import com.york.cs.swe.authentication.UserManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Observable;

public class Application extends android.app.Application {

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
	
	
	public static  String ACCOUNT_TYPE;// = ROOT_PACKAGE + "." + APP_NAME;// "com.york.cs.todolite2";
	public static  String CONTENT_AUTHORITY;// = ROOT_PACKAGE + "."	+ APP_NAME;;

	public static  String SERVER_URI_USER;// = SERVER_URL + "/User";
	public static  String KEY_TOKEN_TYPE;// = "AccountAuth.TOKEN_TYPE";
	public static  String SYNC_URL;// = GATEWAY_URL + "/" + DATABASE_BUCKET			+ " ";
	


	private static final String PREF_CURRENT_LIST_ID = "CurrentListId";
	private static final String PREF_CURRENT_USER_ID = "CurrentUserId";

	private Manager manager;
	private Database database;

	private int syncCompletedChangedCount;
	private int syncTotalChangedCount;
	private OnSyncProgressChangeObservable onSyncProgressChangeObservable;
	private OnSyncUnauthorizedObservable onSyncUnauthorizedObservable;
	
	protected static Context mContext;

	public enum AuthenticationType {
		FACEBOOK, CUSTOM_COOKIE
	}

	// By default, this should be set to FACEBOOK. To test "custom cookie" auth,
	// set this to CUSTOM_COOKIE.
	private AuthenticationType authenticationType = AuthenticationType.CUSTOM_COOKIE;

	private void initDatabase() {
		try {
			Manager.enableLogging(Log.TAG, Log.VERBOSE);
			Manager.enableLogging(Log.TAG_SYNC, Log.DEBUG);
			Manager.enableLogging(Log.TAG_QUERY, Log.DEBUG);
			Manager.enableLogging(Log.TAG_VIEW, Log.DEBUG);
			Manager.enableLogging(Log.TAG_DATABASE, Log.DEBUG);

			manager = new Manager(new AndroidContext(getApplicationContext()),
					Manager.DEFAULT_OPTIONS);
		} catch (IOException e) {
			Log.e(TAG, "Cannot create Manager object", e);
			return;
		}

		try {
			database = manager.getDatabase(DATABASE_NAME);
		} catch (CouchbaseLiteException e) {
			Log.e(TAG, "Cannot get Database", e);
			return;
		}
	}

	private void initObservable() {
		onSyncProgressChangeObservable = new OnSyncProgressChangeObservable();
		onSyncUnauthorizedObservable = new OnSyncUnauthorizedObservable();
	}

	private synchronized void updateSyncProgress(int completedCount,
			int totalCount) {
		onSyncProgressChangeObservable
				.notifyChanges(completedCount, totalCount);
	}

	public void startReplicationSyncWithCustomCookie(String name, String value,
			String path, Date expirationDate, boolean secure, boolean httpOnly) {

		Replication[] replications = createReplications();
		Replication pullRep = replications[0];
		Replication pushRep = replications[1];

		pullRep.setCookie(name, value, path, expirationDate, secure, httpOnly);
		pushRep.setCookie(name, value, path, expirationDate, secure, httpOnly);

		pullRep.start();
		pushRep.start();

		Log.v(TAG, "Start Replication Sync ...");

	}

	public void startReplicationSyncUser(String name, String value) {
		
		 URL syncUrl;
	        try {
	            syncUrl = new URL(SYNC_URL);
	        } catch (MalformedURLException e) {
	            throw new RuntimeException(e);
	        }

	        Authenticator authenticator = new BasicAuthenticator("sebas5","a1a2a3");
		
		 Replication pullReplication = database.createPullReplication(syncUrl);
	        pullReplication.setContinuous(true);
	        pullReplication.setAuthenticator(authenticator);

	        Replication pushReplication = database.createPushReplication(syncUrl);
	        pushReplication.setContinuous(true);
	        pushReplication.setAuthenticator(authenticator);

	        pullReplication.start();
	        pushReplication.start();

	     //   pullReplication.addChangeListener(this);
	     //   pushReplication.addChangeListener(this)
/*
		
		Log.v(TAG, "Start Replication Sync ...");
        Replication[] replications = createReplications();
        Replication pullRep = replications[0];
        Replication pushRep = replications[1];

        Authenticator authenticator= new BasicAuthenticator(name, value);//AuthenticatorFactory.createBasicAuthenticator(name, value);
        Log.v(TAG, "Start Replication Sync ... +"+name+value);
        
        pullRep.setAuthenticator(authenticator);
        pushRep.setAuthenticator(authenticator);

        pullRep.start();
        pushRep.start();

        Log.v(TAG, "Start Replication Sync ...");*/

    }

	public void startReplicationSyncWithStoredCustomCookie() {

		Replication[] replications = createReplications();
		Replication pullRep = replications[0];
		Replication pushRep = replications[1];

		pullRep.start();
		pushRep.start();

		Log.v(TAG, "Start Replication Sync ...");

	}

	public void startReplicationSyncWithFacebookLogin(String accessToken,
			String email) {

		Authenticator facebookAuthenticator = AuthenticatorFactory
				.createFacebookAuthenticator(accessToken);

		Replication[] replications = createReplications();
		Replication pullRep = replications[0];
		Replication pushRep = replications[1];

		pullRep.setAuthenticator(facebookAuthenticator);
		pushRep.setAuthenticator(facebookAuthenticator);

		pullRep.start();
		pushRep.start();

		Log.v(TAG, "Start Replication Sync ...");
	}

	public Replication[] createReplications() {

		URL syncUrl;
		try {
			syncUrl = new URL(SYNC_URL);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Invalid Sync Url", e);
			throw new RuntimeException(e);
		}

		Replication pullRep = database.createPullReplication(syncUrl);
		pullRep.setContinuous(true);
		pullRep.addChangeListener(getReplicationChangeListener());

		Replication pushRep = database.createPushReplication(syncUrl);
		pushRep.setContinuous(true);
		pushRep.addChangeListener(getReplicationChangeListener());

		return new Replication[] { pullRep, pushRep };

	}

	private Replication.ChangeListener getReplicationChangeListener() {
		return new Replication.ChangeListener() {
			@Override
			public void changed(Replication.ChangeEvent event) {
				Replication replication = event.getSource();
				if (replication.getLastError() != null) {
					Throwable lastError = replication.getLastError();
					if (lastError instanceof HttpResponseException) {
						HttpResponseException responseException = (HttpResponseException) lastError;
						if (responseException.getStatusCode() == 401) {
							onSyncUnauthorizedObservable.notifyChanges();
						}
					}
				}
				updateSyncProgress(replication.getCompletedChangesCount(),
						replication.getChangesCount());
			}
		};
	}

	@Override
	public void onCreate() {
		super.onCreate();

		
		
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
		ROOT_PACKAGE = "com.york.cs.todolite1";
		
		/* local database name, */
		DATABASE_NAME = "todo1";
	
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
		initObservable();
		
	}

	public Database getDatabase() {
		return this.database;
	}

	public String getCurrentListId() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		return sp.getString(PREF_CURRENT_LIST_ID, null);
	}

	public void setCurrentListId(String id) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (id != null) {
			sp.edit().putString(PREF_CURRENT_LIST_ID, id).apply();
		} else {
			sp.edit().remove(PREF_CURRENT_LIST_ID);
		}
	}

	public String getCurrentUserId() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		//String userId = sp.getString(PREF_CURRENT_USER_ID, null);
		String userId = UserManager.getCurrentUserId(getContext());
		
		return userId;
	}
/*
	public void setCurrentUserId(String id) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (id != null) {
			sp.edit().putString(PREF_CURRENT_USER_ID, id).apply();
		} else {
			sp.edit().remove(PREF_CURRENT_USER_ID).apply();
		}
	}
	*/

	public OnSyncProgressChangeObservable getOnSyncProgressChangeObservable() {
		return onSyncProgressChangeObservable;
	}

	public OnSyncUnauthorizedObservable getOnSyncUnauthorizedObservable() {
		return onSyncUnauthorizedObservable;
	}

	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	static class OnSyncProgressChangeObservable extends Observable {
		private void notifyChanges(int completedCount, int totalCount) {
			SyncProgress progress = new SyncProgress();
			progress.completedCount = completedCount;
			progress.totalCount = totalCount;
			setChanged();
			notifyObservers(progress);
		}
	}

	static class OnSyncUnauthorizedObservable extends Observable {
		private void notifyChanges() {
			setChanged();
			notifyObservers();
		}
	}

	static class SyncProgress {
		public int completedCount;
		public int totalCount;
	}

	public class Authenticar1 implements Authenticator {
	}

	public static Context getContext() {
		return mContext;
	}

	
}
