package io.github.aentfs.phony;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Connects the android system with the {@link PhonyAuthenticator}.
 */
public class PhonyAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        PhonyAuthenticator authenticator = new PhonyAuthenticator(this);
        return authenticator.getIBinder();
    }
}
