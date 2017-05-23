package io.github.aentfs.phony;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Connects the android system with the {@link PhonyAuthenticator}.
 * BLA2
 */

public class PhonyAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private PhonyAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PhonyAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
