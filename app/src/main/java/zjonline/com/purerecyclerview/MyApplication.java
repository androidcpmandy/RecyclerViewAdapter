package zjonline.com.purerecyclerview;

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

public class MyApplication extends Application{

    private static RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

//        refWatcher = setupLeakCanary();


//        new Thread() {
//            @Override
//            public void run() {
//                showDebugModeWarning();
//            }
//        }.start();
    }

    private void showDebugModeWarning() {
        try {
            Looper.prepare();
            String info = "现在您打开了 SensorsData SDK 的 'DEBUG_ONLY' 模式，此模式下只校验数据但不导入数据，数据出错时会以 Toast 的方式提示开发者，请上线前一定使用 DEBUG_OFF 模式。";
            Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
            Looper.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private RefWatcher setupLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return RefWatcher.DISABLED;
//        }
//        return LeakCanary.install(this);
//    }

//    public static RefWatcher getRefWatcher() {
//        return refWatcher;
//    }
}
