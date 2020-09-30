package com.touchlogger;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphViewActivity extends AppCompatActivity {
    LineGraphSeries<DataPoint> series;
    LineChart lineChart;
    DatabaseHandler database = new DatabaseHandler(this,Boolean.FALSE);
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        double y;
        lineChart = findViewById(R.id.line_chart);
        LineDataSet lineDataSet1 = null;
        try {
            lineDataSet1 = new LineDataSet(dataValues(),"Daily Swipe Count");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        lineDataSet1.setLineWidth(3);
        lineDataSet1.setCircleColor(Color.RED);
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet1.setCircleHoleRadius(1);
        lineDataSet1.setValueTextSize(8);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        data.setValueTextSize(12);
        data.setValueFormatter(new MyValueFormatter());

        //data.addDataSet(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();

        //lineChart.setBackgroundColor(Color.BLUE);
        lineChart.setDrawGridBackground(true);
        lineChart.setDrawBorders(true);
        lineChart.setBorderWidth(3);
        Description description = new Description();
        description.setText("Swipe Analysis");
        description.setTextColor(Color.BLACK);
        description.setTextSize(25);
        lineChart.setDescription(description);

        Legend legend = new Legend();
        legend = lineChart.getLegend();

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MyAxisValueFormatter());

//        YAxis yAxis = lineChart.getAxisRight();
  //      yAxis.setValueFormatter(new MyYAxisValueFormatter());


    }

    public int count=2;

    private ArrayList<Entry> dataValues() throws ParseException {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        String datesDb = database.fetchDates("dates");
        String cnt = database.fetchDates("cnt");
        List<String> dates = Arrays.asList(datesDb.split("\\s*,\\s*"));
        List<String> cnts = Arrays.asList(cnt.split("\\s*,\\s*"));
        count = dates.size();
        for(int i=1;i<dates.size();i++) {
            Date date=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(dates.get(i));
            dataVals.add(new Entry(date.getTime(),Integer.parseInt(cnts.get(i))));
        }
        return dataVals;
    }


    public static String getDate(long milliSeconds, String dateFormat)
    {
        Log.d("graph","called getDate() : "+milliSeconds);
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        calendar.add(calendar.DATE,1);
        Log.d("graph"," returning : "+formatter.format(calendar.getTime()));
        return formatter.format(calendar.getTime());
    }

    public class MyValueFormatter implements IValueFormatter{

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return Math.round(value)+"";
        }
    };

    private class MyAxisValueFormatter implements IAxisValueFormatter{

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            axis.setLabelCount(count-1,true);
            return getDate((long) value,"dd/MM");
        }
    };

    private class MyYAxisValueFormatter implements IAxisValueFormatter{

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            axis.setLabelCount(count-1,true);
            return "";
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