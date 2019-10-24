package zjonline.com.purerecyclerview;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Printer;

import com.mandy.recyclerview.log.Logger;

public class BlockMonitor {
    private Handler monitorHandler;
    private MonitorRunnable monitorRunnable;
    private StringBuilder sb;
    private static BlockMonitor instance;

    private BlockMonitor() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        HandlerThread handlerThread = new HandlerThread("Monitor");
        handlerThread.start();
        monitorHandler = new Handler(handlerThread.getLooper());
        monitorRunnable = new MonitorRunnable();
        sb = new StringBuilder();
    }

    public synchronized static BlockMonitor getInstance() {
        if (instance == null) {
            instance = new BlockMonitor();
        }
        return instance;
    }

    public void monitor() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        Looper.getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {
                if (x.contains(">>>>> Dispatching to")) {
                    monitorHandler.postDelayed(monitorRunnable, 16);
                } else if (x.contains("<<<<< Finished to")) {
                    monitorHandler.removeCallbacks(monitorRunnable);
                }
            }
        });
    }

    private class MonitorRunnable implements Runnable {

        @Override
        public void run() {
            sb.delete(0, sb.length());
            sb.append("****************************BlockInfo Start****************************").append("\n");
            StackTraceElement[] stackTraceElements = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement element : stackTraceElements) {
                sb.append(element).append("\n");
            }
            sb.append("****************************BlockInfo   End****************************");
            Logger.log(sb.toString());
        }
    }

    public static void main(String... args){
        System.out.print("hello"+args[0]);
    }
}