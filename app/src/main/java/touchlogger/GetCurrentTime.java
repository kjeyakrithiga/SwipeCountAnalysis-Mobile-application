package com.touchlogger;

import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetCurrentTime  {

    private static Context context;
    public GetCurrentTime(Context context){
        this.context = context;
    }

    public static String getTime(){
        Calendar calendar = Calendar.getInstance();
//        Date currentLocalTime = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("KK:MM:SS a");
        String time = dateFormat.format(calendar.getTime());
    //    Toast.makeText(context,""+time,Toast.LENGTH_LONG).show();
        return time;
    }
}