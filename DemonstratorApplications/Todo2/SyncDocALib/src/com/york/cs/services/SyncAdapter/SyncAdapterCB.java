/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.york.cs.services.SyncAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.replicator.Replication;
import com.york.cs.couchbaseapi.Application;
import com.york.cs.couchbaseapi.ClassDBFactory;
import com.york.cs.services.authentication.AccountGeneral;

/**
 * 
 * /** Define a sync adapter for the app.
 * 
 * This class is instantiated in {@link SyncService}, which also binds
 * SyncAdapter to the system. SyncAdapter should only be initialized in
 * SyncService, never anywhere else.
 * 
 * <p>
 * The system calls onPerformSync() via an RPC call through the IBinder object
 * supplied by SyncService.
 * 
 * @author sebas
 * 
 */
public class SyncAdapterCB extends AbstractThreadedSyncAdapter {

	private static final String TAG = "SyncAdapterCB";

	private final AccountManager mAccountManager;
	private OnSyncProgressChangeObservable onSyncProgressChangeObservable;
	private OnSyncUnauthorizedObservable onSyncUnauthorizedObservable;

	// Global variables
	// Define a variable to contain a content resolver instance
	ContentResolver mContentResolver;

	/**
	 * Constructor. Obtains handle to content resolver for later use.
	 */

	public SyncAdapterCB(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
		mContentResolver = context.getContentResolver();
	}

	public SyncAdapterCB(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		mAccountManager = AccountManager.get(context);
		mContentResolver = context.getContentResolver();
	}

	/**
	 * Called by the Android system in response to a request to run the sync
	 * adapter. The work required to read data from the network, parse it, and
	 * store it done here. Extending AbstractThreadedSyncAdapter ensures that
	 * all methods within SyncAdapter run on a background thread. For this
	 * reason, blocking I/O and other long-running tasks can be run
	 * <em>in situ</em>, and you don't have to set up a separate thread for
	 * them. .
	 * 
	 * <p>
	 * This is where we actually perform any work required to perform a sync.
	 * {@link AbstractThreadedSyncAdapter} guarantees that this will be called
	 * on a non-UI thread, so it is safe to peform blocking I/O here.
	 * 
	 * <p>
	 * The syncResult argument allows you to pass information back to the method
	 * that triggered the sync.
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.d(TAG, "onPerformSync for account");

		// Building a print of the extras we got
		StringBuilder sb = new StringBuilder();
		if (extras != null) {
			for (String key : extras.keySet()) {
				sb.append(key + "[" + extras.get(key) + "] ");
			}
		}

		Log.d(TAG, "onPerformSync for account[" + account.name + "]. Extras: "
				+ sb.toString());

		try {
			// Get the auth token for the current account and
			// the userObjectId, needed for creating items on Parse.com account
			String authToken = mAccountManager.blockingGetAuthToken(account,
					AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
			String userObjectId = mAccountManager.getUserData(account,
					AccountGeneral.USERDATA_USER_OBJ_ID);

			Log.d(TAG, "start sunc" + authToken + "-" + userObjectId);

			Application application = (Application) getContext();

			Database database = application.getDatabase();
			initObservable();
			startSyncWithCustomCookie(database, authToken);

		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (IOException e) {
			syncResult.stats.numIoExceptions++;
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			syncResult.stats.numAuthExceptions++;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startSyncWithCustomCookie(Database database, String cookieVal) {

		String cookieName = "SyncGatewaySession";
		boolean isSecure = false;
		boolean httpOnly = false;

		// expiration date - 1 day from now
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int numDaysToAdd = 1;
		cal.add(Calendar.DATE, numDaysToAdd);
		Date expirationDate = cal.getTime();

		startReplicationSyncWithCustomCookie(database, cookieName, cookieVal,
				"/", expirationDate, isSecure, httpOnly);

	}

	public void startReplicationSyncWithCustomCookie(Database database,
			String name, String value, String path, Date expirationDate,
			boolean secure, boolean httpOnly) {

		Replication[] replications = createReplications(database);
		Replication pullRep = replications[0];
		Replication pushRep = replications[1];

		pullRep.setCookie(name, value, path, expirationDate, secure, httpOnly);
		pushRep.setCookie(name, value, path, expirationDate, secure, httpOnly);

		pullRep.start();
		pushRep.start();

		Log.v(TAG, "Finished Replication Sync ...");

		if (Application.CLEAR_CHACHE_AFTER_SYNC) {
			ClassDBFactory classDBFactory = ClassDBFactory.getInstance();

			classDBFactory.clear();
		}

	}

	public Replication[] createReplications(Database database) {

		// A Couchbase Lite pull or push Replication between a local and a
		// remote Database.

		URL syncUrl;
		try {
			syncUrl = new URL(Application.SYNC_URL);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Invalid Sync Url", e);
			throw new RuntimeException(e);
		}

		Replication pullRep = database.createPullReplication(syncUrl);

		/*
		 * A Replication object represents a replication (or "sync") task that
		 * transfers changes between a local database and a remote one. To
		 * replicate, you first get a new Replication object from a Database,
		 * then configure its settings, then tell it to start. The actual
		 * replication runs asynchronously on a background thread; you can
		 * monitor its progress by observing notifications posted by the
		 * Replication object when its state changes, as well as notifications
		 * posted by the database when documents are changed by the replicator.
		 * 
		 * A typical application will create a pair of replications (push and
		 * pull) at launch time, both pointing to the URL of a server run by the
		 * application vendor. These stay active continuously during the
		 * lifespan of the app, uploading and downloading documents as changes
		 * occur and when the network is available.
		 */

		/*
		 * One-shot vs Continuous: By default a replication runs long enough to
		 * transfer all the changes from the source to the target database, then
		 * quits. A continuous replication, on the other hand, will stay active
		 * indefinitely, watching for further changes to occur and transferring
		 * them.
		 */
		pullRep.setContinuous(false);
		pullRep.addChangeListener(getReplicationChangeListener());

		Replication pushRep = database.createPushReplication(syncUrl);
		pushRep.setContinuous(false);
		pushRep.addChangeListener(getReplicationChangeListener());

		return new Replication[] { pullRep, pushRep };

	}

	private void initObservable() {
		onSyncProgressChangeObservable = new OnSyncProgressChangeObservable();
		onSyncUnauthorizedObservable = new OnSyncUnauthorizedObservable();
	}

	/**
	 * method used to update sync progress
	 * 
	 * @param completedCount
	 *            progress
	 * @param totalCount
	 *            total count to be reached
	 */
	private void updateSyncProgress(int completedCount, int totalCount) {
		onSyncProgressChangeObservable
				.notifyChanges(completedCount, totalCount);
	}

	/**
	 * 
	 * @return A delegate that can be used to listen for Replication changes
	 */
	private Replication.ChangeListener getReplicationChangeListener() {
		// A delegate that can be used to listen for Replication changes
		return new Replication.ChangeListener() {

			@Override
			public void changed(Replication.ChangeEvent event) {
				// The type of event raised by a Replication when any of the
				// following properties change: mode, running, error, completed,
				// total.
				Replication replication = event.getSource();
				if (replication.getLastError() != null) {
					Throwable lastError = replication.getLastError();
					if (lastError instanceof HttpResponseException) {
						HttpResponseException responseException = (HttpResponseException) lastError;
						if (responseException.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
							onSyncUnauthorizedObservable.notifyChanges();
						}
					}
				}

				updateSyncProgress(replication.getCompletedChangesCount(),
						replication.getChangesCount());

			}
		};
	}

	public OnSyncProgressChangeObservable getOnSyncProgressChangeObservable() {
		return onSyncProgressChangeObservable;
	}

	public OnSyncUnauthorizedObservable getOnSyncUnauthorizedObservable() {
		return onSyncUnauthorizedObservable;
	}

	/**
	 * Observable is used to notify a group of Observer objects when a change
	 * occurs. This class represents an observable object, or "data" in the
	 * model-view paradigm. It represents an object that the application wants
	 * to have observed, sync progress in this case. An observable object can
	 * have one or more observers. An observer may be any object that implements
	 * interface Observer. After an observable instance changes, an application
	 * calling the Observable's notifyObservers method causes all of its
	 * observers to be notified of the change by a call to their update method.
	 * 
	 * @author sebas
	 * 
	 */
	public static class OnSyncProgressChangeObservable extends Observable {
		private void notifyChanges(int completedCount, int totalCount) {
			final SyncProgress progress = new SyncProgress();
			progress.completedCount = completedCount;
			progress.totalCount = totalCount;
			// Sets the changed flag for this Observable. After calling
			// setChanged(), hasChanged() will return true

			Handler handler = new Handler(Looper.getMainLooper());

			System.out.println("********************Hander 1");
			handler.post(new Runnable() {

				@Override
				public void run() {
					setChanged();
					// If hasChanged() returns true, calls the update() method
					// for every
					// Observer in the list of observers using the specified
					// argument.
					// Afterwards calls clearChanged().
					notifyObservers(progress);

				}
			});

		}
	}

	/**
	 * @author sebas
	 * 
	 */
	public static class OnSyncUnauthorizedObservable extends Observable {
		private void notifyChanges() {

			Handler handler = new Handler(Looper.getMainLooper());

			System.out.println("********************Hander 2");
			handler.post(new Runnable() {

				@Override
				public void run() {
					setChanged();
					// If hasChanged() returns true, calls the update() method
					// for every
					// Observer in the list of observers using the specified
					// argument.
					// Afterwards calls clearChanged().
					notifyObservers();

				}
			});

		}
	}

	/**
	 * @author sebas
	 * 
	 */
	public static class SyncProgress {
		public int completedCount;
		public int totalCount;
	}

}
