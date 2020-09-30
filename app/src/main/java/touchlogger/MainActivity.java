package com.touchlogger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.touchlogger.capture.CaptureIntentMessage;
import com.touchlogger.capture.CaptureService;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<CaptureIntentMessage, Intent> intents;
    static TextView textView;
    static TextView yesterdayView;
    static TextView weekView;
    static TextView maxView;
    static TextView totalView;
    static TextView avgView;
    static MainActivity activity ;
    static Button btn,btn1;

    DatabaseHandler databaseHandler;
    String cnt;
    String yesterday;
    String week;
    String max;
    String total;
    String avg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHandler = new DatabaseHandler(this,true);

        cnt = databaseHandler.fetchTodaySwipes();
        yesterday = databaseHandler.fetchYesterdaySwipes();
        week = databaseHandler.fetchWeekSwipes();
        max = databaseHandler.fetchMaximumSwipesPerDay();
        total = databaseHandler.fetchTotalDays();
        avg = databaseHandler.fetchAverageSwipes();

        textView = (TextView) findViewById(R.id.myText);
        yesterdayView = (TextView) findViewById(R.id.myTextYesterday);
        weekView = (TextView) findViewById(R.id.myTextWeek);
        maxView = (TextView) findViewById(R.id.myTextMaximum);
        totalView = (TextView) findViewById(R.id.myTextTotal);
        avgView = (TextView) findViewById(R.id.myTextAverage);

        textView.setText(cnt);
        yesterdayView.setText(yesterday);
        weekView.setText(week);
        maxView.setText(max);
        avgView.setText(avg);
        totalView.setText(total);
        
        btn = (Button) findViewById(R.id.graph);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGraph();
            }
        });
        btn1 = (Button) findViewById(R.id.bar_graph);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayBarGraph();
            }
        });

        intents = new HashMap<>();
        activity = this;
        for (CaptureIntentMessage message : CaptureIntentMessage.values()) {
            Intent intent = new Intent(this, CaptureService.class);
            intent.setAction(message.name());
            intents.put(message, intent);
        }
//            textView.setText("helllooo");
//        Toast.makeText("this","starting");
        startService(intents.get(CaptureIntentMessage.START));


    }

    public static MainActivity getInstance(){
        if(textView.getText()==""){
//            Toast.makeText(MainActivity.this,textView.getText(),Toast.LENGTH_LONG).show();
            textView.setText("HELLO_1");
        }
        else{
  //          Toast.makeText(MainActivity.this,"Error loading text!!!",Toast.LENGTH_LONG).show();

        }
        return activity;
    }
    public TextView getTextView(){
        return textView;
    }

    public void startButtonClick(View v) {
        startService(intents.get(CaptureIntentMessage.START));

    }

    public void stopButtonClick(View v) {
        startService(intents.get(CaptureIntentMessage.STOP));

    }
    public void displayGraph(){
        Log.d("displayactivity","displayGraph");
        Intent intent = new Intent(this, GraphViewActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right,
                R.anim.slide_out_left);
    }

    public void displayBarGraph(){
        Log.d("displayactivity","displayBarGraph");
        Intent intent = new Intent(this, BarGraph.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right,
                R.anim.slide_out_left);
    }


}