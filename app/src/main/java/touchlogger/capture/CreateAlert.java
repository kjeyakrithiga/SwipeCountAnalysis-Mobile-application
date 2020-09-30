package com.touchlogger.capture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;

import com.touchlogger.MainActivity;
import com.touchlogger.R;

import static com.touchlogger.capture.printCountStatus.today;

public class CreateAlert extends Activity implements Runnable{

    static Context context;

    public CreateAlert(MainActivity context){
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setMessage(){
        run();
    }


    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("Reached threshold swipes !!!");
                builder.setTitle("SWIPE COUNT");

                //Setting message manually and performing action on button click
                builder.setMessage("You have reached a SWIPE count of "+today+" today!!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("AlertDialogExample");
                WindowManager.LayoutParams lp = alert.getWindow().getAttributes();
                lp.height = 200;
                alert.getWindow().setAttributes(lp);

                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alert.show();

            }
        });
    }
}