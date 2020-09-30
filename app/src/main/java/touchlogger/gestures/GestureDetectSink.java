package com.touchlogger.gestures;

import com.touchlogger.gestures.Gesture;
import com.touchlogger.touch.TouchEvent;

import java.util.ArrayList;


public interface GestureDetectSink {
    void onGestureDetect(Gesture gesture, ArrayList<TouchEvent> events);
}
