package com.touchlogger;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BarGraph extends AppCompatActivity {

    BarChart barChart;
    DatabaseHandler databaseHandler = new DatabaseHandler(this,Boolean.FALSE);

    public int count=2;
    public ArrayList<String> gesturesExt = new ArrayList<String>(5);
//    String[] s = new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);
        barChart = findViewById(R.id.bar_chart);

        BarDataSet barDataSet = new BarDataSet(dataValues(),"Gestures");
        barDataSet.setBarShadowColor(Color.GRAY);
        barDataSet.setColor(Color.BLUE);

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barData.setValueTextSize(12);
        barData.setValueFormatter(new BarGraph.MyValueFormatter());

        barChart.setData(barData);
        barChart.invalidate();

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new BarGraph.MyAxisValueFormatter());
        gesturesExt.add(0,"LongPress");
        gesturesExt.add(1,"Scroll");
        gesturesExt.add(2,"Swipe");
        gesturesExt.add(3,"Tap");
        gesturesExt.add(4,"TwoFinger");

        barChart.setDrawGridBackground(true);
        barChart.setDrawBorders(true);
        barChart.setBorderWidth(3);

        Description description = new Description();
        description.setText("Gesture Analysis");
        description.setTextColor(Color.BLACK);
        description.setTextSize(25);
        barChart.setDescription(description);


    }

    private ArrayList<BarEntry> dataValues(){
        ArrayList<BarEntry> dataVals = new ArrayList<>();
        String gesture = databaseHandler.fetchGestures("gesture");
        String cnt = databaseHandler.fetchGestures("cnt");
        List<String> gestures = Arrays.asList(gesture.split("\\s*[|]\\s*"));
        List<String> cnts = Arrays.asList(cnt.split("\\s*[|]\\s*"));
        count = 0;
        Log.d("gestures","gestures length : "+gestures.size()+" cnt length : "+cnts.size());
        for(int i=1;i<gestures.size();i++) {
            if (gestures.get(i).equals("Detected: LongPress") || gestures.get(i).equals("Detected: Scroll") || gestures.get(i).equals("Detected: Swipe") ||
                    gestures.get(i).equals("Detected: Tap") || gestures.get(i).equals("Detected: TwoFinger")) {
                count+=1;
                if(gestures.get(i).equals("Detected: LongPress")) {
                    gesturesExt.add(i - 2, "TwoFinger");
                }
                else{
                    gesturesExt.add(i - 2, gestures.get(i));
                }
                Log.d("gestures","inserted : "+i+"  "+gesturesExt.get(i-2));
                dataVals.add(new BarEntry(i-2, Integer.parseInt(cnts.get(i))));
                Log.d("gesture","dataSet has "+(i-2));
            }
        }
        return dataVals;

    }

    private class MyAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            axis.setLabelCount(5,true);
            Log.d("gestures"," index :  "+Math.round(value));
            return gesturesExt.get(Math.round(value));
        }
    };

    public class MyValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return Math.round(value)+"";
        }
    };

    @Override

    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,
                R.anim.slide_out_right);
    }


}