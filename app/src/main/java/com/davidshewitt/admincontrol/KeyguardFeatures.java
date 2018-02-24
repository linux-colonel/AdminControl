package com.davidshewitt.admincontrol;

import android.app.admin.DevicePolicyManager;

/**
 * Logic for Keyguard Disabled Features.
 */

class KeyguardFeatures {
    /**
     * Takes into account any other already disabled keyguard features when disabling fingerprints.
     * @param currentDisabledFeatures Any features that are currently disabled.
     * @param fingerprintDisabled True if disabling fingerprints, false otherwise.
     * @return An integer representing the disabled keyguard features.
     * @see <a href=https://developer.android.com/reference/android/app/admin/DevicePolicyManager.html#setKeyguardDisabledFeatures(android.content.ComponentName,%20int)>Device Admin API</a>
     */
    static int determineKeyguardDisabledFeatures(
            int currentDisabledFeatures,
            boolean fingerprintDisabled) {
                int keyguardDisabledFeatures;
        if(fingerprintDisabled){
            keyguardDisabledFeatures =
                    currentDisabledFeatures | DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT;
        } else {
            keyguardDisabledFeatures =
                    currentDisabledFeatures & (~DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
        }
        return keyguardDisabledFeatures;
    }
}
