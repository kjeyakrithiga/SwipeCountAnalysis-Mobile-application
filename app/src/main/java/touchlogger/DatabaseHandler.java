package com.touchlogger;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME = "SwipeCount.db";
    public static final String TABLE_NAME = "CountHolder";

    public static final String COLUMN_ID = "Count_ID ";
    public static final String SWIPE_TIME = "Swipe_Time ";
    public static final String SWIPE_TYPE = "Swipe_Type ";
    public static final String SWIPE_COUNT = "Swipe_Count";
    SQLiteDatabase database ;
    public DatabaseHandler(@Nullable Context context,Boolean First) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        database = this.getWritableDatabase();
        //if(First) {
          //  context.deleteDatabase(DATABASE_NAME);
        //}
        Log.d("DB operations","calling contructor");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB operations","TABLE Created");
        //  Toast.makeText(DatabaseHandler.this,"DB creation",Toast.LENGTH_LONG).show();
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ( "+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT" +
                " , "+SWIPE_TIME+" DATETIME DEFAULT (datetime('now','localtime')), "+SWIPE_TYPE+" TEXT, "+SWIPE_COUNT +" INTEGER ) ");
//        db.execSQL("CREATE TABLE Sample (sampleID INTEGER PRIMARY KEY)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB operations","dropping table..");
        db.execSQL("DROP TABLE "+TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS Sample");
        onCreate(db);
    }

    public String insertData(String swipeType,String count){
        Log.d("DB operations","called insert data");
        database = this.getWritableDatabase();
        Log.d("DB operations","getWritableDatabase");
        ContentValues contentValues = new ContentValues();
//        contentValues.put(SWIPE_TIME,getDateTime());
        contentValues.put(SWIPE_TYPE,swipeType);
        contentValues.put(SWIPE_COUNT,Integer.parseInt(count));
        database.insert(TABLE_NAME,null,contentValues);
        Log.d("DB operations","inserted values successfully!!!"+swipeType+" "+count+"");
        Log.d("DB operations","calling contructor");
        return "inserted";
    }

    public String fetchTodaySwipes(){
        Log.d("DB operations","fetch called");
        database = this.getReadableDatabase();
        Log.d("DB operations","getreadable database");
        String cnt;
        String type;
        Date date1;
        Calendar calendar = Calendar.getInstance();
        date1 = calendar.getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(date1);
        Log.d("DB operations","date ..."+date);
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE Swipe_Time >= Datetime( '"+date+"' ) ",null);
        Log.d("DB operations","query executing ...");
        while(cursor.moveToNext()){
            cnt = cursor.getString(0);
            Log.d("DB operations","count is "+cnt);
            cursor.close();
            return cnt;
        }
        return "";
    }


    public String fetchYesterdaySwipes(){
        Log.d("DB operations","fetch yesterday called");
        database = this.getReadableDatabase();
        Log.d("DB operations","getreadable database");
        String cnt;
        Date yes,tomorrow;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        yes = calendar.getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = formatter.format(yes);

        calendar.add(Calendar.DATE, 1);
        tomorrow = calendar.getTime();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        String tomo = formatter.format(tomorrow);

        Log.d("DB operations","yesterday date ..."+yesterday);
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE Swipe_Time >= Datetime( '"+yesterday+"' ) AND Swipe_Time < Datetime( '"+tomo+"' ) ",null);
        Log.d("DB operations","query executing ...");
        while(cursor.moveToNext()){
            cnt = cursor.getString(0);
            Log.d("DB operations","yesterday count is "+cnt);
            cursor.close();
            return cnt;
        }
        return "";
    }

    public String fetchWeekSwipes(){
        Log.d("DB operations","fetch week called");
        database = this.getReadableDatabase();
        Log.d("DB operations","getreadable database");
        String cnt;
        String type;
        Date date1;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        date1 = calendar.getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(date1);
        Log.d("DB operations","last week date ..."+date);
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE Swipe_Time >= Datetime( '"+date+"' ) ",null);
        Log.d("DB operations","query executing ...");
        while(cursor.moveToNext()){
            cnt = cursor.getString(0);
            Log.d("DB operations","count is "+cnt);
            cursor.close();
            return cnt;
        }
        return "";
    }

    public String fetchMaximumSwipesPerDay(){
        Log.d("DB operations","fetch week called");
        database = this.getReadableDatabase();
        Log.d("DB operations","getreadable database");
        String cnt,date;
//        Cursor cursor = database.rawQuery("SELECT MAX(COUNT(*)) as date FROM "+TABLE_NAME+"  GROUP BY DATE(Swipe_Time)",null);
        Cursor cursor = database.rawQuery("SELECT MAX(countEachDay),date FROM (SELECT DATE(Swipe_Time) as date,COUNT(*) as countEachDay FROM "+TABLE_NAME+" GROUP BY date)",null);
        Log.d("DB operations","query executing ...");
        while(cursor.moveToNext()){
            cnt = cursor.getString(0);
            date = cursor.getString(1);
            Log.d("DB operations","max count is "+cnt+" on "+date);
            cursor.close();
            return cnt+" on "+date;
        }
        return "";
    }

    public String fetchTotalDays(){
        Log.d("DB operations","total days called");
        database = this.getReadableDatabase();
        Log.d("DB operations","getreadable database");
        String cnt;
//        Cursor cursor = database.rawQuery("SELECT MAX(COUNT(*)) as date FROM "+TABLE_NAME+"  GROUP BY DATE(Swipe_Time)",null);
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM (SELECT DATE(Swipe_Time) as date FROM "+TABLE_NAME+" GROUP BY date)",null);
        Log.d("DB operations","query executing ...");
        while(cursor.moveToNext()){
            cnt = cursor.getString(0);
            Log.d("DB operations","total days is "+cnt);
            cursor.close();
            return cnt;
        }
        return "";
    }

    public String fetchAverageSwipes(){
        Log.d("DB operations","fetch average called");
        database = this.getReadableDatabase();
        Log.d("DB operations","getreadable database");
        String cnt;
        Cursor cursor = database.rawQuery("SELECT ROUND(AVG(total)) FROM (SELECT DATE(Swipe_Time) as date,COUNT(*) as total FROM "+TABLE_NAME+" GROUP BY date)",null);
        Log.d("DB operations","query executing ...");
        while(cursor.moveToNext()){
            cnt = cursor.getString(0);
            Log.d("DB operations","average "+cnt);
            cursor.close();
            return cnt;
        }
        return "";
    }

    public String fetchDates(String s){
            Log.d("Db operations","fetching dates");
            database = this.getReadableDatabase();
            Log.d("DB operations","getreadable database");
            String cnt="";
            Cursor cursor = database.rawQuery("SELECT DATE(Swipe_Time), COUNT(*) FROM "+TABLE_NAME+" GROUP BY DATE(Swipe_Time)",null);
            Log.d("DB operations","query executing ...");
            String result = "";
            while(cursor.moveToNext()){
                result += ","+cursor.getString(0);
                cnt += ","+cursor.getString(1);
                Log.d("DB operations","result "+result);
            }
            cursor.close();
            if(s.equals("dates")) {
                return result;
            }
            else if(s.equals("cnt")){
                return cnt;
            }
            return "";
        }

        public String fetchGestures(String s){
            Log.d("Db operations","fetching gestures");
            database = this.getReadableDatabase();
            Log.d("DB operations","getreadable database");
            String cnt="";

            Cursor cursor = database.rawQuery("SELECT DISTINCT Swipe_Type, COUNT(*) FROM "+TABLE_NAME+" GROUP BY Swipe_Type",null);
            Log.d("DB operations","query executing ...");
            String gestures = "";
//            String avg="";
            while(cursor.moveToNext()){
                gestures += "|"+cursor.getString(0);
                cnt += "|"+cursor.getString(1);
                Log.d("DB operations","gesture "+gestures+" cnt : "+cnt);
            }
            cursor.close();
            if(s.equals("gesture")) {
                return gestures;
            }
            else if(s.equals("cnt")){
                return cnt;
            }
            return "";
        }

}
