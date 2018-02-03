package com.davidshewitt.admincontrol;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class ControlDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onPasswordFailed(Context context, Intent intent) {

    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {

    }
}
