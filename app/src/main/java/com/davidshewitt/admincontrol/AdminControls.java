package com.davidshewitt.admincontrol;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class AdminControls {

    private final DevicePolicyManager devicePolicyManager;
    private final ComponentName deviceOwnerComponent;

    public AdminControls(Context context) {
        devicePolicyManager = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceOwnerComponent = new ControlDeviceAdminReceiver().getWho(context);
    }

    public ComponentName getDeviceOwnerComponent() {
        return deviceOwnerComponent;
    }

    /**
     * Checks if the app has device admin privs.
     */
    public boolean hasDeviceAdmin() {
        return devicePolicyManager.isAdminActive(deviceOwnerComponent);
    }

    public boolean isFingerprintEnabled() {
        return !KeyguardFeatures.isFingerprintDisabled(
                devicePolicyManager.getKeyguardDisabledFeatures(deviceOwnerComponent));
    }

    public boolean setFingerprintEnabled(boolean enabled) {
        int features = KeyguardFeatures.setFingerprintDisabled(
                devicePolicyManager.getKeyguardDisabledFeatures(deviceOwnerComponent), !enabled);
        try {
            devicePolicyManager.setKeyguardDisabledFeatures(deviceOwnerComponent, features);
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean lockRequiringStrongAuth() {
        try {
            devicePolicyManager.lockNow();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
