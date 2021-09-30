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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

/**
 * A {@link PreferenceActivity} that lets the user control advanced security settings on their
 * device.
 */
public class ControlYourDeviceActivity extends AppCompatPreferenceActivity {
    private static final int ACTIVATION_REQUEST = 1;

    private AdminControls adminControls;

    /**
     * Preference change listener for setting fingerprint policy on the Lock Screen.
     */
    private static final Preference.OnPreferenceChangeListener sFingerprintLockscreenListener
            = (preference, newValue) -> {
        ControlYourDeviceActivity activity = ((ControlYourDeviceActivity) preference.getContext());

        if (!activity.adminControls.hasDeviceAdmin()) {
            activity.showPermissionExplanation();
            return false;
        }

        if (activity.adminControls.setFingerprintEnabled(!(Boolean) newValue)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FingerprintToggleTileService.triggerUpdate(activity);
            }
            return true;
        }
        return false;
    };

    /**
     * Preference click listener for locking the screen
     * and forcing strong authentication for the next unlock.
     */
    private static final Preference.OnPreferenceClickListener lockNowListener = preference -> {
        ControlYourDeviceActivity activity = ((ControlYourDeviceActivity)preference.getContext());

        if (!activity.adminControls.hasDeviceAdmin()) {
            activity.showPermissionExplanation();
            return false;
        }

        if (activity.adminControls.lockRequiringStrongAuth()) {
            activity.finish();
        }
        return true;
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
            findPreference("lockNow")
                    .setOnPreferenceClickListener(lockNowListener);
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
        adminControls = new AdminControls(this);
        setupActionBar();
    }

    /**
     * Show android system dialog to request device admin permission.
     */
    private void promptDeviceAdmin() {
        Intent requestDeviceAdminIntent =
                new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        requestDeviceAdminIntent
                .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        adminControls.getDeviceOwnerComponent());
        startActivityForResult(requestDeviceAdminIntent, ACTIVATION_REQUEST);
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
}
