package com.york.cs.swe.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.util.Log;
import com.york.cs.swe.todolite.Application;
import com.york.cs.swe.authentication.User;
import com.york.cs.swe.authentication.UserManager;
import com.york.cs.swe.authentication.AccountGeneral;
import com.york.cs.swe.R;

public abstract class BaseActivity extends Activity {

	private static final String TAG = "BaseActivity";

	// private AccountManager mAccountManager;
	// private String authToken = null;

	// private Account mConnectedAccount;

	protected Application application;

	// protected TodoDB todoDB;

	/**
	 * Handle to a SyncObserver. The ProgressBar element is visible until the
	 * SyncObserver reports that the sync is complete.
	 * 
	 * <p>
	 * This allows us to delete our SyncObserver once the application is no
	 * longer in the foreground.
	 */
	private Object mSyncObserverHandle;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		application = (Application) this.getApplication();

		super.onCreate(savedInstanceState);
		showSyncProgress(true);

	}

	@Override
	public void onResume() {
		super.onResume();
		mSyncStatusObserver.onStatusChanged(0);

		// Watch for sync state changes
		final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING
				| ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
		mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask,
				mSyncStatusObserver);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mSyncObserverHandle != null) {
			ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
			mSyncObserverHandle = null;
		}
	}

	protected abstract void showSyncProgress(boolean setActive);

	/**
	 * Add Login button if the user has not been logged in.
	 * 
	 * @param menu
	 */
	protected void addLoginButton(Menu menu) {

		if (UserManager.getCurrentUserId(this) == null
				|| !UserManager.existsAccount(this)) {
			MenuItem shareMenuItem = menu.add(getResources().getString(
					R.string.action_login));
			shareMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			shareMenuItem
					.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem menuItem) {
							Application application = (Application) getApplication();
							switch (application.getAuthenticationType()) {
							case CUSTOM_COOKIE:
								loginWithCustomCookieAndStartSync();
								break;
							default:
								break;

							}

							return true;
						}
					});

			// TODO REMOVE THIS TOAST
			Toast.makeText(getApplicationContext(),
					"Shared account: " + UserManager.getCurrentUserId(this),
					Toast.LENGTH_LONG).show();
			;
		}

	}

	/**
	 * adds sinc button to menu
	 * 
	 * @param menu
	 */
	protected void addSyncNowButton(Menu menu) {

		if (UserManager.getCurrentUserId(this) != null
				&& UserManager.existsAccount(this)) {
			MenuItem syncnowItem = menu.add(getResources().getString(
					R.string.sync_nowButton));
			syncnowItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			syncnowItem
					.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem menuItem) {

							Log.d(TAG, "syncnowItem");

							UserManager.syncCurretUserNow(getBaseContext());

							invalidateOptionsMenu();
							return true;
						}
					});
		}
	}

	protected void addRemoveaccountNowButton(Menu menu) {

		if (UserManager.getCurrentUserId(this) != null
				&& UserManager.existsAccount(getBaseContext())) {
			MenuItem syncnowItem = menu.add(getResources().getString(
					R.string.remove_accountMenu));
			syncnowItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			syncnowItem
					.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem menuItem) {

							Log.d(TAG, "addRemoveaccountNowButton");

							UserManager.removeAccount(getBaseContext());

							invalidateOptionsMenu();
							return true;
						}
					});
		}
	}

	/**
	 * 
	 * 
	 * - Your app prompts user for credentials - Your app directly contacts your
	 * app server with these credentials - Your app server creates a session on
	 * the Sync Gateway, which returns a cookie - Your app server returns this
	 * cookie to your app
	 * 
	 * Having obtained the cookie in the manner above, you would then call
	 * startSyncWithCustomCookie() with this cookie.
	 * 
	 */
	protected void loginWithCustomCookieAndStartSync() {

		getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE,
				AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

	}

	/**
	 * Get an auth token for the account. If not exist - add it and then return
	 * its auth token. If one exist - return its auth token. If more than one
	 * exists - show a picker and return the select account's auth token.
	 * 
	 * @param accountType
	 * @param authTokenType
	 */
	private void getTokenForAccountCreateIfNeeded(String accountType,
			String authTokenType) {

		Log.d(TAG, " getTokenForAccountCreateIfNeeded");
		AccountManager mAccountManager = AccountManager.get(this);

		@SuppressWarnings("unused")
		final AccountManagerFuture<Bundle> future = mAccountManager
				.getAuthTokenByFeatures(accountType, authTokenType, null, this,
						null, null, new AccountManagerCallback<Bundle>() {
							@Override
							public void run(AccountManagerFuture<Bundle> future) {
								Bundle bundle = null;
								String authToken = null;
								try {
									bundle = future.getResult();
									authToken = bundle
											.getString(AccountManager.KEY_AUTHTOKEN);
									if (authToken != null) {// TODO delete
										String accountName = bundle
												.getString(AccountManager.KEY_ACCOUNT_NAME);
										Log.d(" BasicActivity", "accountName: "
												+ accountName);
										Account mConnectedAccount = new Account(
												accountName,
												AccountGeneral.ACCOUNT_TYPE);
										invalidateOptionsMenu();
									}

								} catch (Exception e) {
									e.printStackTrace();
									showMessage(e.getMessage());
								}
							}
						}, null);
	}

	private void showMessage(final String msg) {
		if (msg == null || msg.trim().equals(""))
			return;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	/**
	 * Creates a new anonymous SyncStatusObserver. It's attached to the app's
	 * ContentResolver in onResume(), and removed in onPause(). If status
	 * changes, it sets the state of the Refresh button. If a sync is active or
	 * pending, the Refresh button is replaced by an indeterminate ProgressBar;
	 * otherwise, the button itself is displayed.
	 */
	private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
		/** Callback invoked with the sync adapter status changes. */
		@Override
		public void onStatusChanged(int which) {

			BaseActivity.this.runOnUiThread(new Runnable() {
				/**
				 * The SyncAdapter runs on a background thread. To update the
				 * UI, onStatusChanged() runs on the UI thread.
				 */
				@Override
				public void run() {
					// Create a handle to the account that was created by
					// SyncService.CreateSyncAccount(). This will be used to
					// query the system to
					// see how the sync status has changed.
					// Account account = GenericAccountService.GetAccount();

					if (UserManager.getCurrentDeviceAccount(getBaseContext()) == null) {
						// GetAccount() returned an invalid value. This
						// shouldn't happen, but
						// we'll set the status to "not refreshing".
						showSyncProgress(false);
						return;
					}

					// Test the ContentResolver to see if the sync adapter is
					// active or pending.
					// Set the state of the refresh button accordingly.
					boolean syncActive = ContentResolver.isSyncActive(
							UserManager
									.getCurrentDeviceAccount(getBaseContext()),
							Application.CONTENT_AUTHORITY);
					boolean syncPending = ContentResolver.isSyncPending(
							UserManager
									.getCurrentDeviceAccount(getBaseContext()),
							Application.CONTENT_AUTHORITY);
					showSyncProgress(syncActive || syncPending);
				}
			});
		}
	};
}
