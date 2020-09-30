package com.touchlogger.touch;

import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

public class TouchPoint {
    /** X coordinate of touch event */
    public Integer  x;
    /** Y coordinate of touch event */
    public Integer  y;

    /** pressure of touch event */
    @SerializedName("p")
    public Integer pressure;
    /** Timestamp of touch event */
    @SerializedName("t")
    public Long timestamp;

    public TouchPoint(int x, int y, int p, long timestamp) {
        this.x = x;
        this.y = y;
        this.pressure = p;
        this.timestamp = timestamp;
//        Toast.makeText(TouchPoint.this,"Touch detevted",Toast.LENGTH_LONG).show();
    }

    public TouchPoint(TouchPoint other) {
        this.x = other.x;
        this.y = other.y;
        this.pressure = other.pressure;
        this.timestamp = other.timestamp;
    }

    public TouchPoint() {

    }

    public boolean isEmpty() {
        return x == null && y == null && pressure == null;
    }

}