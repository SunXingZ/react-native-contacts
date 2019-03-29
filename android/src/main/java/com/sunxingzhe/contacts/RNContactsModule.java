
package com.sunxingzhe.contacts;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.content.ContentResolver;
import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;

import java.util.Map;

public class RNContactsModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  private final ReactApplicationContext reactContext;
  protected Promise callback;

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
      this.callback.reject("false");
      this.callback = null;
      return;
    }
  }

  @ReactMethod
  public void launchContact(ReadableMap options, final Promise callback) {
    this.callback = callback;
    final Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      this.callback.reject("false");
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
          return;
        }
        if (data.getData() != null) {
          ContentResolver reContentResolverol = getReactApplicationContext().getContentResolver();
          Cursor cursor = reContentResolverol.query(data.getData(), new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },null, null, null);
          if (cursor != null && cursor.moveToFirst()) {
            params.putString("givenName", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            params.putString("phoneNumber", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            this.callback.resolve(params);
          }
        } else {
          this.callback.reject("false");
        }
      } else {
        this.callback.reject("false");
      }
    }
    this.callback = null;
  }

  @Override
  public void onNewIntent(Intent intent) {}
}