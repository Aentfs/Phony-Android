package io.github.aentfs.phony.phone;

import android.content.Intent;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import io.github.aentfs.phony.sip.PhonySipUtil;

public class PhonyConnectionService extends ConnectionService {

    private static final String TAG = "PhonyConnectionService";

    public static final String EXTRA_PHONE_ACCOUNT = "KEY_PHONE_ACCOUNT";

    public static final String EXTRA_INCOMING_CALL_INTENT = "KEY_PHONE_ACCOUNT";

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.d(TAG, "onCreateIncomingConnection: called.");

        if (request.getExtras() == null) {
            return Connection.createFailedConnection(new DisconnectCause(DisconnectCause.ERROR, "No extras on request."));
        }

        Intent sipIntent = request.getExtras().getParcelable(EXTRA_INCOMING_CALL_INTENT);
        if (sipIntent == null) {
            return Connection.createFailedConnection(new DisconnectCause(DisconnectCause.ERROR, "No SIP intent."));
        }

        try {
            SipAudioCall audioCall = PhonySipUtil.getSipManager(this).takeAudioCall(sipIntent, null);

            PhonyConnection connection = new PhonyConnection(audioCall);

            connection.setAddress(Uri.parse(audioCall.getPeerProfile().getUriString()), TelecomManager.PRESENTATION_ALLOWED);
            connection.setInitialized();

            return connection;
        } catch (SipException e) {
            e.printStackTrace();
            return Connection.createFailedConnection(new DisconnectCause(DisconnectCause.ERROR, "SipExecption", "Check the stack trace for more information.", e.getLocalizedMessage()));
        }
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.d(TAG, "onCreateOutgoingConnection: called.");

        try {
            SipAudioCall audioCall = PhonySipUtil.getSipManager(this).makeAudioCall(connectionManagerPhoneAccount.getId(), Uri.decode(request.getAddress().toString()), null, PhonySipUtil.EXPIRY_TIME);

            PhonyConnection connection = new PhonyConnection(audioCall);

            connection.setAddress(request.getAddress(), TelecomManager.PRESENTATION_ALLOWED);
            connection.setInitialized();

            return connection;
        } catch (SipException e) {
            e.printStackTrace();
            return Connection.createFailedConnection(new DisconnectCause(DisconnectCause.ERROR, "SipExecption", "Check the stack trace for more information.", e.getLocalizedMessage()));
        }
    }
}
