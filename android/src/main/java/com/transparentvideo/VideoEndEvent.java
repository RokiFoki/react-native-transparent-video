package com.transparentvideo;

import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class VideoEndEvent extends Event<VideoEndEvent> {
  public static final String EVENT_NAME = "onEnd";

  public VideoEndEvent(int viewId) {
    super(viewId);
  }

  @Override
  public String getEventName() {
    return EVENT_NAME;
  }

  @Override
  public void dispatch(RCTEventEmitter emitter) {
    emitter.receiveEvent(getViewTag(), getEventName(), null);
  }
}
