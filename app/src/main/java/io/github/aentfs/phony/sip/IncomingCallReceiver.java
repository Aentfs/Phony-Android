package io.github.aentfs.phony.sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import io.github.aentfs.phony.phone.PhonyConnectionService;
import io.github.aentfs.phony.phone.PhonyUtil;

/**
 * Is called when an call comes in.
 */
public class IncomingCallReceiver extends BroadcastReceiver {

    private static final String TAG = "IncomingCallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: called.");

        PhoneAccountHandle phoneAccount = PhonyUtil.createPhoneAccountHandle(context, intent.getStringExtra(PhonyConnectionService.EXTRA_PHONE_ACCOUNT));

        Bundle bundle = new Bundle();
        bundle.putParcelable(PhonyConnectionService.EXTRA_INCOMING_CALL_INTENT, intent);

        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        telecomManager.addNewIncomingCall(phoneAccount, bundle);
    }
}
