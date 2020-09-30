package com.touchlogger.touch;

import java.util.ArrayList;

public class TouchEventCapture {

    protected final int dimX;

    protected final int dimY;

    protected final ArrayList<ArrayList<TouchEvent>> concurrentTouchEvents;

    public TouchEventCapture(int dimX, int dimY, ArrayList<ArrayList<TouchEvent>> concurrentTouchEvents) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.concurrentTouchEvents = concurrentTouchEvents;
    }

}
