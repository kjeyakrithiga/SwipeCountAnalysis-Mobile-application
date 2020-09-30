package com.touchlogger.touch;

import java.util.ArrayList;

public class TouchEventsSource {

    private ArrayList<TouchEventsSink> sinks = new ArrayList<>();

    public void registerCallback(TouchEventsSink sink) {
        sinks.add(sink);
    }

    public void unregisterCallback(TouchEventsSink sink) {
        sinks.remove(sink);
    }

    protected void sendTouchEvents(ArrayList<TouchEvent> touchEvents) {
        for(TouchEventsSink touchEventsSink : sinks) {
            touchEventsSink.onTouchEvents(touchEvents);
        }
    }

}
