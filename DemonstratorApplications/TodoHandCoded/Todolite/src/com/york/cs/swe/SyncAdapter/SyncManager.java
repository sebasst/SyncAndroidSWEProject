package com.york.cs.swe.SyncAdapter;

import com.york.cs.swe.todolite.Application;
import com.york.cs.swe.authentication.UserManager;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

public class SyncManager {

	/**
	 * Use this option if you don't have to force a data transfer in response to
	 * data changes, but you do want to ensure your data is regularly updated.
	 * Similarly, you can use this option if you don't want a fixed schedule for
	 * your sync adapter, but you do want it to run frequently.
	 * 
	 * Since the method setSyncAutomatically() doesn't disable
	 * addPeriodicSync(), your sync adapter may be triggered repeatedly in a
	 * short period of time. If you do want to run your sync adapter
	 * periodically on a regular schedule, you should disable
	 * setSyncAutomatically().
	 * 
	 * @param context
	 */
	public static void syncWithNetMsg(Context context) {

		ContentResolver mResolver = context.getContentResolver();

		Account mConnectedAccount = UserManager
				.getCurrentDeviceAccount(context);

		if (mConnectedAccount == null) {
			ContentResolver.setSyncAutomatically(mConnectedAccount,
					Application.CONTENT_AUTHORITY, true);

		}

	}

	public static void syncScheduledPeriodically(Context context,
			Long frequencySec) {

		// Get the content resolver for your app
		ContentResolver mResolver = context.getContentResolver();

		Account mConnectedAccount = UserManager
				.getCurrentDeviceAccount(context);

		/*
		 * Turn on periodic syncing
		 */
		ContentResolver.addPeriodicSync(mConnectedAccount,
				Application.CONTENT_AUTHORITY, null, frequencySec);

	}

	/**
	 * Trigger a synchronization process for the current account
	 * 
	 * allowing users to run a sync on demand means that the sync runs by
	 * itself, which is inefficient use of network and power resources. Also,
	 * providing sync on demand leads users to request a sync even if there's no
	 * evidence that the data has changed, and running a sync that doesn't
	 * refresh data is an ineffective use of battery power. In general, your app
	 * should either use other signals to trigger a sync or schedule them at
	 * regular intervals, without user input.
	 * 
	 * @param context
	 */
	public static void syncCurretUserNow(Context context) {
		Account mConnectedAccount = UserManager
				.getCurrentDeviceAccount(context);

		Bundle bundle = new Bundle();
		// Performing a sync no matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		// Performing a sync matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(mConnectedAccount,
				Application.CONTENT_AUTHORITY, bundle);
		// Application application = (Application) Application.getContext();

		// ContentResolver.isSyncActive(account, authority)

		// application.setcurrentUser(new User());

	}

	/**
	 * @param context
	 */
	public static boolean isSyncActive(Context context) {
		Account mConnectedAccount = UserManager
				.getCurrentDeviceAccount(context);

		Bundle bundle = new Bundle();
		// Performing a sync no matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		// Performing a sync matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		// ContentResolver.requestSync(mConnectedAccount,
		// Application.CONTENT_AUTHORITY, bundle);
		// Application application = (Application) Application.getContext();

		return ContentResolver.isSyncActive(mConnectedAccount,
				Application.CONTENT_AUTHORITY);

		// application.setcurrentUser(new User());

	}

	/**
	 * @param context
	 */
	public static boolean isSyncPending(Context context) {
		Account mConnectedAccount = UserManager
				.getCurrentDeviceAccount(context);

		Bundle bundle = new Bundle();
		// Performing a sync no matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		// Performing a sync matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		// ContentResolver.requestSync(mConnectedAccount,
		// Application.CONTENT_AUTHORITY, bundle);
		// Application application = (Application) Application.getContext();

		return ContentResolver.isSyncPending(mConnectedAccount,
				Application.CONTENT_AUTHORITY);

		// application.setcurrentUser(new User());

	}

	public static void unscheduleSync(Context context) {
		Account mConnectedAccount = UserManager
				.getCurrentDeviceAccount(context);

		Bundle bundle = new Bundle();
		// Performing a sync no matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		// Performing a sync matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		// ContentResolver.requestSync(mConnectedAccount,
		// Application.CONTENT_AUTHORITY, bundle);
		// Application application = (Application) Application.getContext();
		ContentResolver.removePeriodicSync(mConnectedAccount,
				Application.CONTENT_AUTHORITY, null);

		// application.setcurrentUser(new User());

	}

}
