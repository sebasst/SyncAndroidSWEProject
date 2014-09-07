package com.york.cs.swe.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.york.cs.swe.R;
import com.york.cs.swe.authentication.AccountGeneral;
import com.york.cs.swe.authentication.User;
import com.york.cs.swe.authentication.UserManager;
/**
 * In charge of the Sign up process. Since it's not an AuthenticatorActivity
 * decendent, it returns the result back to the calling activity, which is an
 * AuthenticatorActivity, and it return the result back to the Authenticator
 * 
 */
public class SignUpActivity extends Activity {

	private String TAG = getClass().getSimpleName();
	//private String mAccountType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

		setContentView(R.layout.act_register);

		findViewById(R.id.alreadyMemberRegister).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setResult(RESULT_CANCELED);
						finish();
					}
				});
		findViewById(R.id.submitRegister).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						createAccount();
					}
				});
	}

	/**
	 * creates account
	 */
	private void createAccount() {

		final ProgressDialog dialog = ProgressDialog.show(this,
				getString(R.string.conectingMessage),
				getString(R.string.please_waitMessage), true);
		dialog.show();

		new AsyncTask<String, Void, Intent>() {

			String name = ((TextView) findViewById(R.id.accountNameRegister))
					.getText().toString().trim();
			String accountName = ((TextView) findViewById(R.id.accountEmailAddressRegister))
					.getText().toString().trim();
			String accountPassword = ((TextView) findViewById(R.id.accountPasswordRegister))
					.getText().toString().trim();

			@Override
			protected Intent doInBackground(String... params) {

				Log.d(TAG, "> Started authenticating");

				Bundle data = new Bundle();
				try {
					/*User user = sServerAuthenticate.userSignUp(name,
							accountName, accountPassword,
							AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
					if (user != null)
						authtoken = user.getSessionToken();
*/
					
					User user = new User(accountName, name, accountName, accountPassword);
					user.print();
					
					
					if((UserManager.userSignUp(getBaseContext(),user)==null))data.putString(AccountManager.KEY_ERROR_MESSAGE, "Error Creating Account");
				
					user.print();
					
					data.putString(AccountManager.KEY_ACCOUNT_NAME, user.getEmail());
					data.putString(AccountManager.KEY_ACCOUNT_TYPE,
							AccountGeneral.ACCOUNT_TYPE);
					data.putString(AccountManager.KEY_AUTHTOKEN, user.getSessionToken());
					data.putString(AccountManager.KEY_PASSWORD, user.getPassword());

					// We keep the user's object id as an extra data on the
					// account.
					// It's used later for determine ACL for the data we send to
					// the service
					Bundle userData = new Bundle();
					userData.putString(AccountGeneral.USERDATA_USER_OBJ_ID, user.getId());
					userData.putString(AccountGeneral.USERDATA_USER_OBJ_NAME, user.getName());
					userData.putString(AccountGeneral.USERDATA_USER_OBJ_EMAIL, user.getEmail());
					userData.putString(AccountGeneral.USERDATA_USER_OBJ_PASSWORD, user.getPassword());
					
					data.putBundle(AccountManager.KEY_USERDATA, userData);

					data.putString(AccountGeneral.PARAM_USER_PASS, accountPassword);

				} catch (Exception e) {
					data.putString(AccountGeneral.KEY_ERROR_MESSAGE, e.getMessage());
					showMessage("Error creating account 3");
				}

				final Intent res = new Intent();
				res.putExtras(data);
				return res;
			}

			@Override
			protected void onPostExecute(Intent intent) {
				
				if (intent.hasExtra(AccountGeneral.KEY_ERROR_MESSAGE)) {
					
					
					dialog.dismiss();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							SignUpActivity.this);
					builder.setMessage(
							getString(R.string.creationAccountErrorMessage))
							.setCancelable(false)
							.setPositiveButton(getString(android.R.string.ok),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											invalidateOptionsMenu();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();

					Log.d(TAG, "Error creating Account1");

				} else {
					
					dialog.dismiss();
					
					Bundle data=intent.getExtras();
					Bundle userData=data.getBundle(AccountManager.KEY_USERDATA);
					
					User user = new User();
					user.setId(userData.getString(AccountGeneral.USERDATA_USER_OBJ_ID));
					user.setName(userData.getString(AccountGeneral.USERDATA_USER_OBJ_NAME));
					user.setEmail(userData.getString(AccountGeneral.USERDATA_USER_OBJ_EMAIL));
					user.setPassword(userData.getString(AccountGeneral.USERDATA_USER_OBJ_PASSWORD));
					
					user.print();
					
					
					
					intent.putExtra("useraccountID", user.getId());
					intent.putExtra("username", user.getName());
					intent.putExtra("useraccountPassword", user.getPassword());
					intent.putExtra("useremail", user.getEmail());

					setResult(RESULT_OK, intent);
					finish();
				}
			}
		}.execute();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
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
}
