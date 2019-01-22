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

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

import static com.davidshewitt.admincontrol.ControlYourDeviceActivity.*;


public class ControlDeviceAdminReceiver extends DeviceAdminReceiver {
    public static final int DEFAULT_MAX_UNLOCK_ATTEMPTS_BEFORE_RESTART = 10;

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        advancedBruteForceProtection(context);
    }

    /**
     * Get the ComponentName for this class. This is necessary for a lot of DevicePolicyManager methods.
     * @return
     */
    protected static ComponentName getComponentName() {
        Class<ControlDeviceAdminReceiver> clazz = ControlDeviceAdminReceiver.class;
        return new ComponentName(clazz.getPackage().getName(), clazz.getName());
    }

    /**
     * If the device has too many failed unlock attempts, then restart it (depending on the configuration
     * given by the user.
     * @param context
     */
    @SuppressLint("NewApi") // because Lint does not detect my first if-condition checking the API
    private void advancedBruteForceProtection(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.i(LOG_TAG, "This android version doesn't support DevicePolicyManager.reboot()");
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(context.getString(R.string.key_restart_after_too_many_unlock_attempts), false)) {
            Log.i(LOG_TAG, "Advanced unlock brute-force protection is disabled");
            return;
        }

        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        Objects.requireNonNull(dpm);

        String key = context.getString(R.string.key_max_unlock_attempts_before_restart);
        String _maxAttempts = preferences.getString(key, null);
        int maxAttempts = NumberUtils.toInt(_maxAttempts, DEFAULT_MAX_UNLOCK_ATTEMPTS_BEFORE_RESTART);

        int currentFailedPasswordAttempts = dpm.getCurrentFailedPasswordAttempts();
        if (currentFailedPasswordAttempts <= maxAttempts) {
            Log.i(LOG_TAG, "current failed password attempts: " + currentFailedPasswordAttempts);
            Log.i(LOG_TAG, "max failed attempts for reboot: " + maxAttempts);
            return;
        }

        try {
            Log.i(LOG_TAG, "Execute an reboot.");
            dpm.reboot(getComponentName());
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "Cant reboot device due to an ongoing call.", e);
        }
    }
}
