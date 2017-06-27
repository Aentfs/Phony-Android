package io.github.aentfs.phony.phone;

import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.util.Log;

/**
 * Handles the {@link Connection} for calling via the backend.
 */
public final class PhonyConnection extends Connection {

    private static final String TAG = "PhonyConnection";

    private SipAudioCall mSipCall;

    private SipAudioCall.Listener mSipListener = new SipAudioCall.Listener() {

        @Override
        public void onCallEstablished(SipAudioCall call) {
            call.startAudio();

            if (call.isMuted()) {
                call.toggleMute();
            }

            PhonyConnection.this.setActive();
        }

        @Override
        public void onCallEnded(SipAudioCall call) {
            PhonyConnection.this.setDisconnected(new DisconnectCause(DisconnectCause.REMOTE));
        }
    };

    public PhonyConnection(SipAudioCall sipCall) throws SipException {
        setInitializing();

        setAudioModeIsVoip(true);

        setConnectionCapabilities(CAPABILITY_MUTE);

        mSipCall = sipCall;

        mSipCall.setListener(mSipListener);
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
        if (state.isMuted() != mSipCall.isMuted()) {
            mSipCall.toggleMute();
        }

        switch (state.getRoute()) {
            case CallAudioState.ROUTE_WIRED_OR_EARPIECE:
                mSipCall.setSpeakerMode(false);
                break;

            case CallAudioState.ROUTE_SPEAKER:
                mSipCall.setSpeakerMode(true);
                break;
        }
    }

    @Override
    public void onDisconnect() {
        Log.d(TAG, "onDisconnect: called.");

        try {
            mSipCall.endCall();

            setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));

            destroy();
        } catch (SipException e) {
            e.printStackTrace();
        }

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
