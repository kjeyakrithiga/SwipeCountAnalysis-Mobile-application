package com.touchlogger.capture;

import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.touchlogger.DatabaseHandler;
import com.touchlogger.capture.printCountStatus;
import com.touchlogger.MainActivity;
import com.touchlogger.R;
import com.touchlogger.gestures.Gesture;
import com.touchlogger.gestures.GestureDetectSink;
import com.touchlogger.gestures.GestureDetector;
import com.touchlogger.touch.EventParser;
import com.touchlogger.touch.TouchEvent;
import com.touchlogger.touch.TouchEventSerializer;
import com.touchlogger.touch.TouchEventsSink;
import com.touchlogger.touch.TouchEventsSource;
import com.touchlogger.utils.Configuration;
//import com.touchlogger.MainActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.touchlogger.capture.printCountStatus.today;

public class CaptureThread extends Thread {
    private static final String LOGTAG = "CaptureThread";

    public TouchEventProxy proxy = new TouchEventProxy();


    protected CaptureService service;
    protected String device;
    protected boolean cancel = false;
    protected boolean capturing = false;
    protected boolean mute = false;
    protected static int count=-5;
    protected Process currentProcess = null;

    protected EventParser eventParser;
    protected GestureDetector gestureDetector;
    private printCountStatus printCnt;

    public Calendar calendar ;
    public String currentTime;
    public String time;
    public enum Status {
        warning,
        capturing,
        paused
    }
    public class TouchEventProxy extends TouchEventsSource implements TouchEventsSink {

        @Override
        public void onTouchEvents(ArrayList<TouchEvent> touchEvents) {
            sendTouchEvents(touchEvents);
        }
    }

    public CaptureThread(CaptureService srv) {
        service = srv;
        printCnt = new printCountStatus(MainActivity.getInstance());
        //txtView = ;
        Log.i(LOGTAG,"printCnt initiated!!!");
        WindowManager wm = (WindowManager) srv.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point dimensions = new Point();
        display.getSize(dimensions);
        gestureDetector = new GestureDetector(dimensions.x, dimensions.y);
        proxy.registerCallback(gestureDetector);
        gestureDetector.registerCallback(new GestureNotifier());
        calendar = Calendar.getInstance();
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        time="21:04:20";
    }

    public void cancelAsync() {
        this.cancel = true;
        synchronized (this) {
            if (this.currentProcess != null) {
                this.currentProcess.destroy();
            }
        }
    }


    public void run() {
//        Toast.makeText(CaptureThread.this,"Thread starting",Toast.LENGTH_LONG).show();
        Log.i(LOGTAG,"Thread run method");
        CapturePreparation capturePreparation = new CapturePreparation(service);
        try {
            String adbPath = capturePreparation.getAdbPath();
            printStatus("starting capture");
            Log.d(LOGTAG,"adbpath : "+adbPath);
            startAdb(adbPath);
        } catch (IOException e) {
            printStatus("Error during setup, check log");
            return;
        }
//        printCnt.Update(count);
    }





    private int exec(ProcessBuilder processBuilder) throws IOException {
        String output;
        Process process;
        Scanner scanner;

        Log.d(LOGTAG, "executing: " + processBuilder.command());
        process = processBuilder.start();
        Log.d(LOGTAG, "process builder started ... ");

        int result = -1;
        try {
            result = process.waitFor();
        } catch (InterruptedException e) {
            // should not happen
        }

        scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
        output = scanner.hasNext() ? scanner.next() : "no output";
        Log.d(LOGTAG, output);
        if (output.contains("unable to connect to")) result = -1;

        return result;
    }

    public void setOrientationPortrait(boolean orientationPortrait) {
        if(eventParser != null) {
            eventParser.setOrientationPortrait(orientationPortrait);
        }
    }

    public interface ICapture {
        /**
         *
         * @param line current line printed from getevent
         * @return whether to continue capturing
         */
        boolean process(String line);
    }

    private int runCapture(ProcessBuilder processBuilder, String filePath, String cmd, String name, ICapture cap) throws IOException {
        Process process;

//        printStatus("Trying to start " + name);
        processBuilder.command().clear();
        processBuilder.command(filePath, "shell");
        process = processBuilder.start();
        OutputStreamWriter processWriter = new OutputStreamWriter(process.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        int read;
        char[] buffer = new char[4096];

        try {
            Thread.sleep(500);
        } catch(InterruptedException e){}

        String testCommand = "\r\n";
        processWriter.write(testCommand, 0, testCommand.length());
        try {
            processWriter.flush();
        } catch (IOException e) {
            return handleBrokenPipe(name + " shell", process);
        }

        // read control chars
        boolean hasPrompt = false;
        while(!hasPrompt) {
            read = reader.read(buffer);
            for (int i = 0; i < read; i++) {
                if (buffer[i] == '$') {
                    hasPrompt = true;
                    break;
                }
            }
        }
        printStatus(name + " shell established");

        // now that the prompt is there, write the command
        try {
            processWriter.write(cmd, 0, cmd.length());
            processWriter.flush();
        } catch(IOException e) {
            handleBrokenPipe(name + " getevent", process);
        }

        // read the returned command
        reader.read(buffer);
//        printStatus(name + " command sent: " + cmd);

        synchronized (this) { currentProcess = process;
            capturing = true;
        }
        boolean continuing = true;
        while (continuing) {
            String line = reader.readLine();
            if (line == null) break;
            continuing = cap.process(line) && !cancel;
        }
        synchronized (this) { currentProcess = null; }
        printStatus("Stopped capturing, no more input will be logged or analyzed");

        reader.close();
        process.destroy();
        capturing = false;

        return 0;
    }

    private int handleBrokenPipe(String command, Process process) {
        Scanner scanner;
        String error;
        int result;
        scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
        error = scanner.hasNext() ? scanner.next() : "no error";
        Log.e(LOGTAG, error);
        printStatus("Reading touch events failed, error: broken pipe");
        try {
            result = process.waitFor();
            return 1;
        } catch (InterruptedException e1) {
            return 1;
        }
    }

    private void printStatus(String status) {
        if(!mute) {
            printStatus(status, Status.warning);
        }
    }

    private void printStatus(String status, Status type) {
        Log.d(LOGTAG, status);
        if(!(status == "Swipe") && !(status =="Tap")){
            count++;
        }
        if(count>0) {
            printCnt.Update(count,status);
            service.setNotification(status + " " + today, type);
        }
    }

    private ProcessBuilder setUpAdbProcess(String filePath) {
        ProcessBuilder processBuilder = new ProcessBuilder(filePath);
        processBuilder.redirectErrorStream(true);
        processBuilder.environment().put("LD_LIBRARY_PATH", service.getApplicationContext().getApplicationInfo().dataDir);
        processBuilder.environment().put("HOME", service.getApplicationContext().getApplicationInfo().dataDir);
        processBuilder.environment().put("ADB_TRACE" , "adb");
        return processBuilder;
    }

    private void startAdb(String filePath) {
        ProcessBuilder processBuilder;
        int result;
        try {
            processBuilder = setUpAdbProcess(filePath);
            Log.d(LOGTAG,"processbuilder 1 : "+filePath);
            killServer(filePath, processBuilder);
            Log.d(LOGTAG,"killserver 2 : "+filePath);
            if (startServer(filePath, processBuilder)) {
                Log.d(LOGTAG,"inside startAdb filepath : "+filePath);
                return;
            }
            if (connectToAdb(filePath, processBuilder)) {
                return;
            }
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e){}
            printStatus("Please touch screen until this message disappears");
            mute = true;
            result = runCapture(processBuilder, filePath, "getevent -lt\n", "Find device", new ICapture() {
                long nextNotification = 0;
                @Override
                public boolean process(String line) {
                    Log.d(LOGTAG, " line : "+line);
                    if (line.startsWith("[")) {
                        long currentMillis = System.currentTimeMillis();
                        if (currentMillis > nextNotification) {
                            printStatus("Finding device in progress, please touch screen until this status disapears");
                            nextNotification = currentMillis + Configuration.notificationInterval;
                        }
                    }
                    if (line.contains("ABS_MT_")) { // multitouch protocol
                        device = line.substring(line.indexOf('/'), line.indexOf(':'));
                        printStatus("Found device:" + device);
                        return false;
                    }
                    return true;
                }
            });
            mute = false;
            eventParser = new EventParser();
            eventParser.registerCallback(proxy);
            result = runCapture(processBuilder, filePath, "getevent -lt " + device + "\n", "Capture", eventParser);
            printStatus("Trying to stop server");
            processBuilder.command(filePath, "kill-server");
            result = exec(processBuilder);
            if (result != 0) {
                printStatus("kill-server failed");
                return;
            }
            printStatus("Stopped capturing, no more input will be logged or analyzed", Status.paused);
        } catch (IOException e) {
            Log.d(LOGTAG,"entering catch part..");
            e.printStackTrace();
        }
    }

    private boolean connectToAdb(String filePath, ProcessBuilder processBuilder) throws IOException {
        int result;
        printStatus("Trying to connect adb");
        processBuilder.environment().put("ADB_TRACE" , "");
        processBuilder.command(filePath, "connect", "127.0.0.1:6000");
        result = exec(processBuilder);
        if (result != 0) {
            printStatus("connect adb failed, did you run \"adb tcpip 6000\"?");
            return true;
        }
        return false;
    }

    private boolean startServer(String filePath, ProcessBuilder processBuilder) throws IOException {
        int result;
        printStatus("Trying to start server");
        processBuilder.command(filePath, "start-server");
        result = exec(processBuilder);
        if (result == 0) {
        }
        else if (result == 255) {
//            printStatus("start-server returned 255, expected");
        } else {
            printStatus("start-server failed: " + result);
            return true;
        }
        return false;
    }

    private void killServer(String filePath, ProcessBuilder processBuilder) throws IOException {
        int result;
        printStatus("Trying to stop server");
        processBuilder.command(filePath, "kill-server");
        result = exec(processBuilder);
        if (result != 0) {
            printStatus("kill-server failed, ignoring");
        }
    }

    class GestureNotifier implements GestureDetectSink {
        @Override
        public void onGestureDetect(Gesture gesture, ArrayList<TouchEvent> events) {
            printStatus("Detected: " + gesture, Status.capturing);
//            printCnt.Update(count,status);
        }
    }
}