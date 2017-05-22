package io.github.aentfs.phony;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Authenticator for communication with the phony server.
 */
public class PhonyAuthenticator extends AbstractAccountAuthenticator {

    private String TAG = "PhonyAuthenticator";
    private final Context mContext;

    public PhonyAuthenticator(Context context) {
        super(context);

        // Save context for later use.
        this.mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.d(TAG, "editProperties: called.");

        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "addAccount: called.");

        final Bundle bundle = new Bundle();

        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, "Test Account");
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        bundle.putString(AccountManager.KEY_AUTHTOKEN, "123456");

        final Account account = new Account("Test Account", accountType);
        AccountManager.get(mContext).addAccountExplicitly(account, null, null);

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "confirmCredentials: called.");

        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken: called.");

        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.d(TAG, "getAuthTokenLabel: called.");

        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "updateCredentials: called.");

        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        Log.d(TAG, "hasFeatures: called.");

        return null;
    }
}
