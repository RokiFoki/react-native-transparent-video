package com.transparentvideo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.common.MapBuilder;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.bridge.ReadableMap;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransparentVideoViewManager extends SimpleViewManager<LinearLayout> {

  private static List<LinearLayout> sInstances = new ArrayList<>();

  public static final String REACT_CLASS = "TransparentVideoView";
  private static final String TAG = "TransparentVideoViewManager";

  ReactApplicationContext reactContext;

  public TransparentVideoViewManager(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  private boolean eventDispatching = false;
  @Override
  @NonNull
  public LinearLayout createViewInstance(ThemedReactContext reactContext) {
    LinearLayout view = new LinearLayout(this.reactContext);
    AlphaMovieView alphaMovieView = new AlphaMovieView(reactContext, null);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
    lp.gravity = Gravity.CENTER;
    alphaMovieView.setLayoutParams(lp);

    alphaMovieView.setOnVideoEndedListener(() -> {
      if (eventDispatching) return;

      eventDispatching = true;
    
      // ((ReactContext) this.reactContext)
      //   .getJSModule(RCTEventEmitter.class)
      //   .receiveEvent(alphaMovieView.getId(), "onEnd", null);
    
      // Reset the flag after a short delay to avoid stack loops
      new Handler().postDelayed(() -> {
        eventDispatching = false;
      }, 100);
    });

    view.addView(alphaMovieView);
    sInstances.add(view);
    return view;
  }

  public static void destroyView(LinearLayout view) {
    sInstances.remove(view);
  }

  @ReactProp(name = "src")
  public void setSrc(LinearLayout view, ReadableMap src) {
    AlphaMovieView alphaMovieView = (AlphaMovieView)view.getChildAt(0);
    if (alphaMovieView == null) {
      alphaMovieView = new AlphaMovieView(reactContext, null);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
      lp.gravity = Gravity.CENTER;
      alphaMovieView.setLayoutParams(lp);
      alphaMovieView.setAutoPlayAfterResume(true);
      view.addView(alphaMovieView);
    }
    alphaMovieView.setPacked(true);
    String file = src.getString("uri").toLowerCase();
    Log.d(TAG + " setSrc", "file: " + file);

    try {
      Integer rawResourceId = Utils.getRawResourceId(reactContext, file);
      Log.d(TAG + " setSrc", "ResourceID: " + rawResourceId);

      alphaMovieView.setVideoFromResourceId(reactContext, rawResourceId);
    } catch (RuntimeException e) {
      Log.e(TAG + " setSrc", e.getMessage(), e);
      alphaMovieView.setVideoByUrl(file);
    }
  }

  @ReactProp(name = "loop", defaultBoolean = true)
  public void setLoop(LinearLayout view, boolean loop) {
    AlphaMovieView alphaMovieView = (AlphaMovieView)view.getChildAt(0);
    if (alphaMovieView != null) {
      alphaMovieView.setLoop(loop);
    }
  }

  @Override
  public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.of(
      "onEnd", // rename to align with JS
      MapBuilder.of("registrationName", "onEnd")
    );
  }
}
