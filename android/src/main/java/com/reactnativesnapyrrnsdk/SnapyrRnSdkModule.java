package com.reactnativesnapyrrnsdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.util.Log;

import java.util.Objects;

import com.facebook.react.bridge.AssertionException;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;


import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.snapyr.sdk.Snapyr;
import com.snapyr.sdk.Traits;
import com.snapyr.sdk.Properties;
import com.snapyr.sdk.http.ConnectionFactory;
import com.snapyr.sdk.inapp.InAppCallback;
import com.snapyr.sdk.inapp.InAppConfig;
import com.snapyr.sdk.inapp.InAppMessage;

@ReactModule(name = SnapyrRnSdkModule.NAME)
public class SnapyrRnSdkModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    public static final String NAME = "SnapyrRnSdk";
    private Boolean snapyrConfigured = false;
    private Boolean activityCallbacksReplayed = false;

    public SnapyrRnSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    // See https://reactnative.dev/docs/native-modules-android

    @ReactMethod
    public void configure(String withKey, ReadableMap options, Promise promise) {
      try {
        Snapyr.Builder builder = new Snapyr.Builder(this.getCurrentActivity(), withKey)
        .flushQueueSize(1) // makes every event flush to network immediately
        .trackApplicationLifecycleEvents() // Enable this to record certain application events automatically
        .recordScreenViews() // Enable this to record screen views automatically
        .enableSnapyrPushHandling() // enable push for Android
        .configureInAppHandling(
          new InAppConfig()
            .setActionCallback(
              new InAppCallback() {
                @Override
                public void onAction(InAppMessage inAppMessage) {
                  handleInAppMessage(inAppMessage);
                }
              }
            ));

        if (options.hasKey("snapyrEnvironment")) {
          try {
            ConnectionFactory.Environment env = ConnectionFactory.Environment.values()[options.getInt("snapyrEnvironment")];
            builder.snapyrEnvironment(env);
          } catch (Exception e) {
            Log.e("Snapyr", "Invalid environment provided");
          }
        }

        Snapyr snapyr = builder.build();

        // Set the initialized instance as a globally accessible instance.
        Snapyr.setSingletonInstance(snapyr);
        snapyrConfigured = true;
        this.replayActivityCallbacks();
        promise.resolve(withKey);
      } catch(Exception e) {
        Log.d("Snapyr", "Config errored");
        promise.reject("Error initializing Snapyr", e);
      }
    }

    @ReactMethod
    public void identify(String userId, ReadableMap traitsMap, Promise promise) {
      try {
        if (!Snapyr.Valid()) {
          Log.d("Snapyr", "Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          promise.reject("Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          return;
        }

        Traits traits = new Traits();
        if (!Objects.isNull(traitsMap)) {
          traits.putAll(traitsMap.toHashMap());
        }
        Log.d("Snapyr", "identify" + userId);
        Snapyr inst = Snapyr.with(this.getCurrentActivity());
        Log.d("Snapyr", inst.toString());
        inst.identify(userId, traits, null);
        promise.resolve(null);
      } catch (Exception e) {
        Log.d("Snapyr", "Error on identify");
        promise.reject("Error on identify", e);
      }
    }


    @ReactMethod
    public void track(String eventName, ReadableMap props, Promise promise) {
      try {
        if (!Snapyr.Valid()) {
          Log.d("Snapyr", "Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          promise.reject("Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          return;
        }

        Properties properties = new Properties();
        if (!Objects.isNull(props)) {
          properties.putAll(props.toHashMap());
        }

        Log.d("Snapyr", "snapyr track " + eventName);
        Snapyr inst = Snapyr.with(this.getCurrentActivity());
        inst.track(eventName, properties, null);
        promise.resolve(null);
      } catch (Exception e) {
        Log.d("Snapyr", "Error on track");
        promise.reject("Error on track", e);
      }
    }

    @ReactMethod
    public void setPushNotificationToken(String token, Promise promise) {
      try {
        if (!Snapyr.Valid()) {
          Log.d("Snapyr", "Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          promise.reject("Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          return;
        }

        Log.d("Snapyr", "snapyr setPushNotificationToken: " + token);
        Snapyr inst = Snapyr.with(this.getCurrentActivity());
        inst.setPushNotificationToken(token);
        promise.resolve(null);
      } catch (Exception e) {
        Log.d("Snapyr", "Error on setPushNotificationToken");
        promise.reject("Error on setPushNotificationToken", e);
      }
    }

    @ReactMethod
    public void pushNotificationReceived(ReadableMap props, Promise promise) {
      try {
        if (!Snapyr.Valid()) {
          Log.d("Snapyr", "Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          promise.reject("Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          return;
        }

        Properties properties = new Properties();
        if (!Objects.isNull(props)) {
          properties.putAll(props.toHashMap());
        }

        Log.d("Snapyr", "snapyr pushNotificationReceived");
        Snapyr inst = Snapyr.with(this.getCurrentActivity());
        inst.pushNotificationReceived(properties);
        promise.resolve(null);
      } catch (Exception e) {
        Log.d("Snapyr", "Error on pushNotificationReceived");
        promise.reject("Error on pushNotificationReceived", e);
      }
    }

    @ReactMethod
    public void pushNotificationTapped(ReadableMap props, @Nullable String actionId, Promise promise) {
      try {
        if (!Snapyr.Valid()) {
          Log.d("Snapyr", "Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          promise.reject("Snapyr SDK has not yet been configured. Call `configure()` before using this method.");
          return;
        }

        Properties properties = new Properties();
        if (!Objects.isNull(props)) {
          properties.putAll(props.toHashMap());
        }

        Log.d("Snapyr", "snapyr pushNotificationTapped");
        Snapyr inst = Snapyr.with(this.getCurrentActivity());
        inst.pushNotificationClicked(properties);
        promise.resolve(null);
      } catch (Exception e) {
        Log.d("Snapyr", "Error on pushNotificationTapped");
        promise.reject("Error on pushNotificationTapped", e);
      }
    }

    @ReactMethod
    public void reset(Promise promise) {
      try {
        Snapyr inst = Snapyr.with(this.getCurrentActivity());
        Snapyr.clearSingleton();
        inst.shutdown();
        promise.resolve(null);
      } catch (Exception e) {
        Log.d("Snapyr", "Error on reset");
        promise.reject("Error on reset", e);
      }
    }

    /**
     Native Android -> React Native events (trigger RN callbacks from native)
     */

    @ReactMethod
    public void addListener(String eventName) {
      // Set up any upstream listeners or background tasks as necessary
      // Stub - nothing to do here so far but method signature is required
    }

    @ReactMethod
    public void removeListeners(Integer count) {
      // Remove upstream listeners, stop unnecessary background tasks
      // Stub - nothing to do here so far but method signature is required
    }

    private void handleInAppMessage(InAppMessage message) {
      WritableNativeMap map = Arguments.makeNativeMap(message.asValueMap());
      this.getReactApplicationContext()
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit("snapyrInAppMessage", map);
    }

    /**
     * React Native typically starts the main activity before intializing native modules like this one.
     * As a result, Snapyr's activity lifecycle callbacks will be registered after some lifecycle events
     * are already finished, and those callbacks won't be automatically triggered for the main activity.
     * After we have the activity AND Snapyr is initialized, manually "replay" the activity lifecycle
     * events up to "resume" to ensure Snapyr tracks them and knows about the activity.
     * NB Snapyr will handle deduplication of these calls automatically
     */
    public void replayActivityCallbacks() {
      Activity activity = this.getCurrentActivity();
      if (!snapyrConfigured || activityCallbacksReplayed || activity == null) {
        return;
      }

      Snapyr inst = Snapyr.with(activity);

      inst.replayLifecycleOnActivityCreated(activity, null);
      inst.replayLifecycleOnActivityStarted(activity);
      inst.replayLifecycleOnActivityResumed(activity);
      this.activityCallbacksReplayed = true;
    }

    @Override
    public void onHostResume() {
      // Called when React Native moves the main activity to ready state. "Replay" should generally
      // take place in {@link #configure()}, but try again here just in case resume occurs after
      // initialization.
      this.replayActivityCallbacks();
    }

    @Override
    public void onHostPause() {
      // stub for LifecycleEventListener interface
    }

    @Override
    public void onHostDestroy() {
      // stub for LifecycleEventListener interface
    }
}
