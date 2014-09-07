package com.york.cs.swe.activities;

import android.accounts.AccountAuthenticatorActivity;
import com.york.cs.swe.R;
import android.accounts.AccountManager;
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
import com.york.cs.swe.authentication.AccountGeneral;
import com.york.cs.swe.authentication.User;
import com.york.cs.swe.authentication.UserManager;
/**
 * The Authenticator activity.
 * <p/>
 * Called by the Authenticator and in charge of identifing the user.
 * <p/>
 * It sends back to the Authenticator the result.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

	private final String TAG = "AuthenticatorActivity";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_login);

		findViewById(R.id.submitLogin).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						// login process validate that there is data
						String accountName = ((TextView) findViewById(R.id.accountemailLogin))
								.getText().toString();
						String accountPassword = ((TextView) findViewById(R.id.accountPasswordLogin))
								.getText().toString();

						if (accountName == null || accountName.equals("")
								|| accountPassword == null
								|| accountPassword.equals("")) {
							Toast.makeText(
									LoginActivity.this,
									getResources()
											.getString(
													R.string.please_fill_all_the_fieldsSign),
									Toast.LENGTH_LONG).show();
							return;
						}

						submitLogin(accountName, accountPassword);

					}
				});
		findViewById(R.id.signUp).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent signup = new Intent(getBaseContext(),
								SignUpActivity.class);
						signup.putExtras(getIntent().getExtras());
						startActivityForResult(signup,
								AccountGeneral.REQ_SIGNUP);
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// The sign up activity returned that the user has successfully created
		// an account
		if (requestCode == AccountGeneral.REQ_SIGNUP && resultCode == RESULT_OK) {

			finishLogin(data);

		} else
			super.onActivityResult(requestCode, resultCode, data);
	}

	public void submitLogin(String accountName, String accountPassword) {
		final ProgressDialog dialog = ProgressDialog.show(this,
				getString(R.string.conectingMessage),
				getString(R.string.please_waitMessage), true);
		dialog.show();

		new AsyncTask<String, Void, Intent>() {

			@Override
			protected Intent doInBackground(String... params) {

				Bundle data = new Bundle();
				final String userEmail = ((TextView) findViewById(R.id.accountemailLogin))
						.getText().toString();
				final String userPass = ((TextView) findViewById(R.id.accountPasswordLogin))
						.getText().toString();

				try {

					User user = new User();
					user.setId(userEmail);
					user.setEmail(userEmail);
					user.setPassword(userPass);

					if (UserManager.userSignIn(getBaseContext(), user) == null)
						data.putString(AccountManager.KEY_ERROR_MESSAGE,
								"Fail!! please cheach yout internet connection and your email and password");
					;

					data.putString(AccountManager.KEY_ACCOUNT_NAME,
							user.getId());
					data.putString(AccountManager.KEY_ACCOUNT_TYPE,
							AccountGeneral.ACCOUNT_TYPE);
					data.putString(AccountManager.KEY_AUTHTOKEN,
							user.getSessionToken());

					Bundle userData = new Bundle();
					userData.putString(AccountGeneral.USERDATA_USER_OBJ_ID,
							user.getId());
					userData.putString(AccountGeneral.USERDATA_USER_OBJ_NAME,
							user.getName());
					userData.putString(AccountGeneral.USERDATA_USER_OBJ_EMAIL,
							user.getEmail());
					userData.putString(
							AccountGeneral.USERDATA_USER_OBJ_PASSWORD,
							user.getPassword());

					data.putBundle(AccountManager.KEY_USERDATA, userData);
					// data.putString(PARAM_USER_PASS, userPass);

				} catch (Exception e) {

					Log.d(TAG, "Exception catched");
					data.putString(AccountManager.KEY_ERROR_MESSAGE,
							e.getMessage());
				}

				final Intent res = new Intent();
				res.putExtras(data);
				return res;
			}

			@Override
			protected void onPostExecute(Intent intent) {
				if (intent.hasExtra(AccountManager.KEY_ERROR_MESSAGE)) {
					dialog.dismiss();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							LoginActivity.this);
					builder.setMessage(getString(R.string.chechPasswordMsg))
							.setTitle(getString(R.string.not_loggedTitle))
							.setCancelable(false)
							.setPositiveButton(getString(android.R.string.ok),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

										}
									});
					AlertDialog alert = builder.create();
					alert.show();

					Log.d(TAG, "Error logging Account");
				} else {
					dialog.dismiss();
					finishLogin(intent);
				}
			}
		}.execute();
	}

	private void finishLogin(Intent intent) {
		Log.d(TAG, "> finishLogin");
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);

		finish();
	}

}
