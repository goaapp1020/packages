package io.flutter.plugins.googlemaps;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter;

public class GoogleMapsPlugin implements FlutterPlugin, ActivityAware {

  @Nullable Lifecycle lifecycle;

  private static final String VIEW_TYPE = "plugins.flutter.dev/google_maps_android";

  public GoogleMapsPlugin() {}

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    binding
            .getPlatformViewRegistry()
            .registerViewFactory(
                    VIEW_TYPE,
                    new GoogleMapFactory(
                            binding.getBinaryMessenger(),
                            binding.getApplicationContext(),
                            () -> lifecycle));
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {}

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    lifecycle = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  private static final class ProxyLifecycleProvider
          implements ActivityLifecycleCallbacks, LifecycleOwner, LifecycleProvider {

    private final LifecycleRegistry lifecycle = new LifecycleRegistry(this);
    private final int registrarActivityHashCode;

    ProxyLifecycleProvider(Activity activity) {
      this.registrarActivityHashCode = activity.hashCode();
      activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
      if (activity.hashCode() == registrarActivityHashCode) lifecycle.handleLifecycleEvent(Event.ON_CREATE);
    }

    @Override public void onActivityStarted(Activity activity) {
      if (activity.hashCode() == registrarActivityHashCode) lifecycle.handleLifecycleEvent(Event.ON_START);
    }

    @Override public void onActivityResumed(Activity activity) {
      if (activity.hashCode() == registrarActivityHashCode) lifecycle.handleLifecycleEvent(Event.ON_RESUME);
    }

    @Override public void onActivityPaused(Activity activity) {
      if (activity.hashCode() == registrarActivityHashCode) lifecycle.handleLifecycleEvent(Event.ON_PAUSE);
    }

    @Override public void onActivityStopped(Activity activity) {
      if (activity.hashCode() == registrarActivityHashCode) lifecycle.handleLifecycleEvent(Event.ON_STOP);
    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override public void onActivityDestroyed(Activity activity) {
      if (activity.hashCode() == registrarActivityHashCode) {
        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
        lifecycle.handleLifecycleEvent(Event.ON_DESTROY);
      }
    }

    @NonNull @Override public Lifecycle getLifecycle() {
      return lifecycle;
    }
  }
}