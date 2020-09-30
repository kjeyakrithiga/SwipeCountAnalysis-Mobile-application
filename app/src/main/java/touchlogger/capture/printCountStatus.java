package com.touchlogger.capture;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
//import com.touchlogger.MainActivity;
import com.touchlogger.GraphViewActivity;
import com.touchlogger.MainActivity;
import com.touchlogger.R;

public class printCountStatus extends Activity implements Runnable, BackgroundTask.GetMyTaskListener {
    static Context context;
    TextView txtView;
    TextView weekView;
    TextView yesterdayView;
    TextView maxView;
    TextView totalView;
    TextView avgView;

    int count;
    String cnt = "0";
    public static String today = "0";
    String week = "0";
    String yesterday = "0";
    String max = "0";
    String totalDays = "0";
    String avg = "0";

    int fetchData;
    public CreateAlert alert;
    BackgroundTask backgroundTask;
    public printCountStatus(MainActivity context) {
        this.context = context;
        alert = new CreateAlert(context);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void Update(int x,String status) {
        txtView = (TextView) ((MainActivity) context).findViewById(R.id.myText);
        count = x;

        backgroundTask = new BackgroundTask(context,this);

        backgroundTask.execute("add_info",status,Integer.toString(count));
        backgroundTask = new BackgroundTask(context,this);
        backgroundTask.execute("fetchTodaySwipes");
        backgroundTask = new BackgroundTask(context,this);
        backgroundTask.execute("fetchWeekSwipes");
        backgroundTask = new BackgroundTask(context,this);
        backgroundTask.execute("fetchYesterdaySwipes");
        backgroundTask = new BackgroundTask(context,this);
        backgroundTask.execute("fetchMaximumSwipes");
        backgroundTask = new BackgroundTask(context,this);
        backgroundTask.execute("fetchTotalSwipes");
        backgroundTask = new BackgroundTask(context,this);
        backgroundTask.execute("fetchAverageSwipes");
//        GraphViewActivity graph = new GraphViewActivity();


        if(Integer.parseInt(today)>0 && Integer.parseInt(today)%20==0){
//            Log.d("PrintingCount"," "+count+" today : "+Integer.parseInt(today));
            alert.setMessage();
        }
        run();

    }
    @Override
    public void onGetMyTaskComplete(String response) {
        if(response.length()>8 && response.substring(response.length()-8).equals("add_info")){
            cnt=response.substring(0,response.length()-8);
        }
        else if(response.length()>5 && response.substring(response.length()-5).equals("today")){
            today = response.substring(0,response.length()-5);
            Log.d("DB operations","today value : "+response.substring(0,response.length()-5)+" length : "+response.length());
            count = Integer.parseInt(today);
            Log.d("PrintingCount"," On get my task complete called :  "+count+" today : "+today);
        }
        else if(response.length()>4 && response.substring(response.length()-4).equals("week")){
            week = response.substring(0,response.length()-4);
            Log.d("DB operations","week value : "+response.substring(0,response.length()-4)+" length : "+response.length());
        }
        else if(response.length()>9 && response.substring(response.length()-9).equals("yesterday")){
            yesterday = response.substring(0,response.length()-9);
            Log.d("DB operations","yesterday value : "+response.substring(0,response.length()-9)+" length : "+response.length());
        }
        else if(response.length()>3 && response.substring(response.length()-3).equals("max")){
            max = response.substring(0,response.length()-3);
            Log.d("DB operations","max value : "+response.substring(0,response.length()-3)+" length : "+response.length());
        }
        else if(response.length()>5 && response.substring(response.length()-5).equals("total")){
            totalDays = response.substring(0,response.length()-5);
            Log.d("DB operations","total days : "+response.substring(0,response.length()-5)+" length : "+response.length());
        }
        else if(response.length()>7 && response.substring(response.length()-7).equals("average")){
            avg = response.substring(0,response.length()-7);
            Log.d("DB operations","average : "+response.substring(0,response.length()-7)+" length : "+response.length());
        }
        else{

        }

    }


    @Override
    public void run() {
        txtView = (TextView) ((MainActivity) context).findViewById(R.id.myText);
        weekView = (TextView) ((MainActivity) context).findViewById(R.id.myTextWeek);
        yesterdayView = (TextView) ((MainActivity) context).findViewById(R.id.myTextYesterday);
        maxView = (TextView) ((MainActivity) context).findViewById(R.id.myTextMaximum);
        totalView = (TextView) ((MainActivity) context).findViewById(R.id.myTextTotal);
        avgView = (TextView) ((MainActivity) context).findViewById(R.id.myTextAverage);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                txtView.setText(today);
                weekView.setText(week);
                yesterdayView.setText(yesterday);
                maxView.setText(max);
                totalView.setText(totalDays);
                avgView.setText(avg);
            }
        });
    }

}
