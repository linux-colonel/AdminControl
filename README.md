AdminControl
============
This app allows you to control features that are in the 
[Android Device Admin API](https://developer.android.com/guide/topics/admin/device-admin.html)
 but not present in Settings.
 
 Current Features
 ----------------
 * Disable Fingerprint on the Lock Screen
 
 Planned Features
 ----------------
 * Shut down or nuke device if too many incorrect password / PIN attempts.
 
 Building
 --------
 Install the Android SDK and point your `ANDROID_HOME` environment variable to it.  
 Run `./gradlew build`.  
 Outputs are in `app/build/outputs/apk`.  The APK in the debug folder is signed with the test keys and ready for use.  
 It's recommended that you sign the APK in the release folder for production use.
