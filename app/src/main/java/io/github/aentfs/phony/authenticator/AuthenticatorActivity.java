package io.github.aentfs.phony.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.github.aentfs.phony.R;
import io.github.aentfs.phony.phone.PhonyUtil;

/**
 * A login screen that offers login for SIP Server.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    private static final String TAG = "AuthenticatorActivity";

    private AccountManager mAccountManager;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mServerAddressView;
    private EditText mProxyAddressView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAccountManager = AccountManager.get(getBaseContext());

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mServerAddressView = (EditText) findViewById(R.id.server_address);
        mProxyAddressView = (EditText) findViewById(R.id.proxy_address);

        mProxyAddressView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid server address, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mServerAddressView.setError(null);
        mProxyAddressView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String serverAddress = mServerAddressView.getText().toString();
        String proxyAddress = mProxyAddressView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid server address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_incorrect_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid server address.
        if (TextUtils.isEmpty(serverAddress)) {
            mServerAddressView.setError(getString(R.string.error_field_required));
            focusView = mServerAddressView;
            cancel = true;
        } else if (!isServerValid(serverAddress)) {
            mServerAddressView.setError(getString(R.string.error_incorrect_server_address));
            focusView = mServerAddressView;
            cancel = true;
        }

        if (!isProxyAddressValid(proxyAddress)) {
            mProxyAddressView.setError(getString(R.string.error_incorrect_proxy_address));
            focusView = mProxyAddressView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password, serverAddress, proxyAddress);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return !username.isEmpty() && !username.contains(".");
    }

    private boolean isPasswordValid(String password) {
        // TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isServerValid(String serverAddress) {
        return !serverAddress.isEmpty() && serverAddress.contains(".");
    }

    private boolean isProxyAddressValid(String proxyAddress) {
        return proxyAddress.isEmpty() || proxyAddress.contains(".");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Intent> {

        private final String mUsername;
        private final String mPassword;
        private final String mServerAddress;
        private final String mProxyAddress;
        private final String mAccountType;

        UserLoginTask(String username, String password, String serverAddress, String proxyAddress) {
            mUsername = username;
            mPassword = password;
            mServerAddress = serverAddress;
            mProxyAddress = proxyAddress;
            mAccountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        }

        @Override
        protected Intent doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Log.d(TAG, "doInBackground: started authentication.");

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return null;
            }

            String authtoken;
            Bundle data = new Bundle();
            try {
                // TODO get auth token from server
                authtoken = "123456";

                data.putString(AccountManager.KEY_ACCOUNT_NAME, mUsername);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                data.putString(AccountManager.KEY_PASSWORD, mPassword);
                data.putString(PhonyAuthenticator.KEY_SERVER_ADDRESS, mServerAddress);
                data.putString(PhonyAuthenticator.KEY_PROXY_ADDRESS, mProxyAddress);

            } catch (Exception e) {
                data.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
            }

            final Intent res = new Intent();
            res.putExtras(data);
            return res;
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mAuthTask = null;
            showProgress(false);

            if (!intent.hasExtra(AccountManager.KEY_ERROR_MESSAGE)) {
                finishLogin(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void finishLogin(Intent intent) {
        Log.d(TAG, "finishLogin: called.");

        String accountUser = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        String accountServerAddress = intent.getStringExtra(PhonyAuthenticator.KEY_SERVER_ADDRESS);
        String accountProxyAddress = intent.getStringExtra(PhonyAuthenticator.KEY_PROXY_ADDRESS);

        String accountName = accountUser + "@" + accountServerAddress;

        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(TAG, "finishLogin: add account explicitly.");

            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

            Bundle extras = new Bundle();
            extras.putString(PhonyAuthenticator.KEY_SERVER_ADDRESS, accountServerAddress);
            extras.putString(PhonyAuthenticator.KEY_PROXY_ADDRESS, accountProxyAddress);

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, extras);
            mAccountManager.setAuthToken(account, PhonyAuthenticator.AUTH_TOKEN_TYPE, authtoken);

            // Register the phone account
            PhonyUtil.registerNewPhoneAccount(getApplicationContext(), accountName);
        } else {
            Log.d(TAG, "finishLogin: set password.");

            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
