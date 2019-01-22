/*
 * AdminControl - Additional security settings for your Android device.
 * Copyright (C) 2018 Dave Hewitt
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.davidshewitt.admincontrol;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

/**
 * A {@link PreferenceActivity} that lets the user control advanced security settings on their
 * device.
 */
public class ControlYourDeviceActivity extends AppCompatPreferenceActivity {
    public static String LOG_TAG = "com.davidshewitt.admincontrol";
    public static final int DISABLE_DEVICE_UNLOCK_PROTECTION = 0;
    public static final int DEFAULT_MAX_UNLOCK_ATTEMPTS_BEFORE_WIPE = 10;


    private static final int ACTIVATION_REQUEST = 1;

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceOwnerComponent;

    /**
     * Preference change listener for setting fingerprint policy on the Lock Screen.
     */
    private static final Preference.OnPreferenceChangeListener sFingerprintLockscreenListener =
            new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            ControlYourDeviceActivity mainPrefActivity =
                ((ControlYourDeviceActivity)preference.getContext());
            if  (! mainPrefActivity.hasDeviceAdmin()) {
                mainPrefActivity.showPermissionExplanation();
            }

            DevicePolicyManager dpm = mainPrefActivity.getDPM();
            ComponentName deviceOwnerComponent = mainPrefActivity.getDeviceOwnerComponent();
            boolean bValue = (Boolean)o;
            int keyguardDisabledFeatures =
                    KeyguardFeatures.setFingerprintDisabled(
                            dpm.getKeyguardDisabledFeatures(deviceOwnerComponent), bValue);
            try {
                dpm.setKeyguardDisabledFeatures(deviceOwnerComponent, keyguardDisabledFeatures);
            } catch (SecurityException s) {
                return false;
            }
            return true;
        }
    };

    /**
     * Listener for the wipe_after_too_many_unlock_attempts SwitchPreference
     */
    private static final Preference.OnPreferenceChangeListener sWipeAfterTooManyUnlockAttemptsListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    ControlYourDeviceActivity mainPrefActivity =
                            ((ControlYourDeviceActivity) preference.getContext());

                    if (!mainPrefActivity.hasDeviceAdmin()) {
                        mainPrefActivity.showPermissionExplanation();
                        return false;
                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainPrefActivity);
                    String key = mainPrefActivity.getString(R.string.key_max_unlock_attempts_before_wipe);
                    String _maxTries = preferences.getString(key, null);
                    int maxTries = NumberUtils.toInt(_maxTries, DEFAULT_MAX_UNLOCK_ATTEMPTS_BEFORE_WIPE);
                    mainPrefActivity.setMaximumFailedPasswordsForWipe((Boolean) o, maxTries);
                    return true;
                }
            };

    /**
     * Listener for the max_unlock_attempts_for_wipe EditTextPreference
     */
    private static final Preference.OnPreferenceChangeListener sMaxUnlockAttemptsBeforeWipeListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    ControlYourDeviceActivity mainPrefActivity =
                            ((ControlYourDeviceActivity) preference.getContext());

                    if (!mainPrefActivity.hasDeviceAdmin()) {
                        mainPrefActivity.showPermissionExplanation();
                        return false;
                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainPrefActivity);
                    String key = mainPrefActivity.getString(R.string.key_wipe_after_too_many_unlock_attempts);
                    boolean enabled = preferences.getBoolean(key, false);
                    int maxTries = NumberUtils.toInt((String) o, DEFAULT_MAX_UNLOCK_ATTEMPTS_BEFORE_WIPE);
                    mainPrefActivity.setMaximumFailedPasswordsForWipe(enabled, maxTries);
                    return true;
                }
            };

    /**
     * Listener for the restart_after_too_many_unlock_attempts SwitchPreference
     */
    private static final Preference.OnPreferenceChangeListener sRestartAfterTooManyUnlockAttemptsListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    ControlYourDeviceActivity mainPrefActivity =
                            ((ControlYourDeviceActivity) preference.getContext());

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        mainPrefActivity.showUnsupportedAndroidVersion();
                        return false;
                    }

                    DevicePolicyManager dpm = mainPrefActivity.getDPM();
                    ComponentName deviceOwnerComponent = mainPrefActivity.getDeviceOwnerComponent();

                    if (!mainPrefActivity.hasDeviceAdmin()) {
                        mainPrefActivity.promptDeviceAdmin();
                        return false;
                    }

                    if (!dpm.isDeviceOwnerApp(deviceOwnerComponent.getPackageName())) {
                        mainPrefActivity.showDeviceOwnershipExplanation();
                        return false;
                    }

                    return true;
                }
            };

    /**
     * Main preference fragment.
     */
    public static class DevControlPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            findPreference("disableFingerprintLockscreen")
                    .setOnPreferenceChangeListener(sFingerprintLockscreenListener);
            findPreference(getString(R.string.key_wipe_after_too_many_unlock_attempts))
                    .setOnPreferenceChangeListener(sWipeAfterTooManyUnlockAttemptsListener);
            findPreference(getString(R.string.key_max_unlock_attempts_before_wipe))
                    .setOnPreferenceChangeListener(sMaxUnlockAttemptsBeforeWipeListener);
            findPreference(getString(R.string.key_restart_after_too_many_unlock_attempts))
                    .setOnPreferenceChangeListener(sRestartAfterTooManyUnlockAttemptsListener);
        }

    }

    /**
     * Calls the method setMaximumFailedPasswordsForWipe from DevicePolicyManager wit the correct
     * parameter.
     * if @param enabled == false -> setMaximumFailedPasswordsForWipe(0),
     * else setMaximumFailedPasswordsForWipe(@param maxTries)
     * @param enabled
     * @param maxTries
     */
    private void setMaximumFailedPasswordsForWipe(boolean enabled, int maxTries) {
        int tries = enabled ? maxTries : DISABLE_DEVICE_UNLOCK_PROTECTION;
        try {
            DevicePolicyManager dpm = getDPM();
            ComponentName deviceOwnerComponent = getDeviceOwnerComponent();
            Log.i(LOG_TAG, "Set setMaximumFailedPasswordsForWipe to " + tries);
            dpm.setMaximumFailedPasswordsForWipe(deviceOwnerComponent, tries);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "DevicePolicyManager.setMaximumFailedPasswordsForWipe failed", e);
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || DevControlPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceOwnerComponent = new ControlDeviceAdminReceiver().getWho(this);
        setupActionBar();
    }

    private ComponentName getDeviceOwnerComponent() {
        return mDeviceOwnerComponent;
    }

    private DevicePolicyManager getDPM() {
        return mDPM;
    }

    /**
     * Checks if the app has device admin privs.
     * */
    private boolean hasDeviceAdmin() { return mDPM.isAdminActive(mDeviceOwnerComponent);}

    /**
     * Show android system dialog to request device admin permission.
     * */
    private void promptDeviceAdmin() {
            Intent requestDeviceAdminIntent =
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            requestDeviceAdminIntent
                    .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceOwnerComponent);
            startActivityForResult(requestDeviceAdminIntent, ACTIVATION_REQUEST);
    }

    private void promptDeviceOwnership() {
        try {
            Process process = Runtime.getRuntime().exec(
                    new String[] { "su", "-c", "dpm set-device-owner " + mDeviceOwnerComponent.flattenToString()});
            process.waitFor();
        } catch (InterruptedException|IOException ex) {
            Log.w(LOG_TAG, "Cannot set the device-owner with a root command - maybe the device is not rooted or the user didn't give root permission to the app.", ex);
        }

        if (!mDPM.isDeviceOwnerApp(mDeviceOwnerComponent.getPackageName())) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/linux-colonel/AdminControl")));
        }
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
       ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Do not show the back button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * Shows the explanation of permissions before requesting them.
     * */
    private void showPermissionExplanation(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_explain_permissions_title)
                .setMessage(R.string.dialog_explain_permissions)
                .setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       promptDeviceAdmin();
                    }
                    })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private void showUnsupportedAndroidVersion() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_error_title)
                .setMessage("This feature is only supported for Android 7.0 or above")
                .setPositiveButton(R.string.dialog_ok, null)
                .show();
    }

    private void showDeviceOwnershipExplanation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_explain_permissions_title)
                .setMessage(R.string.dialog_explain_device_owner_permission)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        promptDeviceOwnership();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
}
