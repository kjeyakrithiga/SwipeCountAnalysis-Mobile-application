package com.touchlogger.capture;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.touchlogger.DatabaseHandler;

public class BackgroundTask extends AsyncTask<String,Void,String> {

    Context context;

    public interface GetMyTaskListener {
        public void onGetMyTaskComplete(String response);
    }

    String request;
    GetMyTaskListener listener;

    BackgroundTask(Context context,GetMyTaskListener listener){
        this.listener = listener;
        this.context=context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected String doInBackground(String... params) {
        Log.d("DB operations","calling background task...");
        String method = params[0];
        DatabaseHandler databaseHandler = new DatabaseHandler(context,Boolean.FALSE);
        if(method.equals("add_info")){
            Log.d("DB operations","add_info method called...");
            String status = params[1];
            String count = params[2];
            databaseHandler.insertData(status,count);
            return count;
        }
        else if(method.equals("fetchTodaySwipes")) {
            Log.d("DB operations", "fetch today swipes calling...");
            String today = databaseHandler.fetchTodaySwipes();
            return today+"today";
        }
        else if(method.equals("fetchWeekSwipes")) {
            Log.d("DB operations", "fetch week swipes calling...");
            String week = databaseHandler.fetchWeekSwipes();
            return week+"week";
        }
        else if(method.equals("fetchYesterdaySwipes")){
            String yesterday = databaseHandler.fetchYesterdaySwipes();
            return yesterday+"yesterday";
        }
        else if(method.equals("fetchMaximumSwipes")){
            String max = databaseHandler.fetchMaximumSwipesPerDay();
            return max+"max";
        }
        else if(method.equals("fetchTotalSwipes")){
            String totalDays = databaseHandler.fetchTotalDays();
            return totalDays+"total";
        }
        else if(method.equals("fetchAverageSwipes")){
            String avg = databaseHandler.fetchAverageSwipes();
            return avg+"average";
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onGetMyTaskComplete(s);
    }

}
