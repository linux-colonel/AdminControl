package com.davidshewitt.admincontrol;


import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;


import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class ControlYourDeviceActivity extends AppCompatPreferenceActivity {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceOwnerComponent;

    /**
     * Preference change listener for setting fingerprint policy on the Lock Screen.
     *
     */
    private static final Preference.OnPreferenceChangeListener sFingerprintLockscreenListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            DevicePolicyManager dpm = ((ControlYourDeviceActivity)preference.getContext()).getDPM();
            ComponentName deviceOwnerComponent = ((ControlYourDeviceActivity)preference.getContext()).getDeviceOwnerComponent();
            boolean bValue = (Boolean)o;
            int keyguardDisabledFeatures;
            if(bValue){
                keyguardDisabledFeatures = dpm.getKeyguardDisabledFeatures(deviceOwnerComponent) | DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT;
            } else {
                keyguardDisabledFeatures = dpm.getKeyguardDisabledFeatures(deviceOwnerComponent) & (~DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
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
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceOwnerComponent = new ControlDeviceAdminReceiver().getWho(this);
        setupActionBar();
        addPreferencesFromResource(R.xml.preferences);
        findPreference("disableFingerprintLockscreen").setOnPreferenceChangeListener(sFingerprintLockscreenListener);
    }

    private DevicePolicyManager getDPM(){
        return mDPM;
    }

    private ComponentName getDeviceOwnerComponent(){
        return mDeviceOwnerComponent;
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
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

}
