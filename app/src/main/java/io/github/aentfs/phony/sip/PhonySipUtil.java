package io.github.aentfs.phony.sip;

import android.content.Context;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

import java.text.ParseException;

/**
 * Utility class for interacting with the SIP system.
 */
public class PhonySipUtil {
    
    private static SipManager mSipManager = null;

    public static final int EXPIRY_TIME = 60;
    
    private PhonySipUtil() {//empty
    }

    /**
     * Get the sip account manager for the current context.
     *
     * @param context The current context to use.
     * @return The {@link SipManager} for the given context.
     */
    public static SipManager getSipManager(Context context) {
        if (mSipManager == null) {
            mSipManager = SipManager.newInstance(context.getApplicationContext());
        }

        return mSipManager;
    }

    public static SipProfile createSipProfile(String username, String password, String domain, String proxyAddress) throws ParseException {
        SipProfile.Builder builder = new SipProfile.Builder(username, domain);
        builder.setPassword(password)
                .setOutboundProxy(proxyAddress);

        return builder.build();
    }
}
