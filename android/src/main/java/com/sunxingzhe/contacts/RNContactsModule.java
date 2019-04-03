
package com.sunxingzhe.contacts;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.provider.ContactsContract;
import android.content.ContentResolver;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.provider.Settings;
import android.Manifest;
import android.os.Build;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.modules.core.PermissionAwareActivity;

import java.util.Map;

public class RNContactsModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  private final ReactApplicationContext reactContext;
  protected Promise callback;

  private PermissionListener listener = new PermissionListener() {
    public boolean onRequestPermissionsResult(final int requestCode,@NonNull final String[] permissions,@NonNull final int[] grantResults) {
      boolean permissionsGranted = true;
      for (int i = 0; i < permissions.length; i++) {
        final boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
        permissionsGranted = permissionsGranted && granted;
      }
      if (callback == null) {
        return false;
      }
      callback.resolve(permissionsGranted ? "true" : "false");
      callback = null;
      return true;
    }
  };

  public RNContactsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.reactContext.addActivityEventListener(this);
  }

  @Override
  public String getName() {
    return "RNContacts";
  }

  @ReactMethod
  public void authorization(final Promise callback) {
    this.callback = callback;
    final Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      this.callback.reject("can't find current Activity.");
      this.callback = null;
      return;
    }
    boolean hasPermission = ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    if (hasPermission) {
      this.callback.resolve("true");
    } else {
      final Boolean dontAskAgain = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_CONTACTS);
      if (dontAskAgain) {
        this.callback.reject("false");
        this.callback = null;
      } else {
        if (currentActivity instanceof ReactActivity) {
          ((ReactActivity) currentActivity).requestPermissions(new String[]{ Manifest.permission.READ_CONTACTS }, 1, listener);
        } else if (currentActivity instanceof PermissionAwareActivity) {
          ((PermissionAwareActivity) currentActivity).requestPermissions(new String[]{ Manifest.permission.READ_CONTACTS }, 1, listener);
        } else {
          this.callback.reject("false");
          this.callback = null;
        }
      }
    }
  }

  @ReactMethod
  public void openSettings(final Promise callback) {
    this.callback = callback;
    final Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      this.callback.reject("can't find current Activity.");
      this.callback = null;
      return;
    }
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", getReactApplicationContext().getPackageName(), null);
    intent.setData(uri);
    currentActivity.startActivityForResult(intent, 2);
  }

  @ReactMethod
  public void launchContact(ReadableMap options, final Promise callback) {
    this.callback = callback;
    final Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      this.callback.reject("can't find current Activity.");
      this.callback = null;
      return;
    }
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
    currentActivity.startActivityForResult(intent, 1);
  }

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    WritableMap params = Arguments.createMap();
    if(resultCode == Activity.RESULT_OK){
      if (requestCode == 1) {
        if(data == null){
          this.callback.reject("false");
          this.callback = null;
          return;
        }
        if (data.getData() != null) {
          ContentResolver reContentResolverol = getReactApplicationContext().getContentResolver();
          Cursor cursor = reContentResolverol.query(data.getData(), new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },null, null, null);
          if (cursor != null && cursor.moveToFirst()) {
            params.putString("givenName", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            params.putString("familyName", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            params.putString("phoneNumber", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            this.callback.resolve(params);
          }
        } else {
          this.callback.reject("false");
        }
      } else if (requestCode == 2) {
        
      }
    }
    this.callback = null;
  }

  @Override
  public void onNewIntent(Intent intent) {}
}