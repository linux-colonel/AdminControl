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
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;

/**
 * A {@link PreferenceActivity} that lets the user control advanced security settings on their
 * device.
 */
public class ControlYourDeviceActivity extends AppCompatPreferenceActivity {
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
            DevicePolicyManager dpm = ((ControlYourDeviceActivity)preference.getContext()).getDPM();
            ComponentName deviceOwnerComponent =
                    ((ControlYourDeviceActivity)preference.getContext()).getDeviceOwnerComponent();
            boolean bValue = (Boolean)o;
            int keyguardDisabledFeatures;
            if(bValue){
                keyguardDisabledFeatures =
                        dpm.getKeyguardDisabledFeatures(deviceOwnerComponent)
                                | DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT;
            } else {
                keyguardDisabledFeatures =
                        dpm.getKeyguardDisabledFeatures(deviceOwnerComponent)
                                & (~DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
            }
            try {
                dpm.setKeyguardDisabledFeatures(deviceOwnerComponent, keyguardDisabledFeatures);
            } catch (SecurityException s) {
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
        promptDeviceAdmin();
    }

    private ComponentName getDeviceOwnerComponent() {
        return mDeviceOwnerComponent;
    }

    private DevicePolicyManager getDPM() {
        return mDPM;
    }

    /**
     * Checks if the app has DeviceAdmin and prompts if it does not.
     * */
    private void promptDeviceAdmin(){
        if (! mDPM.isAdminActive(mDeviceOwnerComponent)) {
            Intent requestDeviceAdminIntent =
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            requestDeviceAdminIntent
                    .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceOwnerComponent);
            startActivityForResult(requestDeviceAdminIntent, ACTIVATION_REQUEST);
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
}
