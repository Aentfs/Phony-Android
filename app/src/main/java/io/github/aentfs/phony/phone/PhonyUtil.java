package io.github.aentfs.phony.phone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;

import io.github.aentfs.phony.R;

/**
 * A utility class for creating an maintaining {@link PhonyConnectionService}s.
 */
public final class PhonyUtil {

    private PhonyUtil() {//empty
    }

    /**
     * Create a {@link PhoneAccountHandle} for the given parameter.
     *
     * @param context The context to use for the {@link ComponentName}.
     * @param accountName The name of the account. Normaly the SIP URI.
     * @return The build {@link PhoneAccountHandle}.
     */
    public static PhoneAccountHandle createPhoneAccountHandle(Context context, String accountName) {
        return new PhoneAccountHandle(
                new ComponentName(context, PhonyConnectionService.class),
                accountName);
    }

    /**
     * Create a new {@link PhoneAccount} and register it with the system.
     *
     * @param context     The context to use for finding the services and resources.
     * @param accountName The name of the account to add - must be an international phonenumber.
     */
    public static void registerNewPhoneAccount(Context context, String accountName) {
        PhoneAccountHandle accountHandle = createPhoneAccountHandle(context, accountName);

        PhoneAccount phone = PhoneAccount.builder(accountHandle, context.getResources().getString(R.string.app_name))
                .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher_round))
                .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                .addSupportedUriScheme(PhoneAccount.SCHEME_SIP)
                .setAddress(Uri.parse("sip:" + accountName))
                .build();

        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        telecomManager.registerPhoneAccount(phone);

        // Let the user enable our phone account
        // TODO Show toast so the user knows what is happening
        context.startActivity(new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS));
    }
}
