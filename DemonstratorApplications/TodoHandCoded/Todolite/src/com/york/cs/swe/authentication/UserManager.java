package com.york.cs.swe.authentication;


import static com.york.cs.swe.authentication.AccountGeneral.sServerAuthenticate;
import com.york.cs.swe.todolite.Application;
import java.util.Iterator;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;


public class UserManager {

	/**
	 * Sign up the user in the server, if exists a different account, it is
	 * deleted
	 * 
	 * @return sessionCookie if account created successfully in server and
	 *         device, null if not
	 * @throws Exception
	 *             if anny error accours
	 */
	public static String userSignUp(Context context, User user)
			throws Exception {

		// Application application = (Application) Application.getContext();

		String oldUserEmail = getCurrentUserEmail(context);

		// remove accoun if it is different
		if (oldUserEmail != null && !oldUserEmail.equals(user.getEmail())) {
			Log.d("User sigunup", "removing old accound");

			removeAccount(context);

		}

		User userTemp = sServerAuthenticate.userSignUp(user.getName(),
				user.getEmail(), user.getPassword(),
				user.getDefaultAccountType());

		if (userTemp == null)
			Log.d("UserManager", "user null 1");
		else if (userTemp.getSessionToken() == null)
			Log.d("UserManager", "user token null 1");
		if (userTemp == null || userTemp.getSessionToken() == null)
			return null;

		String sessionToken = userTemp.getSessionToken();
		user.setSessionToken(sessionToken);

		if (oldUserEmail == null || !oldUserEmail.equals(user.getEmail())) {

			setAsCurrentUser(user);
			addAccountToDevice(context, user);
		}

		return sessionToken;
	}

	public static String userSignIn(Context context, User user)
			throws Exception {

		// Application application = (Application) Application.getContext();

		String oldUserEmail = getCurrentUserEmail(context);

		// remove accoun if it is different
		if (oldUserEmail != null && !oldUserEmail.equals(user.getEmail())) {

			removeAccount(context);
		}

		User userTemp = sServerAuthenticate.userSignIn(user.getEmail(),
				user.getPassword(), user.getDefaultAccountType());

		if (userTemp == null || userTemp.getSessionToken() == null)
			return null;

		user.setSessionToken(userTemp.getSessionToken());

		if (oldUserEmail == null || !oldUserEmail.equals(user.getEmail())) {

			setAsCurrentUser(user);
			addAccountToDevice(context, user);
		}

		return user.getSessionToken();
	}

	public static boolean addAccountToDevice(Context context, User user) {
		AccountManager mAccountManager = AccountManager.get(context);

		final Account account = new Account(user.getEmail(),
				AccountGeneral.ACCOUNT_TYPE);

		Bundle userData = new Bundle();
		userData.putString(AccountGeneral.USERDATA_USER_OBJ_ID, user.getId());
		userData.putString(AccountGeneral.USERDATA_USER_OBJ_NAME,
				user.getName());
		userData.putString(AccountGeneral.USERDATA_USER_OBJ_EMAIL,
				user.getEmail());
		userData.putString(AccountGeneral.USERDATA_USER_OBJ_PASSWORD,
				user.getPassword());

		boolean result = mAccountManager.addAccountExplicitly(account,
				user.getPassword(), userData);
		mAccountManager.setAuthToken(account, AccountGeneral.ACCOUNT_TYPE,
				user.getSessionToken());

		return result;
	}

	public static boolean setAsCurrentUser(User user) {

		// Application application = (Application) Application.getContext();

		setcurrentUser(user, Application.getContext());

		return true;

	}

	/**
	 * Validate if exists an account created in the device
	 * 
	 * @param context
	 *            app
	 * @return true if exists an account, false if not
	 */
	public static boolean existsAccount(Context context) {
		AccountManager mAccountManager;
		mAccountManager = AccountManager.get(context);
		Account[] accounts = mAccountManager
				.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

		if (accounts.length > 0)
			return true;
		else
			return false;

	}

	/**
	 * Retrieves the current account from the device
	 * 
	 * @param context
	 *            app
	 * @return account, null if not exists
	 */
	public static Account getCurrentDeviceAccount(Context context) {
		Account mConnectedAccount;
		AccountManager mAccountManager;
		mAccountManager = AccountManager.get(context);
		Account[] accounts = mAccountManager
				.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

		if (accounts.length <= 0)
			return null;
		else {

			mConnectedAccount = accounts[0];

			return mConnectedAccount;
		}

	}

	/**
	 * Removes current Account from account mananager and from application
	 * preferences
	 * 
	 * @param context
	 */
	public static void removeAccount(Context context) {

		AccountManager mAccountManager = AccountManager.get(context);

		Account account = getCurrentDeviceAccount(context);
		mAccountManager.removeAccount(account, null, null);
		removeCurretUserFromApp(context);

	}

	/**
	 * Removes current Account from application preferences
	 * 
	 * @param context
	 */
	public static void removeCurretUserFromApp(Context context) {
		// Application application = (Application) Application.getContext();

		setcurrentUser(new User(), context);

	}

	/**
	 * Trigger a synchronization process for the current account
	 * 
	 * @param context
	 */
	public static void syncCurretUserNow(Context context) {
		Account mConnectedAccount = getCurrentDeviceAccount(context);

		Bundle bundle = new Bundle();
		// Performing a sync no matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		// Performing a sync matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(mConnectedAccount,
				Application.CONTENT_AUTHORITY, bundle);
		// Application application = (Application) Application.getContext();

		// application.setcurrentUser(new User());

	}

	private static final String PREF_CURRENT_USER_ID = "CurrentUserId";
	private static final String PREF_CURRENT_USER_NAME = "CurrentUserName";
	private static final String PREF_CURRENT_USER_EMAIL = "CurrentUserEmail";
	private static final String PREF_CURRENT_USER_PASSWORD = "CurrentUserPassword";

	private static final String PREF_CURRENT_USER_PROFILE_ID = "CurrentUserProfileId";;

	public static void updateDocumentOwner(final String newOwner) {
		Log.d(">>>>>>updateing owner with new user" + newOwner, "Total ");
		Database database = ((Application) Application.getContext())
				.getDatabase();
		Query query = database.createAllDocumentsQuery();
		Log.d("updateing owner with new user", "Total ");
		query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);

		QueryEnumerator result;
		try {
			Log.d("updating owner with new user", "Total ");
			result = query.run();

			Log.d("updateing owner with new user",
					"Total Docs: " + result.getCount());

			for (Iterator<QueryRow> it = result; it.hasNext();) {
				QueryRow row = it.next();

				String oldOwner = (String) row.getDocument().getProperty(
						"owner_id");

				if (oldOwner == null) {

					Document doc = row.getDocument();
					doc.update(new Document.DocumentUpdater() {
						@Override
						public boolean update(UnsavedRevision newRevision) {
							Map<String, Object> properties = newRevision
									.getUserProperties();
							properties.put("owner_id", newOwner);
							newRevision.setUserProperties(properties);
							return true;
						}
					});
					Log.d("updateing owner with new user" + newOwner,
							"****updated ");

				} else if (newOwner != null && newOwner.equals(oldOwner)) {
					Log.d("updateing owner with new user" + newOwner,
							"**** not updated ");
					continue;
				} else {
					// purge from database
					Log.d("updateing owner with new user" + newOwner,
							"**** purged");
					Document doc = row.getDocument();
					doc.purge();

				}

			}

		} catch (CouchbaseLiteException e) {

			e.printStackTrace();
		}

	}

	public static void setcurrentUser(User user, Context context) {
		final String userId = user.getId();

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (userId != null) {
			sp.edit().putString(PREF_CURRENT_USER_ID, userId).apply();
		} else {

			sp.edit().remove(PREF_CURRENT_USER_ID).apply();
		}

		String accountName = user.getName();
		if (accountName != null) {
			sp.edit().putString(PREF_CURRENT_USER_NAME, accountName).apply();
		} else {
			sp.edit().remove(PREF_CURRENT_USER_NAME).apply();
		}

		String accountEmail = user.getEmail();
		if (accountEmail != null) {
			sp.edit().putString(PREF_CURRENT_USER_EMAIL, accountEmail).apply();
		} else {

			sp.edit().remove(PREF_CURRENT_USER_EMAIL).apply();
		}

		String accountPassword = user.getPassword();
		if (accountPassword != null) {
			sp.edit().putString(PREF_CURRENT_USER_PASSWORD, accountPassword)
					.apply();
		} else {
			sp.edit().remove(PREF_CURRENT_USER_PASSWORD).apply();
		}
		//TODO PROFILE
/*
		if (user.getName() != null) {
			Profile profile = new Profile(user.getId(), user.getName());
			profile.setUserEmail(user.getEmail());
			try {
				profile.saveProfile();
				if (profile.getId() != null) {
					sp.edit()
							.putString(PREF_CURRENT_USER_PROFILE_ID,
									profile.getId()).apply();
				} else {
					sp.edit().remove(PREF_CURRENT_USER_PROFILE_ID).apply();
				}
			} catch (CouchbaseLiteException e) {

				e.printStackTrace();
			}
		}*/
		// new Thread(new Runnable() {
		// public void run() {

		updateDocumentOwner(user.getEmail());
		// }
		// }).start();

	}
/*
	public static Profile getCurrentUserProfile(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		String userProfileId = sp.getString(PREF_CURRENT_USER_PROFILE_ID, null);
		if (userProfileId == null)
			return null;

		Application application = (Application) context.getApplicationContext();
		return Profile.getById(application.getDatabase(), userProfileId);

	}
*/
	public static void setCurrentUserId(String id, Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (id != null) {
			sp.edit().putString(PREF_CURRENT_USER_ID, id).apply();
		} else {
			sp.edit().remove(PREF_CURRENT_USER_ID);
		}
	}

	public static String getCurrentUserId(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		String userId = sp.getString(PREF_CURRENT_USER_ID, null);
		return userId;
	}

	public static String getCurrentUserProfileId(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		String userId = sp.getString(PREF_CURRENT_USER_PROFILE_ID, null);
		return userId;
	}

	/**
	 * @return userId
	 */
	public static String getCurrentUserName(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		String userName = sp.getString(PREF_CURRENT_USER_NAME, null);
		return userName;
	}

	/**
	 * @return current user email
	 */
	public static String getCurrentUserEmail(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		String userName = sp.getString(PREF_CURRENT_USER_EMAIL, null);
		return userName;
	}

	/**
	 * @return current user password
	 */
	public static String getCurrentUserPassword(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		String userName = sp.getString(PREF_CURRENT_USER_PASSWORD, null);
		return userName;
	}

}
