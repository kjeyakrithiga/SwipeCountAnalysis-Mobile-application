package com.touchlogger.touch;

import java.util.ArrayList;

public interface TouchEventsSink {

    void onTouchEvents(ArrayList<TouchEvent> touchEvents);
}
