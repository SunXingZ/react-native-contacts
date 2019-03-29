
package com.sunxingzhe.contacts;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNContactsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNContactsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNContacts";
  }
}