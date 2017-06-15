package io.github.aentfs.phony.phone;

import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.util.Log;

/**
 * Handles the {@link Connection} for calling via the backend.
 */
public final class PhonyConnection extends Connection {

    private static final String TAG = "PhonyConnection";

    public PhonyConnection()
    {
        setInitialized();
    }

    @Override
    public void onDisconnect() {
        Log.d(TAG, "onDisconnect: called.");

        setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
    }

    @Override
    public void onAbort() {
        Log.d(TAG, "onAbort: called.");

        onDisconnect();
    }

    @Override
    public void onAnswer() {
        Log.d(TAG, "onAnswer: called.");

        super.onAnswer();
    }

    @Override
    public void onReject() {
        Log.d(TAG, "onReject: called.");

        super.onReject();
    }
}
