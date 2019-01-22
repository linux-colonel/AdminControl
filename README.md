AdminControl
============
This app allows you to control features that are in the 
[Android Device Admin API](https://developer.android.com/guide/topics/admin/device-admin.html)
 but not present in Settings.
 
[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/packages/com.davidshewitt.admincontrol/) 
 
 Current Features
 ----------------
 * Disable Fingerprint on the Lock Screen

 Grant the app device owner permission for non-rooted smartphones
 ----------------------------------------------------------------
 * This is necessary when you want to use the "restart after too many incorrect unlock attempts"
 * You have to install the Android Debug Bridge (adb) on your computer
 * Enable Android-Debugging on your smartphone and connect it to your computer
 * Execute `adb shell dpm set-device-owner com.davidshewitt.admincontrol/com.davidshewitt.admincontrol.ControlDeviceAdminReceiver`
 * Done

 Revoke device owner permission for uninstalling the app
 -------------------------------------------------------
 * Execute `adb shell dpm remove-active-admin com.davidshewitt.admincontrol/com.davidshewitt.admincontrol.ControlDeviceAdminReceiver`
 
 Planned Features
 ----------------
 * Shut down or nuke device if too many incorrect password / PIN attempts.
 
 Building
 --------
 Install the Android SDK and point your `ANDROID_HOME` environment variable to it.  
 Run `./gradlew build`.  
 Outputs are in `app/build/outputs/apk`.  The APK in the debug folder is signed with the test keys and ready for use.  
 It's recommended that you sign the APK in the release folder for production use.
